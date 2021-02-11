/*
 *
 * 2021 David Osemwota.
 */
package io.davidosemwota.microlytxtask.ui.home.adaptor

import android.view.LayoutInflater
import io.davidosemwota.microlytxtask.data.PhoneDetail
import io.davidosemwota.microlytxtask.databinding.ItemPhoneDetailBinding
import io.davidosemwota.microlytxtask.ui.base.BaseViewHolder

class PhoneDetailViewHolder(
    layoutInflater: LayoutInflater
) : BaseViewHolder<ItemPhoneDetailBinding>(
    binding = ItemPhoneDetailBinding.inflate(layoutInflater)
) {

    fun bind(phoneDetail: PhoneDetail) {
        binding.title.text = phoneDetail.title
        binding.subTitle.text = phoneDetail.subTitle
    }
}
