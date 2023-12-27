package bangkit.capstone.vloc.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import bangkit.capstone.vloc.data.VlocRepository
import bangkit.capstone.vloc.data.model.FavoritesResponse
import bangkit.capstone.vloc.data.model.UserModel
import bangkit.capstone.vloc.data.model.UserResponseItem
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.HttpException

class ProfileViewModel(private val repository: VlocRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userResponse = MutableLiveData<UserResponseItem>()
    val userResponse: LiveData<UserResponseItem> = _userResponse

    private val _favResponse = MutableLiveData<FavoritesResponse>()
    val favResponse: LiveData<FavoritesResponse> = _favResponse



    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getUserDetails(token: String, userId: Int) {
        _isLoading.value = false
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val successResponse = repository.getUserDetails(token, userId)
                _userResponse.value = successResponse
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, UserResponseItem::class.java)
                _userResponse.value = errorResponse
                _isLoading.value = false
            }
        }
    }

    fun getUserFavorites(token: String, userId: Int) {
        _isLoading.value = false
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val successResponse = repository.getUserFavorites(token, userId)
                _favResponse.value = successResponse
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, FavoritesResponse::class.java)
                _favResponse.value = errorResponse
                _isLoading.value = false
            }
        }
    }

    // add favorite to local database
    fun getFavorites() = repository.getAllFavorites()

    fun changeProfile(token: String, userId: Int, file: MultipartBody.Part) {
        viewModelScope.launch {
            repository.changePhotoProfile(token, userId, file)
        }
    }


}