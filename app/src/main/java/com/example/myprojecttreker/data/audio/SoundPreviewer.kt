package com.example.myprojecttreker.data.audio

import android.content.Context
import android.media.MediaPlayer

class SoundPreviewer(private val context: Context) {
    private var player: MediaPlayer? = null

    fun play(sound: AppSound) {
        stop()
        player = MediaPlayer.create(context, sound.rawRes)?.apply {
            setOnCompletionListener { release(); player = null }
            start()
        }
    }

    fun stop() {
        player?.runCatching { stop() }
        player?.release()
        player = null
    }
}
