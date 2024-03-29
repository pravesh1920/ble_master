package com.troido.hexinput.ui.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Button
import android.widget.LinearLayout
import com.troido.hexinput.KeyboardManager
import com.troido.hexinput.R

/**
 * Represents a keyboard specialised for input of hexadecimal content. Contains buttons for all hex
 * values and a remove button, arranged in a grid, just like in a classic keyboard. Layout of buttons
 * is different between portrait and landscape orientation in order to utilize all available space
 * on screen (so that there are no blank parts of screen that are reserved for the view
 * or so that the buttons are not to wide).
 */
class HexKeyboardView(context : Context, attributeSet: AttributeSet?) : BaseHexKeyboardView(context, attributeSet) {
    private var charButtonsMap = mutableMapOf(
        R.id.button_0 to '0',
        R.id.button_1 to '1',
        R.id.button_2 to '2',
        R.id.button_3 to '3',
        R.id.button_4 to '4',
        R.id.button_5 to '5',
        R.id.button_6 to '6',
        R.id.button_7 to '7',
        R.id.button_8 to '8',
        R.id.button_9 to '9',
        R.id.button_a to 'A',
        R.id.button_b to 'B',
        R.id.button_c to 'C',
        R.id.button_d to 'D',
        R.id.button_e to 'E',
        R.id.button_f to 'F'
    )
    private var backspaceButtonId = R.id.button_backspace
    private var hideButtonId = R.id.button_hide
    private var llKeyboardId = R.id.ll_keyboard

    init {
        inflate(context,R.layout.hex_keyboard,this)
        setupKeyboard()
    }

    constructor(context: Context) : this(context,null)

    @SuppressLint("ClickableViewAccessibility")
    private fun setupKeyboard() {
        charButtonsMap.forEach {
            val button = findViewById<Button>(it.key)
            button?.setOnClickListener { _ -> notifyValueTyped(it.value) }
        }

        val backspaceButton = findViewById<Button>(backspaceButtonId)
        backspaceButton.setOnLongClickListener {
            notifyRemoveKeyLongPress()
            true
        }
        backspaceButton.setOnTouchListener { _, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> notifyRemoveKeyDown()
                MotionEvent.ACTION_UP -> notifyRemoveKeyUp()
            }
            false
        }

        val hideButton = findViewById<Button>(hideButtonId)
        val keyboardLayout = findViewById<LinearLayout>(llKeyboardId)
        hideButton.setOnClickListener {
            KeyboardManager.hideHexKeyboard(keyboardLayout)
        }
    }



}