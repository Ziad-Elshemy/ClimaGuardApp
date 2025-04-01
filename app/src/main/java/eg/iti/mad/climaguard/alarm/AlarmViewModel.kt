package eg.iti.mad.climaguard.alarm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import eg.iti.mad.climaguard.favorite.FavoriteViewModel
import eg.iti.mad.climaguard.model.AlarmEntity
import eg.iti.mad.climaguard.model.LocationEntity
import eg.iti.mad.climaguard.model.Response
import eg.iti.mad.climaguard.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlarmViewModel(private val repo: Repository) : ViewModel()  {


    private val TAG = "AlarmViewModel"

    private val _alarmsList = MutableStateFlow<Response<List<AlarmEntity>>>(Response.Loading)
    val alarmsList = _alarmsList.asStateFlow()

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()


    fun getAlarms() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repo.getAllAlarms()
                result
                    .catch { ex ->
                        _alarmsList.value = Response.Failure(ex)
                        _message.emit("Error From DB ${ex.message}")
                    }
                    .collect {
                        _alarmsList.value = Response.Success(it!!)
                    }
            } catch (ex: Exception) {

                Log.e(TAG, "AlarmViewModel: Error ${ex.message}")
                _message.emit("Error from coroutines ${ex.message}")
            }
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repo.removeAlarm(alarm)
                if (result > 0) {
                    _message.emit("${alarm.locationName} Deleted Successfully!")

                } else {
                    _message.emit("Alarm is already deleted res: $result")
                }
            } catch (ex: Exception) {
                _message.emit("Couldn't delete Alarm :${ex.message}")
            }
        }
    }

    fun saveAlarm(alarm: AlarmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repo.addAlarm(alarm)
                if (result > 0) {
                    _message.emit("${alarm.locationName} Added Successfully!")

                } else {
                    _message.emit("Alarm is already added res: $result")
                }
            } catch (ex: Exception) {
                _message.emit("Couldn't add Alarm :${ex.message}")
            }
        }
    }

}

class AlarmFactory(private val repo: Repository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlarmViewModel(repo) as T
    }

}