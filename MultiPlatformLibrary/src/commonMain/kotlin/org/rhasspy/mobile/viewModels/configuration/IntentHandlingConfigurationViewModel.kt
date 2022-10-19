package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.IntentHandlingOptions
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.ConfigurationSettings

class IntentHandlingConfigurationViewModel : ViewModel() {

    //unsaved data
    private val _intentHandlingOption = MutableStateFlow(ConfigurationSettings.intentHandlingOption.value)
    private val _intentHandlingHttpEndpoint = MutableStateFlow(ConfigurationSettings.intentHandlingHttpEndpoint.value)
    private val _intentHandlingHassEndpoint = MutableStateFlow(ConfigurationSettings.intentHandlingHassEndpoint.value)
    private val _intentHandlingHassAccessToken = MutableStateFlow(ConfigurationSettings.intentHandlingHassAccessToken.value)
    private val _isIntentHandlingHassEvent = MutableStateFlow(ConfigurationSettings.isIntentHandlingHassEvent.value)

    //unsaved ui data
    val intentHandlingOption = _intentHandlingOption.readOnly
    val intentHandlingHttpEndpoint = _intentHandlingHttpEndpoint.readOnly
    val intentHandlingHassEndpoint = _intentHandlingHassEndpoint.readOnly
    val intentHandlingHassAccessToken = _intentHandlingHassAccessToken.readOnly
    val isIntentHandlingHassEvent = _isIntentHandlingHassEvent.readOnly
    val isIntentHandlingHassIntent = _isIntentHandlingHassEvent.mapReadonlyState { !it }

    //show input field for endpoint
    val isRemoteHttpEndpointVisible = _intentHandlingOption.mapReadonlyState { it == IntentHandlingOptions.RemoteHTTP }
    val isHomeAssistantSettingsVisible = _intentHandlingOption.mapReadonlyState { it == IntentHandlingOptions.HomeAssistant }

    //all options
    val intentHandlingOptionsList = IntentHandlingOptions::values

    //set new intent handling option
    fun selectIntentHandlingOption(option: IntentHandlingOptions) {
        _intentHandlingOption.value = option
    }

    //edit endpoint
    fun changeIntentHandlingHttpEndpoint(endpoint: String) {
        _intentHandlingHttpEndpoint.value = endpoint
    }

    //edit endpoint
    fun changeIntentHandlingHassEndpoint(endpoint: String) {
        _intentHandlingHttpEndpoint.value = endpoint
    }

    //edit endpoint
    fun changeIntentHandlingHassAccessToken(token: String) {
        _intentHandlingHassAccessToken.value = token
    }

    //choose hass intent handling as event
    fun selectIntentHandlingHassEvent() {
        _isIntentHandlingHassEvent.value = true
    }

    //choose hass intent handling as intent
    fun selectIntentHandlingHassIntent() {
        _isIntentHandlingHassEvent.value = false
    }

    /**
     * save data configuration
     */
    fun save() {
        ConfigurationSettings.intentHandlingOption.data.value = _intentHandlingOption.value
        ConfigurationSettings.intentHandlingHttpEndpoint.data.value = _intentHandlingHttpEndpoint.value
        ConfigurationSettings.intentHandlingHassEndpoint.data.value = _intentHandlingHassEndpoint.value
        ConfigurationSettings.intentHandlingHassAccessToken.data.value = _intentHandlingHassAccessToken.value
        ConfigurationSettings.isIntentHandlingHassEvent.data.value = _isIntentHandlingHassEvent.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}