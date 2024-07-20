package com.example.myclockapp

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun StopWatchScreen() {
    var laps by remember {
        mutableStateOf(listOf<Long>())
    }
    var t1 by remember {
        mutableLongStateOf(0L)
    }
    var t2 by remember {
        mutableLongStateOf(0L)
    }
    var tSpent by remember {
        mutableLongStateOf(0L)
    }
    var paused by remember {
        mutableStateOf(true)
    }
    val animatedFractionFilled = remember {
        Animatable(0f)
    }
    var fractionMarked by remember {
        mutableFloatStateOf(-1f)
    }

    val scope = rememberCoroutineScope()

    fun start() {
        paused = false
        t1 = System.currentTimeMillis()
    }

    fun pause() {
        paused = true
        t2 += System.currentTimeMillis() - t1
    }

    fun reset() {
        paused = true
        t2 = 0L
        tSpent = 0L
        laps = mutableListOf<Long>()
        fractionMarked = -1f
        scope.launch {
            animatedFractionFilled.snapTo(0f)
        }
    }

    fun getMilliSecondsSpent(): Long {
        return if(paused) t2 else System.currentTimeMillis() - t1 + t2
    }

    fun addLap() {
        laps = MutableList<Long>(laps.size + 1) {
            if(it < laps.size) {
                laps[it]
            }
            else {
                tSpent
            }
        }
        scope.launch {
            animatedFractionFilled.snapTo(0f)
        }
        if(laps.size > 1) {
            fractionMarked = (laps[laps.size - 1] - laps[laps.size - 2]).toFloat() / laps[0]
        }
    }

    LaunchedEffect(key1 = true, key2 = paused) {
        while(!paused) {
            tSpent = getMilliSecondsSpent()
            if(laps.isNotEmpty()) {
                scope.launch {
                    animatedFractionFilled.animateTo(
                        targetValue = (tSpent - laps[laps.size - 1]).toFloat() / laps[0],
                        animationSpec = tween(
                            durationMillis = 100,
                            easing = LinearEasing
                        )
                    )
                }
            }
            else {
                animatedFractionFilled.snapTo(0f)
            }
            delay(100)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Text(
                text = "Stopwatch",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                fontSize = 23.sp
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = {
                        reset()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = Color.White
                    )
                }

                FilledTonalButton(
                    onClick = {
                        if (paused) {
                            start()
                        } else {
                            pause()
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (paused) Icons.Default.PlayArrow else Icons.Default.Menu,
                        contentDescription = "Play / pause",
                        tint = Color.White
                    )
                }

                FilledTonalButton(
                    onClick = {
                        addLap()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "lap",
                        tint = Color.White
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(vertical = 0.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (laps.isEmpty()) Arrangement.Center else Arrangement.Top
        ) {
            StopWatchClock(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                milliSecondsSpent = tSpent,
                fractionFilled = animatedFractionFilled.value,
                fractionMarked = fractionMarked,
                strokeWidthFraction = 0.05f
            )

            ListOfLaps(laps)
        }
    }
}

@Composable
fun ListOfLaps(laps: List<Long>){
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        items(laps.size) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text("#${laps.size - it}")

                Text(milliSecondsToString(
                    if(it > laps.size - 2) {
                        laps[laps.size - 1 - it]
                    }
                    else {
                        laps[laps.size - 1 - it] - laps[laps.size - 2 - it]
                         },
                    false))

                Text(milliSecondsToString(laps[laps.size - 1 - it], false))
            }
        }
    }
}

@Composable
fun StopWatchClock(
    modifier: Modifier,
    milliSecondsSpent: Long,
    circularProgressBarColor: Color = Color.Red,
    markerColor: Color = Color.White,
    fractionFilled: Float,
    fractionMarked: Float,
    strokeWidthFraction: Float
    ) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = milliSecondsToString(milliSecondsSpent),
            fontSize = 25.sp
        )

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokeWidth = this.size.width * strokeWidthFraction
            if(fractionFilled != 0f) {
                drawArc(
                    color = circularProgressBarColor,
                    startAngle = -90f,
                    sweepAngle = 360 * fractionFilled,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    ),
                    size = Size(
                        this.size.width - strokeWidth,
                        this.size.width - strokeWidth
                    ),
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                )
            }

            if(fractionMarked > 0f) {
                drawArc(
                    color = markerColor,
                    startAngle = 360 * fractionMarked - 90,
                    sweepAngle = 1f,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth
                    ),
                    size = Size(
                        this.size.width - strokeWidth,
                        this.size.width - strokeWidth
                    ),
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                )
            }
        }
    }
}

fun milliSecondsToString(milliSeconds: Long, doNotShowMilliSeconds: Boolean = true) : String {
    val hour = milliSeconds / 3600000
    val minute = milliSeconds / 60000 - hour * 60
    val second = milliSeconds / 1000 - minute * 60 - hour * 3600
    val milliSec = milliSeconds - second * 1000 - minute * 60000 - hour * 3600000


    return (if (hour > 0) "$hour" + "h " else "") +
            (if (minute > 0 || hour > 0) "$minute" + "m " else "") +
            (if(doNotShowMilliSeconds || second > 0 || minute > 0 || hour > 0) "$second" + "s "
            else "") +
            (if(!doNotShowMilliSeconds) "$milliSec" + "ms" else "")
}

