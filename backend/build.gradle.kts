import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

var ktorVersion = "1.2.6"
var logbackVersion = "1.2.3"


plugins {
    kotlin("jvm") version "1.3.61"
}

repositories {
    jcenter()
    mavenCentral()
    maven(uri("https://dl.bintray.com/kotlin/ktor"))
    maven(uri("https://dl.bintray.com/kotlin/kotlinx"))
    maven(uri("https://dl.bintray.com/kotlin/kotlin-dev"))
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":shared"))
    implementation(files(":frontend"))

    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.13.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

val jar = tasks.findByName("jar") as Jar

jar.apply {
    dependsOn(":frontend:browserWebpack")

    outputs.upToDateWhen { false }

    doFirst {
        val frontend = project(":frontend")
        val browserWebpack = frontend.tasks.named<KotlinWebpack>("browserWebpack")

        val file = File(browserWebpack.get().destinationDirectory, "frontend.js")
        from(file)
    }
}

tasks.register<JavaExec>("run") {
    dependsOn("jar")

    main = "com.bdudelsack.fullstack.ApplicationKt"

    classpath(configurations["runtimeClasspath"].resolvedConfiguration.files, jar.archiveFile.get().toString())
    args = listOf()
}
