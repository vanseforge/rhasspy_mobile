package org.rhasspy.mobile.logic.nativeutils

import dev.icerock.moko.resources.FileResource
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.settings.option.AudioOutputOption

/**
 * plays audio on the device
 *
 * some data (Byte list)
 * File resource
 * specific file
 */
expect class AudioPlayer() : Closeable {

    /**
     * represents if audio player is currently playing
     */
    val isPlayingState: StateFlow<Boolean>

    /**
     * play byte list
     *
     * on Finished is called when playing has been finished
     * on Error is called when an playback error occurs
     */
    fun playData(
        data: ByteArray,
        volume: Float,
        audioOutputOption: AudioOutputOption,
        onFinished: (() -> Unit)? = null,
        onError: ((exception: Exception?) -> Unit)? = null
    )

    /**
     * play file from resources
     *
     * volume is the playback volume, can be changed live
     * audio output option defines the channel (sound or notification)
     * on Finished is called when playing has been finished
     * on Error is called when an playback error occurs
     */
    fun playFileResource(
        fileResource: FileResource,
        volume: StateFlow<Float>,
        audioOutputOption: AudioOutputOption,
        onFinished: (() -> Unit)? = null,
        onError: ((exception: Exception?) -> Unit)? = null
    )

    /**
     * play file from storage
     *
     * volume is the playback volume, can be changed live
     * audio output option defines the channel (sound or notification)
     * on Finished is called when playing has been finished
     * on Error is called when an playback error occurs
     */
    fun playSoundFile(
        filename: String,
        volume: StateFlow<Float>,
        audioOutputOption: AudioOutputOption,
        onFinished: (() -> Unit)? = null,
        onError: ((exception: Exception?) -> Unit)? = null
    )

    /**
     * stop playback
     */
    fun stop()

}