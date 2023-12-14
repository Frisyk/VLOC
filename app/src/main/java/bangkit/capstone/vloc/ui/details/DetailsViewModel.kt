package bangkit.capstone.vloc.ui.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import bangkit.capstone.vloc.data.VlocRepository
import bangkit.capstone.vloc.data.model.DetailsResponse
import bangkit.capstone.vloc.data.model.ListDestinationItem
import bangkit.capstone.vloc.data.model.UserModel
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailsViewModel(private val repository: VlocRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _destination = MutableLiveData<DetailsResponse?>()
    val destination: LiveData<DetailsResponse?> = _destination

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
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
