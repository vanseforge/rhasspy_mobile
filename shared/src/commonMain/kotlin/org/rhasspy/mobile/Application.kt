package org.rhasspy.mobile

import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import dev.icerock.moko.resources.desc.StringDesc
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.rhasspy.mobile.logic.logger.FileLogger
import org.rhasspy.mobile.logic.nativeutils.BackgroundService
import org.rhasspy.mobile.logic.nativeutils.NativeApplication
import org.rhasspy.mobile.logic.nativeutils.OverlayPermission
import org.rhasspy.mobile.logic.nativeutils.isDebug
import org.rhasspy.mobile.logic.readOnly
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.logic.settings.option.MicrophoneOverlaySizeOption

abstract class Application : NativeApplication(), KoinComponent {
    private val logger = Logger.withTag("Application")
    private val _isHasStarted = MutableStateFlow(false)
    override val isHasStarted = _isHasStarted.readOnly

    @OptIn(ExperimentalKermitApi::class)
    fun onCreated() {
        startKoin {
            // declare used modules
            modules(module {
                single<NativeApplication> { this@Application }
            }, serviceModule, viewModelModule, factoryModule, nativeModule)
        }

        CoroutineScope(Dispatchers.Default).launch {
            if (!isDebug() && !isInstrumentedTest()) { //TODO must be done in platform specific code
                Logger.addLogWriter(
                    CrashlyticsLogWriter(
                        minSeverity = Severity.Info,
                        minCrashSeverity = Severity.Assert
                    )
                )
            }
            Logger.addLogWriter(FileLogger)

            logger.i { "######## Application started ########" }

            //initialize/load the settings, generate the MutableStateFlow
            AppSetting
            ConfigurationSetting

            setCrashlyticsCollectionEnabled(
                if (!isDebug() && !isInstrumentedTest()) {
                    AppSetting.isCrashlyticsEnabled.value
                } else false
            )

            //setup language
            StringDesc.localeType = StringDesc.LocaleType.Custom(AppSetting.languageType.value.code)

            //start foreground service if enabled
            if (AppSetting.isBackgroundServiceEnabled.value) {
                BackgroundService.start()
            }

            //check if overlay permission is granted
            checkOverlayPermission()
            startServices()
            startOverlay()
            _isHasStarted.value = true
        }
    }

    override suspend fun updateWidgetNative() {
        updateWidget()
    }

    abstract fun startOverlay()

    abstract fun stopOverlay()

    abstract suspend fun updateWidget()

    override suspend fun startTest() {
        BackgroundService.stop()
        stopOverlay()
        unloadKoinModules(listOf(viewModelModule, serviceModule))
        loadKoinModules(listOf(serviceModule, viewModelModule))
    }

    override suspend fun stopTest() {
        reloadServiceModules()
    }

    override suspend fun reloadServiceModules() {
        stopOverlay()
        unloadKoinModules(listOf(viewModelModule, serviceModule))
        loadKoinModules(listOf(serviceModule, viewModelModule))
        startServices()
        startOverlay()
    }

    private fun checkOverlayPermission() {
        if (!OverlayPermission.isGranted()) {
            if (AppSetting.microphoneOverlaySizeOption.value != MicrophoneOverlaySizeOption.Disabled ||
                AppSetting.isWakeWordLightIndicationEnabled.value
            ) {
                logger.w { "reset overlay settings because permission is missing" }
                //reset services that need the permission
                AppSetting.microphoneOverlaySizeOption.value = MicrophoneOverlaySizeOption.Disabled
                AppSetting.isWakeWordLightIndicationEnabled.value = false
            }
        }
    }

    private fun startServices() {
        get<WebServerService>()
        get<MqttService>()
        get<DialogManagerService>()
    }

}