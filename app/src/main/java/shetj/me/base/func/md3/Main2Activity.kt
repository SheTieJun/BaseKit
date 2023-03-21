package shetj.me.base.func.md3

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import me.shetj.base.base.AbBindingActivity
import shetj.me.base.R
import shetj.me.base.databinding.ActivityMain2Binding

class Main2Activity : AbBindingActivity<ActivityMain2Binding>() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val TAG = Main2Activity::class.java.simpleName
    private val main2TestVM :Main2TestVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main2)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun initView() {

    }

    override fun initData() {
        binding.vm = main2TestVM
    }

    override fun isEnableGrayTheme(): Boolean {
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main2)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun setUpClicks() {

    }
}