package com.example.myclockapp

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun SetScreen(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.StopWatch.route, //Clock.route,
        enterTransition = {
            fadeIn(animationSpec = tween(200))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200))
        }
        ) {
        composable(route = Screen.Clock.route) {
            ClockScreen()
        }

        composable(route = Screen.Timer.route) {
            TimerScreen(navController)
        }

        composable(route = Screen.StopWatch.route) {
            StopWatchScreen()
        }

        composable(
            route = Screen.SecondTimerScreen.route + "/{seconds}",
            arguments = listOf(
                navArgument("seconds") {
                    type = NavType.IntType
                    nullable = false
                }
            )
        ) {backStackEntry->
            backStackEntry.arguments?.getInt("seconds")
                ?.let { SecondTimerScreen(navController, it) }
        }
    }
}

