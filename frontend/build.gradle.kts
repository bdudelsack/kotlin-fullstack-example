plugins {
    id("org.jetbrains.kotlin.js") version "1.3.61"
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-js"))
}

kotlin.target.browser { }