package com.win12.tvlauncher

import android.graphics.Color
import android.graphics.drawable.GradientDrawable

object ThemeManager {

    data class DesktopTheme(
        val name: String,
        val colors: IntArray,
        val accentColor: String,
        val taskbarColor: String
    )

    val themes = listOf(
        DesktopTheme(
            name = "🌌 Win12 经典深空蓝",
            colors = intArrayOf(
                Color.parseColor("#0F172A"),
                Color.parseColor("#1E3A8A"),
                Color.parseColor("#090D16")
            ),
            accentColor = "#38BDF8",
            taskbarColor = "#CC1E293B"
        ),
        DesktopTheme(
            name = "💜 赛博朋克紫",
            colors = intArrayOf(
                Color.parseColor("#180E2B"),
                Color.parseColor("#4C1D95"),
                Color.parseColor("#0F071E")
            ),
            accentColor = "#C084FC",
            taskbarColor = "#CC2E1065"
        ),
        DesktopTheme(
            name = "🌿 极光翡翠绿",
            colors = intArrayOf(
                Color.parseColor("#061A14"),
                Color.parseColor("#065F46"),
                Color.parseColor("#02100C")
            ),
            accentColor = "#34D399",
            taskbarColor = "#CC064E3B"
        )
    )

    fun applyTheme(rootView: android.view.View, theme: DesktopTheme) {
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            theme.colors
        )
        rootView.background = gradient
    }
}
