pluginManagement {
    repositories {
        gradlePluginPortal()
        google{
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven("https://jitpack.io"){
            content {
                includeGroup("com.github.*")
            }
        }
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://jitpack.io")
    }
}
rootProject.name = "BaseKit"
include(":app")
include(":baseKit")
include(":benchmark")
//include(":microbenchmark")

//apply(from="https://gist.githubusercontent.com/SheTieJun/f4cb1bd33997c2b46d9e3df40b95a02e/raw/888fd1f95857f1ae278dbafc0428e4c2b0d05ccf/config-maven-privacy-check.gradle")
//apply(from="gradle/private-maven.gradle")

val list = ArrayList<String>()
//初始化阶段开始时间
var beginOfSetting = System.currentTimeMillis()
//配置阶段开始时间
var beginOfConfig = 0L
//配置阶段是否开始了，只执行一次
var configHasBegin = false
//存放每个 build.gradle 执行之前的时间
var beginOfProjectConfig = HashMap<Any, Long>()
//执行阶段开始时间
var beginOfTaskExecute = 0L
//初始化阶段执行完毕
gradle.projectsLoaded {
    list.add("初始化总耗时 ${System.currentTimeMillis() - beginOfSetting} ms")
}

//build.gradle 执行前
gradle.beforeProject {
    if (!configHasBegin) {
        configHasBegin = true
        beginOfConfig = System.currentTimeMillis()
    }
    beginOfProjectConfig[project] = System.currentTimeMillis()
}

//build.gradle 执行后
gradle.afterProject {
    val begin = beginOfProjectConfig[project] ?: 0L
    list.add("配置阶段，$project 耗时：${System.currentTimeMillis() - begin} ms")
}

//配置阶段完毕
gradle.taskGraph.whenReady {
    list.add("配置阶段总耗时：${System.currentTimeMillis() - beginOfConfig} ms")
    beginOfTaskExecute = System.currentTimeMillis()
}


gradle.taskGraph.addTaskExecutionGraphListener {
    var startTs: Long
    it.allTasks.forEach { task ->
        startTs = 0L
        task.doFirst {
            startTs = System.currentTimeMillis()
        }
        task.doLast {
            list.add("执行阶段，$this 耗时：${System.currentTimeMillis() - startTs} ms")
        }
    }
}


gradle.addBuildListener(object : BuildListener {
    override fun settingsEvaluated(settings: Settings) { }

    override fun projectsLoaded(gradle: Gradle) {}

    override fun projectsEvaluated(gradle: Gradle) { }

    override fun buildFinished(result: BuildResult) {
        list.add("执行阶段总耗时：${System.currentTimeMillis() - beginOfTaskExecute}")
        list.forEach {
            println(it)
        }
    }
})



