package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.isEmpty
import androidx.core.view.ViewCompat
import androidx.core.view.children
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.groupByBounds
import ru.skillbranch.skillarticles.extensions.setPaddingOptionally
import kotlin.properties.Delegates

class MarkdownContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private lateinit var copyListener: (String) -> Unit
    private var elements: List<MarkdownElement> = emptyList()
    private var layoutManager: LayoutManager = LayoutManager()

    //for restore
    private var ids = arrayListOf<Int>()

    var textSize by Delegates.observable(14f) { _, old, value ->
        if (value == old) return@observable
        this.children.forEach {
            it as IMarkdownView
            it.fontSize = value
        }
    }
    var isLoading: Boolean = true
    private val padding = context.dpToIntPx(8)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedHeight = paddingTop
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        children.forEach {
            measureChild(it, widthMeasureSpec, heightMeasureSpec)
            usedHeight += it.measuredHeight
        }

        usedHeight += paddingBottom
        setMeasuredDimension(width, usedHeight)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var usedHeight = paddingTop
        val bodyWidth = right - left - paddingLeft - paddingRight
        val left = paddingLeft
        val right = paddingLeft + bodyWidth

        children.forEach {
            if (it is MarkdownTextView) {
                it.layout(
                    left - paddingLeft / 2,
                    usedHeight,
                    r - paddingRight / 2,
                    usedHeight + it.measuredHeight
                )
            } else {
                it.layout(
                    left,
                    usedHeight,
                    right,
                    usedHeight + it.measuredHeight
                )
            }
            usedHeight += it.measuredHeight
        }
    }

    fun setContent(content: List<MarkdownElement>) {
        if (elements.isNotEmpty()) return
        elements = content
        content.forEachIndexed { ind, it ->
            when (it) {
                is MarkdownElement.Text -> {
                    val tv = MarkdownTextView(context, textSize).apply {
                        setPaddingOptionally(
                            left = padding,
                            right = padding
                        )
                    }

                    MarkdownBuilder(context)
                        .markdownToSpan(it)
                        .run {
                            tv.setText(this, TextView.BufferType.SPANNABLE)
                        }

                    addView(tv)
                }

                is MarkdownElement.Image -> {
                    val iv = MarkdownImageView(
                        context,
                        textSize,
                        it.image.url,
                        it.image.text,
                        it.image.alt
                    )
                    addView(iv)
                    layoutManager.attachToParent(iv, ind)
                }

                is MarkdownElement.Scroll -> {
                    val sv = MarkdownCodeView(
                        context,
                        textSize,
                        it.blockCode.text.toString()
                    )
                    sv.copyListener = copyListener
                    addView(sv)
                    layoutManager.attachToParent(sv, ind)
                }
            }
        }
    }

    fun renderSearchResult(searchResult: List<Pair<Int, Int>>) {
        children.forEach { view ->
            view as IMarkdownView
            view.clearSearchResult()
        }

        if (searchResult.isEmpty()) return


        val bounds = elements.map { it.bounds }
        val result = searchResult.groupByBounds(bounds)

        children.forEachIndexed { index, view ->
            view as IMarkdownView
//search for child with markdown element offset
            view.renderSearchResult(result[index], elements[index].offset)
        }
    }

    fun renderSearchPosition(
        searchPosition: Pair<Int, Int>?
    ) {
        searchPosition ?: return
        val bounds = elements.map { it.bounds }

        val index = bounds.indexOfFirst { (start, end) ->
            val boundRange = start..end
            val (startPos, endPos) = searchPosition
            startPos in boundRange && endPos in boundRange
        }

        if (index == -1) return
        val view = getChildAt(index)
        view as IMarkdownView
        view.renderSearchPosition(searchPosition, elements[index].offset)
    }

    fun clearSearchResult() {
        children.forEach { view ->
            view as IMarkdownView
            view.clearSearchResult()
        }
    }

    fun setCopyListener(listener: (String) -> Unit) {
        copyListener = listener
    }

    override fun onSaveInstanceState(): Parcelable? {
        val state = SavedState(super.onSaveInstanceState())
        state.layout = layoutManager
        return state
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>?) {
        //save children manually without markdown text view
        children
            .forEachIndexed { ind, it ->
                //Log.e("MarkdownContentView", "dispatchSaveInstanceState ${it.id}")
                if (it !is MarkdownTextView) it.saveHierarchyState(layoutManager.container)
            }
        //save only markdownContentView
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>?) {
        val superState = container?.get(id) as SavedState
        layoutManager = superState.layout
        children.forEachIndexed { ind, it ->
            //Log.e("MarkdownContentView", "dispatchRestoreInstanceState ${it.id}")
            if (it !is MarkdownTextView) layoutManager.restoreChild(it, ind)
        }
        super.dispatchRestoreInstanceState(container)
    }


    private class LayoutManager() : Parcelable {
        var ids: MutableMap<Int, Int> = LinkedHashMap()
        var container: SparseArray<Parcelable> = SparseArray()

        constructor(parcel: Parcel) : this() {
            ids = parcel.readSerializable() as LinkedHashMap<Int, Int>
            container =
                parcel.readSparseArray<Parcelable>(this::class.java.classLoader) as SparseArray<Parcelable>
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeSerializable(ids as LinkedHashMap)
            parcel.writeSparseArray(container)
        }

        fun attachToParent(view: View, index: Int) {
            //Log.e("MarkdownContentView", "attach to parent $index")
            if (container.isEmpty()) {
                ViewCompat.generateViewId().also {
                    view.id = it
                    ids[index] = it
                }
            } else {
                view.id = ids[index]!!
                view.restoreHierarchyState(container)
            }
        }

        fun restoreChild(view: View, index: Int) {
            //Log.e("MarkdownContentView", "restoreChild ${view.id} $index")
            view.id = ids[index]!!
            view.restoreHierarchyState(container)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<LayoutManager> {
            override fun createFromParcel(parcel: Parcel): LayoutManager = LayoutManager(parcel)
            override fun newArray(size: Int): Array<LayoutManager?> = arrayOfNulls(size)
        }
    }

    private class SavedState : BaseSavedState, Parcelable {
        var layout: LayoutManager = LayoutManager()

        constructor(superState: Parcelable?) : super(superState)

        constructor(src: Parcel) : super(src) {
            layout = src.readParcelable(LayoutManager::class.java.classLoader)!!
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeParcelable(layout, flags)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }
}