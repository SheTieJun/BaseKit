package com.shetj.benchmark

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ColdStartupBenchmark : AbstractStartupBenchmark(StartupMode.COLD)

abstract class AbstractStartupBenchmark(private val startupMode: StartupMode) {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @RequiresApi(Build.VERSION_CODES.N)
    @Test
    fun startupNoCompilation() = startup(CompilationMode.None())


    @RequiresApi(Build.VERSION_CODES.N)
    @Test
    fun startupPartialCompilation() = startup(
        CompilationMode.Partial(
            baselineProfileMode = BaselineProfileMode.Disable,
            warmupIterations = 3
        )
    )

    @RequiresApi(Build.VERSION_CODES.N)
    @Test
    fun startupPartialWithBaselineProfiles() =
        startup(CompilationMode.Partial(baselineProfileMode = BaselineProfileMode.Require))

    @Test
    fun startupFullCompilation() = startup(CompilationMode.Full())

    private fun startup(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
//        packageName = "shetj.me.base.dev.demo",
        packageName = "shetj.me.base",
        metrics = listOf(StartupTimingMetric()),
        compilationMode = compilationMode,
        iterations = 10,
        startupMode = startupMode,
        setupBlock = {
            pressHome()
        }
    ) {
        startActivityAndWait()
    }
}