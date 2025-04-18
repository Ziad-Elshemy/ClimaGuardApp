package eg.iti.mad.climaguard.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import eg.iti.mad.climaguard.R

sealed class NavigationRoute(val route: String) {
    object Home : NavigationRoute("home_screen")
    object Alarm : NavigationRoute("alarm_screen")
    object Favorite : NavigationRoute("favorite_screen")
    object Setting : NavigationRoute("setting_screen")
//    object Maps : NavigationRoute("maps_screen")
//    object FavItem : NavigationRoute("fav_item_screen")

    object FavItem : NavigationRoute("fav_item_screen/{lat}/{lon}") {
        fun createRoute(lat: Double, lon: Double) = "fav_item_screen/$lat/$lon"
    }

    object Maps : NavigationRoute("maps_screen/{screenType}") {
        fun createRoute(screenType: String) = "maps_screen/$screenType"
    }

}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navigationItems = listOf(
        NavigationItem(stringResource(R.string.home), Icons.Default.Cloud, NavigationRoute.Home.route),
        NavigationItem(stringResource(R.string.alarm), Icons.Default.Alarm, NavigationRoute.Alarm.route),
        NavigationItem(stringResource(R.string.favorite), Icons.Default.Favorite, NavigationRoute.Favorite.route),
        NavigationItem(stringResource(R.string.setting), Icons.Default.Settings, NavigationRoute.Setting.route)
    )

    val currentDestination = navController.currentBackStackEntryAsState().value
    val selectedNavigationIndex = navigationItems.indexOfFirst {
        it.route == currentDestination?.destination?.route
    }.coerceAtLeast(0)

    val shouldShowBottomNav = (currentDestination?.destination?.route != NavigationRoute.Maps.route && currentDestination?.destination?.route != NavigationRoute.FavItem.route)

    if (shouldShowBottomNav){
        NavigationBar(containerColor = MaterialTheme.colorScheme.primary) {
            navigationItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedNavigationIndex == index,
                    onClick = {
                        if (currentDestination?.destination?.route != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                    label = {
                        if (index == selectedNavigationIndex) {Text(item.title, color = if (index == selectedNavigationIndex) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.secondary)} },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.surface,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        }
    }

}
