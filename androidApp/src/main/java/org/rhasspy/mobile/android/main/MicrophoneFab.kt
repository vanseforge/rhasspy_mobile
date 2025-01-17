package org.rhasspy.mobile.android.main

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.R
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.elements.FloatingActionButton
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewModel

/**
 * Floating Action Button with microphone
 */
@Composable
fun MicrophoneFab(
    modifier: Modifier = Modifier,
    iconSize: Dp,
    viewModel: MicrophoneFabViewModel,
    onClick: () -> Unit
) {

    val isActionEnabled by viewModel.isActionEnabled.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()

    FloatingActionButton(
        modifier = modifier
            .testTag(TestTag.MicrophoneFab)
            .let {
                if (viewModel.isShowBorder.collectAsState().value) {
                    return@let it.border(
                        8.dp,
                        MaterialTheme.colorScheme.errorContainer,
                        FloatingActionButtonDefaults.shape
                    )
                }
                it
            },
        onClick = {
            if (isActionEnabled) {
                onClick()
            }
        },
        isEnabled = isActionEnabled,
        containerColor = getContainerColorForMicrophoneFab(isActionEnabled, isRecording),
        contentColor = getContentColorForMicrophoneFab(isActionEnabled, isRecording),
        icon = {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = if (viewModel.isShowMicOn.collectAsState().value) Icons.Filled.Mic else Icons.Filled.MicOff,
                contentDescription = MR.strings.wakeUp,
            )
        }
    )

}


@Composable
fun getContainerColorForMicrophoneFab(isActionEnabled: Boolean, isRecording: Boolean): Color {
    return when {
        isRecording -> MaterialTheme.colorScheme.errorContainer
        isActionEnabled -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    }
}

@Composable
fun getContentColorForMicrophoneFab(isActionEnabled: Boolean, isRecording: Boolean): Color {
    return when {
        isRecording -> MaterialTheme.colorScheme.onErrorContainer
        isActionEnabled -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.4f)
    }
}

@Composable
@DrawableRes
fun getContainerForMicrophoneFabLegacy(isActionEnabled: Boolean, isRecording: Boolean): Int {
    return when {
        isRecording -> R.drawable.microphone_widget_background_error
        isActionEnabled -> R.drawable.microphone_widget_background_primary
        else -> R.drawable.microphone_widget_background_primary_04
    }
}

@Composable
@ColorRes
fun getMicrophoneFabIconLegacy(
    isMicOn: Boolean,
    isActionEnabled: Boolean,
    isRecording: Boolean
): Int {
    return when {
        isRecording -> if (isMicOn) R.drawable.ic_mic_on_error_container else R.drawable.ic_mic_off_on_error_container
        isActionEnabled -> if (isMicOn) R.drawable.ic_mic_on_primary_container else R.drawable.ic_mic_off_on_primary_container
        else -> if (isMicOn) R.drawable.ic_mic_on_primary_container_04 else R.drawable.ic_mic_off_on_primary_container_04
    }
}