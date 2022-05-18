package com.jobget.ledger.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trans_table")
data class TransItem (
    var dateStr : String = "",
    var title : String = "",
    var value : Int = 0,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    )