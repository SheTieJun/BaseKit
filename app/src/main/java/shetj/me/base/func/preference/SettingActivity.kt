package shetj.me.base.func.preference

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


    override fun addObservers() {
        super.addObservers()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        val name = sharedPreferences.getString("signature", "");
        name.logI("name")


        sharedPreferences.registerOnSharedPreferenceChangeListener { preferences, key ->
            // do something
            when (key) {
                "signature" -> {
                    preferences.getString("signature", "").logI("name")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}