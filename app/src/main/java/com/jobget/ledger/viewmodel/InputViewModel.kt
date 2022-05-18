package com.jobget.ledger.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jobget.ledger.model.TransItem

class InputViewModel : ViewModel() {
    val transData = MutableLiveData<TransItem>()

    init {
        transData.value = TransItem()
    }

    fun setDateStr (dateStr : String) {
        var transItem = transData.value!!
        transItem.dateStr = dateStr
        transData.value = transItem
    }

    fun setTitleStr (title : String) {
        var transItem = transData.value!!
        transItem.title = title
        transData.value = transItem
    }

    fun setPriceValue (priceValue : Int) {
        var transItem = transData.value!!
        transItem.value = priceValue
        transData.value = transItem
    }
}