package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.AudioPlayingOptions
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.ConfigurationSettings

/**
 * ViewModel for Audio Playing Configuration
 *
 * Current Option
 * Endpoint value
 * if Endpoint option should be shown
 * all Options as list
 */
class AudioPlayingConfigurationViewModel : ViewModel() {

    //unsaved data
    private val _audioPlayingOption = MutableStateFlow(ConfigurationSettings.audioPlayingOption.value)
    private val _audioPlayingEndpoint = MutableStateFlow(ConfigurationSettings.audioPlayingEndpoint.value)

    //unsaved ui data
    val audioPlayingOption = _audioPlayingOption.readOnly
    val audioPlayingEndpoint = _audioPlayingEndpoint.readOnly

    //show input field for endpoint
    val isAudioPlayingEndpointVisible = _audioPlayingOption.mapReadonlyState { it == AudioPlayingOptions.RemoteHTTP }

    //all options
    val audioPlayingOptionsList = AudioPlayingOptions::values

    //set new audio playing option
    fun selectAudioPlayingOption(option: AudioPlayingOptions) {
        _audioPlayingOption.value = option
    }

    //edit endpoint
    fun changeAudioPlayingEndpoint(endpoint: String) {
        _audioPlayingEndpoint.value = endpoint
    }

    /**
     * save data configuration
     */
    fun save() {
        ConfigurationSettings.audioPlayingOption.data.value = _audioPlayingOption.value
        ConfigurationSettings.audioPlayingEndpoint.data.value = _audioPlayingEndpoint.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}