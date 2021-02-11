/*
 *
 * 2021 David Osemwota.
 */
package io.davidosemwota.microlytxtask.ui.extentions

import androidx.recyclerview.widget.RecyclerView
import io.davidosemwota.microlytxtask.ui.RecyclerViewItemDecoration

/**
 * Add an [RecyclerViewItemDecoration] to this RecyclerView.
 *
 * @param spacingPx Spacing in pixels to set as a item margin.
 */
fun RecyclerView.setItemDecorationSpacing(spacingPx: Float) {
    addItemDecoration(
        RecyclerViewItemDecoration(spacingPx.toInt())
    )
}
