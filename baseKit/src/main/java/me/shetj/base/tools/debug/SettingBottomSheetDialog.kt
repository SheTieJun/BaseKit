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
