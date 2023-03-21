package shetj.me.base.func.md3

import android.annotation.SuppressLint
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
import shetj.me.base.jankstats.JankStatsAggregator
import timber.log.Timber

/**
 * ActivityMain2Binding : ViewDataBinding
 */
class Main2Activity : AbBindingActivity<ActivityMain2Binding>() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val main2TestVM: Main2TestVM by viewModels()

    private lateinit var jankStatsAggregator: JankStatsAggregator

    @SuppressLint("TimberArgCount")
    private val jankReportListener =
        JankStatsAggregator.OnJankReportListener { reason, totalFrames, jankFrameData ->
            // A real app could do something more interesting, like writing the info to local storage and later on report it.

            Timber.run {
                tag("JankStatsSample")
                    .v("%s%s", "%sjankFrames = ", "%s, ", "%s%s", "%totalFrames = ", "%s), ", "*** Jank Report (%s", reason, totalFrames, jankFrameData.size)
            }

            jankFrameData.forEach { frameData ->
                Timber.tag("JankStatsSample").v(frameData.toString())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setSupportActionBar(mBinding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main2)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Initialize JankStats with an aggregator for the current window.
        jankStatsAggregator = JankStatsAggregator(window, jankReportListener)
    }


    override fun onPause() {
        super.onPause()
        // Before disabling tracking, issue the report with (optionally) specified reason.
        jankStatsAggregator.issueJankReport("Activity paused")
        jankStatsAggregator.jankStats.isTrackingEnabled = false
    }

    override fun onResume() {
        super.onResume()
        // [START aggregator_tracking_enabled]
        jankStatsAggregator.jankStats.isTrackingEnabled = true
    }

    override fun initView() {

    }

    override fun initData() {
        mBinding.vm = main2TestVM
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