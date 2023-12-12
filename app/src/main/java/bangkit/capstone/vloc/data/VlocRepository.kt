package bangkit.capstone.vloc.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import bangkit.capstone.vloc.data.local.database.VlocDatabase
//import bangkit.capstone.vloc.data.local.database.VlocDatabase
import bangkit.capstone.vloc.data.local.pref.UserPreference
import bangkit.capstone.vloc.data.model.StoryResponse
import bangkit.capstone.vloc.data.model.ListDestinationItem
import bangkit.capstone.vloc.data.model.LoginRequest
import bangkit.capstone.vloc.data.model.LoginResponse
import bangkit.capstone.vloc.data.model.PostResponse
import bangkit.capstone.vloc.data.model.RegisterRequest
import bangkit.capstone.vloc.data.model.UserModel
import bangkit.capstone.vloc.data.paging.VlocRemoteMediator
//import bangkit.capstone.vloc.data.paging.VlocRemoteMediator
import bangkit.capstone.vloc.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class VlocRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val vlocDatabase: VlocDatabase,
//    private val destinationDatabase: StoryDatabase
) {

    // pref session login
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun postRegister(user: RegisterRequest): PostResponse {
        return apiService.register(user)
    }

    suspend fun postLogin(user: LoginRequest): LoginResponse {
        return apiService.login(user)
    }

    // database
    fun getDestination(token: String): LiveData<PagingData<ListDestinationItem>> {

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = VlocRemoteMediator(vlocDatabase, apiService, token),
            pagingSourceFactory = {
//                StoryPagingSource(token, apiService)
                vlocDatabase.vlocDao().getAllStory()
            }
        ).liveData
    }

//    fun getStories(token: String): LiveData<PagingData<ListDestinationItem>> {
//        @OptIn(ExperimentalPagingApi::class)
//        return Pager(
//            config = PagingConfig(
//                pageSize = 5
//            ),
//            remoteMediator = StoryRemoteMediator(destinationDatabase, apiService, token),
//            pagingSourceFactory = {
////                StoryPagingSource(token, apiService)
//                destinationDatabase.destinationDao().getAllStory()
//            }
//        ).liveData
//    }

    companion object {
        @Volatile
        private var instance: VlocRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            vlocDatabase: VlocDatabase
        ): VlocRepository =
            instance ?: synchronized(this) {
                instance ?: VlocRepository(userPreference, apiService, vlocDatabase)
            }.also { instance = it }
    }
}