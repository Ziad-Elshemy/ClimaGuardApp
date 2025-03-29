package eg.iti.mad.climaguard.favitemscreen

import android.location.Location
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.landscapist.glide.GlideImage
import eg.iti.mad.climaguard.R
import eg.iti.mad.climaguard.home.HomeViewModel
import eg.iti.mad.climaguard.home.LoadingIndicator
import eg.iti.mad.climaguard.home.WeatherScreenUi
import eg.iti.mad.climaguard.model.CurrentResponse
import eg.iti.mad.climaguard.model.ForecastResponse
import eg.iti.mad.climaguard.model.ListItem
import eg.iti.mad.climaguard.model.Response
import eg.iti.mad.climaguard.utils.Utility
import kotlin.math.sin

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
                        daysList = daysList
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

