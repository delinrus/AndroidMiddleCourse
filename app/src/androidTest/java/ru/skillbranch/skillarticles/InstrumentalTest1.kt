package ru.skillbranch.skillarticles

import android.graphics.*
import android.graphics.drawable.VectorDrawable
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.view.View.TEXT_DIRECTION_FIRST_STRONG
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.*
import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import ru.skillbranch.skillarticles.markdown.spans.*


/**
 *  Instrumented test, which will execute on an Android device
 *
 *  See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
class InstrumentalTest1 {
    companion object {
        var scaleDensity: Float = 0f

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            scaleDensity =
                ApplicationProvider.getApplicationContext<App>().resources.displayMetrics.scaledDensity
        }
    }

    //defaults
    private val defaultColor = Color.GRAY
    private val currentMargin = 0
    private val lineTop = 0 //lineTop for line under test
    private val lineBase = 14.dp() //lineBase for line under test
    private val lineBottom = 20.dp() //lineBottom for line under test
    private val defaultFontAscent = (-10).dp() //16sp fontsize
    private val defaultFontDescent = (4).dp() //16sp fontsize
    private val canvasWidth = 360.dp()

    //mocks
    private lateinit var canvas: Canvas
    private lateinit var paint: Paint
    private lateinit var layout: Layout

    //text under test
    private lateinit var text: SpannableString

    //overwrite mocks before each test
    @Before
    fun setup() {
        canvas = mockk(relaxed = true) { //overwrite only need functions other functions relaxed
            every { getWidth() } returns canvasWidth
        }
        paint = mockk(relaxed = true) {
            every { getColor() } returns defaultColor
        }
        layout = mockk(relaxed = true)

        text = SpannableString("text")
    }

    @Test
    fun unordered_list() {
        //settings
        val color = Color.RED
        val gap = 8.dpf()
        val radius = 4.dpf()

        //set span on text
        val span = UnorderedListSpan(gap, radius, color)
        text.setSpan(span, 0, 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        //check leading margin
        assertEquals((4 * radius + gap).toInt(), span.getLeadingMargin(true))


        //bullet draw
        span.drawLeadingMargin(
            canvas, paint, currentMargin, TEXT_DIRECTION_FIRST_STRONG,
            lineTop, lineBase, lineBottom, text, 0, text.length,
            true, layout
        )

        //check order call
        verifyOrder {
            //check first set color to paint
            paint.color = color
            //check draw circle bullet
            canvas.drawCircle(
                gap + currentMargin + radius,
                (lineTop + lineBottom) / 2f,
                radius, paint
            )
            //check paint color restore
            paint.color = defaultColor
        }
    }

    @Test
    fun draw_quote() {
        //settings
        val color = Color.RED
        val gap = 8.dpf()
        val lineWidth = 4.dpf()

        val span = BlockquotesSpan(gap, lineWidth, color)
        text.setSpan(span, 0, 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        //check leading margin
        assertEquals((lineWidth + gap).toInt(), span.getLeadingMargin(true))

        //check line draw
        span.drawLeadingMargin(
            canvas, paint, currentMargin, TEXT_DIRECTION_FIRST_STRONG,
            lineTop, lineBase, lineBottom, text, 0, text.length,
            true, layout
        )

        //check order call
        verifyOrder {
            //check first set color to painyt
            paint.color = color
            paint.strokeWidth = lineWidth
            //check draw line
            canvas.drawLine(
                lineWidth / 2f,
                lineTop.toFloat(),
                lineWidth / 2,
                lineBottom.toFloat(),
                paint
            )

            //check paint color restore
            paint.color = defaultColor
        }

    }

    private fun Int.dp() = (this * scaleDensity).toInt()
    private fun Int.dpf() = this * scaleDensity
}
