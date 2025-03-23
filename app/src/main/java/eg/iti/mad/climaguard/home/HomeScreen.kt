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
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import eg.iti.mad.climaguard.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sin

@Composable
    fun HomeScreen(viewModel: HomeViewModel){
        viewModel.getCurrentWeather()
    val currentWeatherState = viewModel.currentResponse.observeAsState()
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(Color(0xFF507D83))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Current temperature
        Log.d("HomeScreen", "HomeScreen: ${currentWeatherState.value?.main?.humidity}")

        Text("${currentWeatherState.value?.name}", color = Color.White, fontSize = 20.sp)

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
                Text(text = "${currentWeatherState.value?.weather?.get(0)?.description}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Text(text = "${currentWeatherState.value?.name}", fontSize = 14.sp, color = Color.DarkGray)
            }

            Image(
                painter = painterResource(id = R.drawable.header),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )

            Column(
                horizontalAlignment = Alignment.End
            ) {
                val dateFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
                val formattedDate = dateFormat.format(Date(
                    currentWeatherState.value?.dt?.times(1000L) ?: 0
                ))
                Text(text = "Date", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Text(text = "${formattedDate}", fontSize = 14.sp, color = Color.DarkGray)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${currentWeatherState.value?.main?.temp?:0}", color = Color.White, fontSize = 60.sp, fontWeight = FontWeight.Bold)
            Text(text = "°C", color = Color.White, fontSize = 20.sp, modifier = Modifier.size(60.dp)
                .padding(bottom = 20.dp))
        }
        Text("Feels like ${currentWeatherState.value?.main?.feelsLike?:0}°", color = Color.DarkGray, fontSize = 16.sp)
        Text("High ${currentWeatherState.value?.main?.tempMax?:0}° • Low ${currentWeatherState.value?.main?.tempMin?:0}°", color = Color.DarkGray, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Next hour forecast
        ForecastCard("Hourly forecast") {
            val hours = listOf("Now", "9 PM", "10 PM", "11 PM", "12 AM", "1 AM", "2 AM")
            LazyRow {
                items(hours) { hour ->
                    HourlyItem(hour, "88°", R.drawable.snow, "10%")
                }
            }
        }

        //row1
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CircularWithProgressCard(
                "Clouds",
                "${currentWeatherState.value?.clouds?.all}%",
                "",
                currentWeatherState.value?.clouds?.all?.toFloat()?:0f,
                Color.White
            )
            HumidityCard(currentWeatherState.value?.main?.humidity?.toFloat()?:0f)
        }

        Spacer(modifier = Modifier.height(16.dp))

        //row2
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CircularWithProgressCard(
                "Humidity",
                "${currentWeatherState.value?.main?.humidity}%",
                "",
                currentWeatherState.value?.main?.humidity?.toFloat()?:0f,
                Color.Blue
            )
            CircularCard(
                value = currentWeatherState.value?.main?.pressure.toString(),
                title = "Pressure",
                unit = "hpa",
                icon = Icons.Default.PlayArrow
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
fun HourlyItem(time: String, temp: String, icon: Int, chance: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
        Text(time, color = Color.White, fontSize = 14.sp)
        Image(painter = painterResource(id = icon), contentDescription = null, modifier = Modifier.size(32.dp))
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
        Image(painter = painterResource(id = icon), contentDescription = null, modifier = Modifier.size(32.dp))
        Text("$high / $low", color = Color.White, fontSize = 16.sp)
        Text(chance, color = Color.DarkGray, fontSize = 14.sp)
    }
    }

@Composable
fun HumidityCard(humidity: Float) {

    Card(
        modifier = Modifier
            .size(150.dp)
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
                Text(
                    text = "Humidity",
                    color = Color.White,
                    fontSize = 14.sp
                )
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
    modifier: Modifier = Modifier
) {
    val progress = animateFloatAsState(targetValue = progress / 100f, animationSpec = tween(1000))
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        modifier = modifier
            .size(120.dp)
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(4.dp).fillMaxSize()
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
                Text(text = title, color = Color.White, fontSize = 12.sp)
                Text(text = value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
        shape = CircleShape, // جعل الكارد دائريًا
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        modifier = modifier
            .size(120.dp)
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
                    modifier = Modifier.size(16.dp).padding(end = 4.dp)
                )
                Text(text = title, color = Color.White, fontSize = 12.sp)
            }
            Text(text = value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = unit, color = Color.DarkGray, fontSize = 10.sp)
        }
    }
}
