package eg.iti.mad.climaguard.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import eg.iti.mad.climaguard.home.HomeViewModel
import eg.iti.mad.sharedandstateflow.WeatherHomeScreen
import eg.iti.mad.sharedandstateflow.WeatherScreen

@Composable
    fun ProfileScreen(viewModel: HomeViewModel){
        Box (modifier = Modifier
            .fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
//            Text(
//                text = "Profile Screen",
//                style = MaterialTheme.typography.headlineLarge
//            )
            WeatherScreen(viewModel)
        }
    }
