package bangkit.capstone.vloc.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import bangkit.capstone.vloc.data.model.ListDestinationItem
import bangkit.capstone.vloc.data.remote.ApiService

class StoryPagingSource(private val token: String, private val apiService: ApiService) : PagingSource<Int, ListDestinationItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListDestinationItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getDestination(token, position, params.loadSize).listDestination
            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListDestinationItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}