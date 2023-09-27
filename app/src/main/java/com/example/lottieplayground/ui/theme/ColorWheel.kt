package com.example.lottieplayground.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.lottieplayground.data.HSV
import com.example.lottieplayground.data.toColor
import com.example.lottieplayground.data.toRGBA
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ColorWheel(diameter: Int, selectedColor: HSV) {
    val s = 1.0f
    val v = selectedColor.v
    val radius = diameter / 2f
    val alpha = 1.0f

    val colorSweepGradientBrush = remember(selectedColor.v, diameter) {
        val wheelColors = arrayOf(
            HSV(0f, s, v, alpha),
            HSV(60f, s, v, alpha),
            HSV(120f, s, v, alpha),
            HSV(180f, s, v, alpha),
            HSV(240f, s, v, alpha),
            HSV(300f, s, v, alpha),
            HSV(360f, s, v, alpha)
        ).map {
            it.toRGBA().toColor()
        }
        Brush.sweepGradient(wheelColors, Offset(radius, radius))
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(colorSweepGradientBrush)
        drawSelectionIndicator(selectedColor = selectedColor, width = diameter)
    }
}

private fun DrawScope.drawSelectionIndicator(selectedColor: HSV, width: Int) {
    val strokeWidth = 2.dp.toPx()
    val r = 8.dp.toPx()
    drawCircle(
        color = Color.White,
        radius = r,
        center = positionForColor(selectedColor, IntSize(width, width)),
        style = Stroke(width = strokeWidth)
    )
}

private fun positionForColor(color: HSV, size: IntSize): Offset {
    val radians = color.h.toRadian()
    val phi = color.s
    val x: Float = ((phi * cos(radians)) + 1) / 2f
    val y: Float = ((phi * sin(radians)) + 1) / 2f
    return Offset(
        x = (x * size.width),
        y = (y * size.height)
    )
}

private fun Float.toRadian(): Float = this / 180.0f * PI.toFloat()