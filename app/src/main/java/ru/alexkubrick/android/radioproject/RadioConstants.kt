package ru.alexkubrick.android.radioproject

object RadioConstants {
    object ACTION {
        const val STARTFOREGROUND_ACTION = "ru.alexkubrick.android.radioproject.action.START_FOREGROUND"
        const val STOPFOREGROUND_ACTION = "ru.alexkubrick.android.radioproject.action.STOP_FOREGROUND"
    }

    const val NOTIFICATION_CHANNEL_ID = "radioplayer_channel"
    const val NOTIFICATION_ID = 1
    const val RADIOURL = "https://bookradio.hostingradio.ru:8069/fm" //   audio/mpeg
}