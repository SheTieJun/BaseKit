/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package shetj.me.base.view

import android.content.Context
import android.text.Editable
import android.text.Selection
import android.text.Spannable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.Toast
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatEditText
import me.shetj.base.tools.file.StringUtils.Companion.containsEmoji

@Keep
class ContainsEmojiEditText : AppCompatEditText {
    //输入表情前的光标位置
    private var cursorPos = 0

    //输入表情前EditText中的文本
    private var inputAfterText: String? = null

    //是否重置了EditText的内容
    private var resetText = false
    private var mContext: Context

    constructor(context: Context) : super(context) {
        mContext = context
        initEditText()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        initEditText()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        initEditText()
    }

    private fun initEditText() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!resetText) {
                    cursorPos = selectionEnd
                    inputAfterText = s.toString()
                }
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!resetText) {
                    if (count - before >= 2) {
                        val input = s.subSequence(start + before, start + count)
                        if (containsEmoji(input.toString())) {
                            resetText = true
                            Toast.makeText(mContext, "请不要使用特殊符号表情！", Toast.LENGTH_SHORT).show()
                            setText(inputAfterText)
                            val text: CharSequence? = text
                            if (text != null) {
                                val spanText = text as Spannable
                                Selection.setSelection(spanText, text.length)
                            }
                        }
                    }
                } else {
                    resetText = false
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }
}