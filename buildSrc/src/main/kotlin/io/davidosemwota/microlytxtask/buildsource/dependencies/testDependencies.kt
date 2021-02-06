@file:Suppress("SpellCheckingInspection")
package io.davidosemwota.microlytxtask.buildsource.dependencies

/**
 * Test dependencies.
 */
object Junit5 {
    private const val version = "5.6.0"
    const val junit_api = "org.junit.jupiter:junit-jupiter-api:$version"
    const val junit_engine = "org.junit.jupiter:junit-jupiter-engine:$version"
}

object Mockito {
    private const val version = "2.10.0"
    const val mockito = "org.mockito:mockito-core:$version"
}

object Mockk {
    private const val version = "1.10.5"
    const val mockk = "io.mockk:mockk:$version"
}