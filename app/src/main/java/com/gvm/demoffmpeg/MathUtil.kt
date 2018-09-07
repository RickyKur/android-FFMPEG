package com.gvm.demoffmpeg

import android.content.Context
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Brought to you by rickykurniawan on 06/09/18.
 */
object MathUtil {

    /**
     * Return px to dp
     *
     * @param context context of resources
     * @param px      px to convert to dp
     * @return dp value
     */
    fun returnValueAsDp(context: Context, px: Int): Int {
        return try {
            val typedValueInDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px.toFloat(), context.resources.displayMetrics)
            typedValueInDp.toInt()
        } catch (ex: NumberFormatException) {
            px
        }
    }

    /**
     * Return px to font sp
     *
     * @param context context object
     * @param px      px convert to sp
     * @return sp value
     */
    fun returnValueAsSp(context: Context, px: Int): Float {
        return try {
            return px / context.resources.displayMetrics.scaledDensity
        } catch (ex: Exception) {
            px.toFloat()
        }
    }

    fun convertSpToPixels(sp: Float, context: Context): Int =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics).toInt()

    /**
     * Convert thousands to K, million to M, billion to B
     *
     * @param count value of count
     * @return String formatted of value
     */
    fun withSuffix(count: Int): String {
        if (count < 1000) return "" + count
        val exp = (Math.log(count.toDouble()) / Math.log(1000.0)).toInt()
        return String.format("%.1f %c",
                count / Math.pow(1000.0, exp.toDouble()),
                "KMGTPE"[exp - 1])
    }

    /**
     * Get a percentage value
     *
     * @param numerator   number of fraction
     * @param denominator number of total
     * @return int percentage
     */
    fun getPercentage(numerator: Int, denominator: Int): Int {
        val percentage = numerator * 100.0 / denominator
        return Math.round(percentage).toInt()
    }

    /**
     * Return ceiling value
     *
     * @param value value to be converted
     * @return Ceiling value
     */
    fun ceilingValue(value: Float): Float = Math.ceil(value.toDouble()).toFloat()

    /**
     * Return floor value
     *
     * @param value value to be converted
     * @return Floor value
     */
    fun floorValue(value: Float): Float = Math.floor(value.toDouble()).toFloat()


    /**
     * Return screen width in dp
     *
     * @param context Context for display metrics
     * @return Value of screen width
     */
    fun getScreenWidth(context: Context): Float {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay

        return display.width.toFloat()
    }

    /**
     * Return screen height in dp
     *
     * @param context Context for display metrics
     * @return Value of screen height
     */
    fun getScreenHeight(context: Context): Float {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay

        return display.height.toFloat()
    }

    /**
     * Get margin for polling picture, give extra margin for device older than lollipop
     * otherwise the view will take another row below
     *
     * @return int margin
     */
    val marginSpaceForPollingPicture: Int
        get() = if (Build.VERSION.SDK_INT >= 21) 15
        else 20

    /**
     * Sets view to have an equal width and height
     *
     * @param view View to adjust
     */
    fun setEqualWidthAndHeight(view: View) {
        try {
            view.post {
                val dimension = view.measuredWidth
                if (dimension == 0) {
                    setEqualWidthAndHeight(view) /*Retry if height still returns 0*/
                    return@post
                }
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.height = dimension
                params.width = FrameLayout.LayoutParams.MATCH_PARENT
                view.layoutParams = params
            }
        } catch (ex: Exception) {
            Log.e("Error","Error setting view equals width and height")
        }
    }

    fun roundDecimal(value: Double): Double {
        return try {
            val decimalFormat = DecimalFormat("#.##")
            decimalFormat.roundingMode = RoundingMode.CEILING
            java.lang.Double.parseDouble(decimalFormat.format(value))
        } catch (ex: NumberFormatException) {
            ex.printStackTrace()
            0.0
        }
    }

    fun getSixteenToNineRatio(width: Float): Double = ((width * 9) / 16).toDouble()

    fun getTwentyFiveToSixteenRatio(width: Float): Double = ((width * 25) / 16).toDouble()

    fun getDMVideoHeightBasedOnRatio(context: Context, ratio: Double): Int {
        var ratioVal = ratio
        val width = getScreenWidth(context)
        if (ratioVal <= 0.0) { /*Avoid error from daily motion if video ratio returned is 0 or below*/
            ratioVal = 1.0 /*Sets the default daily motion ratio to 1.0*/
        }
        val height = (width / ratioVal).toFloat()
        return height.toInt()
    }
}