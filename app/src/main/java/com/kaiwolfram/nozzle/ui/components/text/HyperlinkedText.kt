package com.kaiwolfram.nozzle.ui.components.text

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.kaiwolfram.nozzle.data.utils.extractUrls
import com.kaiwolfram.nozzle.data.utils.fixUrl
import com.kaiwolfram.nozzle.ui.theme.HyperlinkBlue

private const val URL_TAG = "URL"

@OptIn(ExperimentalTextApi::class)
@Composable
fun HyperlinkedText(
    text: String,
    onClickNonLink: () -> Unit,
    maxLines: Int = 64,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    if (text.isNotBlank()) {
        val annotatedString = remember(text) { buildAnnotatedString(text) }
        val uriHandler = LocalUriHandler.current
        ClickableText(
            text = annotatedString,
            maxLines = maxLines,
            overflow = overflow,
            onClick = {
                val url = annotatedString
                    .getStringAnnotations(URL_TAG, it, it)
                    .firstOrNull()
                if (url != null) {
                    uriHandler.openUri(fixUrl(url.item))
                } else {
                    onClickNonLink()
                }
            }
        )
    }
}

private fun buildAnnotatedString(text: String): AnnotatedString {
    return buildAnnotatedString {
        append(text)
        extractUrls(text).forEach { url ->
            val startIndex = text.indexOf(url)
            val endIndex = startIndex + url.length
            addStyle(
                style = SpanStyle(
                    color = HyperlinkBlue,
                    textDecoration = TextDecoration.Underline
                ),
                start = startIndex,
                end = endIndex
            )
            addStringAnnotation(
                tag = URL_TAG,
                annotation = url,
                start = startIndex,
                end = endIndex
            )
        }
    }
}
