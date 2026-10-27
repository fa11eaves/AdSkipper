package com.example.adskipper

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class SkipAdService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val pkgName = event.packageName?.toString() ?: return

        if (RuleManager.excludedPackages.contains(pkgName)) return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            RuleManager.onWindowChanged(pkgName)
        }

        if (!RuleManager.isWithinActiveWindow()) return

        val rootNode = rootInActiveWindow ?: return
        scanAndExecute(rootNode)
    }

    private fun scanAndExecute(rootNode: AccessibilityNodeInfo) {
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(rootNode)

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            val text = (node.text ?: node.contentDescription)?.toString()?.trim()
            if (!text.isNullOrEmpty() && text.length <= 10) {
                if (RuleManager.defaultKeywords.any { it.matches(text) }) {
                    if (clickNodeOrGesture(node)) break
                }
            }
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }
    }

    private fun clickNodeOrGesture(node: AccessibilityNodeInfo): Boolean {
        var current: AccessibilityNodeInfo? = node
        while (current != null) {
            if (current.isClickable) {
                return current.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            current = current.parent
        }

        val rect = Rect()
        node.getBoundsInScreen(rect)
        if (rect.width() > 0 && rect.height() > 0) {
            val path = Path().apply { moveTo(rect.centerX().toFloat(), rect.centerY().toFloat()) }
            val stroke = GestureDescription.StrokeDescription(path, 0, 45)
            val gesture = GestureDescription.Builder().addStroke(stroke).build()
            return dispatchGesture(gesture, null, null)
        }
        return false
    }

    override fun onInterrupt() {}
}
