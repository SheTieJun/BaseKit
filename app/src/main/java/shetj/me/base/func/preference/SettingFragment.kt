package shetj.me.base.func.preference

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import shetj.me.base.R

/**
 *
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2024/2/28<br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b>  <br>
 */
class SettingFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}