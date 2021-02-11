/*
 *
 * 2021 David Osemwota.
 */
package io.davidosemwota.microlytxtask.ui.home.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.davidosemwota.microlytxtask.data.PhoneDetail
import io.davidosemwota.microlytxtask.ui.base.BaseListAdaptor

class PhoneDetailAdaptor : BaseListAdaptor<PhoneDetail>(
    itemsSame = { old, new -> old.title == new.title },
    contentsSame = { old, new -> old.title == new.title && old.subTitle == new.subTitle }
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater,
        viewType: Int
    ) = PhoneDetailViewHolder(inflater)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PhoneDetailViewHolder -> holder.bind(getItem(position))
        }
    }
}
