package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.*
import android.view.inputmethod.BaseInputConnection
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.switchmaterial.SwitchMaterial
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.dpToPx
import ru.skillbranch.skillarticles.extensions.setPaddingOptionally
import ru.skillbranch.skillarticles.ui.custom.behaviors.SubmenuBehavior
import kotlin.math.hypot

class ArticleSubmenu(baseContext: Context):
    ViewGroup(ContextThemeWrapper(baseContext, R.style.ArticleBarsTheme), null, 0),
    CoordinatorLayout.AttachedBehavior {

    var isOpen = false
    private var menuWidth: Int = context.dpToIntPx(200)
    private var menuHeight: Int = context.dpToIntPx(96)

    //Views
    val btnTextDown: CheckableImageView
    val btnTextUp: CheckableImageView
    val switchMode: SwitchMaterial
    val tvLabel: TextView



    init {
        id = R.id.submenu
        val marg = dpToIntPx(8)
        val elev = dpToPx(8)
        layoutParams = CoordinatorLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.BOTTOM
            dodgeInsetEdges = Gravity.BOTTOM
            setMargins(0, 0, marg, marg)
        }

        //add material bg for handle elevation and color surface
        val materialBg = MaterialShapeDrawable.createWithElevationOverlay(context)
        materialBg.elevation = elevation
        background = materialBg
        materialBg.elevation = elev
        elevation = elev
        isVisible = false

        val backGroundRes = context.attrValue(R.attr.selectableItemBackground)
        btnTextDown = CheckableImageView(context).apply {
            setPaddingOptionally(top = context.dpToIntPx(12), bottom = context.dpToIntPx(12))
            setImageResource(R.drawable.ic_title_black_24dp)
            imageTintList = context.getColorStateList(R.color.tint_color)
            setBackgroundColor(backGroundRes)
        }
        addView(btnTextDown)

        btnTextUp = CheckableImageView(context).apply {
            setPaddingOptionally(top = context.dpToIntPx(8), bottom = context.dpToIntPx(8))
            setImageResource(R.drawable.ic_title_black_24dp)
            imageTintList = context.getColorStateList(R.color.tint_color)
            setBackgroundColor(backGroundRes)
        }
        addView(btnTextUp)

        switchMode = SwitchMaterial(context).apply {
        }
        addView(switchMode)

        tvLabel = TextView(context).apply {
            text = "Темный режим"
            setTextColor(context.attrValue(R.attr.colorOnSurface))
        }
        addView(tvLabel)
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<ArticleSubmenu> {
        return SubmenuBehavior()
    }

    fun open() {
        if (isOpen || !isAttachedToWindow) return
        isOpen = true
        animatedShow()
    }

    fun close() {
        if (!isOpen || !isAttachedToWindow) return
        isOpen = false
        animatedHide()
    }

    private fun animatedShow() {
        val endRadius = hypot(menuWidth.toDouble(), menuHeight.toDouble()).toInt()
        val anim = ViewAnimationUtils.createCircularReveal(
            this,
            menuWidth,
            menuHeight,
            0f,
            endRadius.toFloat()
        )
        anim.doOnStart {
            visibility = View.VISIBLE
        }
        anim.start()
    }

    private fun animatedHide() {
        val endRadius = hypot(menuWidth.toDouble(), menuHeight.toDouble()).toInt()
        val anim = ViewAnimationUtils.createCircularReveal(
            this,
            menuWidth,
            menuHeight,
            endRadius.toFloat(),
            0f
        )
        anim.doOnEnd {
            visibility = View.GONE
        }
        anim.start()
    }

    //save state
    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.ssIsOpen = isOpen
        return savedState
    }

    //restore state
    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        if (state is SavedState) {
            isOpen = state.ssIsOpen
            visibility = if (isOpen) View.VISIBLE else View.GONE
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wms = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        val hms = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        tvLabel.measure(wms, hms)
        switchMode.measure(wms, hms)
        setMeasuredDimension(menuWidth, menuHeight)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val bodyWidth = r - l - paddingLeft - paddingRight
        val left = paddingLeft
        val right = paddingLeft + bodyWidth

        val textBtnHeight = context.dpToIntPx(40);
        val center = (r - l) / 2
        val horizontalDividerY = paddingTop + textBtnHeight

        btnTextDown.layout(
            left,
            paddingTop,
            center,
            horizontalDividerY
        )

        btnTextUp.layout(
            center,
            paddingTop,
            right,
            horizontalDividerY
        )

        val horizontalMargin = context.dpToIntPx(16)
        val lablelCenterY = (measuredHeight - horizontalDividerY) / 2 + horizontalDividerY
        switchMode.layout(
            right - horizontalMargin - switchMode.measuredWidth,
            lablelCenterY - switchMode.measuredHeight / 2,
            right - horizontalMargin,
            lablelCenterY + switchMode.measuredHeight / 2
        )

        tvLabel.layout(
            left + horizontalMargin,
            lablelCenterY - tvLabel.measuredHeight / 2,
            left + horizontalMargin + tvLabel.measuredWidth,
            lablelCenterY + tvLabel.measuredHeight / 2
        )
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun dispatchDraw(canvas: Canvas?) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.color_divider)
            strokeWidth = context.dpToPx(1)
        }
        val w = measuredWidth.toFloat()
        val h = measuredHeight.toFloat()
        val h1 = context.dpToPx(40)

        canvas?.drawLine(0f, h1, w, h1, paint)  // horizontal divider
        canvas?.drawLine(w / 2, 0f, w / 2, h1, paint) // vertical divider
        super.dispatchDraw(canvas)
    }

    private class SavedState : BaseSavedState, Parcelable {
        var ssIsOpen: Boolean = false

        constructor(superState: Parcelable?) : super(superState)

        constructor(src: Parcel) : super(src) {
            ssIsOpen = src.readInt() == 1
        }

        override fun writeToParcel(dst: Parcel, flags: Int) {
            super.writeToParcel(dst, flags)
            dst.writeInt(if (ssIsOpen) 1 else 0)
        }

        override fun describeContents() = 0

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }

}