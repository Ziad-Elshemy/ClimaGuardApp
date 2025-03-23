package eg.iti.mad.climaguard.navigation

import androidx.compose.material.icons.Icons
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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class NavigationRoute(val route: String) {
    object Home : NavigationRoute("home_screen")
    object Profile : NavigationRoute("profile_screen")
    object Maps : NavigationRoute("maps_screen")
    object Setting : NavigationRoute("setting_screen")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navigationItems = listOf(
        NavigationItem("Home", Icons.Default.Home, NavigationRoute.Home.route),
        NavigationItem("Profile", Icons.Default.Person, NavigationRoute.Profile.route),
        NavigationItem("Maps", Icons.Default.LocationOn, NavigationRoute.Maps.route),
        NavigationItem("Setting", Icons.Default.Settings, NavigationRoute.Setting.route)
    )

    val currentDestination = navController.currentBackStackEntryAsState().value
    val selectedNavigationIndex = navigationItems.indexOfFirst {
        it.route == currentDestination?.destination?.route
    }.coerceAtLeast(0)

    NavigationBar(containerColor = Color.White) {
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
                    if (index == selectedNavigationIndex) {Text(item.title, color = if (index == selectedNavigationIndex) Color.Black else Color.Gray)} },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
