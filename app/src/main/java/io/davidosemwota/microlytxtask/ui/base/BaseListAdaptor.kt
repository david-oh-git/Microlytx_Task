/*
 *
 * 2021 David Osemwota.
 */
package io.davidosemwota.microlytxtask.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Base recyclerView adaptor using [ListAdapter].
 *
 * @param itemsSame A function that checks if two items are the same.
 * @param contentsSame A function that checks if two items have same content.
 */
abstract class BaseListAdaptor<T>(
    itemsSame: (T, T) -> Boolean,
    contentsSame: (T, T) -> Boolean
) : ListAdapter<T, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
            itemsSame(oldItem, newItem)

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
            contentsSame(oldItem, newItem)
    }
) {

    /**
     * This is called when the recyclerView needs a new viewHolder.
     *
     * @param parent The parent View containing the list of viewHolders.
     * @param inflater Instantiates the layout file.
     * @param viewType The view type.
     * @return A new viewHolder of the view type.
     */
    abstract fun onCreateViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater,
        viewType: Int
    ): RecyclerView.ViewHolder

    /**
     * This is called when the recyclerView needs a new viewHolder.
     *
     * @param parent The parent View containing the list of viewHolders.
     * @param viewType The view type.
     * @return A new viewHolder of the view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        onCreateViewHolder(
            parent = parent,
            inflater = LayoutInflater.from(parent.context),
            viewType = viewType
        )
}
