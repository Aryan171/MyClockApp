package com.example.myclockapp

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Date
import java.util.Locale

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
