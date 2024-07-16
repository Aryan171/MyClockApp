package com.example.myclockapp

sealed class Screen(
    val route: String
) {
    data object Clock: Screen("clock")
    data object Timer: Screen("timer")
    data object StopWatch: Screen("stopWatch")
}