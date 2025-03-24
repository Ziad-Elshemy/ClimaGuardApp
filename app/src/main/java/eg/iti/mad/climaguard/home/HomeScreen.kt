package eg.iti.mad.climaguard.home

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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.landscapist.glide.GlideImage
import eg.iti.mad.climaguard.R
import eg.iti.mad.climaguard.model.Response
import eg.iti.mad.climaguard.utils.Utility
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sin

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    viewModel.getCurrentWeather()
//    val currentWeatherState = viewModel.currentResponse.observeAsState()
    val uiState by viewModel.currentResponse.collectAsStateWithLifecycle()

    when (uiState) {
        is Response.Loading -> {
            LoadingIndicator()
        }

        is Response.Success -> {
            val responseData = (uiState as Response.Success).data
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .background(
                        if (!Utility.isDayTime(responseData.dt?.toLong()?:0L))
                        Color(0xFF2F4042) else Color(0xFF95CBD2)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                // Current temperature
                Log.d("HomeScreen", "HomeScreen: ${responseData.main?.humidity}")

                Text("${responseData?.name}", color = Color.White, fontSize = 20.sp)

                //2nd header row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "${responseData.weather?.get(0)?.description}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${responseData?.name}",
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                    }

                    GlideImage(
                        imageModel = { "https://openweathermap.org/img/wn/${responseData.weather?.get(0)?.icon}@2x.png" },
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        loading = {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        },
                        failure = {
                            Image(
                                painter = painterResource(id = R.drawable.snow),
                                contentDescription = "Placeholder Image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                        }
                    )

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        val (date, time) = Utility.formatTimestamp(responseData.dt?.times(1000L) ?: 0)
                        Text(
                            text = "$time",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(text = "$date", fontSize = 14.sp, color = Color.DarkGray)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${responseData?.main?.temp ?: 0}",
                        color = Color.White,
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "°C", color = Color.White, fontSize = 20.sp, modifier = Modifier
                            .size(60.dp)
                            .padding(bottom = 20.dp)
                    )
                }
                Text(
                    "Feels like ${responseData?.main?.feelsLike ?: 0}°",
                    color = Color.DarkGray,
                    fontSize = 16.sp
                )
                Text(
                    "High ${responseData?.main?.tempMax ?: 0}° • Low ${responseData?.main?.tempMin ?: 0}°",
                    color = Color.DarkGray,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Next hour forecast
                ForecastCard("Hourly forecast") {
                    val hours = listOf("Now", "9 PM", "10 PM", "11 PM", "12 AM", "1 AM", "2 AM")
                    LazyRow {
                        items(hours) { hour ->
                            HourlyItem(hour, "88°", "10d", "10%")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                //row1
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CircularWithProgressCard(
                        "Clouds",
                        "${responseData.clouds?.all}%",
                        "",
                        responseData.clouds?.all?.toFloat() ?: 0f,
                        Color.White,
                        icon = ImageVector.vectorResource(id = R.drawable.ic_cloud)
                    )
                    HumidityCard(responseData.main?.humidity?.toFloat() ?: 0f,
                        icon = ImageVector.vectorResource(id = R.drawable.ic_humidity))
                }

                Spacer(modifier = Modifier.height(16.dp))

                //row2
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CircularCard(
                        value = responseData.main?.pressure.toString(),
                        title = "Pressure",
                        unit = "hpa",
                        icon = ImageVector.vectorResource(id = R.drawable.ic_pressure)
                    )

                    CircularCard(
                        value = responseData.wind?.speed.toString(),
                        title = "Wind Speed",
                        unit = "meter/sec",
                        icon = ImageVector.vectorResource(id = R.drawable.ic_wind)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                //row3
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val visibility = responseData.visibility?.div(1000)?:0
                    CircularCard(
                        value = visibility.toString(),
                        title = "Visibility",
                        unit = "KM",
                        icon = ImageVector.vectorResource(id = R.drawable.ic_visibility)
                    )
                    val seaLevel : Int = responseData.main?.seaLevel?:0
                    val grandLevel : Int = responseData.main?.grndLevel?:0
                    val altitude = Utility.calculateAltitude(seaLevel.toDouble(),grandLevel.toDouble())
                    CircularCard(
                        value = altitude.toString(),
                        title = "Alt",
                        unit = "m",
                        icon = ImageVector.vectorResource(id = R.drawable.ic_altitude)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Next 5 days forecast
                ForecastCard("10-day forecast") {
                    val days = listOf("Today", "Fri", "Sat", "Sun", "Mon")
                    Column {
                        days.forEach { day ->
                            DailyItem(day, "89°", "81°", R.drawable.header, "20%")
                        }
                    }
                }
            }
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

@Composable
fun LoadingIndicator() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        CircularProgressIndicator()
    }

}

@Composable
fun ForecastCard(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2A2A2A))
            .padding(16.dp)
    ) {
        Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
fun HourlyItem(time: String, temp: String, iconThumbnail: String, chance: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
        Text(time, color = Color.White, fontSize = 14.sp)
//        Image(
//            painter = painterResource(id = icon),
//            contentDescription = null,
//            modifier = Modifier.size(32.dp)
//        )
        GlideImage(
            imageModel = { "https://openweathermap.org/img/wn/${iconThumbnail}@2x.png" },
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp)),
            loading = {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            },
            failure = {
                Text(text = "Failed to load", color = Color.Red)
            }
        )
        Text(temp, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(chance, color = Color.DarkGray, fontSize = 12.sp)
    }
}

@Composable
fun DailyItem(day: String, high: String, low: String, icon: Int, chance: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(day, color = Color.White, fontSize = 16.sp)
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Text("$high / $low", color = Color.White, fontSize = 16.sp)
        Text(chance, color = Color.DarkGray, fontSize = 14.sp)
    }
}

@Composable
fun HumidityCard(humidity: Float,icon: ImageVector) {

    Card(
        modifier = Modifier
            .size(150.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black // الخلفية الأساسية
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            WaveAnimation(humidity)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp)
                    )
                    Text(text = "Humidity", color = Color.White, fontSize = 14.sp)
                }
                Text(
                    text = "${humidity.toInt()}%",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
fun WaveAnimation(humidity: Float) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(humidity) {
        animatedProgress.animateTo(
            targetValue = humidity / 100f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val waveHeight = size.height * (1 - animatedProgress.value)
        val path = Path().apply {
            moveTo(0f, waveHeight)
            for (i in 0..size.width.toInt() step 20) {
                lineTo(i.toFloat(), waveHeight - 10 * sin(i.toFloat() * 0.1f))
            }
            lineTo(size.width, waveHeight)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path, brush = Brush.verticalGradient(listOf(Color(0xFF6A1B9A), Color(0xFF8E24AA))))
    }
}

@Composable
fun CircularWithProgressCard(
    title: String,
    value: String,
    unit: String,
    progress: Float,
    progressColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val progress = animateFloatAsState(targetValue = progress / 100f, animationSpec = tween(1000))
    Card(
        modifier = modifier
            .size(150.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = progress.value * 360,
                    useCenter = false,
                    style = Stroke(width = 10f, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp)
                    )
                    Text(text = title, color = Color.White, fontSize = 14.sp)
                }
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = unit, color = Color.DarkGray, fontSize = 10.sp)
            }
        }
    }
}


@Composable
fun CircularCard(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        modifier = modifier
            .size(150.dp)
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                )
                Text(text = title, color = Color.White, fontSize = 14.sp)
            }
            Text(text = value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = unit, color = Color.DarkGray, fontSize = 10.sp)
        }
    }
}
