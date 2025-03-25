package eg.iti.mad.climaguard.favorite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eg.iti.mad.climaguard.R
import eg.iti.mad.climaguard.navigation.NavigationRoute

@Composable
fun FavoriteScreen(navController: NavController/*, viewModel: FavoriteViewModel*/) {
//    val favoritePlaces by viewModel.favoritePlaces.collectAsState(initial = emptyList())

    val favoritePlaces = listOf(
        PlaceEntity(1, "Giza Pyramids", 29.9792, 31.1342, R.drawable.header),
        PlaceEntity(2, "Eiffel Tower", 48.8584, 2.2945, R.drawable.favorite),
        PlaceEntity(3, "Statue of Liberty", 40.6892, -74.0445, R.drawable.cloudy)
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(NavigationRoute.Maps.route)
            }) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Open Map")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(favoritePlaces){ place ->
                FavoriteItem(place = place, onDelete = {})
            }

        }
    }
}

data class PlaceEntity(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val imageResId: Int
)

@Composable
fun FavoriteItem(place: PlaceEntity, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = place.imageResId),
            contentDescription = place.name,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = place.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}


