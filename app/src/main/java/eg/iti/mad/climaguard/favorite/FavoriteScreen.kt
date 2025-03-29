package eg.iti.mad.climaguard.favorite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import eg.iti.mad.climaguard.R
import eg.iti.mad.climaguard.home.LoadingIndicator
import eg.iti.mad.climaguard.model.LocationEntity
import eg.iti.mad.climaguard.model.Response
import eg.iti.mad.climaguard.navigation.NavigationRoute

@Composable
fun FavoriteScreen(navController: NavController, viewModel: FavoriteViewModel) {
//    val favoritePlaces by viewModel.favoritePlaces.collectAsState(initial = emptyList())

    viewModel.getProducts()

    val uiState by viewModel.locationsList.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(

        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(NavigationRoute.Maps.route)
            }) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Open Map")
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->


        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            when(uiState){
                is Response.Loading ->{
                    LoadingIndicator()
                }

                is Response.Success -> {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        items((uiState as Response.Success).data){ location ->
                            FavoriteItem(place = location,navController,
                                onDeleteFromFavClicked = {
                                    viewModel.deleteLocationFromFav(location)
                                })
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

            LaunchedEffect(Unit) {
                viewModel.message.collect{message->
                    if (!message.isNullOrBlank()){
                        snackBarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun FavoriteItem(place: LocationEntity,navController: NavController,
                 onDeleteFromFavClicked: ()->Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFDFFCE5) // Light blue background
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(NavigationRoute.FavItem.createRoute(place.lat, place.lon)) }
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Location Icon (Placeholder for Image)
            Image(
                painter = painterResource(R.drawable.ic_favourite_location),
                contentDescription = place.name,
                modifier = Modifier
                    .height(80.dp)
                    .width(80.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Column(
                modifier = Modifier
                    .weight(1f) // Takes remaining space
                    .padding(start = 10.dp)
            ) {
                // Country Name
                Text(
                    text = place.country,
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Location Name
                Text(
                    text = place.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

            }

            // Delete Icon Button
            IconButton(
                onClick = onDeleteFromFavClicked,
                modifier = Modifier
                    .size(40.dp) // Adjust size if needed
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    modifier = Modifier.size(32.dp),
                    contentDescription = "Delete",
                    tint = Color(0xFFE91E63) // Pinkish color like the button before
                )
            }
        }
    }
}






