package eg.iti.mad.sharedandstateflow
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.KeyboardType
import eg.iti.mad.climaguard.home.HomeViewModel
import eg.iti.mad.climaguard.test.CircularDataUsage
import eg.iti.mad.climaguard.test.HumidityCard
import eg.iti.mad.climaguard.test.SpeedometerProgress

@Composable
fun WeatherScreen(viewModel: HomeViewModel) {

//    viewModel.getCurrentWeather()

    Box (modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column (modifier = Modifier.verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally){

            var value = remember { mutableStateOf(10f) }
            val progress = animateFloatAsState(targetValue = value.value / 100f, animationSpec = tween(1000))

            CircularProgressIndicator(
                progress = { progress.value },
                modifier = Modifier.size(180.dp),
                color = Color.Green,
                strokeWidth = 20.dp,
            )

            Text(
                text = "Home Screen",
                style = MaterialTheme.typography.headlineLarge
            )

            CircularDataUsage(value.value,100f)

            Button(onClick = {}) {
                Text(
                    text = "Home Screen",
                )
            }

            TextField( value = value.value.toString(),
                onValueChange = {
                    value.value = it.toFloat()
                }, keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            HumidityCard(value.value)

            SpeedometerProgress(value.value)

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                eg.iti.mad.climaguard.test.WeatherCard(
                    "Humidity",
                    "65%",
                    "",
                    value.value,
                    Color.Blue
                )
                eg.iti.mad.climaguard.test.WeatherCard(
                    "AQI",
                    "41",
                    "Good",
                    value.value,
                    Color.Green
                )
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                eg.iti.mad.climaguard.test.WeatherCard(
                    "Humidity",
                    "65%",
                    "",
                    value.value,
                    Color.Blue
                )
                HumidityCard(value.value)
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                eg.iti.mad.climaguard.test.WeatherCard(
                    "Humidity",
                    "65%",
                    "",
                    value.value,
                    Color.Blue
                )
                eg.iti.mad.climaguard.test.WeatherCard(
                    "AQI",
                    "41",
                    "Good",
                    value.value,
                    Color.Green
                )
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                eg.iti.mad.climaguard.test.WeatherCard(
                    "Humidity",
                    "65%",
                    "",
                    value.value,
                    Color.Blue
                )
                eg.iti.mad.climaguard.test.WeatherCard(
                    "AQI",
                    "41",
                    "Good",
                    value.value,
                    Color.Green
                )
            }

        }


    }

}