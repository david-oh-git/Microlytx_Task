/*
 * MIT License
 *
 * Copyright (c) 2021   David Osemwota.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.davidosemwota.microlytxtask.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import io.davidosemwota.microlytxtask.data.PhoneDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _listOfPhoneDetails = MutableStateFlow<List<PhoneDetail>>(
        emptyList()
    )
    val listOfPhoneDetails: LiveData<List<PhoneDetail>> = liveData {
        _listOfPhoneDetails
            .collect { emit(it) }
    }

    fun addPhoneDetail(
        phoneDetail: PhoneDetail,
        checkIfPresent: Boolean = false
    ) = viewModelScope.launch {
        val oldList = _listOfPhoneDetails.value.toMutableList()
        if (checkIfPresent) {
            val oldItem = oldList.find { it.title == phoneDetail.title }
            oldList.remove(oldItem)
            oldList.add(phoneDetail)
            _listOfPhoneDetails.value = oldList

            return@launch
        }

        val newList = mutableSetOf<PhoneDetail>()
        newList.addAll(oldList)
        newList.add(phoneDetail)

        _listOfPhoneDetails.value = newList.toMutableList()
    }

    fun flushPhoneDetails() = viewModelScope.launch {
        _listOfPhoneDetails.value = emptyList()
    }
}
