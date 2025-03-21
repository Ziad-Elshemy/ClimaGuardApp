package eg.iti.mad.climaguard.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.newsapp.api.ApiManager
import kotlinx.coroutines.GlobalScope

@Composable
    fun HomeScreen(viewModel: HomeViewModel){
        viewModel.getCurrentWeather()
        Box (modifier = Modifier
            .fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Column {
                Text(
                    text = "Home Screen",
                    style = MaterialTheme.typography.headlineLarge
                )
                Button(onClick = {}) {
                    Text(
                        text = "Home Screen",
                    )
                }
            }


        }
    }
