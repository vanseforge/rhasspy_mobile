package org.rhasspy.mobile

import org.koin.dsl.module
import org.rhasspy.mobile.logic.closeableSingle
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.nativeutils.AudioRecorder
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingServiceParams
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceParams
import org.rhasspy.mobile.logic.services.homeassistant.HomeAssistantService
import org.rhasspy.mobile.logic.services.homeassistant.HomeAssistantServiceParams
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.services.indication.IndicationService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingServiceParams
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionServiceParams
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioServiceParams
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.mqtt.MqttServiceParams
import org.rhasspy.mobile.logic.services.recording.RecordingService
import org.rhasspy.mobile.logic.services.settings.AppSettingsService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextServiceParams
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechServiceParams
import org.rhasspy.mobile.logic.services.udp.UdpService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordServiceParams
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.logic.services.webserver.WebServerServiceParams
import org.rhasspy.mobile.viewmodel.AppViewModel
import org.rhasspy.mobile.viewmodel.configuration.AudioPlayingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.DialogManagementConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.IntentHandlingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.IntentRecognitionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.MqttConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.RemoteHermesHttpConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.SpeechToTextConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.TextToSpeechConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.WakeWordConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.WebServerConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.test.AudioPlayingConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.test.DialogManagementConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.test.IntentHandlingConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.test.IntentRecognitionConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.test.MqttConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.test.RemoteHermesHttpConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.test.SpeechToTextConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.test.TextToSpeechConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.test.WakeWordConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.test.WebServerConfigurationTest
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewModel
import org.rhasspy.mobile.viewmodel.overlay.IndicationOverlayViewModel
import org.rhasspy.mobile.viewmodel.overlay.MicrophoneOverlayViewModel
import org.rhasspy.mobile.viewmodel.screens.AboutScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.ConfigurationScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.HomeScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.LogScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.SettingsScreenViewModel
import org.rhasspy.mobile.viewmodel.settings.AutomaticSilenceDetectionSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.BackgroundServiceSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.DeviceSettingsSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.IndicationSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.LanguageSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.LogSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.MicrophoneOverlaySettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.SaveAndRestoreSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.sound.ErrorIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.sound.RecordedIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.sound.WakeIndicationSoundSettingsViewModel


val serviceModule = module {
    closeableSingle { LocalAudioService() }
    closeableSingle { AudioPlayingService() }
    closeableSingle { IntentHandlingService() }
    closeableSingle { IntentRecognitionService() }
    closeableSingle { SpeechToTextService() }
    closeableSingle { TextToSpeechService() }
    closeableSingle { MqttService() }
    closeableSingle { HttpClientService() }
    closeableSingle { WebServerService() }
    closeableSingle { HomeAssistantService() }
    closeableSingle { WakeWordService() }
    closeableSingle { RecordingService() }
    closeableSingle { DialogManagerService() }
    closeableSingle { AppSettingsService() }
    closeableSingle { IndicationService() }
    closeableSingle { ServiceMiddleware() }

    single { params -> params.getOrNull<LocalAudioServiceParams>() ?: LocalAudioServiceParams() }
    single { params ->
        params.getOrNull<AudioPlayingServiceParams>() ?: AudioPlayingServiceParams()
    }
    single { params ->
        params.getOrNull<IntentHandlingServiceParams>() ?: IntentHandlingServiceParams()
    }
    single { params ->
        params.getOrNull<IntentRecognitionServiceParams>() ?: IntentRecognitionServiceParams()
    }
    single { params ->
        params.getOrNull<SpeechToTextServiceParams>() ?: SpeechToTextServiceParams()
    }
    single { params ->
        params.getOrNull<TextToSpeechServiceParams>() ?: TextToSpeechServiceParams()
    }
    single { params -> params.getOrNull<MqttServiceParams>() ?: MqttServiceParams() }
    single { params -> params.getOrNull<HttpClientServiceParams>() ?: HttpClientServiceParams() }
    single { params -> params.getOrNull<WebServerServiceParams>() ?: WebServerServiceParams() }
    single { params ->
        params.getOrNull<HomeAssistantServiceParams>() ?: HomeAssistantServiceParams()
    }
    single { params -> params.getOrNull<WakeWordServiceParams>() ?: WakeWordServiceParams() }
    single { params ->
        params.getOrNull<DialogManagerServiceParams>() ?: DialogManagerServiceParams()
    }

    closeableSingle { AudioPlayingConfigurationTest() }
    closeableSingle { DialogManagementConfigurationTest() }
    closeableSingle { IntentHandlingConfigurationTest() }
    closeableSingle { IntentRecognitionConfigurationTest() }
    closeableSingle { MqttConfigurationTest() }
    closeableSingle { RemoteHermesHttpConfigurationTest() }
    closeableSingle { SpeechToTextConfigurationTest() }
    closeableSingle { TextToSpeechConfigurationTest() }
    closeableSingle { WakeWordConfigurationTest() }
    closeableSingle { WebServerConfigurationTest() }
}

val viewModelModule = module {
    single { AppViewModel() }
    single { HomeScreenViewModel() }
    single { MicrophoneFabViewModel() }
    single { ConfigurationScreenViewModel() }
    single { AudioPlayingConfigurationViewModel() }
    single { DialogManagementConfigurationViewModel() }
    single { IntentHandlingConfigurationViewModel() }
    single { IntentRecognitionConfigurationViewModel() }
    single { MqttConfigurationViewModel() }
    single { RemoteHermesHttpConfigurationViewModel() }
    single { SpeechToTextConfigurationViewModel() }
    single { TextToSpeechConfigurationViewModel() }
    single { WakeWordConfigurationViewModel() }
    single { WebServerConfigurationViewModel() }
    single { LogScreenViewModel() }
    single { SettingsScreenViewModel() }
    single { AboutScreenViewModel() }
    single { AutomaticSilenceDetectionSettingsViewModel() }
    single { BackgroundServiceSettingsViewModel() }
    single { DeviceSettingsSettingsViewModel() }
    single { IndicationSettingsViewModel() }
    single { WakeIndicationSoundSettingsViewModel() }
    single { RecordedIndicationSoundSettingsViewModel() }
    single { ErrorIndicationSoundSettingsViewModel() }
    single { LanguageSettingsViewModel() }
    single { LogSettingsViewModel() }
    single { MicrophoneOverlaySettingsViewModel() }
    single { SaveAndRestoreSettingsViewModel() }
    single { MicrophoneOverlayViewModel() }
    single { IndicationOverlayViewModel() }
}

val factoryModule = module {
    factory { params -> UdpService(params[0], params[1]) }
}

val nativeModule = module {
    closeableSingle { AudioRecorder() }
}