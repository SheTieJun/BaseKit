/*
 * MIT License
 *
 * Copyright (c) 2021 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package tools/*
 * MIT License
 *
 * Copyright (c) 2021 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.apache.tools.ant.taskdefs.condition.And
import org.gradle.api.artifacts.dsl.DependencyHandler
import tools.DependencyLibs.AndroidX.Constraint
import tools.DependencyLibs.AndroidX.Lifecycle
import tools.DependencyLibs.AndroidX.Room
import tools.DependencyLibs.AndroidX.WorkManager


object DependencyLibs {

    val apiList = mutableListOf<String>().apply {
        add(Room.runtime)
        add(Room.ktx)

        add(Lifecycle.livedata)
        add(Lifecycle.viewmodel)
        add(Lifecycle.runtime)

        add(Constraint.constraintLayout)

        add(AndroidX.appcompat)
        add(AndroidX.palette)
        add(AndroidX.coreKtx)
        add(AndroidX.recyclerview)
        add(AndroidX.cardview)
        add(AndroidX.material)
        add(AndroidX.animationCore)
        add(AndroidX.swiperefreshlayout)
        add(AndroidX.fragmentKtx)
        add(AndroidX.startup)
        add(AndroidX.cryptoSp)
        add(AndroidX.activityKtx)

        add(Koin.koinAndroid)
        add(Koin.koinWorkManager)

        add(Third.BRV)
        add(Third.glide)
        add(Third.gson)
        add(Third.timber)

        add(Retrofit2.gson)
        add(Retrofit2.retrofit)


        add(Coroutines.android)
        add(Coroutines.core)


    }


    val retrofit2Lib =  mutableListOf<String>().apply {
        add(Retrofit2.gson)
        add(Retrofit2.retrofit)
    }

    val roomLib = mutableListOf<String>().apply{
        add(Room.runtime)
        add(Room.ktx)
    }

    val androidLib = mutableListOf<String>().apply{
        add(AndroidX.appcompat)
        add(AndroidX.palette)
        add(AndroidX.coreKtx)
        add(AndroidX.recyclerview)
        add(AndroidX.cardview)
        add(AndroidX.material)
        add(AndroidX.animationCore)
        add(AndroidX.swiperefreshlayout)
        add(AndroidX.fragmentKtx)
        add(AndroidX.startup)
        add(AndroidX.cryptoSp)
        add(AndroidX.activityKtx)
        add(AndroidX.datastore)
        add(AndroidX.datastoreCore)

        add(Lifecycle.livedata)
        add(Lifecycle.viewmodel)
        add(Lifecycle.runtime)

        add(Constraint.constraintLayout)

        add(WorkManager.worker)
    }


    val koinLib = mutableListOf<String>().apply {
        add(Koin.koinAndroid)
        add(Koin.koinWorkManager)
    }


    val gsonLib = mutableListOf<String>().apply {
        add(Third.gson)
    }

    val otherLib = mutableListOf<String>().apply {
        add(Third.BRV)
        add(Third.glide)
        add(Third.timber)
    }

    val coroutines = mutableListOf<String>().apply {
        add(Coroutines.android)
        add(Coroutines.core)
    }

    object Coroutines {
        private const val version = "1.6.0"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object Retrofit2 {
        private const val version = "2.9.0"
        const val retrofit = "com.squareup.retrofit2:retrofit:$version"
        const val gson = "com.squareup.retrofit2:converter-gson:$version"
    }



    object Third {
        const val BRV = "com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.7"
        const val gson = "com.google.code.gson:gson:2.9.0"

        //图片加载
        const val glide = "com.github.bumptech.glide:glide:4.13.1"
        const val timber = "com.jakewharton.timber:timber:5.0.1"
    }

    object Koin {
        private const val koin_version = "3.1.2"

        // Koin main features for Android (Scope,ViewModel ...)
        const val koinAndroid = "io.insert-koin:koin-android:$koin_version"

        // Koin for Jetpack WorkManager
        const val koinWorkManager = "io.insert-koin:koin-androidx-workmanager:$koin_version"
    }

    object AndroidX {

        const val appcompat = "androidx.appcompat:appcompat:1.4.2"
        const val palette = "androidx.palette:palette:1.0.0"
        const val coreKtx = "androidx.core:core-ktx:1.7.0"
        const val recyclerview = "androidx.recyclerview:recyclerview:1.2.1"
        const val cardview = "androidx.cardview:cardview:1.0.0"
        const val material = "com.google.android.material:material:1.5.0"
        const val animationCore = "androidx.core:core-animation:1.0.0-alpha02"
        const val swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:1.4.0"
        const val startup = "androidx.startup:startup-runtime:1.1.0"
        const val cryptoSp = "androidx.security:security-crypto:1.1.0-alpha03"
        const val activityKtx = "androidx.activity:activity-ktx:1.4.0"
        const val datastore = "androidx.datastore:datastore-preferences:1.0.0"
        const val datastoreCore = "androidx.datastore:datastore-core:1.0.0"

        object Constraint {
            private const val constraintlayout = "2.1.3"
            const val constraintLayout = "androidx.constraintlayout:constraintlayout:$constraintlayout"
        }


        object Lifecycle {
            private const val version = "2.4.0"
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
        }


        object Room {
            private const val version = "2.4.2"
            const val runtime = "androidx.room:room-runtime:$version"
            const val ktx = "androidx.room:room-ktx:$version"
            const val compiler = "androidx.room:room-compiler:$version"
        }

        object WorkManager{
            private const val workVersion = "2.7.0"
            const val worker = "androidx.work:work-runtime-ktx:$workVersion"
        }
    }


}

fun DependencyHandler.implementation(depName: String) {
    add("implementation", depName)
}

fun DependencyHandler.kapt(depName: String) {
    add("kapt", depName)
}

fun DependencyHandler.ksp(depName: String) {
    add("ksp", depName)
}

fun DependencyHandler.compileOnly(depName: String) {
    add("compileOnly", depName)
}

fun DependencyHandler.api(depName: String) {
    add("api", depName)
}

fun DependencyHandler.annotationProcessor(depName: String) {
    add("annotationProcessor", depName)
}

//region 具体库

fun DependencyHandler.addRoom(type:String = "api"){
    DependencyLibs.roomLib.forEach { depName ->
        add(type, depName)
    }
    ksp(Room.compiler)
}

fun DependencyHandler.addAndroid(type:String = "api"){
    DependencyLibs.androidLib.forEach { depName ->
        add(type, depName)
    }
}


fun DependencyHandler.addRetrofit2(type:String = "api"){
    DependencyLibs.retrofit2Lib.forEach { depName ->
        add(type, depName)
    }
}

fun DependencyHandler.addGson(type:String = "api"){
    DependencyLibs.gsonLib.forEach { depName ->
        add(type, depName)
    }
}

fun DependencyHandler.addKoin(type:String = "api"){
    DependencyLibs.koinLib.forEach { depName ->
        add(type, depName)
    }
}

fun DependencyHandler.addOther(type:String = "api"){
    DependencyLibs.otherLib.forEach { depName ->
        add(type, depName)
    }
}

fun DependencyHandler.addCoroutines(type:String = "api"){
    DependencyLibs.coroutines.forEach { depName ->
        add(type, depName)
    }
}

//endregion