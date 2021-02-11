/*
 *
 * 2021 David Osemwota.
 */
package io.davidosemwota.microlytxtask.ui.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Base view holder
 *
 * @param binding Data binding generated for the layout.
 */
abstract class BaseViewHolder<T : ViewBinding>(
    val binding: T
) : RecyclerView.ViewHolder(binding.root)
