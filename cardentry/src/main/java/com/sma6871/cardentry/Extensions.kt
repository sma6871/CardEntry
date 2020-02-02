package com.sma6871.cardentry

import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


fun View.toPxF(dp: Int): Float {
    return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics)
}

fun View.spToPxF(sp: Int): Float {
    return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp.toFloat(),
            resources.displayMetrics)
}

fun View.getColor(@ColorRes colorId: Int): Int {
    return ContextCompat.getColor(context, colorId)
}

fun CharSequence.onlyNumber(): String {
    return toString().toEnglishNumbers().trim().replace(Regex("[^0-9]"), "")
}

fun String.toEnglishNumbers(): String {
    var result = this
    val arabic = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    val persian = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    ('0'..'9').forEachIndexed { index, number ->
        result = result.replace(persian[index], number, true)
                        .replace(arabic[index], number, true)
    }
    return result
}