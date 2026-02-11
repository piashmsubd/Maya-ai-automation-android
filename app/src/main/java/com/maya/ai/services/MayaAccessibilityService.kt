package com.maya.ai.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MayaAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(Dispatchers.Main)
    
    companion object {
        private var instance: MayaAccessibilityService? = null
        
        fun getInstance(): MayaAccessibilityService? = instance
        
        private val _events = MutableSharedFlow<AccessibilityEvent>(replay = 0)
        val events: SharedFlow<AccessibilityEvent> = _events
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            scope.launch {
                _events.emit(it)
            }
        }
    }

    override fun onInterrupt() {
        // Handle interruption
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    // UI Automation Functions

    /**
     * Click on a node with specific text
     */
    fun clickOnText(text: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val targetNode = findNodeByText(rootNode, text)
        return targetNode?.performAction(AccessibilityNodeInfo.ACTION_CLICK) ?: false
    }

    /**
     * Click on a specific coordinate
     */
    fun clickAt(x: Float, y: Float): Boolean {
        val path = Path().apply {
            moveTo(x, y)
        }
        
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()
        
        return dispatchGesture(gesture, null, null)
    }

    /**
     * Type text in the focused input field
     */
    fun typeText(text: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val focusedNode = findFocusedNode(rootNode) ?: return false
        
        val arguments = Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        }
        
        return focusedNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
    }

    /**
     * Scroll down
     */
    fun scrollDown(): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val scrollableNode = findScrollableNode(rootNode) ?: return false
        return scrollableNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
    }

    /**
     * Scroll up
     */
    fun scrollUp(): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val scrollableNode = findScrollableNode(rootNode) ?: return false
        return scrollableNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
    }

    /**
     * Swipe gesture
     */
    fun swipe(startX: Float, startY: Float, endX: Float, endY: Float, duration: Long = 300): Boolean {
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }
        
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, duration))
            .build()
        
        return dispatchGesture(gesture, null, null)
    }

    /**
     * Go back
     */
    fun goBack(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_BACK)
    }

    /**
     * Go home
     */
    fun goHome(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_HOME)
    }

    /**
     * Open recent apps
     */
    fun openRecents(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_RECENTS)
    }

    /**
     * Open notifications
     */
    fun openNotifications(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
    }

    /**
     * Open quick settings
     */
    fun openQuickSettings(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
    }

    /**
     * Get all text on screen
     */
    fun getScreenText(): String {
        val rootNode = rootInActiveWindow ?: return ""
        val textBuilder = StringBuilder()
        extractText(rootNode, textBuilder)
        return textBuilder.toString()
    }

    /**
     * Find node containing specific text
     */
    private fun findNodeByText(node: AccessibilityNodeInfo?, text: String): AccessibilityNodeInfo? {
        if (node == null) return null
        
        if (node.text?.toString()?.contains(text, ignoreCase = true) == true ||
            node.contentDescription?.toString()?.contains(text, ignoreCase = true) == true) {
            return node
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            val result = findNodeByText(child, text)
            if (result != null) return result
        }
        
        return null
    }

    /**
     * Find the currently focused node
     */
    private fun findFocusedNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        
        if (node.isFocused) return node
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            val result = findFocusedNode(child)
            if (result != null) return result
        }
        
        return null
    }

    /**
     * Find a scrollable node
     */
    private fun findScrollableNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        
        if (node.isScrollable) return node
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            val result = findScrollableNode(child)
            if (result != null) return result
        }
        
        return null
    }

    /**
     * Recursively extract all text from the UI tree
     */
    private fun extractText(node: AccessibilityNodeInfo?, textBuilder: StringBuilder) {
        if (node == null) return
        
        node.text?.let {
            if (it.isNotBlank()) {
                textBuilder.append(it).append(" ")
            }
        }
        
        node.contentDescription?.let {
            if (it.isNotBlank()) {
                textBuilder.append(it).append(" ")
            }
        }
        
        for (i in 0 until node.childCount) {
            extractText(node.getChild(i), textBuilder)
        }
    }

    /**
     * Open an app by package name
     */
    fun openApp(packageName: String): Boolean {
        val pm = packageManager
        val intent = pm.getLaunchIntentForPackage(packageName)
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(it)
            return true
        }
        return false
    }
}
