package eg.iti.mad.climaguard.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.newsapp.api.ApiManager
import eg.iti.mad.climaguard.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val repo:Repository):ViewModel() {


    fun getCurrentWeather(){
        viewModelScope.launch(Dispatchers.IO){
            //the view model must get the lat and long from GPS
            repo.getCurrentWeather(44.34,10.99)
        }
    }

}

class HomeFactory(private val repo: Repository): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo) as T
    }

}