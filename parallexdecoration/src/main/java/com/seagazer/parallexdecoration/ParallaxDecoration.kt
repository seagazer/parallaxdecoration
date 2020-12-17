package com.seagazer.parallexdecoration

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView

/**
 * A special item decoration for recyclerView, it can display any bitmaps by looper as background of recyclerView.
 * The bitmaps of background can opt to be scrolled within this recyclerView in a parallax fashion.
 * Notice that all of the bitmaps must has the same size.
 *
 * If you want to custom your item decoration, extend this class and override ItemDecoration.onDraw(Canvas, RecyclerView, RecyclerView.State)
 * and call super.onDraw(Canvas, RecyclerView, RecyclerView.State).
 *
 * Author: Seagazer
 * Date: 2020/12/5
 */
open class ParallaxDecoration(private val context: Context) : RecyclerView.ItemDecoration() {
    private var screenWidth = 0
    private var screenHeight = 0
    private var maxVisibleCount = 0
    private var minVisibleCount = 0
    private var bitmapWidth = 0
    private var bitmapHeight = 0
    private var bitmapCount = 0
    private var bitmapPool = mutableListOf<Bitmap>()
    private var isHorizontal = true

    /**
     * The parallax percent to be scrolled.
     */
    var parallax: Float = 1.0f

    /**
     * True auto scale the bitmap to fill the size of canvas.
     */
    var autoFill = false
    private var am: ActivityManager? = null
    private lateinit var bitmapOption: BitmapFactory.Options
    private var scale = 1f
    private var scaleBitmapWidth = 0
    private var scaleBitmapHeight = 0

    /**
     * Init bitmaps to draw background.
     */
    fun setupBitmap(bitmaps: List<Bitmap>) {
        bitmapPool.clear()
        bitmapPool.addAll(bitmaps)
        updateConfig()
    }

    /**
     * Add a bitmap to draw background.
     */
    fun addBitmap(bitmap: Bitmap) {
        bitmapPool.add(bitmap)
        updateConfig()
    }

    /**
     * Init local resource drawables to draw background.
     */
    fun setupResource(resources: List<Int>) {
        bitmapPool.clear()
        for (resourceId in resources) {
            bitmapPool.add(decodeBitmap(resourceId))
        }
        updateConfig()
    }

    /**
     * Add a local resource drawable to draw background.
     */
    fun addResource(resourceId: Int) {
        bitmapPool.add(decodeBitmap(resourceId))
        updateConfig()
    }

    private fun decodeBitmap(resourceId: Int): Bitmap {
        // check device and memory save
        if (am == null) {
            bitmapOption = BitmapFactory.Options()
            am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            if (am!!.isLowRamDevice) {
                bitmapOption.inPreferredConfig = Bitmap.Config.RGB_565
            }
        }
        return BitmapFactory.decodeResource(context.resources, resourceId, bitmapOption)
    }

    private fun updateConfig() {
        bitmapCount = bitmapPool.size
        bitmapWidth = bitmapPool[0].width
        bitmapHeight = bitmapPool[0].height
        scaleBitmapWidth = bitmapWidth
        scaleBitmapHeight = bitmapHeight
        // check bitmap width and height
        bitmapPool.forEach {
            if (it.width != bitmapWidth || it.height != bitmapHeight) {
                throw RuntimeException("Every bitmap of the backgrounds must has the same size!")
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (bitmapPool.isNotEmpty()) {
            // if layoutManager is null, just throw exception
            val lm = parent.layoutManager!!
            // step1. check orientation
            isHorizontal = lm.canScrollHorizontally()
            // step2. check maxVisible count
            // step3. if autoFill, calculate the scale bitmap size
            if (screenWidth == 0 || screenHeight == 0) {
                screenWidth = c.width
                screenHeight = c.height
                val allInScreen: Int
                val doubleOutOfScreen: Boolean
                if (isHorizontal) {
                    if (autoFill) {
                        scale = screenHeight * 1f / bitmapHeight
                        scaleBitmapWidth = (bitmapWidth * scale).toInt()
                    }
                    allInScreen = screenWidth / scaleBitmapWidth
                    doubleOutOfScreen = screenWidth % scaleBitmapWidth > 1
                } else {
                    if (autoFill) {
                        scale = screenWidth * 1f / bitmapWidth
                        scaleBitmapHeight = (bitmapHeight * scale).toInt()
                    }
                    allInScreen = screenHeight / scaleBitmapHeight
                    doubleOutOfScreen = screenHeight % scaleBitmapHeight > 1
                }
                minVisibleCount = allInScreen + 1
                maxVisibleCount = if (doubleOutOfScreen) allInScreen + 2 else minVisibleCount
            }
            // step4. find the firstVisible index
            // step5. calculate the firstVisible offset
            val parallaxOffset: Float
            val firstVisible: Int
            val firstVisibleOffset: Float
            if (isHorizontal) {
                parallaxOffset = lm.computeHorizontalScrollOffset(state) * parallax
                firstVisible = (parallaxOffset / scaleBitmapWidth).toInt()
                firstVisibleOffset = parallaxOffset % scaleBitmapWidth
            } else {
                parallaxOffset = lm.computeVerticalScrollOffset(state) * parallax
                firstVisible = (parallaxOffset / scaleBitmapHeight).toInt()
                firstVisibleOffset = parallaxOffset % scaleBitmapHeight
            }
            // step6. calculate the best draw count
            val bestDrawCount =
                if (firstVisibleOffset.toInt() == 0) minVisibleCount else maxVisibleCount
            // step7. translate to firstVisible offset
            c.save()
            if (isHorizontal) {
                c.translate(-firstVisibleOffset, 0f)
            } else {
                c.translate(0f, -firstVisibleOffset)
            }
            // step8. if autoFill, scale the canvas to draw
            if (autoFill) {
                c.scale(scale, scale)
            }
            // step9. draw from current first visible bitmap, the max looper count is the best draw count by step6
            for ((i, currentIndex) in (firstVisible until firstVisible + bestDrawCount).withIndex()) {
                if (isHorizontal) {
                    c.drawBitmap(
                        bitmapPool[currentIndex % bitmapCount],
                        i * bitmapWidth.toFloat(),
                        0f,
                        null
                    )
                } else {
                    c.drawBitmap(
                        bitmapPool[currentIndex % bitmapCount],
                        0f,
                        i * bitmapHeight.toFloat(),
                        null
                    )
                }
            }
            c.restore()
        }
    }

}