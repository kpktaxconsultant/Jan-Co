package com.example.ui.navigation

sealed class ScreenRoute(val route: String) {
    object Home : ScreenRoute("home")
    object Calculator : ScreenRoute("calculator")
    object Result : ScreenRoute("result")
    object History : ScreenRoute("history")
    object TaxGuide : ScreenRoute("tax_guide")
    object Admin : ScreenRoute("admin")
    object About : ScreenRoute("about")
    object Settings : ScreenRoute("settings")
}
