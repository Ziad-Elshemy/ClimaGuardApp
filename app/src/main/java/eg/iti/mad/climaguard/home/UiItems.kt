package eg.iti.mad.climaguard.home

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.glide.GlideImage
import eg.iti.mad.climaguard.R
import eg.iti.mad.climaguard.model.CurrentResponse
import eg.iti.mad.climaguard.model.ForecastResponse
import eg.iti.mad.climaguard.model.ListItem
import eg.iti.mad.climaguard.utils.Utility
import kotlin.math.sin


@Composable
fun WeatherScreenUi(
    responseData : CurrentResponse,
    responseForecast : ForecastResponse?,
    hourlyList :List<ListItem?>?,
    daysList :List<ListItem?>?
){
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF665B57), Color(0xFF284486)),
                    start = Offset(0f, 0f),
                    end = Offset(0f, 4000f)
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        // Current temperature
        Log.d("HomeScreen", "HomeScreen: ${responseData.main?.humidity}")

        Text("${responseData.name}", color = Color.White, fontSize = 20.sp)

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
                    text = "${responseForecast?.city?.country}, ${responseForecast?.city?.name}",
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

        Row (verticalAlignment = Alignment.CenterVertically){
            Text(
                stringResource(R.string.feels_like),
                color = Color.DarkGray,
                fontSize = 16.sp
            )
            Text(
                "${responseData?.main?.feelsLike ?: 0}°",
                color = Color.DarkGray,
                fontSize = 16.sp
            )
        }

        Row (verticalAlignment = Alignment.CenterVertically){
            Text(
                stringResource(R.string.high),
                color = Color.DarkGray,
                fontSize = 16.sp
            )
            Text(
                "${responseData?.main?.tempMax ?: 0}° ",
                color = Color.DarkGray,
                fontSize = 16.sp
            )
            Text(
                " • ",
                color = Color.DarkGray,
                fontSize = 16.sp
            )
            Text(
                stringResource(R.string.low),
                color = Color.DarkGray,
                fontSize = 16.sp
            )
            Text(
                " ${responseData?.main?.tempMin ?: 0}°",
                color = Color.DarkGray,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Next hour forecast
        ForecastCard(stringResource(R.string.hourly_forecast), Icons.Default.Timer) {
            LazyRow {
                items(hourlyList.orEmpty()) { hourlyItem ->
                    hourlyItem?.let {
                        val time = Utility.getTimeDay(hourlyItem.dt?.times(1000L) ?: 0)
                        HourlyItem(time, "${hourlyItem.main?.temp}°", hourlyItem.weather?.get(0)?.icon?:"10d" , "10%")
                    }

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
                stringResource(R.string.clouds),
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
                title = stringResource(R.string.pressure),
                unit = "hpa",
                icon = ImageVector.vectorResource(id = R.drawable.ic_pressure)
            )

            CircularCard(
                value = responseData.wind?.speed.toString(),
                title = stringResource(R.string.wind_speed),
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
                title = stringResource(R.string.visibility),
                unit = "KM",
                icon = ImageVector.vectorResource(id = R.drawable.ic_visibility)
            )
            val seaLevel : Int = responseData.main?.seaLevel?:0
            val grandLevel : Int = responseData.main?.grndLevel?:0
            val altitude = Utility.calculateAltitude(seaLevel.toDouble(),grandLevel.toDouble())
            CircularCard(
                value = altitude.toString(),
                title = stringResource(R.string.alt),
                unit = "m",
                icon = ImageVector.vectorResource(id = R.drawable.ic_altitude)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Next 5 days forecast
        ForecastCard(stringResource(R.string._5_day_forecast), Icons.Default.Today) {
            Column {
                daysList?.forEach{ daysItem ->
                    DailyItem("${daysItem?.dtTxt}",
                        "${daysItem?.main?.tempMax}","${daysItem?.main?.tempMin}"
                        ,daysItem?.weather?.get(0)?.icon?:"10d","10")
                }
//                        days.forEach { day ->
//                            DailyItem(day, "89°", "81°", R.drawable.header, "20%")
//                        }
            }
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
fun ForecastCard(title: String, icon : ImageVector, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2A2A2A))
            .padding(16.dp)
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
                    .size(28.dp)
                    .padding(end = 4.dp)
            )
            Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
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
                Text(text = stringResource(R.string.failed_to_load), color = Color.Red)
            }
        )
        Text(temp, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(chance, color = Color.DarkGray, fontSize = 12.sp)
    }
}

@Composable
fun DailyItem(day: String, high: String, low: String, iconThumbnail: String, chance: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(day, color = Color.White, fontSize = 16.sp)
        GlideImage(
            imageModel = { "https://openweathermap.org/img/wn/${iconThumbnail}@2x.png" },
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp)),
            loading = {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            },
            failure = {
                Text(text = stringResource(R.string.failed_to_load), color = Color.Red)
            }
        )
        Text("$high° / $low°", color = Color.White, fontSize = 16.sp)
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
            containerColor = Color.Black
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
                    Text(text = stringResource(R.string.humidity), color = Color.White, fontSize = 14.sp)
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
        drawPath(path, brush = Brush.verticalGradient(listOf(Color(0xFF502D52), Color(0xFFB488B6))))
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