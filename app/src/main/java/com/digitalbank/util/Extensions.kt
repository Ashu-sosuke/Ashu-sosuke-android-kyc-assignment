package com.digitalbank.util

fun Int.toIndianRupee(): String {
    val str = this.toString()
    if (str.length <= 3) return "₹ $str"
    val lastThree = str.substring(str.length - 3)
    val remaining = str.substring(0, str.length - 3)
    val builder = StringBuilder()
    var count = 0
    for (i in remaining.length - 1 downTo 0) {
        builder.append(remaining[i])
        count++
        if (count == 2 && i > 0) {
            builder.append(',')
            count = 0
        }
    }
    val formattedRemaining = builder.reverse().toString()
    return "₹ $formattedRemaining,$lastThree"
}

fun String.maskIban(): String {
    return "**** " + this.takeLast(4)
}

fun String.toInitials(): String {
    val words = this.trim().split(Regex("\\s+"))
    return when {
        words.isEmpty() -> ""
        words.size == 1 -> words[0].take(2).uppercase()
        else -> (words[0].take(1) + words[1].take(1)).uppercase()
    }
}

fun Long.isExpired(ttlMs: Long): Boolean {
    return System.currentTimeMillis() - this > ttlMs
}
