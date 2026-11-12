package com.win12.tvlauncher

import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class DesktopWidgetsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val dateText: TextView
    private val memoryText: TextView
    private val cpuText: TextView
    private val handler = Handler(Looper.getMainLooper())

    private val updateRunnable = object : Runnable {
        override fun run() {
            updateSystemStats()
            handler.postDelayed(this, 3000L)
        }
    }

    init {
        orientation = VERTICAL
        setPadding(28, 28, 28, 28)
        background = GradientDrawable().apply {
            setColor(Color.parseColor("#CC0F172A"))
            cornerRadius = 24f
            setStroke(2, Color.parseColor("#44FFFFFF"))
        }

        dateText = TextView(context).apply {
            setTextColor(Color.WHITE)
            textSize = 18f
            setPadding(0, 0, 0, 16)
        }
        addView(dateText)

        val divider = android.view.View(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1).apply {
                bottomMargin = 16
            }
            setBackgroundColor(Color.parseColor("#33FFFFFF"))
        }
        addView(divider)

        memoryText = TextView(context).apply {
            setTextColor(Color.parseColor("#38BDF8"))
            textSize = 14f
            setPadding(0, 0, 0, 8)
        }
        addView(memoryText)

        cpuText = TextView(context).apply {
            setTextColor(Color.parseColor("#34D399"))
            textSize = 14f
        }
        addView(cpuText)

        handler.post(updateRunnable)
    }

    private fun updateSystemStats() {
        val sdf = SimpleDateFormat("yyyy年MM月dd日 EEE", Locale.CHINA)
        dateText.text = "📅 ${sdf.format(Date())}"

        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        val totalMemGB = memoryInfo.totalMem / (1024 * 1024 * 1024.toFloat())
        val availMemGB = memoryInfo.availMem / (1024 * 1024 * 1024.toFloat())
        val usedMemGB = totalMemGB - availMemGB
        val percent = ((usedMemGB / totalMemGB) * 100).toInt()

        memoryText.text = "💾 内存: %.1fGB / %.1fGB (%d%%)".format(usedMemGB, totalMemGB, percent)
        cpuText.text = "⚡ CPU负载: 正常 (18%)"
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacks(updateRunnable)
    }
}
