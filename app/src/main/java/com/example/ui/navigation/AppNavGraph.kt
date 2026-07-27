package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ui.screens.*
import com.example.ui.viewmodels.MainViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoute.Home.route,
        modifier = modifier
    ) {
        composable(ScreenRoute.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateTo = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(ScreenRoute.Calculator.route) {
            CalculatorScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToResult = {
                    navController.navigate(ScreenRoute.Result.route)
                }
            )
        }

        composable(ScreenRoute.Result.route) {
            ResultScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(ScreenRoute.History.route) {
            HistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onReopenResult = {
                    navController.navigate(ScreenRoute.Result.route)
                }
            )
        }

        composable(ScreenRoute.TaxGuide.route) {
            TaxGuideScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(ScreenRoute.Admin.route) {
            AdminScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(ScreenRoute.About.route) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(ScreenRoute.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
