package com.greedy.newsbreeze.util

import java.text.SimpleDateFormat
import java.util.*

object Utility {

    fun convertDate(inputString: String) : String {

        var formattedDate: String = ""

        val originalformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        val targetFormat = SimpleDateFormat("dd-MM-yyyy")
        val date = originalformat.parse(inputString)
        formattedDate = targetFormat.format(date)

        return  "" + formattedDate
    }
}