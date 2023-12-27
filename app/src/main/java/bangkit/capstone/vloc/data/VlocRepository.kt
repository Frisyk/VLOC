package bangkit.capstone.vloc.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import bangkit.capstone.vloc.data.local.database.Favorites
import bangkit.capstone.vloc.data.local.database.VlocDatabase
import bangkit.capstone.vloc.data.local.pref.UserPreference
import bangkit.capstone.vloc.data.model.Details
import bangkit.capstone.vloc.data.model.FavoritesResponse
import bangkit.capstone.vloc.data.model.LocationResponseItem
import bangkit.capstone.vloc.data.model.LoginResponse
import bangkit.capstone.vloc.data.model.PostResponse
import bangkit.capstone.vloc.data.model.PredictResponse
import bangkit.capstone.vloc.data.model.Response
import bangkit.capstone.vloc.data.model.UserModel
import bangkit.capstone.vloc.data.model.UserResponseItem
import bangkit.capstone.vloc.data.paging.VlocRemoteMediator
import bangkit.capstone.vloc.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody


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

    suspend fun postRegister(username: String, email: String, password: String): PostResponse {
        return apiService.register(username, email, password)
    }

    suspend fun postLogin(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun getUserDetails(token: String, userId: Int): UserResponseItem {
        return apiService.getUserDetails(token, userId)
    }

    suspend fun changePhotoProfile(token: String, userId: Int, file: MultipartBody.Part): PostResponse {
        return apiService.changePhotoProfile(token, userId, file)
    }


    suspend fun getDetailsLocation(idLocation: String): Details {
        return apiService.getDetailsLocation(idLocation)
    }


    suspend fun getUserFavorites(token: String, userId: Int): FavoritesResponse {
        return apiService.getUserFavorites(token, userId)
    }

    suspend fun postFavorite(token: String, userId: Int?, locationId: String): PostResponse {
        return apiService.postFavorite(token, userId, locationId)
    }

    suspend fun deleteFavorite(token: String, userId: Int, locationId: String): PostResponse {
        return apiService.deleteFavorite(token, userId, locationId)
    }

    suspend fun predictLocation(token: String, file: MultipartBody.Part): PredictResponse {
        return apiService.predictLocation(token, file)
    }

    suspend fun getLocation(): Response {
        return apiService.getLocation()
    }

    suspend fun getLocationBasedCategory(category: String): Response {
        return apiService.getLocationBasedCategory(category)
    }

    // database
    fun getDestination(category: String?): LiveData<PagingData<LocationResponseItem>> {

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = VlocRemoteMediator(vlocDatabase, apiService, category),
            pagingSourceFactory = {
                vlocDatabase.vlocDao().getAllLocation()
            }
        ).liveData
    }


    // local
    suspend fun postFavorite(data: Favorites) {
        return vlocDatabase.favoritesDao().insert(data)
    }
    suspend fun deleteFavorite(data: Favorites) {
        return vlocDatabase.favoritesDao().delete(data)
    }

    fun checkFavorites(id: String?): LiveData<Boolean> {
        return vlocDatabase.favoritesDao().isFavoriteUser(id)
    }

    fun getAllFavorites(): LiveData<List<Favorites>> = vlocDatabase.favoritesDao().getFavorites()



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