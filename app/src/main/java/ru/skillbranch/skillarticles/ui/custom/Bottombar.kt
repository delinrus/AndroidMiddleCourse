package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.VisibleForTesting
import androidx.constraintlayout.widget.Group
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.google.android.material.shape.MaterialShapeDrawable
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.setPaddingOptionally
import ru.skillbranch.skillarticles.ui.custom.behaviors.BottombarBehavior
import kotlin.math.hypot

class Bottombar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {

    var isSearchMode = false

    //Views
    val btnLike: CheckableImageView
    val btnBookmark: CheckableImageView
    val btnShare: ImageView
    val btnSettings: CheckableImageView
    private val searchBar: SearchBar
    private val group: Group

    val btnSearchClose: ImageView
        get() = searchBar.btnSearchClose
    val tvSearchResult: TextView
        get() = searchBar.tvSearchResult
    val btnResultDown: ImageView
        get() = searchBar.btnResultDown
    val btnResultUp: ImageView
        get() = searchBar.btnResultUp


    private val iconSize = context.dpToIntPx(56)
    private val iconPadding = context.dpToIntPx(16)
    private val iconTintColor = context.getColorStateList(R.color.tint_color)

    override fun getBehavior(): CoordinatorLayout.Behavior<Bottombar> {
        return BottombarBehavior()
    }

    init {
        btnLike = CheckableImageView(context).apply {
            setPadding(iconPadding)
            imageTintList = iconTintColor
            setBackgroundResource(R.drawable.ripple)
            setImageResource(R.drawable.like_states)
        }
        addView(btnLike)

        btnBookmark = CheckableImageView(context).apply {
            setPadding(iconPadding)
            imageTintList = iconTintColor
            setBackgroundResource(R.drawable.ripple)
            setImageResource(R.drawable.bookmark_states)
        }
        addView(btnBookmark)

        btnShare = ImageView(context).apply {
            setPadding(iconPadding)
            imageTintList = iconTintColor
            setBackgroundResource(R.drawable.ripple)
            setImageResource(R.drawable.ic_share_black_24dp)
            isClickable = true
            isFocusable = true
        }
        addView(btnShare)

        btnSettings = CheckableImageView(context).apply {
            setPadding(iconPadding)
            imageTintList = iconTintColor
            setBackgroundResource(R.drawable.ripple)
            setImageResource(R.drawable.ic_format_size_black_24dp)
        }
        addView(btnSettings)

        group = Group(context).apply {
            addView(btnLike)
            addView(btnBookmark)
            addView(btnShare)
            addView(btnSettings)
        }

        searchBar = SearchBar().apply {
            isVisible = false
        }
        addView(searchBar)

        val materialBg = MaterialShapeDrawable.createWithElevationOverlay(context)
        materialBg.elevation = elevation
        background = materialBg
    }

    override fun onSaveInstanceState(): Parcelable {
        val saveState = SavedState(super.onSaveInstanceState())
        saveState.ssIsSearchMode = isSearchMode
        return saveState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if (state is SavedState) {
            isSearchMode = state.ssIsSearchMode
            searchBar.isVisible = isSearchMode
            group.isVisible = !isSearchMode
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val usedWidth = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        measureChild(searchBar, widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(usedWidth, iconSize)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val bodyWidth = r - l - paddingLeft - paddingRight
        val left = paddingLeft
        val right = paddingLeft + bodyWidth

        val iconHeight = (b - t - iconSize) / 2
        var offset = left
        btnLike.layout(
            offset,
            iconHeight,
            offset + iconSize,
            iconHeight + iconSize
        )
        offset += iconSize;

        btnBookmark.layout(
            offset,
            iconHeight,
            offset + iconSize,
            iconHeight + iconSize
        )
        offset += iconSize;

        btnShare.layout(
            offset,
            iconHeight,
            offset + iconSize,
            iconHeight + iconSize
        )

        btnSettings.layout(
            right - iconSize,
            iconHeight,
            right,
            iconHeight + iconSize
        )

        searchBar.layout(
            l,
            paddingTop,
            r,
            iconSize - paddingBottom
        )
    }

    fun setSearchState(isSearch: Boolean) {
        if (isSearch == isSearchMode || !isAttachedToWindow) return
        isSearchMode = isSearch
        if (isSearchMode) animatedShowSearch()
        else animateHideSearch()
    }

    fun setSearchInfo(searchCount: Int = 0, position: Int = 0) {
        btnResultUp.isEnabled = searchCount > 0
        btnResultDown.isEnabled = searchCount > 0

        tvSearchResult.text =
            if (searchCount == 0) "Not found" else "${position.inc()} of $searchCount"

        when (position) {
            0 -> btnResultUp.isEnabled = false
            searchCount.dec() -> btnResultDown.isEnabled = false
        }

    }

    private fun animatedShowSearch() {
        searchBar.isVisible = true
        val endRadius = hypot(width.toDouble(), height / 2.toDouble())
        val va = ViewAnimationUtils.createCircularReveal(
            searchBar,
            width,
            height / 2,
            0f,
            endRadius.toFloat()
        )
        va.doOnEnd {
            group.isVisible = false
        }
        va.start()
    }

    private fun animateHideSearch() {
        group.isVisible = true
        val endRadius = hypot(width.toDouble(), height / 2.toDouble())
        val va = ViewAnimationUtils.createCircularReveal(
            searchBar,
            width,
            height / 2,
            endRadius.toFloat(),
            0f
        )
        va.doOnEnd {
            searchBar.isVisible = false
        }
        va.start()
    }

    private class SavedState : BaseSavedState, Parcelable {
        var ssIsSearchMode: Boolean = false

        constructor(superState: Parcelable?) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            ssIsSearchMode = parcel.readByte() != 0.toByte()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeByte(if (ssIsSearchMode) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }

    private inner class SearchBar : ViewGroup(context, null, 0) {
        val btnSearchClose: ImageView
        val tvSearchResult: TextView
        val btnResultDown: ImageView
        val btnResultUp: ImageView

        private val searchIconColor = context.getColorStateList(R.color.tint_search_color)

        @ColorInt
        private val colorPrimary: Int = context.attrValue(R.attr.colorPrimary)

        init {
            setBackgroundColor(resources.getColor(R.color.color_on_article_bar, context.theme))
            btnSearchClose = ImageView(context).apply {
                setImageResource(R.drawable.ic_close_black_24dp)
                imageTintList = searchIconColor
                setPadding(iconPadding)
                setBackgroundResource(R.drawable.ripple)
            }
            addView(btnSearchClose)

            tvSearchResult = TextView(context).apply {
                text = resources.getString(R.string.not_found)
                setTextColor(colorPrimary)
                setPaddingOptionally(left = context.dpToIntPx(16))
            }
            addView(tvSearchResult)

            btnResultDown = ImageView(context).apply {
                setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
                imageTintList = searchIconColor
                setPadding(iconPadding)
                setBackgroundResource(R.drawable.ripple)
            }
            addView(btnResultDown)

            btnResultUp = ImageView(context).apply {
                setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
                imageTintList = searchIconColor
                setPadding(iconPadding)
                setBackgroundResource(R.drawable.ripple)
            }
            addView(btnResultUp)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            tvSearchResult.measure(widthMeasureSpec, heightMeasureSpec)
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            val bodyWidth = r - l - paddingLeft - paddingRight
            val left = paddingLeft
            val right = paddingLeft + bodyWidth

            val iconHeight = (b - t - iconSize) / 2
            var offset = left
            btnSearchClose.layout(
                offset,
                iconHeight,
                offset + iconSize,
                iconHeight + iconSize
            )
            offset += iconSize;

            tvSearchResult.layout(
                offset,
                (b - t - tvSearchResult.measuredHeight) / 2,
                offset + tvSearchResult.measuredWidth,
                (b - t + tvSearchResult.measuredHeight) / 2
            )
            offset += iconSize;


            btnResultUp.layout(
                right - iconSize,
                iconHeight,
                right,
                iconHeight + iconSize
            )

            btnResultDown.layout(
                right - iconSize * 2,
                iconHeight,
                right - iconSize,
                iconHeight + iconSize
            )
        }
    }
}