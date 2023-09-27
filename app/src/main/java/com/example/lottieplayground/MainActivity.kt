package com.example.lottieplayground

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.example.lottieplayground.data.HSV
import com.example.lottieplayground.data.toColor
import com.example.lottieplayground.data.toRGBA
import com.example.lottieplayground.ui.theme.ColorWheelPickerDialog
import com.example.lottieplayground.ui.theme.LottiePlaygroundTheme
import com.example.lottieplayground.ui.theme.OldWayAndroidLottieView

const val USE_OLD_LOTTIE = false

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LottiePlaygroundTheme {
                val color = when (isSystemInDarkTheme()) {
                    true -> Color.White
                    else -> Color.Red
                }
                var openDialog by remember { mutableStateOf(false) }
                var currentColor by remember { mutableStateOf(color) }
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.animation)
                )

                val dynamicProperties = rememberLottieDynamicProperties(
                    rememberLottieDynamicProperty(
                        property = LottieProperty.COLOR_FILTER,
                        value = PorterDuffColorFilter(currentColor.toArgb(), PorterDuff.Mode.SRC),
                        keyPath = arrayOf(
                            "**",
                            "Stroke 1"
                        )
                    ),
                )

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            if (USE_OLD_LOTTIE) {
                                OldWayAndroidLottieView(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f, matchHeightConstraintsFirst = true),
                                    selectedColor = currentColor
                                )
                            } else {
                                LottieAnimation(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f, matchHeightConstraintsFirst = true),
                                    composition = composition,
                                    iterations = LottieConstants.IterateForever,
                                    dynamicProperties = dynamicProperties
                                )
                            }
                            Text(
                                modifier = Modifier
                                    .clickable {
                                        openDialog = true
                                        currentColor = currentColor.copy(alpha = 1.0f)
                                    }
                                    .wrapContentWidth()
                                    .align(Alignment.CenterHorizontally),
                                text = "Pick Diamond Color",
                                color = currentColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (openDialog) {
                        ColorWheelPickerDialog(
                            currentColor = HSV.from(currentColor),
                            onColorChanged = { hsv ->
                                currentColor = hsv.toRGBA().toColor()
                                Log.i("LottiePlayground", "ColorWheelPickerDialog: $currentColor")
                            },
                            onConfirmRequest = {
                                openDialog = false
                            },
                            onDismissRequest = {
                                openDialog = false
                            }
                        )
                    }
                }
            }
        }
    }
}