package com.example.lottieplayground.data

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color
import java.lang.Float.min
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt


/**
 * implement form https://en.wikipedia.org/wiki/HSL_and_HSV
 */
data class HSV(
    @FloatRange(from = 0.0, to = 360.0)
    val h: Float,
    @FloatRange(from = 0.0, to = 1.0)
    val s: Float,
    @FloatRange(from = 0.0, to = 1.0)
    val v: Float,
    @FloatRange(from = 0.0, to = 1.0)
    val a: Float
) {
    companion object {
        fun from(color: Color): HSV {
            //the color we use here is form compose, which is from 0 ~ 1, instead of 0 ~ 255
            val r = color.red
            val g = color.green
            val b = color.blue
            val a = color.alpha

            val maxC = max(max(r, g), b)
            val minC = min(min(r, g), b)

            val delta = maxC - minC

            val h = when {
                delta == 0f -> 0f
                maxC == r -> 60 * (((g - b) / delta) % 6)
                maxC == g -> 60 * (((b - r) / delta) + 2)
                maxC == b -> 60 * (((r - g) / delta) + 4)
                else -> throw IllegalArgumentException("not able to calculate h")
            }

            val s = when (maxC) {
                0f -> 0f
                else -> delta / maxC
            }

            val v = maxC
            return HSV(h, s, v, a)
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun HSV.toRGBA(): RGBA {

    val c = v * s
    val hi = h / 60
    val x = c * (1 - abs(hi.mod(2.0f) - 1))
    val m = v - c

    val tempRGB = when (hi) {
        in 0.0 ..< 1.0 -> RGBA(c + m, x + m, 0f + m, a)
        in 1.0 ..< 2.0 -> RGBA(x + m, c + m, 0f + m, a)
        in 2.0 ..< 3.0 -> RGBA(0f + m, c + m, x + m, a)
        in 3.0 ..< 4.0 -> RGBA(0f + m, x + m, c + m, a)
        in 4.0 ..< 5.0 -> RGBA(x + m, 0f + m, c + m, a)
        in 5.0..6.0 -> RGBA(c + m, 0f + m, x + m, a)
        else -> RGBA(v, v, v, a)
    }
    return RGBA(
        r = tempRGB.r * 255,
        g = tempRGB.g * 255,
        b = tempRGB.b * 255,
        a = tempRGB.a * 255,
    )
}

data class RGBA(
    @FloatRange(from = 0.0, to = 255.0)
    val r: Float,
    @FloatRange(from = 0.0, to = 255.0)
    val g: Float,
    @FloatRange(from = 0.0, to = 255.0)
    val b: Float,
    @FloatRange(from = 0.0, to = 255.0)
    val a: Float
)

fun RGBA.toColor(): Color = Color(
    red = r.roundToInt(),
    green = g.roundToInt(),
    blue = b.roundToInt(),
    alpha = a.roundToInt()
)