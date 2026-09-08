package com.example.ui.util

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

object SoundNotificationHelper {
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
        } catch (e: Throwable) {
            Log.w("SoundNotificationHelper", "Could not initialize ToneGenerator", e)
        }
    }

    /**
     * Tone for when a new order is received at Kitchen or submitted by customer
     */
    fun playNewOrderSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 220)
        } catch (e: Throwable) {
            Log.w("SoundNotificationHelper", "Could not play order sound", e)
        }
    }

    /**
     * Alert tone for when customer requests table service (water, napkins, staff)
     */
    fun playServiceCallBell() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 350)
        } catch (e: Throwable) {
            Log.w("SoundNotificationHelper", "Could not play service call tone", e)
        }
    }

    /**
     * Positive confirmation tone for payment, voucher claim, or order status advance
     */
    fun playSuccessChime() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 180)
        } catch (e: Throwable) {
            Log.w("SoundNotificationHelper", "Could not play chime tone", e)
        }
    }
}
