package bangkit.capstone.vloc.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import bangkit.capstone.vloc.BuildConfig

object ApiConfig {
    fun getApiService(): ApiService {
        val url = BuildConfig.BASE_URL
        val loggingInterceptor =
            if(BuildConfig.DEBUG) { HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY) }
            else { HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE) }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}