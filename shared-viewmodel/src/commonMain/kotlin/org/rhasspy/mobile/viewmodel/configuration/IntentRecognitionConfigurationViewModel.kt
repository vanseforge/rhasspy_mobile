package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.logic.combineAny
import org.rhasspy.mobile.logic.combineState
import org.rhasspy.mobile.logic.combineStateNotEquals
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.mapReadonlyState
import org.rhasspy.mobile.logic.readOnly
import org.rhasspy.mobile.logic.services.httpclient.HttpClientPath
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.logic.settings.option.IntentRecognitionOption
import org.rhasspy.mobile.viewmodel.configuration.test.IntentRecognitionConfigurationTest

class IntentRecognitionConfigurationViewModel : IConfigurationViewModel() {

    //test data
    override val testRunner by inject<IntentRecognitionConfigurationTest>()
    override val logType = LogType.IntentRecognitionService
    override val serviceState get() = get<IntentRecognitionService>().serviceState

    private val _testIntentRecognitionText = MutableStateFlow("")
    val testIntentRecognitionText = _testIntentRecognitionText.readOnly

    //unsaved data
    private val _intentRecognitionOption =
        MutableStateFlow(ConfigurationSetting.intentRecognitionOption.value)
    private val _isUseCustomIntentRecognitionHttpEndpoint =
        MutableStateFlow(ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value)
    private val _intentRecognitionHttpEndpoint =
        MutableStateFlow(ConfigurationSetting.intentRecognitionHttpEndpoint.value)

    //unsaved ui data
    val intentRecognitionOption = _intentRecognitionOption.readOnly
    val intentRecognitionHttpEndpoint =
        combineState(
            _isUseCustomIntentRecognitionHttpEndpoint,
            _intentRecognitionHttpEndpoint
        ) { useCustomIntentRecognitionHttpEndpoint,
            intentRecognitionHttpEndpoint ->
            if (useCustomIntentRecognitionHttpEndpoint) {
                intentRecognitionHttpEndpoint
            } else {
                HttpClientPath.TextToIntent.fromBaseConfiguration()
            }
        }
    val isUseCustomIntentRecognitionHttpEndpoint =
        _isUseCustomIntentRecognitionHttpEndpoint.readOnly
    val isIntentRecognitionHttpEndpointChangeEnabled = isUseCustomIntentRecognitionHttpEndpoint

    override val isTestingEnabled =
        _intentRecognitionOption.mapReadonlyState { it != IntentRecognitionOption.Disabled }

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(
            _intentRecognitionOption,
            ConfigurationSetting.intentRecognitionOption.data
        ),
        combineStateNotEquals(
            _isUseCustomIntentRecognitionHttpEndpoint,
            ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.data
        ),
        combineStateNotEquals(
            _intentRecognitionHttpEndpoint,
            ConfigurationSetting.intentRecognitionHttpEndpoint.data
        )
    )

    //show endpoint settings
    fun isIntentRecognitionHttpSettingsVisible(option: IntentRecognitionOption): Boolean {
        return option == IntentRecognitionOption.RemoteHTTP
    }

    //all options
    val intentRecognitionOptionList = IntentRecognitionOption::values

    //set new intent recognition option
    fun selectIntentRecognitionOption(option: IntentRecognitionOption) {
        _intentRecognitionOption.value = option
    }

    //toggle if custom endpoint is used
    fun toggleUseCustomHttpEndpoint(enabled: Boolean) {
        _isUseCustomIntentRecognitionHttpEndpoint.value = enabled
    }

    //set new intent recognition option
    fun changeIntentRecognitionHttpEndpoint(endpoint: String) {
        _intentRecognitionHttpEndpoint.value = endpoint
    }

    fun updateTestIntentRecognitionText(text: String) {
        _testIntentRecognitionText.value = text
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSetting.intentRecognitionOption.value = _intentRecognitionOption.value
        ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value =
            _isUseCustomIntentRecognitionHttpEndpoint.value
        ConfigurationSetting.intentRecognitionHttpEndpoint.value =
            _intentRecognitionHttpEndpoint.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _intentRecognitionOption.value = ConfigurationSetting.intentRecognitionOption.value
        _isUseCustomIntentRecognitionHttpEndpoint.value =
            ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value
        _intentRecognitionHttpEndpoint.value =
            ConfigurationSetting.intentRecognitionHttpEndpoint.value
    }

    override fun initializeTestParams() {
        get<IntentRecognitionServiceParams> {
            parametersOf(
                IntentRecognitionServiceParams(
                    intentRecognitionOption = _intentRecognitionOption.value
                )
            )
        }

        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isUseCustomTextToSpeechHttpEndpoint = _isUseCustomIntentRecognitionHttpEndpoint.value,
                    intentRecognitionHttpEndpoint = _intentRecognitionHttpEndpoint.value,
                )
            )
        }
    }

    fun runIntentRecognition() = testRunner.runIntentRecognition(_testIntentRecognitionText.value)

}