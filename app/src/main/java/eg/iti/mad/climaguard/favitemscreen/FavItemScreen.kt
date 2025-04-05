package eg.iti.mad.climaguard.favitemscreen

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eg.iti.mad.climaguard.R
import eg.iti.mad.climaguard.home.HomeViewModel
import eg.iti.mad.climaguard.home.LoadingIndicator
import eg.iti.mad.climaguard.home.WeatherScreenUi
import eg.iti.mad.climaguard.model.ForecastResponse
import eg.iti.mad.climaguard.model.Response

@Composable
fun FavItemScreen(viewModel: HomeViewModel, location: Location) {

//    LaunchedEffect(location.latitude, location.longitude) {
        viewModel.updateLocation(location.latitude, location.longitude)
//    }

//    viewModel.fetchWeatherData(location.latitude,location.longitude)

//    val currentWeatherState = viewModel.currentResponse.observeAsState()
    val uiState by viewModel.currentResponse.collectAsStateWithLifecycle()
    val uiForecastState by viewModel.forecastResponse.collectAsStateWithLifecycle()
    val hourlyList by viewModel.hourlyForecastResponseList.collectAsStateWithLifecycle()
    val daysList by viewModel.fiveDaysForecastWeatherList.collectAsStateWithLifecycle()
    val tempUnit by viewModel.tempUnit.collectAsState()

    val tempUnitSymbol = when (tempUnit) {
        "metric" -> stringResource(R.string.c)
        "imperial" -> stringResource(R.string.f)
        "standard" -> stringResource(R.string.k)
        else -> ""
    }
    val windUnitSymbol = when (tempUnit) {
        "metric" -> stringResource(R.string.meter_sec)
        "imperial" -> stringResource(R.string.miles_hour)
        "standard" -> stringResource(R.string.meter_sec)
        else -> ""
    }
    var responseForecast:ForecastResponse? = null
    when(uiForecastState){
        is Response.Loading -> {

        }
        is Response.Success -> {
            responseForecast = (uiForecastState as Response.Success).data
            when (uiState) {
                is Response.Loading -> {
                    LoadingIndicator()
                }

                is Response.Success -> {
                    val responseData = (uiState as Response.Success).data
                    WeatherScreenUi(
                        responseData = responseData,
                        responseForecast = responseForecast,
                        hourlyList = hourlyList,
                        daysList = daysList,
                        tempUnitSymbol = tempUnitSymbol,
                        windUnitSymbol = windUnitSymbol
                    )
                }

                is Response.Failure -> {
                    Text(
                        text = "sorry there is an error",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(),
                        fontSize = 22.sp
                    )
                }


            }
        }
        is Response.Failure -> {

        }
    }



}

