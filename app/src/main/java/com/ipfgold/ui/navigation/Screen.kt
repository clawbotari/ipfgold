package com.ipfgold.ui.navigation

/**
 * Rutas de navegación de la aplicación.
 *
 * Sealed class que define las tres pantallas principales.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object About : Screen("about")
}