package bangkit.capstone.vloc.data.remote

import bangkit.capstone.vloc.data.model.Details
import bangkit.capstone.vloc.data.model.FavoritesResponse
import bangkit.capstone.vloc.data.model.LocationListItem
import bangkit.capstone.vloc.data.model.LocationResponse
import bangkit.capstone.vloc.data.model.LoginResponse
import bangkit.capstone.vloc.data.model.PostResponse
import bangkit.capstone.vloc.data.model.PredictResponse
import bangkit.capstone.vloc.data.model.Response
import bangkit.capstone.vloc.data.model.UserResponseItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): PostResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("users/{id_user}")
    suspend fun getUserDetails(
        @Header("x-access-token") token: String,
        @Path("id_user") userId: Int
    ): UserResponseItem



    @GET("location")
    suspend fun getAllLocation(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): LocationResponse

    @GET("location")
    suspend fun getLocation(): Response

    @GET("location/{id_location}")
    suspend fun getDetailsLocation(
        @Path("id_location") idLocation: String
    ) : Details

    @GET("/location")
    suspend fun getLocationByCategory(
        @Query("category") category: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ):  LocationResponse

    @GET("/location")
    suspend fun getLocationBasedCategory(
        @Query("category") category: String,
    ):  Response

    @GET("favorite/{user_id}")
    suspend fun getUserFavorites(
        @Header("x-access-token") token: String,
        @Path("user_id") userId: Int
    ): FavoritesResponse

    @FormUrlEncoded
    @POST("favorite/{user_id}")
    suspend fun postFavorite(
        @Header("x-access-token") token: String,
        @Path("user_id") userId: Int?,
        @Field("location_id") locationId: String
    ): PostResponse

    @FormUrlEncoded
    @DELETE("favorite/{user_id}")
    suspend fun deleteFavorite(
        @Header("x-access-token") token: String,
        @Path("user_id") userId: Int,
        @Field("location_id") locationId: String
    ): PostResponse

    @Multipart
    @POST("predict")
    suspend fun predictLocation(
        @Header("x-access-token") token: String,
        @Part file: MultipartBody.Part
    ): PredictResponse

    @Multipart
    @PUT("users/profile/{user_id}")
    suspend fun changePhotoProfile(
        @Header("x-access-token") token: String,
        @Path("user_id") userId: Int,
        @Part image: MultipartBody.Part
    ): PostResponse
}