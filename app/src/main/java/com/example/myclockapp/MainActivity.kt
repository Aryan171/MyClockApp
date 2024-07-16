package com.example.myclockapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.myclockapp.ui.theme.MyClockAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyClockAppTheme {
                val bottomBarItems = listOf(
                    BottomBarItem(
                        title = "Timer",
                        icon = Icons.Default.KeyboardArrowDown,
                        route = Screen.Timer.route
                    ),
                    BottomBarItem(
                        title = "Clock",
                        icon = Icons.Default.CheckCircle,
                        route = Screen.Clock.route
                    ),
                    BottomBarItem(
                        title = "Stop Watch",
                        icon = Icons.Default.Home,
                        route = Screen.StopWatch.route
                    )
                )

                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        BottomBar(
                            navController,
                            bottomBarItems
                        )
                    }
                ) {
                    Surface(modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                    ) {
                        SetScreen(navController)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar(
    navController: NavHostController,
    bottomBarItems: List<BottomBarItem>
) {
    var selectedIndex by remember {
        mutableIntStateOf(1)
    }

    NavigationBar(
        modifier = Modifier.fillMaxWidth()
    ) {
        for(i in bottomBarItems.indices) {
            NavigationBarItem(
                selected = i == selectedIndex,
                onClick = {
                    selectedIndex = i
                    navController.navigate(bottomBarItems[i].route)
                },
                icon = {
                    Icon(
                        imageVector = bottomBarItems[i].icon,
                        contentDescription =  bottomBarItems[i].title,
                        tint = if(selectedIndex == i) Color.White else Color.Gray
                    )
                },
                label = {
                    Text(bottomBarItems[i].title)
                }
            )
        }
    }
}