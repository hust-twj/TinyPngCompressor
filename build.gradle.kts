//https://intellij-support.jetbrains.com/hc/en-us/community/posts/24237513501202-Android-Studio-Missing-essential-plugins-com-android-tools-design-org-jetbrains-android-2025

// 插件版本和 Kotlin 版本定义
plugins {
    id("java")
    // IntelliJ 插件开发的 Gradle 插件
    id("org.jetbrains.intellij") version "1.13.3"
    //  Kotlin JVM 插件
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
}

group = "com.husttwj"
//1. change version
//2. update changelog in plugin.xml
version = "1.2.3"

repositories {
    mavenCentral()
}


// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    // 指定插件要兼容的 IntelliJ 平台版本，这里选择 Android Studio 对应的版本
    version.set("2023.1.5")

    //AI--Android Studio; IC--IntelliJ IDEA Community Edition
    type.set("IC")

    plugins.set(listOf("org.jetbrains.android"))

    // 插件的名称
    pluginName.set("TinyPngCompressor")

    // https://yoncise.com/logs/2022/08/03/88356600/
    // 将插件的版本兼容，只设置最低，不做最高版本限制，需要改两个地方
    // 1.updateSinceUntilBuild.set(false) 设置为FALSE
    // 2.patchPluginXml 任务里移除 untilBuild 的设置
    updateSinceUntilBuild.set(false)
}

// 配置依赖项
dependencies {
    //libs
    //implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin 标准库
    implementation(kotlin("stdlib-jdk8"))
    // OkHttp 库，用于发送 HTTP 请求，实现图片压缩时与 TinyPNG API 通信
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    implementation("com.tinify:tinify:1.6.2")

    implementation("com.twelvemonkeys.imageio:imageio-webp:3.12.0")

}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    buildSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        //最小兼容AS版本，对应 IntelliJ IDEA 2023.2 版本
        sinceBuild.set("231")
        //最大兼容AS版本，兼容 2024.3 系列的所有版本（包括小版本更新）
        //untilBuild.set("243.*")
        untilBuild.set("")  // 留空表示无上限
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

// 确保 Kotlin 目录也支持 Java 文件
sourceSets["main"].java.srcDirs("src/main/java", "src/main/kotlin")