package tools

import org.gradle.api.Action
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.accessors.runtime.addDependencyTo
import tools.DependencyLibs.AndroidX.Constraint
import tools.DependencyLibs.AndroidX.Lifecycle
import tools.DependencyLibs.AndroidX.Paging
import tools.DependencyLibs.AndroidX.Room
import tools.DependencyLibs.AndroidX.WorkManager
import tools.DependencyLibs.Guava
import tools.DependencyLibs.navLib


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

    val retrofit2Lib = mutableListOf<String>().apply {
        add(Retrofit2.gson)
        add(Retrofit2.retrofit)
    }

    val roomLib = mutableListOf<String>().apply {
        add(Room.runtime)
        add(Room.ktx)
    }

    val androidLib = mutableListOf<String>().apply {
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
        add(AndroidX.activityKtx)
        add(Mine.dataStoreLib)

        add(AndroidX.datastoreCore)
        add(AndroidX.dragAndDrop)
        add(AndroidX.webKit)
        add(AndroidX.window)
        add(AndroidX.concurrent)
//        add(AndroidX.browser) //国内手机自带的浏览器效果很差

        add(Lifecycle.livedata)
        add(Lifecycle.viewmodel)
        add(Lifecycle.runtime)

        add(Constraint.constraintLayout)

        add(WorkManager.worker)
        add(WorkManager.worker_multiprocess)

        add(AndroidX.Navigation.navigation)
        add(AndroidX.Navigation.navigationUi)

        //        add(Mine.activityLib) //base 已经实现了
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
        add(Mine.QMUI)
    }

    val coroutinesLib = mutableListOf<String>().apply {
        add(Coroutines.android)
        add(Coroutines.core)
    }

    val navLib = mutableListOf<String>().apply {
        add(AndroidX.Navigation.navigation)
        add(AndroidX.Navigation.navigationUi)
    }

    val proInstallerLib = mutableListOf<String>().apply {
        add(AndroidX.Benchmark.profileinstaller)
    }


    object Coroutines {
        private const val version = "1.7.3"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"

        //https://github.com/hoc081098/FlowExt
        const val flowExt = "io.github.hoc081098:FlowExt-jvm:0.5.0"
    }

    object Retrofit2 {
        private const val version = "2.9.0"
        const val retrofit = "com.squareup.retrofit2:retrofit:$version"
        const val gson = "com.squareup.retrofit2:converter-gson:$version"
    }


    object Third {
        const val BRV = "com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.11"
        const val gson = "com.google.code.gson:gson:2.10.1"
        //图片加载
        const val glide = "com.github.bumptech.glide:glide:4.13.1"
        const val timber = "com.jakewharton.timber:timber:5.0.1"
    }

    object Koin {
        private const val koin_version = "3.4.3"

        // Koin main features for Android (Scope,ViewModel ...)
        const val koinAndroid = "io.insert-koin:koin-android:$koin_version"

        // Koin for Jetpack WorkManager
        const val koinWorkManager = "io.insert-koin:koin-androidx-workmanager:$koin_version"
    }

    object AndroidX {

        const val appcompat = "androidx.appcompat:appcompat:1.6.1"
        const val palette = "androidx.palette:palette:1.0.0"
        const val coreKtx = "androidx.core:core-ktx:1.12.0"
        const val recyclerview = "androidx.recyclerview:recyclerview:1.3.1"
        const val cardview = "androidx.cardview:cardview:1.0.0"
        const val material = "com.google.android.material:material:1.9.0"
        const val animationCore = "androidx.core:core-animation:1.0.0-rc01"
        const val swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:1.6.2"
        const val startup = "androidx.startup:startup-runtime:1.1.1"
        const val activityKtx = "androidx.activity:activity-ktx:1.8.1"
        const val datastore = "androidx.datastore:datastore-preferences:1.0.0"
        const val datastoreCore = "androidx.datastore:datastore-core:1.0.0"
        const val dragAndDrop = "androidx.draganddrop:draganddrop:1.0.0"
        const val webKit = "androidx.webkit:webkit:1.8.0"
        const val window = "androidx.window:window:1.1.0"
        const val browser = "androidx.browser:browser:1.7.0"
        const val concurrent = "androidx.concurrent:concurrent-futures-ktx:1.1.0"

        object Constraint {
            private const val constraintlayout = "2.1.4"
            const val constraintLayout = "androidx.constraintlayout:constraintlayout:$constraintlayout"
        }

        object Paging {
            private const val pagingVersion = "3.2.0"
            const val paging = "androidx.paging:paging-runtime:$pagingVersion"
        }

        object Lifecycle {
            private const val version = "2.5.1"
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
        }

        object Navigation {
            private const val navVersion = "2.6.0"
            const val navigation = "androidx.navigation:navigation-fragment-ktx:$navVersion"
            const val navigationUi = "androidx.navigation:navigation-ui-ktx:$navVersion"
        }

        object Room {
            private const val version = "2.6.0"  //kotlin 1.9.0之后需要升级到2.6.0
            const val runtime = "androidx.room:room-runtime:$version"
            const val ktx = "androidx.room:room-ktx:$version"
            const val compiler = "androidx.room:room-compiler:$version"
        }

        object WorkManager {
            private const val workVersion = "2.8.1"
            const val worker = "androidx.work:work-runtime-ktx:$workVersion"
            const val worker_multiprocess = "androidx.work:work-multiprocess:$workVersion"
        }

        object Benchmark {

            const val profileinstaller = "androidx.profileinstaller:profileinstaller:1.2.0"
        }
    }


    object Mine {
        private const val libVersion = "1.1.0"
        const val dataStoreLib  = "com.github.SheTieJun.androidx-ktx:datastore:$libVersion"
        const val activityLib   = "com.github.SheTieJun.androidx-ktx:activity:$libVersion"
        const val QMUI          = "com.github.SheTieJun:SimQUMI:0.0.1"
    }

    private const val guavaVersion = "32.1.2-android"

    enum class Guava(val value: String) {
        guava("com.google.guava:guava:$guavaVersion")
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

fun DependencyHandler.apiTransitive(depName: String) {
    val dependencyConfiguration = Action<ExternalModuleDependency> {
        isTransitive = true
    }
    addDependencyTo(
        this, "api", depName, dependencyConfiguration
    )
}

val defAction = Action<ExternalModuleDependency> {
}

//region 具体库

fun DependencyHandler.addRoom(dependencyConfiguration: Action<ExternalModuleDependency> = defAction) {
    DependencyLibs.roomLib.forEach { depName ->
        addDependencyTo(
            this, "api", depName, dependencyConfiguration
        )
    }
    kapt(Room.compiler)
}

fun DependencyHandler.addAndroid(dependencyConfiguration: Action<ExternalModuleDependency> = defAction) {
    DependencyLibs.androidLib.forEach { depName ->
        addDependencyTo(
            this, "api", depName, dependencyConfiguration
        )
    }
}


fun DependencyHandler.addRetrofit2(dependencyConfiguration: Action<ExternalModuleDependency> = defAction) {
    DependencyLibs.retrofit2Lib.forEach { depName ->
        addDependencyTo(
            this, "api", depName, dependencyConfiguration
        )
    }
}

fun DependencyHandler.addGson(dependencyConfiguration: Action<ExternalModuleDependency> = defAction) {
    DependencyLibs.gsonLib.forEach { depName ->
        addDependencyTo(
            this, "api", depName, dependencyConfiguration
        )
    }
}

fun DependencyHandler.addKoin(dependencyConfiguration: Action<ExternalModuleDependency> = defAction) {
    DependencyLibs.koinLib.forEach { depName ->
        addDependencyTo(
            this, "api", depName, dependencyConfiguration
        )
    }
}

fun DependencyHandler.addOther(dependencyConfiguration: Action<ExternalModuleDependency> = defAction) {
    DependencyLibs.otherLib.forEach { depName ->
        addDependencyTo(
            this, "api", depName, dependencyConfiguration
        )
    }
}

fun DependencyHandler.addCoroutines(dependencyConfiguration: Action<ExternalModuleDependency> = defAction) {
    DependencyLibs.coroutinesLib.forEach { depName ->
        addDependencyTo(
            this, "api", depName, dependencyConfiguration
        )
    }
}

fun DependencyHandler.addPaging(dependencyConfiguration: Action<ExternalModuleDependency> = defAction) {
    addDependencyTo(
        this, "api", Paging.paging, dependencyConfiguration
    )
}

fun DependencyHandler.addNav(dependencyConfiguration: Action<ExternalModuleDependency> = defAction) {
    navLib.forEach { depName ->
        addDependencyTo(
            this, "api", depName, dependencyConfiguration
        )
    }
}

fun DependencyHandler.addProInstaller(dependencyConfiguration: Action<ExternalModuleDependency> = defAction) {
    DependencyLibs.proInstallerLib.forEach { depName ->
        addDependencyTo(
            this, "api", depName, dependencyConfiguration
        )
    }
}

fun DependencyHandler.addGuava(dependencyConfiguration: Action<ExternalModuleDependency> = defAction) {
    Guava.values().forEach { depName ->
        addDependencyTo(
            this, "api", depName.value, dependencyConfiguration
        )
    }
}
//endregion