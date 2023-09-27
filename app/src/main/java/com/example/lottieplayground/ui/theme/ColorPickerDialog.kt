package com.example.lottieplayground.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.lottieplayground.data.HSV
import com.example.lottieplayground.data.toColor
import com.example.lottieplayground.data.toRGBA

@Composable
fun ColorWheelPickerDialog(
    currentColor: HSV,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    onColorChanged: (hsv: HSV) -> Unit
) {
    var updatedHsv by remember {
        mutableStateOf(currentColor)
    }

    AlertDialog(
        title = {
            Text(text = "Color Picker")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                ColorWheelPicker(
                    selectedColor = updatedHsv,
                    onColorChanged = {
                        updatedHsv = it
                    }
                )
                Spacer(modifier = Modifier.size(16.dp))
                AlphaSeekBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp),
                    selectedColor = updatedHsv
                ) { alpha ->
                    updatedHsv = updatedHsv.copy(a = alpha)
                }
                Spacer(modifier = Modifier.size(16.dp))
                Row(modifier = Modifier.wrapContentSize(align = Alignment.CenterStart)) {
                    Text(
                        modifier = Modifier.align(alignment = Alignment.CenterVertically),
                        text = "Selected Color:"
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 32.dp, height = 32.dp)
                            .clip(shape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 8.dp))
                            .background(
                                color = updatedHsv
                                    .toRGBA()
                                    ?.toColor()!!
                            )
                    )
                }

            }
        },
        onDismissRequest = {
            onDismissRequest.invoke()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmRequest.invoke()
                    onColorChanged(updatedHsv)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest.invoke()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}