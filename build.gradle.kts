
// 插件版本和 Kotlin 版本定义
plugins {
   // id("java")
    // 应用 IntelliJ 插件开发的 Gradle 插件
    id("org.jetbrains.intellij") version "1.13.3"
    // 应用 Kotlin JVM 插件
    kotlin("jvm") version "1.9.22"
}

group = "com.husttwj"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


// 配置 Kotlin 编译选项
//tasks.withType<KotlinCompile> {
//    kotlinOptions.jvmTarget = "17"
//}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
//    version.set("2024.3")
    type.set("IC") // Target IDE Platform
//
//    plugins.set(listOf(/* Plugin Dependencies */))

    // 指定插件要兼容的 IntelliJ 平台版本，这里选择 Android Studio 对应的版本
    version.set("2024.2.5")
    // 插件的名称
    pluginName.set("ImageCompressor111")
    // 发布渠道，这里设置为默认渠道
    updateSinceUntilBuild.set(false)
}

// 配置依赖项
dependencies {
    // 新增IntelliJ平台核心依赖
   // implementation(intellijCore())

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))


    // Kotlin 标准库
    implementation(kotlin("stdlib-jdk8"))
    // OkHttp 库，用于发送 HTTP 请求，实现图片压缩时与 TinyPNG API 通信
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    implementation("com.tinify:tinify:1.6.2")
    // JUnit 5 测试框架
    //testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
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
        sinceBuild.set("242")
        untilBuild.set("252.*")
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
