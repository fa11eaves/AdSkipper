package com.example.adskipper

import android.os.SystemClock

object RuleManager {
    private var currentForegroundPackage = ""
    private var appLaunchTimestamp = 0L
    private const val ACTIVE_WINDOW_MS = 5500L

    val defaultKeywords = listOf(
        "^跳过$".toRegex(),
        "^跳过[\\s\\d秒sS\\|]*$".toRegex(),
        "^[\\d秒sS\\s]*跳过$".toRegex(),
        "^[sS]kip$".toRegex()
    )

    val excludedPackages = setOf(
        "com.android.systemui",
        "com.miui.home",
        "com.miui.securitycenter",
        "com.android.settings",
        "com.example.adskipper"
    )

    fun onWindowChanged(pkg: String) {
        if (pkg != currentForegroundPackage && !excludedPackages.contains(pkg)) {
            currentForegroundPackage = pkg
            appLaunchTimestamp = SystemClock.uptimeMillis()
        }
    }

    fun isWithinActiveWindow(): Boolean {
        return (SystemClock.uptimeMillis() - appLaunchTimestamp) < ACTIVE_WINDOW_MS
    }
}
