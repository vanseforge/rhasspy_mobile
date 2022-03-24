package org.rhasspy.mobile.settings

import org.rhasspy.mobile.data.*

object ConfigurationSettings {

    val siteId = ConfigurationSetting(SettingsEnum.SiteId, "mobile")

    val isHttpSSL = ConfigurationSetting(SettingsEnum.HttpSSL, false)

    val isMQTTEnabled = ConfigurationSetting(SettingsEnum.MQTT_ENABLED, false)
    val isMqttSSL = ConfigurationSetting(SettingsEnum.MQTT_SSL, false)
    val mqttHost = ConfigurationSetting(SettingsEnum.MQTTHost, "")
    val mqttPort = ConfigurationSetting(SettingsEnum.MQTTPort, "")
    val mqttUserName = ConfigurationSetting(SettingsEnum.MQTTUserName, "")
    val mqttPassword = ConfigurationSetting(SettingsEnum.MQTTPassword, "")

    val isUDPOutput = ConfigurationSetting(SettingsEnum.UDPOutput, false)
    val udpOutputHost = ConfigurationSetting(SettingsEnum.UDPOutputHost, "")
    val udpOutputPort = ConfigurationSetting(SettingsEnum.UDPOutputPort, "")

    val wakeWordOption = ConfigurationSetting(SettingsEnum.WakeWordOption, WakeWordOption.Disabled)
    val wakeWordAccessToken = ConfigurationSetting(SettingsEnum.WakeWordAccessToken, "")
    val wakeWordKeywordOption = ConfigurationSetting(SettingsEnum.WakeWordKeywordOption, WakeWordKeywordOption.JARVIS)
    val wakeWordKeywordSensitivity = ConfigurationSetting(SettingsEnum.WakeWordKeywordSensitivity, 0.5f)

    val speechToTextOption = ConfigurationSetting(SettingsEnum.SpeechToTextOption, SpeechToTextOptions.Disabled)
    val speechToTextHttpEndpoint = ConfigurationSetting(SettingsEnum.SpeechToTextHttpEndpoint, "")

    val intentRecognitionOption = ConfigurationSetting(SettingsEnum.IntentRecognitionOption, IntentRecognitionOptions.Disabled)
    val intentRecognitionEndpoint = ConfigurationSetting(SettingsEnum.IntentRecognitionEndpoint, "")

    val textToSpeechOption = ConfigurationSetting(SettingsEnum.TextToSpeechOption, TextToSpeechOptions.Disabled)
    val textToSpeechEndpoint = ConfigurationSetting(SettingsEnum.TextToSpeechEndpoint, "")

    val audioPlayingOption = ConfigurationSetting(SettingsEnum.AudioPlayingOption, AudioPlayingOptions.Disabled)
    val audioPlayingEndpoint = ConfigurationSetting(SettingsEnum.AudioPlayingEndpoint, "")

    val dialogueManagementOption = ConfigurationSetting(SettingsEnum.DialogueManagementOption, DialogueManagementOptions.Local)

    val intentHandlingOption = ConfigurationSetting(SettingsEnum.IntentHandlingOption, IntentHandlingOptions.Disabled)
    val intentHandlingEndpoint = ConfigurationSetting(SettingsEnum.IntentHandlingEndpoint, "")
    val intentHandlingHassUrl = ConfigurationSetting(SettingsEnum.IntentHandlingHassUrl, "")
    val intentHandlingHassAccessToken = ConfigurationSetting(SettingsEnum.IntentHandlingHassAccessToken, "")
    val isIntentHandlingHassEvent = ConfigurationSetting(SettingsEnum.IsIntentHandlingHassEvent, false)

}
