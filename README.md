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
## TODO: Auto reload

The frontend module can be started in auto reload mode with the following command.

```bash
./gradlew :frontend:run -t
```

It seems not to work yet and fails with following error.

```log
> Task :shared:jsPackageJson UP-TO-DATE
> Task :frontend:packageJson UP-TO-DATE
> Task :kotlinNodeJsSetup SKIPPED
> Task :kotlinNpmInstall
> Task :shared:compileKotlinJs UP-TO-DATE
> Task :shared:jsProcessResources NO-SOURCE
> Task :shared:jsMainClasses UP-TO-DATE
> Task :shared:jsJar UP-TO-DATE
✖ ｢wdm｣: Hash: 6513f41ecb91190706be
Version: webpack 4.41.2
Time: 22ms
Built at: 2019-12-10 13:20:36
      Asset     Size  Chunks             Chunk Names
frontend.js  892 KiB    main  [emitted]  main
Entrypoint main = frontend.js
[./kotlin/kotlin-fullstack-example-frontend.js] 382 bytes {main} [built] [failed] [1 error]
    + 33 hidden modules

ERROR in ./kotlin/kotlin-fullstack-example-frontend.js
Module build failed (from /home/bdudelsack/Projects/kotlin-fullstack-example/build/js/packages_imported/kotlin-source-map-loader/1.3.61/kotlin-source-map-loader.js):
Error: ENOENT: no such file or directory, open '/home/bdudelsack/Projects/kotlin-fullstack-example/build/js/packages/kotlin-fullstack-example-frontend/kotlin/kotlin-fullstack-example-frontend.js'
 @ multi ./kotlin/kotlin-fullstack-example-frontend.js main[0]
> Task :frontend:compileKotlinJs
> Task :frontend:processResources UP-TO-DATE
> Task :frontend:browserRun

BUILD SUCCESSFUL in 1s
8 actionable tasks: 3 executed, 5 up-to-date
```
