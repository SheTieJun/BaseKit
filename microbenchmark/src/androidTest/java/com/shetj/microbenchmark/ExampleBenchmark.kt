

package com.shetj.microbenchmark

import android.util.Log
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class ExampleBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

//如需对 Activity 进行基准化分析，请使用 ActivityTestRule 或 ActivityScenarioRule。如需对界面代码进行基准化分析，请使用 @UiThreadTest。
//    @get:Rule
//    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun log() {
        benchmarkRule.measureRepeated {
            Log.d("LogBenchmark", "the cost of writing this log method will be measured")
        }
    }

    @Test
    fun benchmarkSomeWork() {
        benchmarkRule.measureRepeated {
            doSomething()
        }
    }


    /**
     * 测试的内容
     */
    private fun doSomething() {


    }


}