package io.davidosemwota.microlytxtask.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import io.davidosemwota.microlytxtask.data.PhoneDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _listOfPhoneDetails = MutableStateFlow<List<PhoneDetail>>(emptyList())
    private val listOfPhoneDetails: LiveData<List<PhoneDetail>> = liveData {
        _listOfPhoneDetails
                .collect { emit(it) }
    }

    fun addPhoneDetail(phoneDetail: PhoneDetail) = viewModelScope.launch {
        val oldList = _listOfPhoneDetails.value
        val newList = mutableListOf<PhoneDetail>()
        newList.addAll(oldList)
        newList.add(phoneDetail)

        _listOfPhoneDetails.value = newList
    }
}