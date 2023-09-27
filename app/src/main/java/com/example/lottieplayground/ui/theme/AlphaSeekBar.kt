package com.example.lottieplayground.ui.theme

import android.content.res.Resources.getSystem
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import com.example.lottieplayground.data.HSV
import com.example.lottieplayground.data.toColor
import com.example.lottieplayground.data.toRGBA
import kotlin.math.ceil

@Composable
fun AlphaSeekBar(
    modifier: Modifier = Modifier,
    selectedColor: HSV,
    onAlphaChanged: (Float) -> Unit
) {

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
    ) {
        val brush = remember(selectedColor) {
            Brush.horizontalGradient(
                listOf(
                    Color(0x00ffffff),
                    selectedColor.copy(a = 1.0f).toRGBA().toColor()
                )
            )
        }
        val width by remember(constraints.maxWidth) {
            mutableIntStateOf(constraints.maxWidth)
        }

        var selectedOffset by remember {
            mutableFloatStateOf(constraints.maxWidth.toFloat())
        }

        fun selectAlpha(newPosition: Offset) {
            newPosition.offsetToAlpha(width.toFloat())?.let {
                selectedOffset = newPosition.x
                onAlphaChanged(it)
            }
        }

        Canvas(modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .pointerInput(width) {
                awaitEachGesture {
                    val down = awaitFirstDown(false)
                    selectAlpha(down.position)
                    drag(down.id) { change ->
                        selectAlpha(change.position)
                        if (change.positionChange() != Offset.Zero) change.consume()
                    }
                }
            }
        ) {
            clipRect {
                drawAlphaSeekBarTransparentBackground()
            }
            drawRect(brush = brush)
            drawAlphaSeekBarIndicator(offset = selectedOffset)
        }
    }
}

private fun Offset.offsetToAlpha(width: Float): Float? {
    return when (val ret = (x - 8f.dp2Px()) / (width - 16f.dp2Px())) {
        in 0f..1f -> ret
        else -> null
    }
}

private fun DrawScope.drawAlphaSeekBarTransparentBackground() {
    val darkColor = Color.LightGray
    val lightColor = Color.White

    val gridSizePx = 8.dp.toPx()
    val cellCountX = ceil(this.size.width / gridSizePx).toInt()
    val cellCountY = ceil(this.size.height / gridSizePx).toInt()
    for (i in 0 until cellCountX) {
        for (j in 0 until cellCountY) {
            val color = if ((i + j) % 2 == 0) darkColor else lightColor

            val x = i * gridSizePx
            val y = j * gridSizePx
            drawRect(color, Offset(x, y), Size(gridSizePx, gridSizePx))
        }
    }
}

private fun DrawScope.drawAlphaSeekBarIndicator(offset: Float) {
    val strokeWidth = 2.dp.toPx()
    val radius = 6.dp.toPx()
    val color = Color.DarkGray
    val upper = this.size.width - (strokeWidth + radius)
    val lower = (strokeWidth + radius)
    val center =
        Offset(
            x = when {
                offset in lower..upper -> offset
                offset > upper -> upper
                offset < lower -> lower
                else -> throw IllegalArgumentException("not able to calculate center")
            },
            y = this.size.height / 2
        )

    drawCircle(color = color, radius = radius, center = center, style = Stroke(width = strokeWidth))
}

fun Float.dp2Px() = (this * getSystem().displayMetrics.density).toFloat()