### 移除aar 中的内容，然后尝试重新打包

- JavaCompile：用于编译Java源代码的Task类型。
- Test：用于执行测试的Task类型。
- Jar：用于打包生成JAR文件的Task类型。
- War：用于打包生成WAR文件的Task类型。
- Copy：用于复制文件或目录的Task类型。
- Delete：用于删除文件或目录的Task类型。
- Exec：用于执行外部命令的Task类型。
- Zip：用于打包生成ZIP文件的Task类型。
- Javadoc：用于生成Java文档的Task类型。
- Publish：用于发布构件到仓库的Task类型。

```groovy

configurations.maybeCreate("default")
artifacts.add("default", file('libs/OauthSDK_V.4.1.0.aar')) //这个相当于导入libs/***.aar到我们新建的模块
//定义获取导入的aar的方法
def getAar() {
    //获取到名称为default的Configuration
    Configuration c = configurations.getByName("default")
    //在Configuration中通过PublishArtifactSet获取到对应的文件
    def files = c.artifacts.files.filter {
        it.name ==~ /.*\.aar/
    }
    def file = null
    if (!files.empty) {
        file = files[0]
    }
    return file
}

def unZipAarDir = "$buildDir/libs/"
def unZipAarFile = "$buildDir/libs/unZip"
def unZipAarFileJar = "$buildDir/libs/unZip/classes.jar"
def unZipAarFileJarDir = "$buildDir/libs/jarDir"
def unZipAarFileJarLast = "$buildDir/libs/jar"
// 定义解压后的目录变量，字符类型，解压输出目录是build/libs/下面


//定义解压任务，可以直接gradle命令执行
task unZipAar(type: Copy) {
    def zipFile = getAar()
    def outputDir = unZipAarFile
    from zipTree(zipFile)
    into outputDir
    println "-------outputDir path = $outputDir"
}


task unZipJarSoure(type: Copy){
    def jarFile = file(unZipAarFileJar)
    def renamedJarFile = file(unZipAarFileJar + '.zip')
    println "-------renamedJarFile path = $renamedJarFile"
    jarFile.renameTo(renamedJarFile)
    from zipTree(renamedJarFile)
    into unZipAarFileJarDir
}

task packageJar(type: Jar) {

    archiveFileName = 'classes.jar'
    println "-------input path = $unZipAarFileJarDir"
    from(unZipAarFileJarDir) {
        include '*/**'
    }
    into("")
    println "-------outputDir path = $unZipAarFileJarLast"
}

//定义 重新打包任务，可以直接gradle命令执行
task zipAarExclude(type: Zip) {
    description '生成一个排除某个class之后的aar包'
    baseName "reAarFinalName" //重新打包后的名字
    extension "aar"  //打包后文件后缀
    from unZipAarFile
    getDestinationDirectory().set(file("$buildDir"))  // 设置打包后的目录
}

// //方法2：可以借助解压工具，把jar中的class 直接删除
task deleteClass(type: Delete) {
    def classFile = file('xxx')
    delete classFile
}


```