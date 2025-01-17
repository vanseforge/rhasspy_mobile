package org.rhasspy.mobile.logic.services.audioplaying

import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.logic.settings.option.AudioPlayingOption

data class AudioPlayingServiceParams(
    val audioPlayingOption: AudioPlayingOption = ConfigurationSetting.audioPlayingOption.value
)