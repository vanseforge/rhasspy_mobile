package org.rhasspy.mobile.logic.nativeutils

import android.content.Context
import android.os.PowerManager
import co.touchlab.kermit.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * handles indication of wake up locally
 */
actual object NativeIndication : KoinComponent {

    private val logger = Logger.withTag("NativeIndication")
    private var wakeLock: PowerManager.WakeLock? = null

    private val context = get<NativeApplication>()

    /**
     * wake up screen if possible
     */
    @Suppress("DEPRECATION")
    actual fun wakeUpScreen() {
        wakeLock =
            (context.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                    "Rhasspy::WakeWordDetected"
                ).apply {
                    acquire(10 * 60 * 1000L /*10 minutes*/)
                }
            }
    }


    /**
     * remove wake lock and let screen go off
     */
    actual fun releaseWakeUp() {
        try {
            wakeLock?.release()
        } catch (e: Exception) {
            logger.w(e) { "wakelock release exception" }
        }
    }

}