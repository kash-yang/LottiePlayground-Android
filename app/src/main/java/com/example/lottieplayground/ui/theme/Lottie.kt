package com.example.lottieplayground.ui.theme

import android.animation.ValueAnimator
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.view.Gravity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.example.lottieplayground.R


/**
 * For us to get the key path because of this:
 * https://github.com/airbnb/lottie-android/issues/2080
 */
@Composable
fun OldWayAndroidLottieView(
    modifier: Modifier = Modifier,
    selectedColor: Color
) {
    val context = LocalContext.current
    val lottieView = remember { LottieAnimationView(context) }
    // Adds view to Compose
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { lottieView }
    ) { view ->
        // View's been inflated - add logic here if necessary
        with(view) {
            setAnimation(R.raw.animation)
            playAnimation()
            repeatMode = LottieDrawable.RESTART
            repeatCount = ValueAnimator.INFINITE
            foregroundGravity = Gravity.CENTER

            val applyColor: (keyPath: KeyPath, color: Int) -> Unit = { keyPath, color ->
                val filter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC)
                this.addValueCallback(
                    keyPath,
                    LottieProperty.COLOR_FILTER,
                    LottieValueCallback(filter)
                )
            }
            this.addLottieOnCompositionLoadedListener {
                resolveKeyPath(KeyPath("**")).forEach {
                    Log.i("LottiePlayground", "key path: $it")
                }
                applyColor(KeyPath("**", "Stroke 1"), selectedColor.toArgb())
            }
        }
    }
}