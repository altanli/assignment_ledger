package com.jobget.ledger.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.jobget.ledger.database.UserDatabase
import com.jobget.ledger.model.TransItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TransViewModel : ViewModel() {
    private lateinit var context : Context
    private val _status = MutableLiveData<String>()
    private lateinit var transData : LiveData<List<TransItem>>

    val status: LiveData<String> = _status

    fun getTransData () : LiveData<List<TransItem>> {
        return transData
    }

    fun setContext (context: Context) {
        this.context = context
        fetchData()
    }

    fun closeDB () {
        val userDatabase = UserDatabase.getDatabase(context)
        viewModelScope.launch {
            userDatabase.close()
        }
    }

    fun addTransItem (transItem: TransItem) {
        val userDatabase = UserDatabase.getDatabase(context)
        CoroutineScope(Dispatchers.IO).launch {
            userDatabase.userDao().insertToRoomDatabase(transItem)
            fetchData()
        }
    }

    fun deleteTransItem (transItem: TransItem) {
        val userDatabase = UserDatabase.getDatabase(context)
        CoroutineScope(Dispatchers.IO).launch {
            userDatabase.userDao().deleteSingleUserDetails(transItem.id)
            fetchData()
        }
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                val userDatabase = UserDatabase.getDatabase(context)
                transData = userDatabase.userDao().getTransDetails().asLiveData()
            } catch (e: Exception) {
            }
        }
    }
}