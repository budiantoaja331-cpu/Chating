package com.example

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

fun parseMarkdownLite(text: String): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        // Match **bold** or *italic*
        val pattern = Regex("\\*\\*(.*?)\\*\\*|\\*(.*?)\\*")
        val matches = pattern.findAll(text)

        for (match in matches) {
            append(text.substring(currentIndex, match.range.first))

            val boldGroup = match.groups[1]
            val italicGroup = match.groups[2]

            if (boldGroup != null) {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(boldGroup.value)
                }
            } else if (italicGroup != null) {
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(italicGroup.value)
                }
            }
            currentIndex = match.range.last + 1
        }
        append(text.substring(currentIndex))
    }
}
