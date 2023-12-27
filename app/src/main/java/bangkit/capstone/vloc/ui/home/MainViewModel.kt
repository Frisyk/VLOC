package bangkit.capstone.vloc.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bangkit.capstone.vloc.data.VlocRepository
import bangkit.capstone.vloc.data.model.LocationListItem
import bangkit.capstone.vloc.data.model.LocationResponseItem
import bangkit.capstone.vloc.data.model.UserModel
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel (private val repository: VlocRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading



    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }


    private val _response = MutableLiveData<List<LocationListItem>>()
    val response: LiveData<List<LocationListItem>> = _response

    fun getLocation() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getLocation()
                _isLoading.value = false
                _response.value = response.locationList
            } catch (e: HttpException) {
                _isLoading.value = false
            }
        }
    }

    fun getLocationByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getLocationBasedCategory(category)
                _isLoading.value = false
                _response.value = response.locationList
            } catch (e: HttpException) {
                _isLoading.value = false
            }
        }
    }

    fun getAllDestination(category: String?): LiveData<PagingData<LocationResponseItem>>? {
        _isLoading.value = true
        return try {
            val response = repository.getDestination(category).cachedIn(viewModelScope)
            _isLoading.value = false
            response
        } catch (e: HttpException) {
            _isLoading.value = false
            null
        }
    }

}