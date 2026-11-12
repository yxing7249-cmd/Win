package com.win12.tvlauncher

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class LockScreenManager(private val context: Context, private val onUnlocked: () -> Unit) {

    private val targetPin = "1234"
    private val inputPin = ""
    private lateinit var pinDisplay: TextView
    lateinit var lockOverlay: LinearLayout
    private var currentInput = ""

    init {
        buildLockView()
    }

    private fun buildLockView() {
        lockOverlay = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(Color.parseColor("#EE0F172A"), Color.parseColor("#EE1E3A8A"))
            )
            setPadding(48, 48, 48, 48)
        }

        val title = TextView(context).apply {
            text = "🔒 Win12 家长控制锁屏"
            setTextColor(Color.WHITE)
            textSize = 22f
            setPadding(0, 0, 0, 12)
        }
        lockOverlay.addView(title)

        val subtitle = TextView(context).apply {
            text = "请输入 4 位访问密码 (默认: 1234)"
            setTextColor(Color.parseColor("#94A3B8"))
            textSize = 15f
            setPadding(0, 0, 0, 24)
        }
        lockOverlay.addView(subtitle)

        pinDisplay = TextView(context).apply {
            text = "----"
            setTextColor(Color.parseColor("#38BDF8"))
            textSize = 32f
            setPadding(0, 0, 0, 32)
        }
        lockOverlay.addView(pinDisplay)

        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("C", "0", "OK")
        )

        for (rowNums in rows) {
            val rowLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                setPadding(0, 8, 0, 8)
            }
            for (num in rowNums) {
                val btn = TextView(context).apply {
                    text = num
                    setTextColor(Color.WHITE)
                    textSize = 18f
                    gravity = Gravity.CENTER
                    background = GradientDrawable().apply {
                        setColor(Color.parseColor("#331E293B"))
                        cornerRadius = 16f
                        setStroke(1, Color.parseColor("#55FFFFFF"))
                    }
                    layoutParams = LinearLayout.LayoutParams(110, 80).apply {
                        setMargins(10, 0, 10, 0)
                    }
                    setOnClickListener {
                        handleKeyClick(num)
                    }
                }
                rowLayout.addView(btn)
            }
            lockOverlay.addView(rowLayout)
        }
    }

    private fun handleKeyClick(num: String) {
        when (num) {
            "C" -> {
                currentInput = ""
            }
            "OK" -> {
                if (currentInput == targetPin) {
                    lockOverlay.visibility = android.view.View.GONE
                    onUnlocked()
                    Toast.makeText(context, "解锁成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show()
                    currentInput = ""
                }
            }
            else -> {
                if (currentInput.length < 4) {
                    currentInput += num
                }
            }
        }
        val mask = "*".repeat(currentInput.length) + "-".repeat(4 - currentInput.length)
        pinDisplay.text = mask
    }
}
