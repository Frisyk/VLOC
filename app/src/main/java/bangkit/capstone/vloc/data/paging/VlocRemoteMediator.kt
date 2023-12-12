package bangkit.capstone.vloc.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import bangkit.capstone.vloc.data.local.database.RemoteKeys
import bangkit.capstone.vloc.data.local.database.VlocDatabase
//import bangkit.capstone.vloc.data.local.database.VlocDatabase
import bangkit.capstone.vloc.data.model.ListDestinationItem
import bangkit.capstone.vloc.data.remote.ApiService

@OptIn(ExperimentalPagingApi::class)
class VlocRemoteMediator(
    private val database: VlocDatabase,
    private val apiService: ApiService,
    private val token: String
) : RemoteMediator<Int, ListDestinationItem>() {

    override suspend fun initialize(): InitializeAction {
        Log.d("database", database.vlocDao().toString())
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListDestinationItem>
    ): MediatorResult {
        Log.d("state", loadType.name)
        val page = when (loadType) {
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
            LoadType.REFRESH ->{
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                Log.d("wkwkwkwk", remoteKeys?.id.toString())
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
        }

        Log.d("resumee", state.firstItemOrNull().toString())
        try {
            val responseData = apiService.getStory( token, page, state.config.pageSize)
            Log.d("dataaaa", responseData.listStory.toString())
            val endOfPaginationReached = responseData.listStory.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.vlocDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.listStory.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                database.remoteKeysDao().insertAll(keys)
                database.vlocDao().insertStory(responseData.listStory)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            Log.d("exception", exception.message.toString() )
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListDestinationItem>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            Log.d("keyyy", data.id)
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListDestinationItem>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            Log.d("keyyy", data.id)
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListDestinationItem>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            Log.d("keyyy", position.toString())
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}