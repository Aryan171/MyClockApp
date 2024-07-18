package com.example.myclockapp

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun TimerScreen(navController: NavHostController) {
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
        ),
        label = "TimerPlayButtonSizeAnimation"
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

        TimeText(
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
                    val hours = num / 10000
                    val minutes = num / 100 - hours * 100
                    val seconds = num - minutes * 100 - hours * 10000

                    val totalSeconds = hours * 3600 + minutes * 60 + seconds
                    navController.navigate(Screen.SecondTimerScreen.route + "/$totalSeconds")
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

@Composable
fun TimeText(
    num: Int,
    bigTextFontSize: TextUnit,
    smallTextFontSize: TextUnit,
    startingZeroes: Boolean = true
) {
    val hours = num / 10000
    val minutes = num / 100 - hours * 100
    val seconds = num - minutes * 100 - hours * 10000

    val selectedColor = MaterialTheme.colorScheme.tertiary
    val unselectedColor = Color.Unspecified

    val hoursColor = if(hours != 0) selectedColor else unselectedColor
    val minutesColor = if(hours != 0 || minutes != 0) selectedColor else unselectedColor
    val secondsColor = if(hours != 0 || minutes != 0 || seconds != 0) selectedColor else unselectedColor

    Text(
        text = buildAnnotatedString {
            if(startingZeroes || hours != 0) {
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
            }

            if(startingZeroes || minutes != 0 || hours != 0) {
                withStyle(
                    style = SpanStyle(
                        color = minutesColor,
                        fontSize = bigTextFontSize
                    )
                ) {
                    append((if (minutes < 10 && startingZeroes) "0" else "") + minutes.toString())
                }

                withStyle(
                    style = SpanStyle(
                        color = minutesColor,
                        fontSize = smallTextFontSize
                    )
                ) {
                    append("m ")
                }
            }

            if(startingZeroes || num != 0) {
                withStyle(
                    style = SpanStyle(
                        color = secondsColor,
                        fontSize = bigTextFontSize
                    )
                ) {
                    append((if (seconds < 10 && startingZeroes) "0" else "") + seconds.toString())
                }

                withStyle(
                    style = SpanStyle(
                        color = secondsColor,
                        fontSize = smallTextFontSize
                    )
                ) {
                    append("s")
                }
            }
        }
    )
}
