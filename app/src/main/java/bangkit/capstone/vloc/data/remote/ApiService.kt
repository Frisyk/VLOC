package bangkit.capstone.vloc.data.remote

import bangkit.capstone.vloc.data.model.DestinationResponse
import bangkit.capstone.vloc.data.model.LoginRequest
import bangkit.capstone.vloc.data.model.LoginResponse
import bangkit.capstone.vloc.data.model.PostResponse
import bangkit.capstone.vloc.data.model.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

    @GET("stories")
    suspend fun getDestination(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): DestinationResponse

    @GET("stories")
    suspend fun getDatas(
        @Header("Authorization") token: String
    ) : DestinationResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): PostResponse

//    @GET("stories")
//    suspend fun getStoriesWithLocation(
//        @Header("Authorization") token: String,
//        @Query("location") location : Int = 1,
//    ): StoryResponse
}