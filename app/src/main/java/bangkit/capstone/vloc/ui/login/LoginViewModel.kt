package bangkit.capstone.vloc.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bangkit.capstone.vloc.data.VlocRepository
import bangkit.capstone.vloc.data.model.LoginResponse
import bangkit.capstone.vloc.data.model.UserModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException


class LoginViewModel(private val repository: VlocRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _response = MutableLiveData<LoginResponse>()
    val response: LiveData<LoginResponse> = _response

    fun postLogin(email: String, password: String) {
        _isLoading.value = false
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val successResponse = repository.postLogin(email, password)
                val userId = successResponse.userId
                val name = successResponse.username
                val token = successResponse.token
                val userSession = UserModel(userId, name, email, token, true)
                repository.saveSession(userSession)
                _response.value = successResponse
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                _response.value = errorResponse
                _isLoading.value = false
            }
        }
    }

}