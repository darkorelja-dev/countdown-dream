package com.example.countdowndream

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import java.time.ZoneId
import java.time.ZonedDateTime

class MainActivity : Activity() {

    private val targetMillis: Long = ZonedDateTime
        .of(2026, 10, 25, 7, 0, 0, 0, ZoneId.of("Europe/Belgrade"))
        .toInstant()
        .toEpochMilli()

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeView: TextView

    private val tick = object : Runnable {
        override fun run() {
            update()
            val delay = 1000 - (System.currentTimeMillis() % 1000)
            handler.postDelayed(this, delay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val lp = window.attributes
        lp.screenBrightness = 0.15f
        window.attributes = lp

        timeView = TextView(this).apply {
            setTextColor(Color.WHITE)
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

    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        handler.post(tick)
    }

    override fun onPause() {
        handler.removeCallbacks(tick)
        super.onPause()
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
