package bangkit.capstone.vloc.di

import android.content.Context
import bangkit.capstone.vloc.data.VlocRepository
import bangkit.capstone.vloc.data.local.database.VlocDatabase
import bangkit.capstone.vloc.data.local.pref.UserPreference
import bangkit.capstone.vloc.data.local.pref.dataStore
import bangkit.capstone.vloc.data.remote.ApiConfig


object Injection {
    fun provideRepository(context: Context): VlocRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val database = VlocDatabase.getDatabase(context)
        return VlocRepository.getInstance(pref, apiService, database)
    }
}