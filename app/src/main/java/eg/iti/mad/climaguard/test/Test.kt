package eg.iti.mad.climaguard.test

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

@Composable
fun CircularDataUsage(usage: Float, total: Float) {
    val progress = animateFloatAsState(targetValue = usage / total, animationSpec = tween(1000))

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(150.dp)
    ) {
        Canvas(modifier = Modifier.size(150.dp)) {
            val strokeWidth = 12.dp.toPx()
            val radius = size.minDimension / 2

            drawArc(
                color = Color.LightGray,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                color = Color.Blue,
                startAngle = -90f,
                sweepAngle = progress.value * 360,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Text(
            text = "${(progress.value * 100).toInt()}%",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WeatherCard(
    title: String,
    value: String,
    unit: String,
    progress: Float,
    progressColor: Color,
    modifier: Modifier = Modifier
) {
    val progress = animateFloatAsState(targetValue = progress / 100f, animationSpec = tween(1000))
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        modifier = modifier
            .size(120.dp)
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(4.dp).fillMaxSize()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = progress.value * 360,
                    useCenter = false,
                    style = Stroke(width = 10f, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = title, color = Color.White, fontSize = 12.sp)
                Text(text = value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = unit, color = Color.Gray, fontSize = 10.sp)
            }
        }
    }
}


@Composable
fun SpeedometerProgress(progress: Float) {
    val progress = animateFloatAsState(targetValue = progress / 100f, animationSpec = tween(1000))
    Canvas(modifier = Modifier.size(150.dp)) {
        val strokeWidth = 20f
        val startAngle = 140f
        val sweepAngle = 260f * progress.value

        // background circular
        drawArc(
            color = Color.Gray.copy(alpha = 0.3f),
            startAngle = startAngle,
            sweepAngle = 260f,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )

        // real progress
        drawArc(
            color = Color.Cyan,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )
    }
}


@Composable
fun HumidityCard(humidity: Float) {

    Card(
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black // الخلفية الأساسية
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            WaveAnimation(humidity)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Humidity",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "${humidity.toInt()}%",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}




@Composable
fun WaveAnimation(humidity: Float) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(humidity) {
        animatedProgress.animateTo(
            targetValue = humidity / 100f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val waveHeight = size.height * (1 - animatedProgress.value)
        val path = Path().apply {
            moveTo(0f, waveHeight)
            for (i in 0..size.width.toInt() step 20) {
                lineTo(i.toFloat(), waveHeight - 10 * sin(i.toFloat() * 0.1f))
            }
            lineTo(size.width, waveHeight)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path, brush = Brush.verticalGradient(listOf(Color(0xFF6A1B9A), Color(0xFF8E24AA))))
    }
}


