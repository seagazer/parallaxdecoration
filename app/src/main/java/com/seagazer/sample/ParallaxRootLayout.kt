package com.seagazer.sample

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import kotlin.math.atan
import kotlin.math.tan

/**
 *
 * Author: Seagazer
 * Date: 2020/12/6
 */
class ParallaxRootLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var angle = 0f
    private var width = 0f
    private var height = 0f
    private var path1: Path? = null
    private var path2: Path? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        width = w.toFloat()
        height = h.toFloat()
        path1 = Path()
        path2 = Path()
//        path1 = Path().apply {
//            moveTo(width * 0.1f, 0f)
//            lineTo(width, 0f)
//            lineTo(width, height * 0.3f)
//            lineTo(width * 0.1f, height * 0.5f)
//            close()
//        }
//        path2 = Path().apply {
//            moveTo(0f, height * 0.7f)
//            lineTo(0f, height)
//            lineTo(width * 0.9f, height)
//            lineTo(width * 0.9f, height * 0.5f)
//            close()
//        }
        angle = atan(height * 0.2f / width)
    }

    private val anim = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1200
        interpolator = OvershootInterpolator(1.5f)
        addUpdateListener {
            val percent: Float = it.animatedValue as Float
            path1?.apply {
                val r = 1.1f - percent
                reset()
                moveTo(width, 0f)
                lineTo(width, height * 0.3f)
                lineTo(width * r, height * 0.3f + tan(angle) * width * percent)
                lineTo(width * r, 0f)
                close()
            }
            path2?.apply {
                reset()
                moveTo(0f, height * 0.7f)
                lineTo(0f, height)
                lineTo(width * 0.9f * percent, height)
                lineTo(width * 0.9f * percent, height * 0.7f - tan(angle) * width * 0.9f * percent)
                close()
            }
            invalidate()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        anim.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (anim.isRunning) {
            anim.end()
            anim.cancel()
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        canvas!!.drawColor(resources.getColor(R.color.colorAccent))
        path1?.run {
            paint.color = resources.getColor(R.color.colorPrimaryDark)
            canvas.drawPath(this, paint)
        }
        path2?.run {
            paint.color = resources.getColor(R.color.colorPrimary)
            canvas.drawPath(this, paint)
        }
        super.dispatchDraw(canvas)
    }

}