package com.win12.tvlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var rootContainer: FrameLayout
    private lateinit var windowContainer: FrameLayout
    private lateinit var startMenuPanel: LinearLayout
    private lateinit var taskbar: LinearLayout
    private lateinit var cursorView: CursorOverlayView
    private lateinit var widgetsView: DesktopWidgetsView
    private lateinit var clockText: TextView
    private val handler = Handler(Looper.getMainLooper())

    private var moveDx = 0f
    private var moveDy = 0f
    private var currentSpeed = 14f
    private val maxSpeed = 42f
    private var isMoving = false
    private var cursorModeEnabled = true
    private var isDragLocked = false
    private var isScrollModeActive = false
    private var downTimeMillis: Long = 0

    private val moveRunnable = object : Runnable {
        override fun run() {
            if (isMoving) {
                cursorView.updatePosition(
                    cursorView.cursorX + moveDx * currentSpeed,
                    cursorView.cursorY + moveDy * currentSpeed
                )
                if (currentSpeed < maxSpeed) {
                    currentSpeed += 1.8f
                }
                if (isDragLocked) {
                    simulateTouchMove(cursorView.cursorX, cursorView.cursorY)
                }
                handler.postDelayed(this, 16L)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupWin12Desktop()
        startClockUpdater()
    }

    private fun setupWin12Desktop() {
        rootContainer = FrameLayout(this).apply {
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    Color.parseColor("#0F172A"),
                    Color.parseColor("#1E3A8A"),
                    Color.parseColor("#090D16")
                )
            )
        }

        val desktopIcons = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }
        desktopIcons.addView(createDesktopShortcut("💻 应用中心", "#38BDF8") {
            openAppExplorerWindow()
        })
        desktopIcons.addView(createDesktopShortcut("🌐 浏览器", "#818CF8") {
            openBrowserWindow()
        })
        desktopIcons.addView(createDesktopShortcut("🎨 个性化主题", "#C084FC") {
            openThemeSettingsWindow()
        })
        desktopIcons.addView(createDesktopShortcut("⚙️ 系统设置", "#94A3B8") {
            startActivity(Intent(Settings.ACTION_SETTINGS))
        })
        rootContainer.addView(desktopIcons)

        windowContainer = FrameLayout(this)
        rootContainer.addView(windowContainer)

        startMenuPanel = buildStartMenu().apply { visibility = View.GONE }
        rootContainer.addView(startMenuPanel)

        taskbar = buildTaskbar()
        rootContainer.addView(taskbar)

        widgetsView = DesktopWidgetsView(this).apply {
            layoutParams = FrameLayout.LayoutParams(320, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.TOP or Gravity.END
                setMargins(0, 48, 48, 0)
            }
        }
        rootContainer.addView(widgetsView)

        cursorView = CursorOverlayView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        rootContainer.addView(cursorView)

        val lockManager = LockScreenManager(this) {
            // unlocked
        }
        rootContainer.addView(lockManager.lockOverlay)

        setContentView(rootContainer)
    }

    private fun createDesktopShortcut(label: String, colorHex: String, onClick: () -> Unit): TextView {
        return TextView(this).apply {
            text = label
            setTextColor(Color.WHITE)
            textSize = 16f
            setPadding(24, 18, 24, 18)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#331E293B"))
                cornerRadius = 16f
                setStroke(1, Color.parseColor(colorHex))
            }
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 0, 0, 20)
            layoutParams = lp
            setOnClickListener { onClick() }
        }
    }

    private fun buildTaskbar(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#CC1E293B"))
                cornerRadius = 28f
            }
            setPadding(20, 12, 20, 12)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                setMargins(0, 0, 0, 36)
            }

            val startBtn = TextView(this@MainActivity).apply {
                text = "⊞ 开始"
                setTextColor(Color.WHITE)
                textSize = 15f
                setPadding(20, 10, 20, 10)
                background = GradientDrawable().apply {
                    setColor(Color.parseColor("#3340C4FF"))
                    cornerRadius = 16f
                }
                setOnClickListener {
                    startMenuPanel.visibility =
                        if (startMenuPanel.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                }
            }
            addView(startBtn)

            clockText = TextView(this@MainActivity).apply {
                setTextColor(Color.parseColor("#E0F2FE"))
                textSize = 14f
                setPadding(24, 0, 0, 0)
            }
            addView(clockText)
        }
    }

    private fun buildStartMenu(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 28, 28, 28)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#EE0F172A"))
                cornerRadius = 24f
                setStroke(2, Color.parseColor("#44FFFFFF"))
            }
            layoutParams = FrameLayout.LayoutParams(420, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                setMargins(0, 0, 0, 110)
            }

            val title = TextView(this@MainActivity).apply {
                text = "Win12 开始菜单"
                setTextColor(Color.WHITE)
                textSize = 18f
                setPadding(0, 0, 0, 16)
            }
            addView(title)

            listOf(
                "💻 所有应用" to { openAppExplorerWindow() },
                "🌐 浏览器" to { openBrowserWindow() },
                "🎨 主题" to { openThemeSettingsWindow() },
                "⚙️ 设置" to { startActivity(Intent(Settings.ACTION_SETTINGS)) }
            ).forEach { (label, action) ->
                val item = TextView(this@MainActivity).apply {
                    text = label
                    setTextColor(Color.parseColor("#E2E8F0"))
                    textSize = 15f
                    setPadding(16, 14, 16, 14)
                    setOnClickListener {
                        startMenuPanel.visibility = View.GONE
                        action()
                    }
                }
                addView(item)
            }
        }
    }

    private fun openAppExplorerWindow() {
        val window = createFloatingWindow("应用中心")
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val apps: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)
        apps.take(20).forEach { ri ->
            val label = ri.loadLabel(pm).toString()
            val row = TextView(this).apply {
                text = "📱 $label"
                setTextColor(Color.WHITE)
                textSize = 14f
                setPadding(12, 10, 12, 10)
                setOnClickListener {
                    val launch = pm.getLaunchIntentForPackage(ri.activityInfo.packageName)
                    if (launch != null) startActivity(launch)
                }
            }
            list.addView(row)
        }
        window.addView(list)
        windowContainer.addView(window)
    }

    private fun openBrowserWindow() {
        val window = createFloatingWindow("浏览器")
        val info = TextView(this).apply {
            text = "提示：可用虚拟鼠标在网页中点击与滚动。\n（当前为演示窗口，可接入 WebView）"
            setTextColor(Color.parseColor("#94A3B8"))
            textSize = 14f
            setPadding(16, 16, 16, 16)
        }
        window.addView(info)
        windowContainer.addView(window)
    }

    private fun openThemeSettingsWindow() {
        val window = createFloatingWindow("个性化主题")
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        ThemeManager.themes.forEach { theme ->
            val item = TextView(this).apply {
                text = theme.name
                setTextColor(Color.WHITE)
                textSize = 15f
                setPadding(16, 14, 16, 14)
                setOnClickListener {
                    ThemeManager.applyTheme(rootContainer, theme)
                    Toast.makeText(this@MainActivity, "已应用: ${theme.name}", Toast.LENGTH_SHORT).show()
                }
            }
            list.addView(item)
        }
        window.addView(list)
        windowContainer.addView(window)
    }

    private fun createFloatingWindow(title: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#F01E293B"))
                cornerRadius = 20f
                setStroke(2, Color.parseColor("#66FFFFFF"))
            }
            setPadding(20, 16, 20, 20)
            layoutParams = FrameLayout.LayoutParams(560, 420).apply {
                gravity = Gravity.CENTER
            }

            val header = LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            val titleView = TextView(this@MainActivity).apply {
                text = title
                setTextColor(Color.WHITE)
                textSize = 16f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            header.addView(titleView)
            val closeBtn = TextView(this@MainActivity).apply {
                text = "✕"
                setTextColor(Color.parseColor("#F87171"))
                textSize = 18f
                setPadding(16, 4, 8, 4)
                setOnClickListener {
                    windowContainer.removeView(this@apply)
                }
            }
            header.addView(closeBtn)
            addView(header)
        }
    }

    private fun startClockUpdater() {
        val runnable = object : Runnable {
            override fun run() {
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                clockText.text = sdf.format(Date())
                handler.postDelayed(this, 1000L)
            }
        }
        handler.post(runnable)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (!cursorModeEnabled) return super.onKeyDown(keyCode, event)

        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                moveDx = -1f; moveDy = 0f; startMoving(); return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                moveDx = 1f; moveDy = 0f; startMoving(); return true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                moveDx = 0f; moveDy = -1f; startMoving(); return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                moveDx = 0f; moveDy = 1f; startMoving(); return true
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                if (isScrollModeActive) {
                    // scroll handled elsewhere
                } else if (isDragLocked) {
                    isDragLocked = false
                    simulateTouchUp(cursorView.cursorX, cursorView.cursorY)
                } else {
                    cursorView.triggerClickEffect()
                    simulateTouchClick(cursorView.cursorX, cursorView.cursorY)
                }
                return true
            }
            KeyEvent.KEYCODE_MENU -> {
                isScrollModeActive = !isScrollModeActive
                Toast.makeText(this, if (isScrollModeActive) "滚动模式" else "点击模式", Toast.LENGTH_SHORT).show()
                return true
            }
            KeyEvent.KEYCODE_BACK -> {
                if (startMenuPanel.visibility == View.VISIBLE) {
                    startMenuPanel.visibility = View.GONE
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> {
                stopMoving()
                return true
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                if (event != null && event.isLongPress) {
                    isDragLocked = true
                    downTimeMillis = SystemClock.uptimeMillis()
                    simulateTouchDown(cursorView.cursorX, cursorView.cursorY)
                }
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun startMoving() {
        if (!isMoving) {
            isMoving = true
            currentSpeed = 14f
            handler.post(moveRunnable)
        }
    }

    private fun stopMoving() {
        isMoving = false
        handler.removeCallbacks(moveRunnable)
        currentSpeed = 14f
    }

    private fun simulateTouchClick(x: Float, y: Float) {
        val time = SystemClock.uptimeMillis()
        val down = MotionEvent.obtain(time, time, MotionEvent.ACTION_DOWN, x, y, 0)
        val up = MotionEvent.obtain(time, time + 50, MotionEvent.ACTION_UP, x, y, 0)
        rootContainer.dispatchTouchEvent(down)
        rootContainer.dispatchTouchEvent(up)
        down.recycle()
        up.recycle()
    }

    private fun simulateTouchDown(x: Float, y: Float) {
        val time = SystemClock.uptimeMillis()
        val down = MotionEvent.obtain(time, time, MotionEvent.ACTION_DOWN, x, y, 0)
        rootContainer.dispatchTouchEvent(down)
        down.recycle()
    }

    private fun simulateTouchMove(x: Float, y: Float) {
        val time = SystemClock.uptimeMillis()
        val move = MotionEvent.obtain(downTimeMillis, time, MotionEvent.ACTION_MOVE, x, y, 0)
        rootContainer.dispatchTouchEvent(move)
        move.recycle()
    }

    private fun simulateTouchUp(x: Float, y: Float) {
        val time = SystemClock.uptimeMillis()
        val up = MotionEvent.obtain(downTimeMillis, time, MotionEvent.ACTION_UP, x, y, 0)
        rootContainer.dispatchTouchEvent(up)
        up.recycle()
    }

    private fun dispatchScrollEvent(x: Float, y: Float, scrollDelta: Int) {
        val time = SystemClock.uptimeMillis()
        val scrollEvent = MotionEvent.obtain(time, time, MotionEvent.ACTION_SCROLL, x, y, 0f, scrollDelta.toFloat(), 0)
        rootContainer.dispatchTouchEvent(scrollEvent)
        scrollEvent.recycle()
    }
}
