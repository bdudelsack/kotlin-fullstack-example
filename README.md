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

## Problem with webpack resolving @jetbrains packages

```log
ERROR in ./kotlin/kotlin-fullstack-example-frontend.js
Module not found: Error: Can't resolve 'kotlin-react' in '/home/bdudelsack/Projects/kotlin-fullstack-example/build/js/packages/kotlin-fullstack-example-frontend/kotlin'
 @ ./kotlin/kotlin-fullstack-example-frontend.js 173:128-151
 @ multi ./kotlin/kotlin-fullstack-example-frontend.js

ERROR in ./kotlin/kotlin-fullstack-example-frontend.js
Module not found: Error: Can't resolve 'kotlin-react-dom' in '/home/bdudelsack/Projects/kotlin-fullstack-example/build/js/packages/kotlin-fullstack-example-frontend/kotlin'
 @ ./kotlin/kotlin-fullstack-example-frontend.js 173:81-108
 @ multi ./kotlin/kotlin-fullstack-example-frontend.js
```

In order for webpack to pick the @jetbrains-scoped packages correctly, following configuration need to be applied.

**frontend/webpack.config.d/01.resolve.js**
```js
config.resolve.alias = {
    'kotlin-css-js': '@jetbrains/kotlin-css-js',
    'kotlin-extensions': '@jetbrains/kotlin-extensions',
    'kotlin-react': '@jetbrains/kotlin-react',
    'kotlin-react-dom': '@jetbrains/kotlin-react-dom',
    'kotlin-styled': '@jetbrains/kotlin-styled',
    'kotlinx-html-js': 'kotlinx-html',
};
```

## TODO: Auto reload

The frontend module can be started in auto reload mode with the following command.

```bash
./gradlew :frontend:run -t
```

It seems not to work yet and fails with following error.

```log
✖ ｢wdm｣: Hash: 6513f41ecb91190706be
Version: webpack 4.41.2
Time: 24ms
Built at: 2019-12-09 13:42:11
      Asset     Size  Chunks             Chunk Names
frontend.js  892 KiB    main  [emitted]  main
Entrypoint main = frontend.js
[./kotlin/kotlin-fullstack-example-frontend.js] 382 bytes {main} [built] [failed] [1 error]
    + 33 hidden modules

ERROR in ./kotlin/kotlin-fullstack-example-frontend.js
Module build failed (from /home/bdudelsack/Projects/kotlin-fullstack-example/build/js/packages_imported/kotlin-source-map-loader/1.3.61/kotlin-source-map-loader.js):
Error: ENOENT: no such file or directory, open '/home/bdudelsack/Projects/kotlin-fullstack-example/build/js/packages/kotlin-fullstack-example-frontend/kotlin/kotlin-fullstack-example-frontend.js'
 @ multi ./kotlin/kotlin-fullstack-example-frontend.js main[0]

> Task :frontend:compileKotlinJs FAILED
e: /home/bdudelsack/Projects/kotlin-fullstack-example/frontend/src/main/kotlin/com.bdudelsack.fullstack/Application.kt: (6, 8): Unresolved reference: react
e: /home/bdudelsack/Projects/kotlin-fullstack-example/frontend/src/main/kotlin/com.bdudelsack.fullstack/Application.kt: (7, 8): Unresolved reference: react
e: /home/bdudelsack/Projects/kotlin-fullstack-example/frontend/src/main/kotlin/com.bdudelsack.fullstack/Application.kt: (19, 5): Unresolved reference: render
e: /home/bdudelsack/Projects/kotlin-fullstack-example/frontend/src/main/kotlin/com.bdudelsack.fullstack/Application.kt: (20, 9): Unresolved reference: storeProvider
e: /home/bdudelsack/Projects/kotlin-fullstack-example/frontend/src/main/kotlin/com.bdudelsack.fullstack/Application.kt: (21, 13): Unresolved reference: h1
e: /home/bdudelsack/Projects/kotlin-fullstack-example/frontend/src/main/kotlin/com.bdudelsack.fullstack/Application.kt: (22, 17): Unresolved reference: +
e: /home/bdudelsack/Projects/kotlin-fullstack-example/frontend/src/main/kotlin/com.bdudelsack.fullstack/Application.kt: (25, 13): Cannot access class 'react.RProps'. Check your module classpath for missing or conflicting dependencies
e: /home/bdudelsack/Projects/kotlin-fullstack-example/frontend/src/main/kotlin/com.bdudelsack.fullstack/Application.kt: (25, 22): No value passed for parameter 'props'

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':frontend:compileKotlinJs'.
> Compilation error. See log for more details

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights.

* Get more help at https://help.gradle.org

BUILD FAILED in 516ms
6 actionable tasks: 1 executed, 5 up-to-date

```
