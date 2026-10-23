package com.example.countdowndream

import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.service.dreams.DreamService
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import java.time.ZoneId
import java.time.ZonedDateTime

class CountdownDream : DreamService() {

    // Nedelja, 25.10.2026. u 07:00 po lokalnom (beogradskom) vremenu.
    // Računa se stvarni broj proteklih sekundi, pa je ispravno i kod promene na zimsko vreme.
    private val targetMillis: Long = ZonedDateTime
        .of(2026, 10, 25, 7, 0, 0, 0, ZoneId.of("Europe/Belgrade"))
        .toInstant()
        .toEpochMilli()

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeView: TextView

    private val tick = object : Runnable {
        override fun run() {
            update()
            // Sledeće osvežavanje tačno na početku sledeće sekunde
            val delay = 1000 - (System.currentTimeMillis() % 1000)
            handler.postDelayed(this, delay)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = false   // bilo koji dodir gasi screen saver
        isFullscreen = true
        isScreenBright = false  // prigušen ekran

        timeView = TextView(this).apply {
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 72f)
            typeface = Typeface.MONOSPACE
            gravity = Gravity.CENTER
            maxLines = 1
            setAutoSizeTextTypeUniformWithConfiguration(
                20, 200, 2, TypedValue.COMPLEX_UNIT_SP
            )
        }
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
            setPadding(32, 32, 32, 32)
            addView(
                timeView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        }
        setContentView(root)
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        handler.post(tick)
    }

    override fun onDreamingStopped() {
        handler.removeCallbacks(tick)
        super.onDreamingStopped()
    }

    private fun update() {
        var remaining = (targetMillis - System.currentTimeMillis()) / 1000
        if (remaining < 0) remaining = 0
        val h = remaining / 3600
        val m = (remaining % 3600) / 60
        val s = remaining % 60
        timeView.text = String.format("%02d:%02d:%02d", h, m, s)
    }
}
