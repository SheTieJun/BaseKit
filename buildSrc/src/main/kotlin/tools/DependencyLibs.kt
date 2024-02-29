package tools

import org.gradle.api.Action
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.accessors.runtime.addDependencyTo
import tools.DependencyLibs.Guava


object DependencyLibs {

    val proInstallerLib = mutableListOf<String>().apply {
        add(AndroidX.Benchmark.profileinstaller)
    }


    object AndroidX {



        object Benchmark {
            const val profileinstaller = "androidx.profileinstaller:profileinstaller:1.2.0"
        }
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