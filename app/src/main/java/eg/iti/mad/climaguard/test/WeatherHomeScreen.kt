package eg.iti.mad.sharedandstateflow

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eg.iti.mad.climaguard.R

@Composable
fun WeatherHomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3E8FF)) // Background color
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocationHeader("Giza Governorate, Egypt")
        Spacer(modifier = Modifier.height(8.dp))
        WeatherCard()
        Spacer(modifier = Modifier.height(16.dp))
        WeatherDetails()
        Spacer(modifier = Modifier.height(16.dp))
        ForecastTabs()
    }
}

@Composable
fun LocationHeader(location: String) {
    Text(
        text = location,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
}

@Composable
fun WeatherCard() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(32.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFFDA77F2), Color(0xFFF3E8FF))))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("clear sky", style = MaterialTheme.typography.bodyLarge, color = Color.White)
            Text("308.4°K", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Sun Icon",
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@Composable
fun WeatherDetails() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailItem(Icons.Default.Build, "8.5 m/s", "WIND")
            WeatherDetailItem(Icons.Default.Warning, "10%", "Humidity")
            WeatherDetailItem(Icons.Default.MoreVert, "10,000 km", "Visibility")
            WeatherDetailItem(Icons.Default.Call, "5%", "Clouds")
            WeatherDetailItem(Icons.Default.ThumbUp, "1013 hPa", "Pressure")
        }
    }
}

@Composable
fun WeatherDetailItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = Color(0xFF9C27B0))
        Text(value, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun ForecastTabs() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Today", "Next 4 Days")

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        LazyRow(modifier = Modifier.padding(16.dp)) {
            items(5) { index ->
                ForecastItem()
            }
        }
    }
}

@Composable
fun ForecastItem() {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(80.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
            Image(painter = painterResource(id = R.drawable.ic_launcher_background), contentDescription = "Weather Icon")
            Text("300.5°", fontWeight = FontWeight.Bold)
            Text("9:00 AM", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
