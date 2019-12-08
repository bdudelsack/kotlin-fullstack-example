# Kotlin Fullstack Example (WIP)

## Description

An example on how to use kotlin for creating a clean architectured fullstack web application.

## Motivation

Targets to achieve on this project

* Write backend and frontend code in kotlin and use shared logic
* Use the Gradle DSL
* Use the React UI library
* Use the global state for the frontend

## TODO

- [x] Grade multi project setup
- [x] Shared multiplatform project
- [x] Simple backend with Ktor
- [x] Simple frontend with React in Kotlin/JS
- [x] Use the backend to serve the frontend scripts
- [x] Global store
- [ ] Auto reload
- [ ] Redux or something else for the global state
- [ ] Fetch some data from the backend
- [ ] Implement some UI routing
- [ ] Websocket communication between backend and frontend

## Configuring the project

Project is configured as multilevel gradle project containing `backend`, `frontend` and `shared` modules.

## Add generated JS files to the final JAR-file

Adding the final file compiled with webpack to the distributed JAR-file was somehow tricky. 

TODO: Up to date check had to be disabled because it wasn't taking changes from the `:frontend:browserWebpack` job.
TODO: Automatic detection of the script filename

**backend/gradle.build.kts**
```kotlin
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
```
## Launch backend task

A gradle task to run the backend was taken from the Kotlin Multiplatform project template and rewritten in Gradle DSL.

**backend/gradle.build.kts**
```kotlin
tasks.register<JavaExec>("run") {
    dependsOn("jar")

    main = "com.bdudelsack.fullstack.ApplicationKt"

    classpath(configurations["runtimeClasspath"].resolvedConfiguration.files, jar.archiveFile.get().toString())
    args = listOf()
}
```

## Using of React.createContext()

I had to found out that `React.createContext` do fail with an error:

```
> Task :frontend:compileKotlinJs FAILED
e: /home/bdudelsack/Projects/kotlin-fullstack-example/frontend/src/main/kotlin/com.bdudelsack.fullstack/Application.kt: (10, 11): When accessing module declarations from UMD, they must be marked by both @JsModule and @JsNonModule
```

the solution is to instuct Kotlin/JS compiler to use CommonJS output.

**frontend/gradle.build.kts**
```kotlin
kotlin {
    target {
        useCommonJs()
        browser()
    }

    /* ... */
}
```
