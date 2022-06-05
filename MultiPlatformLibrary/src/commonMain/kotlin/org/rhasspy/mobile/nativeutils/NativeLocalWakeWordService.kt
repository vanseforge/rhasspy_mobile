package org.rhasspy.mobile.nativeutils

/**
 * Listens to WakeWord with Porcupine
 */
expect object NativeLocalWakeWordService {

    /**
     * start listening to wake words
     * requires internet to activate porcupine the very first time
     */
    fun start()

    /**
     * stops porcupine
     */
    fun stop()

}