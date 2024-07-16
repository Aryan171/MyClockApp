package com.example.myclockapp

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SetScreen(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Clock.route
        ) {
        composable(route = Screen.Clock.route) {
            ClockScreen()
        }

        composable(route = Screen.Timer.route) {
            TimerScreen()
        }

        composable(route = Screen.StopWatch.route) {
            StopWatchScreen()
        }
    }
}

@Composable
fun ClockScreen() {
    Text("clock")
}

@Composable
fun TimerScreen() {
    Text("timer")
}

@Composable
fun StopWatchScreen() {
Text("stopwatch")
}