package org.rhasspy.mobile.services.dialogManager

enum class DialogManagerServiceState {
    Idle,                   //doing nothing, hotword from externally awaited
    AwaitingHotWord,        //recording HotWord
    RecordingIntent,        //recording the intent
    TranscribingIntent,     //transcribe the recorded sound to text
    RecognizingIntent,      //recognize the intent from the recorded text
    HandlingIntent,          //doing intent action
    PlayingAudio
}