package com.example.myclockapp

import android.icu.text.SimpleDateFormat
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.delay
import java.util.Date
import java.util.Locale

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
    var currentTimeMillis by remember {
        mutableLongStateOf(System.currentTimeMillis())
    }

    var expanded by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(key1 = currentTimeMillis) {
        delay(if(expanded == 0) 1000L else if(expanded == 1) 100L else 20L)
        currentTimeMillis = System.currentTimeMillis()
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat(
        "hh:mm" + if(expanded == 1) ":ss a" else if(expanded == 2) ":ss:SSS a" else "a",
        Locale.getDefault()
    )

    val currentTimeFormatted = timeFormat.format(Date(currentTimeMillis))
    val currentDateFormatted = dateFormat.format(Date(currentTimeMillis))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                if (expanded == 2) {
                    expanded = 0
                } else {
                    expanded++
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Clock",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
                .padding(16.dp),
            fontSize = 23.sp
        )

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = currentTimeFormatted,
            fontSize = 60.sp
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = currentDateFormatted,
            fontSize = 40.sp
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun TimerScreen() {
    var num by remember {
        mutableIntStateOf(0)
    }

    var playButtonSize by remember {
        mutableStateOf(0.dp)
    }

    val animatedPlayButtonSize = animateDpAsState(
        targetValue = playButtonSize,
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutLinearInEasing
        )
    )

    if(num == 0 && playButtonSize != 0.dp) {
        playButtonSize = 0.dp
    }

    if(num != 0 && playButtonSize == 0.dp) {
        playButtonSize = 106.dp
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Timer",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
                .padding(16.dp),
            fontSize = 23.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        TimerTimeText(
            num = num,
            92.sp,
            20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        TimerKeyboard(
            modifier = Modifier
                .size(110.dp)
                .padding(2.dp),
            fontSize = 30.sp,
            addDigitToEnd = {digit->
                num = (num * 10) + digit
                if(num > 999999) {
                    num -= (num / 1000000) * 1000000
                }
            },
            removeDigitFromEnd = {
                num /= 10
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier.size(110.dp),
            contentAlignment = Alignment.Center
        ) {
            FilledTonalButton(
                onClick = {
                    print("he") // change this
                },
                shape = CircleShape,
                colors = ButtonDefaults.filledTonalButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                modifier = Modifier
                    .size(animatedPlayButtonSize.value)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start Timer",
                    tint = Color.Gray,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
fun StopWatchScreen() {
Text("stopwatch")
}

@Composable
fun TimerTimeText(
    num: Int,
    bigTextFontSize: TextUnit,
    smallTextFontSize: TextUnit
) {
    val hours = num / 10000
    val minutes = num / 100 - hours * 100
    val seconds = num - minutes * 100 - hours * 10000

    val selectedColor = MaterialTheme.colorScheme.tertiary
    val unselectedColor = Color.Unspecified

    val hoursColor = if(hours != 0) selectedColor else unselectedColor
    val minutesColor = if(hours != 0 || minutes != 0) selectedColor else unselectedColor

    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = hoursColor,
                    fontSize = bigTextFontSize
                )
            ) {
                append(hours.toString())
            }

            withStyle(
                style = SpanStyle(
                    color = hoursColor,
                    fontSize = smallTextFontSize
                )
            ) {
                append("h ")
            }

            withStyle(
                style = SpanStyle(
                    color = minutesColor,
                    fontSize = bigTextFontSize
                )
            ) {
                append((if(minutes < 10) "0" else "") + minutes.toString())
            }

            withStyle(
                style = SpanStyle(
                    color = minutesColor,
                    fontSize = smallTextFontSize
                )
            ) {
                append("m ")
            }

            withStyle(
                style = SpanStyle(
                    color = if(num != 0) selectedColor
                    else unselectedColor,
                    fontSize = bigTextFontSize
                )
            ) {
                append((if(seconds < 10) "0" else "") + seconds.toString())
            }

            withStyle(
                style = SpanStyle(
                    color = if(num != 0) selectedColor else unselectedColor,
                    fontSize = smallTextFontSize
                )
            ) {
                append("s")
            }
        }
    )
}

@Composable
fun TimerKeyboard(
    modifier: Modifier = Modifier,
    fontSize: TextUnit,
    addDigitToEnd: (digit: Int) -> Unit,
    removeDigitFromEnd: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for(i in (0..2)) {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                for(j in (1..3)) {
                    TimerKeyboardButton(
                        modifier = modifier,
                        buttonId = (i * 3) + j,
                        onClick = {buttonId ->
                            addDigitToEnd(buttonId)
                        }
                    ) {
                        Text(
                            text = ((i * 3) + j).toString(),
                            fontSize = fontSize
                        )
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            TimerKeyboardButton(
                modifier = modifier,
                onClick = {
                    addDigitToEnd(0)
                    addDigitToEnd(0)
                }
            ) {
                Text(
                    text = "00",
                    fontSize = fontSize
                )
            }

            TimerKeyboardButton(
                modifier = modifier,
                onClick = {
                     addDigitToEnd(0)
                }
            ) {
                Text(
                    text = "0",
                    fontSize = fontSize
                )
            }

            TimerKeyboardButton(
                modifier = modifier,
                onClick = {
                    removeDigitFromEnd()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Backspace",
                    modifier = Modifier.size(
                        with(LocalDensity.current) {
                            fontSize.toDp()
                        }
                    ),
                    tint =  Color.Gray
                )
            }
        }
    }
}

@Composable
fun TimerKeyboardButton(
    modifier: Modifier = Modifier,
    buttonId: Int = 0,
    onClick: (buttonId: Int) -> Unit,
    label: @Composable () -> Unit
) {
    FilledTonalButton(
        onClick = {
            onClick(buttonId)
        },
        shape = CircleShape,
        colors = ButtonDefaults.filledTonalButtonColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier
    ) {
        label()
    }
}