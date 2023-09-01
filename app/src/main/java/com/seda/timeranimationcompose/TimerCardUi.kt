package com.seda.timeranimationcompose

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun TimerCardUi(
    modifier: Modifier,
    timerFirstDuration:Int,
    onTimerDurationChange:(Int)->Unit,
    isTimerStarted:Boolean,//false
    isTimerStartedChange:(Boolean)->Unit,
    timerProgress:Float
) {
    val cardHorizontalPadding by animateDpAsState(
        targetValue = if (!isTimerStarted) 48.dp else 16.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )


    Card(
        elevation = CardDefaults.elevatedCardElevation(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = modifier
            .fillMaxWidth()

            .padding(horizontal = cardHorizontalPadding)

    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Crossfade(
                targetState = isTimerStarted,
                animationSpec = tween(durationMillis = if (isTimerStarted) 0 else 350),
                modifier = Modifier.animateContentSize { initialValue, targetValue ->  }, label = ""
            ) { isTimerStartedCurrentValue ->
                if (!isTimerStartedCurrentValue) {
                      TimePicker(modifier = modifier,
                          seconds =timerFirstDuration / 1000 , onSecondChange ={
                              onTimerDurationChange(it*1000)
                          } )

                }
                else{
                    TimerCountdown(timerProgress = timerProgress, timerDurationInMillis = timerFirstDuration)

                }

            }

            StartCancelTimerButton(
                isTimerStarted = isTimerStarted,
                enabled = timerFirstDuration > 0
            ) {
                isTimerStartedChange(!isTimerStarted)

            }
        }


    }

}


@Composable
private fun TimerCountdown(timerProgress: Float, timerDurationInMillis: Int) {
    val style = remember {
        TextStyle(fontSize = 60.sp, fontWeight = FontWeight.ExtraBold)
    }
    val text = remember(timerProgress, timerDurationInMillis) {
        Log.e("progreess","$timerProgress")
        val seconds = (timerProgress * (timerDurationInMillis / 1000)).roundToInt()
        String.format("%02d : %02d", seconds / 60, seconds % 60)
    }

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally),
        style = style,
        text = text
    )
}
@Composable
private fun StartCancelTimerButton(isTimerStarted: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        border = if (isTimerStarted) ButtonDefaults.outlinedButtonBorder else null,
        colors = if (!isTimerStarted) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary) else ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
        elevation = if (isTimerStarted) null else ButtonDefaults.elevatedButtonElevation()
    ) {
        Text(text = if (!isTimerStarted) "START" else "CANCEL", color = Color.White)
    }
}


@Composable
private fun TimePicker(
    modifier: Modifier,
    seconds:Int,
    onSecondChange:(Int)->Unit
){
    Column(modifier = modifier) {

        Counter(label = "Minutes",
            value = seconds / 60,
            onValueChange ={
            onSecondChange(it.coerceAtLeast(0) * 60 + seconds % 60)
        } )
        Spacer(Modifier.height(16.dp))


        Counter(label = "Seconds",
            value = seconds % 60, onValueChange ={
            onSecondChange(
                (seconds - seconds % 60) + it.coerceIn(0, 59))
        } )

    }


}
@Composable

private fun Counter(
    modifier: Modifier=Modifier,
    label:String,
    value:Int,
    onValueChange:(Int)->Unit
){

    Column(modifier = Modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.secondary
        )

        Row (verticalAlignment = Alignment.CenterVertically, modifier = modifier.padding(16.dp)){
            Text(
                text = String.format("%02d", value),
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onValueChange(value + 1) },
                Modifier.then(
                    Modifier
                        .size(25.dp)
                        .border(1.dp, MaterialTheme.colorScheme.onSecondary, shape = CircleShape))) {
                Icon(
                    imageVector = Icons.TwoTone.Add,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = "Increment $label"
                )
            }

            Spacer(Modifier.width(34.dp))

            IconButton(onClick = { onValueChange(value - 1) },
                Modifier.then(
                    Modifier
                        .size(25.dp)
                        .border(1.dp, MaterialTheme.colorScheme.onSecondary, shape = CircleShape))) {
                Icon(
                    painterResource(id = R.drawable.baseline_remove_24),
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = "Decrease $label"
                )
            }
        }


    }




}