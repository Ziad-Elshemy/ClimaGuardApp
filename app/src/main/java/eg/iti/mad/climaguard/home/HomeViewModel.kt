package eg.iti.mad.climaguard.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.newsapp.api.ApiManager
import eg.iti.mad.climaguard.model.CurrentResponse
import eg.iti.mad.climaguard.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val repo:Repository):ViewModel() {

    private val TAG = "HomeViewModel"

    private val _currentWeather :MutableLiveData<CurrentResponse> = MutableLiveData()
    val currentResponse :LiveData<CurrentResponse> = _currentWeather


    fun getCurrentWeather(){
        viewModelScope.launch(Dispatchers.IO){
            try {

                //the view model must get the lat and long from GPS
                val response = repo.getCurrentWeather(29.39,30.87)
                _currentWeather.postValue(response)
                Log.d(TAG, "getCurrentWeather: ${response.id}")

            }catch (ex:Exception){
                Log.d(TAG, "getCurrentWeather: Error catch ${ex.message}")
            }

        }
    }

}

class HomeFactory(private val repo: Repository): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo) as T
    }

}