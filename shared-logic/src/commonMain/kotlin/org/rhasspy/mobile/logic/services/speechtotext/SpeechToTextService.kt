package org.rhasspy.mobile.logic.services.speechtotext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.middleware.Action.DialogAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceState
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.readOnly
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientResult
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.recording.RecordingService
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.logic.settings.option.SpeechToTextOption

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
open class SpeechToTextService : IService() {
    private val logger = LogType.SpeechToTextService.logger()

    private val params by inject<SpeechToTextServiceParams>()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success)
    val serviceState = _serviceState.readOnly

    private val httpClientService by inject<HttpClientService>()
    private val mqttClientService by inject<MqttService>()
    private val recordingService by inject<RecordingService>()

    private val serviceMiddleware by inject<ServiceMiddleware>()

    private var _speechToTextAudioData: ByteArray = ByteArray(0)
    val speechToTextAudioData get() = _speechToTextAudioData

    private val scope = CoroutineScope(Dispatchers.Default)
    private var collector: Job? = null

    override fun onClose() {
        logger.d { "onClose" }
        //nothing to do
        scope.cancel()
        collector?.cancel()
        collector = null
    }

    /**
     * Speech to Text (Wav Data)
     * used to translate last spoken
     *
     * HTTP:
     * - calls service to translate speech to text, then handles the intent if dialogue manager is set to local
     *
     * RemoteMQTT
     * - audio was already send to mqtt while recording in audioFrame
     *
     * fromMqtt is used to check if silence was detected by remote mqtt device
     */
    suspend fun endSpeechToText(sessionId: String, fromMqtt: Boolean) {
        logger.d { "endSpeechToText sessionId: $sessionId fromMqtt $fromMqtt" }

        //stop collection
        collector?.cancel()
        collector = null
        recordingService.toggleSilenceDetectionEnabled(false)

        //evaluate result
        when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> {
                val result = httpClientService.speechToText(_speechToTextAudioData)
                _serviceState.value = result.toServiceState()
                val action = when (result) {
                    is HttpClientResult.Error -> DialogAction.AsrError(Source.HttpApi)
                    is HttpClientResult.Success -> DialogAction.AsrTextCaptured(
                        Source.HttpApi,
                        result.data
                    )
                }
                serviceMiddleware.action(action)
            }

            SpeechToTextOption.RemoteMQTT -> if (!fromMqtt) _serviceState.value =
                mqttClientService.stopListening(sessionId)

            SpeechToTextOption.Disabled -> {}
        }
    }

    suspend fun startSpeechToText(sessionId: String, fromMqtt: Boolean) {
        logger.d { "startSpeechToText sessionId: $sessionId fromMqtt $fromMqtt" }

        if (collector?.isActive == true) {
            return
        }

        //clear data and start recording
        collector?.cancel()
        collector = null
        _speechToTextAudioData = ByteArray(0)

        if (params.speechToTextOption != SpeechToTextOption.Disabled) {
            recordingService.toggleSilenceDetectionEnabled(true)
            //start collection
            collector = scope.launch {
                recordingService.output.collect {
                    audioFrame(sessionId, it)
                }
            }
        }

        _serviceState.value = when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> ServiceState.Success
            SpeechToTextOption.RemoteMQTT -> if (!fromMqtt) {
                mqttClientService.startListening(sessionId)
            } else ServiceState.Success

            SpeechToTextOption.Disabled -> ServiceState.Disabled
        }
    }

    private suspend fun audioFrame(sessionId: String, data: ByteArray) {
        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "audioFrame dataSize: ${data.size}" }
        }

        _speechToTextAudioData += data

        _serviceState.value = when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> ServiceState.Success
            SpeechToTextOption.RemoteMQTT -> {
                mqttClientService.asrAudioFrame(sessionId, data)
            }

            SpeechToTextOption.Disabled -> ServiceState.Disabled
        }
    }

}