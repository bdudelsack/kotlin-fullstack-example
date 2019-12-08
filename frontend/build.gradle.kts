plugins {
    kotlin("js") version "1.3.61"
}

repositories {
    mavenLocal()
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-js-wrappers")
    maven("https://dl.bintray.com/kotlin/kotlinx")
    mavenCentral()
}

repositories {
    mavenCentral()
}



kotlin {
    target {
        browser {
            webpackTask {

            }
        }
    }

    sourceSets {
        val main by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))

                implementation(npm("react", "^16.9.0"))
                implementation(npm("react-dom", "^16.9.0"))
                implementation(npm("styled-components", "^4.4.1"))
                implementation(npm("inline-style-prefixer", "^5.1.0"))
                implementation(npm("core-js", "^3.4.7"))
                implementation(npm("css-in-js-utils", "^3.0.2"))

//                implementation(npm("@jetbrains/kotlin-react", "16.9.0-pre.83"))
//                implementation(npm("@jetbrains/kotlin-react-dom", "16.9.0-pre.83"))
//                implementation(npm("@jetbrains/kotlin-styled", "1.0.0-pre.83"))
//                implementation(npm("@jetbrains/kotlin-extensions", "1.0.1-pre.83"))
//                implementation(npm("@jetbrains/kotlin-css", "1.0.0-pre.83"))
//                implementation(npm("@jetbrains/kotlin-css-js", "1.0.0-pre.83"))

                var kotlinWrappersVersion = "pre.88-kotlin-1.3.60"

                implementation("org.jetbrains:kotlin-react:16.9.0-${kotlinWrappersVersion}")
                implementation("org.jetbrains:kotlin-react-dom:16.9.0-${kotlinWrappersVersion}")
                implementation("org.jetbrains:kotlin-css:1.0.0-${kotlinWrappersVersion}")
                implementation("org.jetbrains:kotlin-css-js:1.0.0-${kotlinWrappersVersion}")
                implementation("org.jetbrains:kotlin-styled:1.0.0-${kotlinWrappersVersion}")
            }
        }

        val test by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
    }

}

