package org.example.utils

fun ellipsize(text: String, size: Int = 50): String {
    val dots = "..."

    // 省略した場合の末尾につくdots分は文字数は最低限確保が必要
    require(size >= dots.length) {
        "specified size is too small: must be >=${dots.length} but $size"
    }

    return if (text.length <= size) {
        text
    } else {
        val sub = text.substring(0, size - dots.length)
        "$sub$dots"
    }
}