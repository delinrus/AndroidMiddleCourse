package ru.skillbranch.skillarticles.ui.custom.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.Selection
import android.text.Spannable
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.VisibleForTesting
import androidx.core.view.setPadding
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.dpToPx
import ru.skillbranch.skillarticles.extensions.setPaddingOptionally

@SuppressLint("ViewConstructor")
class MarkdownCodeView private constructor(
    context: Context,
    fontSize: Float
) : ViewGroup(context, null, 0), IMarkdownView {
    override var fontSize: Float = fontSize
        set(value) {
            tvCodeView.textSize = value * 0.85f
            field = value
        }
    override val spannableContent: Spannable
        get() = tvCodeView.text as Spannable

    var copyListener: ((String) -> Unit)? = null

    private lateinit var codeString: String

    //views
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val ivCopy: ImageView

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val ivSwitch: ImageView

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val svScroll: HorizontalScrollView
    private val tvCodeView: MarkdownTextView

    //colors
    @ColorInt private val darkSurface: Int = context.attrValue(R.attr.darkSurfaceColor)
    @ColorInt private val darkOnSurface: Int = context.attrValue(R.attr.darkOnSurfaceColor)
    @ColorInt private val lightSurface: Int = context.attrValue(R.attr.lightSurfaceColor)
    @ColorInt private val lightOnSurface: Int = context.attrValue(R.attr.lightOnSurfaceColor)

    //sizes
    private val iconSize = context.dpToIntPx(12)
    private val radius = context.dpToPx(8)
    private val padding = context.dpToIntPx(8)
    private val fadingOffset = context.dpToIntPx(144)
    private val textExtraPadding = context.dpToIntPx(80)
    private val scrollBarHeight = context.dpToIntPx(2)

    //for layout
    private var isSingleLine = false
    private var isDark = false
    private var isManual = false
    private val bgColor
        get() = when {
            !isManual -> context.attrValue(R.attr.colorSurface)
            isDark -> darkSurface
            else -> lightSurface
        }

    private val textColor
        get() = when {
            !isManual -> context.attrValue(R.attr.colorOnSurface)
            isDark -> darkOnSurface
            else -> lightOnSurface
        }

    init {
        tvCodeView = MarkdownTextView(context, fontSize).apply {
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            setTextColor(textColor)
            //setPaddingOptionally(right = textExtraPadding)
        }

        svScroll = object : HorizontalScrollView(context) {
            //TODO custom scroll
        }.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            isHorizontalFadingEdgeEnabled = true
            scrollBarSize = scrollBarHeight
            //add code text to scroll
            addView(tvCodeView)
        }

        addView(svScroll)

        ivCopy = ImageView(context).apply {
            setImageResource(R.drawable.ic_content_copy_black_24dp)
            imageTintList = ColorStateList.valueOf(textColor)
            setOnClickListener {
                copyListener?.invoke(codeString.toString())
            }
        }
        addView(ivCopy)

        ivSwitch = ImageView(context).apply {
            setImageResource(R.drawable.ic_brightness_medium_black_24dp)
            imageTintList = ColorStateList.valueOf(textColor)
            setOnClickListener { toggleColors() }
        }
        addView(ivSwitch)
    }

    constructor(
        context: Context,
        fontSize: Float,
        code: CharSequence,
        padding: Int,
        cornerRadius: Float
    ) : this(context, fontSize) {
        codeString = code.toString()
        isSingleLine = code.lines().size == 1
        tvCodeView.setText(codeString, TextView.BufferType.SPANNABLE)
        setPadding(padding)
        background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = FloatArray(8).apply { fill(radius, 0, size) }
            color = ColorStateList.valueOf(bgColor)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedHeight = 0
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        measureChild(svScroll, widthMeasureSpec, heightMeasureSpec)
        measureChild(ivCopy, widthMeasureSpec, heightMeasureSpec)

        usedHeight += svScroll.measuredHeight + paddingTop + paddingBottom
        setMeasuredDimension(width, usedHeight)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val usedHeight = paddingTop
        val bodyWidth = r - l - paddingLeft - paddingRight
        val left = paddingLeft
        val right = paddingLeft + bodyWidth


        svScroll.layout(
            left,
            usedHeight,
            right,
            usedHeight + svScroll.measuredHeight
        )
    }

    override fun renderSearchPosition(searchPosition: Pair<Int, Int>, offset: Int) {
        //TODO implement me
    }

    private fun toggleColors() {
        //TODO implement me
    }


    private fun applyColors() {
        //TODO implement me
    }
}
