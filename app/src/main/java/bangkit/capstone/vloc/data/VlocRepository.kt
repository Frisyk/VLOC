package bangkit.capstone.vloc.data

import android.location.Location
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
import bangkit.capstone.vloc.data.model.DetailsResponse
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class VlocRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val vlocDatabase: VlocDatabase,
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
                vlocDatabase.vlocDao().getAllStory()
            }
        ).liveData
    }

    suspend fun getDetailsDestination(token: String, destinationId: String?): DetailsResponse{
        return apiService.getDetailsDestination(token, destinationId)
    }

    suspend fun postStory(token: String, multipartBody: MultipartBody.Part, requestBody: RequestBody): PostResponse {
        return apiService.uploadStory(
            token,
            multipartBody,
            requestBody,
        )
    }


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