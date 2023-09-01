package com.seda.timeranimationcompose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seda.timeranimationcompose.ui.WavesLoadingIndicator

@Composable
fun TimerAnimation() {
    Column(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.weight(1f, fill = true)) {
            var timerDurationInMillis by rememberSaveable{ mutableStateOf(0) }
            var isStarted by rememberSaveable {
                mutableStateOf(TimerState.Stopped)
            }
            val timerProgress by timerProgressAsState(
                timerState = isStarted ,
                timerDurationInMillis = timerDurationInMillis
            )
            LaunchedEffect(timerProgress == 0f) {
                if (timerProgress == 0f) {
                    isStarted = TimerState.Stopped
                }
            }
            WavesLoadingIndicator(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.secondary,
                progress = timerProgress
            )

            TimerCardUi(
                modifier = Modifier
                    .padding(bottom = 15.dp)
                    .align(Alignment.Center),
                timerFirstDuration = timerDurationInMillis,
                onTimerDurationChange ={
                    timerDurationInMillis = it
                } ,
                isTimerStarted = isStarted != TimerState.Stopped,
                isTimerStartedChange = {isTimerStarted->
                    isStarted =     if (isTimerStarted) TimerState.Started else TimerState.Stopped
                },
                timerProgress = timerProgress
            )
        }





    }


}

private enum class TimerState {
    Started,
    Stopped
}
@Composable
private fun timerProgressAsState(
    timerState: TimerState,
    timerDurationInMillis: Int
): State<Float> {
    val animatable = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(timerState) {
        val animateToStartOrStopState = timerState == TimerState.Stopped || (timerState == TimerState.Started && animatable.value == 0f)

        if (animateToStartOrStopState) {
            animatable.animateTo(
                targetValue = if (timerState == TimerState.Started) 1f else 0f,
                animationSpec = spring(stiffness = 100f)
            )
        }

        if (timerState == TimerState.Started) {
            animatable.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = timerDurationInMillis,
                    easing = LinearEasing
                ),
            )
        }
    }

    return remember(animatable) {
        derivedStateOf { animatable.value }
    }
}