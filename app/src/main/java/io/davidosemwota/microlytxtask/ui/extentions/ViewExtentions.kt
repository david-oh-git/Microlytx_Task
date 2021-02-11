/*
 *
 * 2021 David Osemwota.
 */
package io.davidosemwota.ui.extentions

import android.view.View

var View.visible
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }
