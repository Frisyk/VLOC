package bangkit.capstone.vloc.data.remote

import bangkit.capstone.vloc.data.model.DetailsResponse
import bangkit.capstone.vloc.data.model.FavoriteData
import bangkit.capstone.vloc.data.model.StoryResponse
import bangkit.capstone.vloc.data.model.LoginRequest
import bangkit.capstone.vloc.data.model.LoginResponse
import bangkit.capstone.vloc.data.model.PostResponse
import bangkit.capstone.vloc.data.model.RegisterRequest
//import bangkit.capstone.vloc.data.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    suspend fun register(
        @Body user: RegisterRequest
    ): PostResponse

    @POST("login")
    suspend fun login(
        @Body user: LoginRequest
    ): LoginResponse

    @GET("stories/{id}")
    suspend fun getDetailsDestination(
        @Header("Authorization") token: String,
        @Path("id") destinationId: String?
    ): DetailsResponse


    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): PostResponse

    @POST("stories")
    suspend fun postFavorite(
        @Body favoriteData: FavoriteData
    ) : PostResponse

    @DELETE("stories")
    suspend fun deleteFavorite(
        @Body favoriteData: FavoriteData
    ) : PostResponse
}