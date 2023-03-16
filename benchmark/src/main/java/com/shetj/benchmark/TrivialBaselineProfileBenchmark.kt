

package com.shetj.benchmark

import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import org.junit.Rule
import org.junit.Test

/**
 *  用来生成 baseline-prof.txt
 *  但是需要root权限
 * @constructor Create empty Trivial baseline profile benchmark
 */
@OptIn(ExperimentalBaselineProfilesApi::class)
class TrivialBaselineProfileBenchmark {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun startup() = baselineProfileRule.collectBaselineProfile(
        packageName = "shetj.me.base.dev.demo",
        profileBlock = {
            startActivityAndWait()
        }
    )
}