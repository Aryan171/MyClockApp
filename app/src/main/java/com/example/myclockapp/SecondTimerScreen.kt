package com.example.myclockapp

import android.media.MediaPlayer
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun SecondTimerScreen(navController: NavHostController, initialSeconds: Int) {
    /**t1 stores the time when the timer was last played*/
    var t1 by remember{
        mutableLongStateOf(System.currentTimeMillis())
    }

    /**t2 stores the time for which the timer was played before last being paused*/
    var t2 by remember {
        mutableLongStateOf(0L)
    }

    /**seconds stores the actual number of seconds for which the timer runs
     * while initialSeconds is the number of seconds set by the user initially*/
    var seconds by remember {
        mutableFloatStateOf(initialSeconds.toFloat())
    }

    var paused by remember {
        mutableStateOf(false)
    }

    var secondsLeft by remember {
        mutableFloatStateOf(seconds)
    }

    var alarmPaused by remember {
        mutableStateOf(true)
    }

    val currentContext = LocalContext.current

    val mediaPlayer = remember {
        MediaPlayer.create(currentContext, R.raw.alarm_sound)
    }

    val delay = 100

    val animatedFractionFilled = animateFloatAsState(
        targetValue = secondsLeft / seconds,
        animationSpec = tween(
            durationMillis = delay,
            easing = LinearEasing
        ), label = "animatedFractionFilled"
    )

    LaunchedEffect(alarmPaused) {
        try {
            if (alarmPaused && mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
            if (!alarmPaused && !mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }
        } catch(_: Exception) {}
    }

    LaunchedEffect(true) {
        while(true) {
            secondsLeft = seconds - if (paused) t2.toFloat() / 1000f
            else ((System.currentTimeMillis() - t1 + t2) / 1000f)
            delay(delay.toLong())
        }
    }

    if(secondsLeft <= 0 && !paused) {
        alarmPaused = false
    }

    if(secondsLeft > 0 || paused) {
        alarmPaused = true
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = secondsToString(initialSeconds) + " timer",
                    fontSize = 32.sp
                )

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .clickable {
                            mediaPlayer.release()
                            navController.navigate(Screen.Timer.route)
                        }
                )
            }

            CircularTimerProgressBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                seconds = secondsLeft.roundToInt(),
                fractionFilled = animatedFractionFilled.value,
                strokeWidthFraction = 0.05f,
                color = Color.Red,
                onClick = {
                    t2 = 0L
                    paused = true
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilledTonalButton(
                    onClick = {
                        seconds = secondsLeft + 60f
                        t1 = System.currentTimeMillis()
                        t2 = 0L
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+1 min",
                            fontSize = 24.sp
                        )
                    }
                }

                FilledTonalButton(
                    onClick = {
                        paused = !paused
                        if(secondsLeft < 0) {
                            alarmPaused = true
                            paused = true
                            seconds = initialSeconds.toFloat()
                            secondsLeft = initialSeconds.toFloat()
                            t1 = System.currentTimeMillis()
                            t2 = 0L
                        }
                        else if(paused) {
                            t2 += System.currentTimeMillis() - t1
                        }
                        else {
                            t1 = System.currentTimeMillis()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if(secondsLeft < 0) Icons.Default.Menu
                            else if(paused) Icons.Default.PlayArrow
                            else Icons.Default.Close,//change this
                            contentDescription = "Play / Pause / Reset"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CircularTimerProgressBar(
    modifier: Modifier = Modifier,
    seconds: Int,
    fractionFilled: Float,
    strokeWidthFraction: Float,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = secondsToString(seconds),
                    fontSize = 48.sp
                )
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                if(fractionFilled < 1f) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        modifier = Modifier
                            .clickable {
                                onClick()
                            }
                            .scale(1.5f),
                        tint = color,
                    )
                }
            }
        }

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokeWidth = this.size.width * strokeWidthFraction
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * fractionFilled,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                ),
                size = Size(this.size.width - strokeWidth,
                    this.size.width - strokeWidth),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            )
        }
    }
}

fun secondsToString(seconds: Int) : String {
    val hour = seconds / 3600
    val minute = seconds / 60 - hour * 60
    val second = seconds - minute * 60 - hour * 3600

    return (if (hour > 0) "$hour" + "h " else "") +
            (if (minute > 0 || hour > 0) "$minute" + "m " else "") + "$second" + "s"
}