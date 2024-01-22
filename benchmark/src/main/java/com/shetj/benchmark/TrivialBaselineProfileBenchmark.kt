

package com.shetj.benchmark

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 *  用来生成 baseline-prof.txt
 *  但是需要root权限
 * @constructor Create empty Trivial baseline profile benchmark
 */
@OptIn(ExperimentalBaselineProfilesApi::class)
@RunWith(AndroidJUnit4::class)
class TrivialBaselineProfileBenchmark {
    @RequiresApi(Build.VERSION_CODES.P)
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @RequiresApi(Build.VERSION_CODES.P)
    @Test
    fun startup() = baselineProfileRule.collectBaselineProfile(
//        packageName = "shetj.me.base.dev.demo",
        packageName = "shetj.me.base",
        profileBlock = {
            startActivityAndWait()
        }
    )
}