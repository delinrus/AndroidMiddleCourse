package ru.skillbranch.skillarticles.ui.custom.markdown

import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import androidx.core.text.getSpans
import ru.skillbranch.skillarticles.ui.custom.spans.SearchFocusSpan
import ru.skillbranch.skillarticles.ui.custom.spans.SearchSpan

interface IMarkdownView {
    var fontSize: Float
    val spannableContent: Spannable

    fun renderSearchResult(
        results: List<Pair<Int, Int>>,
        offset: Int
    ) {
        //TODO implement me
    }

    fun renderSearchPosition(
        searchPosition: Pair<Int, Int>,
        offset: Int
    ){
        //TODO implement me
    }

    fun clearSearchResult(){
        //TODO implement me
    }
}