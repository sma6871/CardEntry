package com.sma6871.cardentry

import android.text.InputFilter
import android.text.Spanned

class CardInputFilter(private val spaces: String, private val maxLength: Int) : InputFilter {
    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
        dest?.let {
            if (source.isNotEmpty() && !dest.isNullOrEmpty()) {

                if (dest.noSpace.length == maxLength)
                    return ""
            }
        }

        return source
    }
}

val String.noSpace
    get() = replace(Regex(" "), "")
val Spanned.noSpace
    get() = replace(Regex(" "), "")
