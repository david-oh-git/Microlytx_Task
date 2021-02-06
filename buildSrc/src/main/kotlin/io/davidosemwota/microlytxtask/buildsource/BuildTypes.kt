package io.davidosemwota.microlytxtask.buildsource
interface BuildType {

    companion object {
        const val debug = "debug"
        const val release = "release"
    }

    val isMinifyEnabled: Boolean
    val isTestCoverageEnabled: Boolean
}

object BuildTypeDebug : BuildType {

    override val isMinifyEnabled: Boolean
        get() = false

    const val application_id_suffix = ".debug"
    const val version_name_suffix = "-debug"
    override val isTestCoverageEnabled = true
}

object BuildTypeRelease : BuildType {

    override val isMinifyEnabled: Boolean
        get() = true
    override val isTestCoverageEnabled: Boolean
        get() = false
}
