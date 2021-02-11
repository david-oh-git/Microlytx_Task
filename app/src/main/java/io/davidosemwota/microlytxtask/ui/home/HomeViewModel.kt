/*
 *
 * 2021 David Osemwota.
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
