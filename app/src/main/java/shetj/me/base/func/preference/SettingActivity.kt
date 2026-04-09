package shetj.me.base.func.preference

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import me.shetj.base.ktx.logI
import me.shetj.base.mvvm.viewbind.BaseBindingActivity
import me.shetj.base.network.sse.SseClient
import shetj.me.base.R
import shetj.me.base.databinding.ActivitySettingBinding


/**
 *
 * ```
 * SharedPreferences sharedPreferences =
 *         PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
 * String name = sharedPreferences.getString("signature", "");
 * ```
 */
class SettingActivity : BaseBindingActivity<ActivitySettingBinding, SettingViewModel>() {

    override fun initBaseView() {
        super.initBaseView()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingFragment())
            .commit()

    }


    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { preferences, key ->
        // do something
        when (key) {
            "signature" -> {
                preferences.getString("signature", "").logI("name")
            }
        }
    }

    override fun addObservers() {
        super.addObservers()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        val name = sharedPreferences.getString("signature", "");
        name.logI("name")


        // TODO Fix: 必须使用强引用保持 OnSharedPreferenceChangeListener，否则会被 WeakHashMap 回收导致监听失效
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}