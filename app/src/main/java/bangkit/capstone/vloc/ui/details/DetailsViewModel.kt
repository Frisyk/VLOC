package bangkit.capstone.vloc.ui.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import bangkit.capstone.vloc.data.VlocRepository
import bangkit.capstone.vloc.data.model.DetailsResponse
import bangkit.capstone.vloc.data.model.FavoriteData
import bangkit.capstone.vloc.data.model.ListDestinationItem
import bangkit.capstone.vloc.data.model.LoginRequest
import bangkit.capstone.vloc.data.model.LoginResponse
import bangkit.capstone.vloc.data.model.PostResponse
import bangkit.capstone.vloc.data.model.UserModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailsViewModel(private val repository: VlocRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _favResponse = MutableLiveData<PostResponse>()
    val favResponse: LiveData<PostResponse> = _favResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _destination = MutableLiveData<DetailsResponse?>()
    val destination: LiveData<DetailsResponse?> = _destination

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

//    fun sendFav(token: String, data: FavoriteData) {
//        _isLoading.value = false
//        viewModelScope.launch {
//            try {
//                _isLoading.value = true
//                val successResponse = repository.postFavorite(token, data)
//                _favResponse.value = successResponse
//            } catch (e: HttpException) {
//                val errorBody = e.response()?.errorBody()?.string()
//                val errorResponse = Gson().fromJson(errorBody, PostResponse::class.java)
//                _favResponse.value = errorResponse
//                _isLoading.value = false
//            }
//        }
//    }

    fun sendFav(data: FavoriteData) {
        _isLoading.value = false
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val successResponse = repository.postFavorite(data)
                _favResponse.value = successResponse
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, PostResponse::class.java)
                _favResponse.value = errorResponse
                _isLoading.value = false
            }
        }
    }

    suspend fun getDetails(token: String, destinationId: String?) {
        _isLoading.value = true
        try {
            val response = repository.getDetailsDestination(token, destinationId)
            _destination.value = response
        } catch (e: HttpException) {
            _error.value = "Error fetching details"
        } finally {
            _isLoading.value = false
        }
    }
}
