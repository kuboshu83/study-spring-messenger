package org.example.utils

/**
 * sizeは3以上に設定してください。
 */
fun ellipsize(text: String, size: Int = 50): String {
    val dots = "..."

    // 省略した場合の末尾につくdots分は文字数は最低限確保が必要
    require(size >= dots.length) {
        "size must be >=${dots.length}: size=$size"
    }

    return if (text.length <= size) {
        text
    } else {
        val subtractedText = text.substring(0, size - dots.length)
        "$subtractedText$dots"
    }
}

private val leftSpace = Regex("""^[\s\u3000]*""")
private val rightSpace = Regex("""[\s\u3000]*$""")
fun trimSpace(text: String): String {
    return text.replace(leftSpace, "").replace(rightSpace, "")
}