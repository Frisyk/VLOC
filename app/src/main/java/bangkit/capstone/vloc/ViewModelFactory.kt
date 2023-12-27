package bangkit.capstone.vloc

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bangkit.capstone.vloc.data.VlocRepository
import bangkit.capstone.vloc.di.Injection
import bangkit.capstone.vloc.setting.SettingViewModel
import bangkit.capstone.vloc.ui.details.DetailsViewModel
import bangkit.capstone.vloc.ui.home.MainViewModel
import bangkit.capstone.vloc.ui.login.LoginViewModel
import bangkit.capstone.vloc.ui.profile.ProfileViewModel
import bangkit.capstone.vloc.ui.register.RegisterViewModel
import bangkit.capstone.vloc.ui.search.SearchViewModel


class ViewModelFactory(private val repository: VlocRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailsViewModel::class.java) -> {
                DetailsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context.applicationContext))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}