package org.rhasspy.mobile.services.dialogManager

import org.rhasspy.mobile.data.DialogManagementOptions
import org.rhasspy.mobile.data.IntentRecognitionOptions
import org.rhasspy.mobile.data.SpeechToTextOptions
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.settings.ConfigurationSettings

data class DialogManagerServiceParams(
    val option: DialogManagementOptions = ConfigurationSettings.dialogManagementOption.value,
    val wakeWordOption: WakeWordOption = ConfigurationSettings.wakeWordOption.value,
    val speechToTextOption: SpeechToTextOptions = ConfigurationSettings.speechToTextOption.value,
    val intentRecognitionOption: IntentRecognitionOptions = ConfigurationSettings.intentRecognitionOption.value,
    val isUdpOutputEnabled: Boolean = ConfigurationSettings.isUdpOutputEnabled.value
)