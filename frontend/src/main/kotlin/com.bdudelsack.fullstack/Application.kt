package com.bdudelsack.fullstack

import react.dom.h1
import react.dom.render
import kotlin.browser.document


fun main() {
    render(document.getElementById("container")) {
        h1 {
            + "Kotlin fullstack example"
        }
    }
}
