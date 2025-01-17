package org.rhasspy.mobile.logic.nativeutils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

expect abstract class NativeApplication() {

    val currentlyAppInBackground: MutableStateFlow<Boolean>
    val isAppInBackground: StateFlow<Boolean>
    abstract val isHasStarted: StateFlow<Boolean>

    abstract suspend fun updateWidgetNative()

    abstract suspend fun reloadServiceModules()

    abstract suspend fun startTest()

    abstract suspend fun stopTest()

    abstract fun setCrashlyticsCollectionEnabled(enabled: Boolean)

    fun isInstrumentedTest(): Boolean

    fun openLink(link: String)

    fun restart()

    fun onCreate()

}