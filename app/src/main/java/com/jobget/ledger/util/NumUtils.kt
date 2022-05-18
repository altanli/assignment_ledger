package com.jobget.ledger.util

class NumUtils {
    companion object {
        val Default_Date_Format = "MMMM d, yyyy"
        fun convertToCurrency(price : Int) : String {
            if (price > 0) {
                return "$" + price
            } else {
                return "-$" + Math.abs(price)
            }
        }
    }
}