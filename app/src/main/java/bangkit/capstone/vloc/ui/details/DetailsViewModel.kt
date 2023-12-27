package bangkit.capstone.vloc.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import bangkit.capstone.vloc.data.VlocRepository
import bangkit.capstone.vloc.data.local.database.Favorites
import bangkit.capstone.vloc.data.model.Details
import bangkit.capstone.vloc.data.model.UserModel
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailsViewModel(private val repository: VlocRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    private val _location = MutableLiveData<Details>()
    val location: LiveData<Details> = _location

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    suspend fun getDetails(destinationId: String) {
        _isLoading.value = true
        try {
            val response = repository.getDetailsLocation(destinationId)
            _location.value = response
        } catch (e: HttpException) {
            _status.value = "Error fetching details"
        } finally {
            _isLoading.value = false
        }
    }


    // local favorites
    fun senFav(data: Favorites) {
        viewModelScope.launch {
            repository.postFavorite(data)
        }
    }

    fun deleteFav(data: Favorites) {
        viewModelScope.launch {
            repository.deleteFavorite(data)
        }
    }

    fun checkFavoriteStatus(id: String?): LiveData<Boolean> {
        return repository.checkFavorites(id)
    }
}
