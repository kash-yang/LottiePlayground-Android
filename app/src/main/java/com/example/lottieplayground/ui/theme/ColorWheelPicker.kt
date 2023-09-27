package com.example.lottieplayground.ui.theme

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.lottieplayground.data.HSV
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.min

@Composable
fun ColorWheelPicker(
    modifier: Modifier = Modifier,
    selectedColor: HSV,
    onColorChanged: (hsv: HSV) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp)
            .wrapContentSize()
            .aspectRatio(1f, matchHeightConstraintsFirst = true)

    ) {
        val diameterPx by remember(constraints.maxWidth) {
            mutableIntStateOf(constraints.maxWidth)
        }

        fun selectColor(newPosition: Offset, animate: Boolean) {
            newPosition.offsetToHSV(
                size = IntSize(diameterPx, diameterPx),
                valueInHSV = selectedColor.v,
                alpha = selectedColor.a

            )?.let {
                onColorChanged(it)
            }
        }

        val inputModifier = Modifier.pointerInput(diameterPx) {
            awaitEachGesture {
                val down = awaitFirstDown(false)
                selectColor(down.position, animate = true)
                drag(down.id) { change ->
                    selectColor(change.position, animate = false)
                    if (change.positionChange() != Offset.Zero) change.consume()
                }
            }
        }
        Box(inputModifier.fillMaxSize()) {
            ColorWheel(diameter = diameterPx, selectedColor = selectedColor)
        }
    }
}

/**
 * https://en.wikipedia.org/wiki/Polar_coordinate_system
 * https://medium.com/@bantic/hand-coding-a-color-wheel-with-canvas-78256c9d7d43
 */

private fun Offset.offsetToHSV(size: IntSize, valueInHSV: Float, alpha: Float): HSV? {
    val centerX = size.width / 2.0
    val centerY = size.height / 2.0
    val dx = x - centerX
    val dy = y - centerY
    val r = hypot(dx, dy)
    val phi = atan2(dy, dx)
    val degree = phi.toDegree() // from -180 to +180
    val radius = min(centerX, centerY)

    return if (r > radius) {
        // the position is not inside the circle
        null
    } else {
        HSV(
            h = ((degree + 360.0) % 360.0).toFloat(), // map range (-180 ~ +180) to (0 ~ 360)
            s = (r / radius).toFloat(), //ratio of saturation
            v = valueInHSV, // we don't do the brightness, so this field should always be 1
            a = alpha // just store alpha here, HSV
        )
    }
}

private fun Double.toDegree() = this * 180 / PI


