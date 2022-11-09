package org.rhasspy.mobile.viewModels.settings.sound

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.AudioPlayer
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.sounds.SoundFile
import org.rhasspy.mobile.settings.sounds.SoundOptions

class RecordedIndicationSoundSettingsViewModel : ViewModel(), IIndicationSoundSettingsViewModel {

    override val isSoundIndicationDefault: StateFlow<Boolean> = AppSettings.recordedSound.data.mapReadonlyState {
        it == SoundOptions.Default.name
    }

    override val isSoundIndicationDisabled: StateFlow<Boolean> = AppSettings.recordedSound.data.mapReadonlyState {
        it == SoundOptions.Disabled.name
    }

    override val customSoundFiles: StateFlow<List<SoundFile>> =
        combineState(AppSettings.recordedSound.data, AppSettings.customRecordedSounds.data) { selected, set ->
            set.map { fileName ->
                SoundFile(fileName, selected == fileName, selected != fileName)
            }.toList()
        }

    override val soundVolume: StateFlow<Float> = AppSettings.recordedSoundVolume.data

    override fun onClickSoundIndicationDefault() {
        AppSettings.recordedSound.value = SoundOptions.Default.name
    }

    override fun onClickSoundIndicationDisabled() {
        AppSettings.recordedSound.value = SoundOptions.Disabled.name
    }

    override fun updateSoundVolume(volume: Float) {
        AppSettings.recordedSoundVolume.value = volume
    }

    override fun selectSoundFile(file: SoundFile) {
        AppSettings.recordedSound.value = file.fileName
    }

    override fun deleteSoundFile(file: SoundFile) {
        if (file.canBeDeleted && !file.selected) {
            val customSounds = AppSettings.customRecordedSounds.data
            AppSettings.customRecordedSounds.value = customSounds.value.toMutableSet().apply {
                remove(file.fileName)
            }
            SettingsUtils.removeSoundFile(subfolder = "recorded", file.fileName)
        }
    }

    override fun playSoundFile() {
        when (AppSettings.recordedSound.value) {
            SoundOptions.Disabled.name -> {}
            SoundOptions.Default.name -> AudioPlayer.playSoundFileResource(MR.files.etc_wav_beep_lo, AppSettings.recordedSoundVolume.value)
            else -> AudioPlayer.playSoundFile("recorded", AppSettings.recordedSound.value, AppSettings.recordedSoundVolume.value)
        }
    }

    override fun chooseSoundFile() {
        SettingsUtils.selectSoundFile(subfolder = "recorded") { fileName ->
            fileName?.also {
                val customSounds = AppSettings.customRecordedSounds.data
                AppSettings.customRecordedSounds.value = customSounds.value.toMutableSet().apply {
                    add(it)
                }
                AppSettings.recordedSound.value = it
            }
        }
    }

}