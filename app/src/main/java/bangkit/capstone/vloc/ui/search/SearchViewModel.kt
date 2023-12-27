package bangkit.capstone.vloc.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import bangkit.capstone.vloc.data.VlocRepository
import bangkit.capstone.vloc.data.model.PredictResponse
import bangkit.capstone.vloc.data.model.UserModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class SearchViewModel(private val repository: VlocRepository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _response = MutableLiveData<PredictResponse>()
    val response: LiveData<PredictResponse> = _response

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun predictImage(token: String, file: MultipartBody.Part) {
        _isLoading.value = false
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val successResponse = repository.predictLocation(token, file)
                _response.value = successResponse
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, PredictResponse::class.java)
                _response.value = errorResponse
                _isLoading.value = false
            }
        }
    }

//    fun getAllStory(token: String): LiveData<PagingData<ListDestinationItem>>? {
//        _isLoading.value = true
//        return try {
//            val response = repository.getDestination(token).cachedIn(viewModelScope)
//            _isLoading.value = false
//            response
//        } catch (e: HttpException) {
//            _isLoading.value = false
//            null
//        }
//    }
}