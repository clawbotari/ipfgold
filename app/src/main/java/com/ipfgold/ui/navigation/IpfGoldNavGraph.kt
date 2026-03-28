package com.ipfgold.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ipfgold.R
import com.ipfgold.ui.about.AboutScreen
import com.ipfgold.ui.home.HomeScreen
import com.ipfgold.ui.settings.SettingsScreen

/**
 * Elemento de la bottom navigation bar.
 */
data class BottomNavItem(
    val route: String,
    val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

/**
 * Lista de elementos de la barra de navegación.
 */
private val BottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Home.route,
        labelRes = R.string.nav_home,
        icon = Icons.Default.Home
    ),
    BottomNavItem(
        route = Screen.Settings.route,
        labelRes = R.string.nav_settings,
        icon = Icons.Default.Settings
    ),
    BottomNavItem(
        route = Screen.About.route,
        labelRes = R.string.nav_about,
        icon = Icons.Default.Info
    )
)

/**
 * Grafo de navegación principal con bottom bar.
 */
@Composable
fun IpfGoldNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = stringResource(item.labelRes)
                            )
                        },
                        label = { Text(stringResource(item.labelRes)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Home.route) {
                HomeScreen()
            }
            composable(route = Screen.Settings.route) {
                SettingsScreen()
            }
            composable(route = Screen.About.route) {
                AboutScreen()
            }
        }
    }
}