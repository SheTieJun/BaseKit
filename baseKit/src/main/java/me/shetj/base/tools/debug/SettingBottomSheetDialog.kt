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


package me.shetj.base.tools.debug

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import com.google.android.material.bottomsheet.BottomSheetDialog
import me.shetj.base.R
import me.shetj.base.ktx.showToast

class SettingBottomSheetDialog(private val mContext: Context) {

    private val bottomSheetDialog: BottomSheetDialog?

    init {
        this.bottomSheetDialog = buildBottomSheetDialog()
    }

    private fun buildBottomSheetDialog(): BottomSheetDialog {
        val bottomSheetDialog = BottomSheetDialog(mContext, R.style.transparent_bottom_dialog_style)
        val rootView = LayoutInflater.from(mContext).inflate(R.layout.base_debug_setting, null)

        rootView.findViewById<CheckBox>(R.id.check_http).apply {
            isChecked = DebugFunc.getInstance().getHttpSetting()
        }.setOnCheckedChangeListener { _, isChecked ->
            DebugFunc.getInstance().setIsOutputHttp(isChecked)
        }
        rootView.findViewById<CheckBox>(R.id.check_log).apply {
            isChecked = DebugFunc.getInstance().getLogSetting()
        }.setOnCheckedChangeListener { _, isChecked ->
            DebugFunc.getInstance().setIsOutputLog(isChecked)
        }
        rootView.findViewById<Button>(R.id.btn_clean).setOnClickListener {
            DebugFunc.getInstance().clearAll()
            "已清除日志信息".showToast()
            dismissBottomSheet()
        }
        bottomSheetDialog.setContentView(rootView)
        return bottomSheetDialog
    }

    fun showBottomSheet() {
        if (bottomSheetDialog != null && !bottomSheetDialog.isShowing) bottomSheetDialog.show()
    }

    private fun dismissBottomSheet() {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing) bottomSheetDialog.dismiss()
    }


}
