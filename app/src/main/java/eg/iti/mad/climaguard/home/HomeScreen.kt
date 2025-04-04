package eg.iti.mad.climaguard.home

import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.wrapContentSize

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eg.iti.mad.climaguard.R

import eg.iti.mad.climaguard.model.ForecastResponse
import eg.iti.mad.climaguard.model.Response


@Composable
fun HomeScreen(viewModel: HomeViewModel,location: Location) {

    LaunchedEffect(location) {
        viewModel.updateLocation(location.latitude, location.longitude)
    }

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



    LaunchedEffect(hourlyList, daysList) {
        if (!(hourlyList!!.isEmpty()||daysList!!.isEmpty())){
            Log.d("hourlyList", "Hourly list updated: ${hourlyList?.get(0)?.main?.tempMax?:"empty"}")
            Log.d("hourlyList", "Days list updated: ${daysList?.get(0)?.main?.tempMax?:"empty"}")
            Log.d("hourlyList", "HomeScreen: ===============================")
        }
    }

    var responseForecast:ForecastResponse? = null
    when(uiForecastState){
        is Response.Loading -> {
            LoadingIndicator()
        }
        is Response.Success -> {

            when (uiState) {
                is Response.Loading -> {
                    LoadingIndicator()
                }

                is Response.Success -> {
                    responseForecast = (uiForecastState as Response.Success).data
                    val responseData = (uiState as Response.Success).data
                    WeatherScreenUi(
                        responseData = responseData,
                        responseForecast = responseForecast,
                        hourlyList = hourlyList,
                        daysList = daysList,
                        tempUnitSymbol = tempUnitSymbol
                    )
                    Log.d("HomeScreen", "HomeScreen: Response.Success")
                }

                is Response.Failure -> {
                    Text(
                        text = stringResource(R.string.failed_to_fetch_data),
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(),
                        fontSize = 22.sp
                    )
                }


            }
        }
        is Response.Failure -> {
            Text(
                text = stringResource(R.string.failed_to_fetch_data),
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(),
                fontSize = 22.sp
            )
        }
    }


}

