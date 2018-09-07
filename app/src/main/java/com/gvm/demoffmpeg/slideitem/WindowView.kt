package com.gvm.demoffmpeg.slideitem

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.gvm.demoffmpeg.R
import kotlinx.android.synthetic.main.card_category_item.view.*
import kotlinx.android.synthetic.main.view_window_categories.view.*

/**
 * Brought to you by rickykurniawan on 06/09/18.
 */
@SuppressLint("InflateParams")
class WindowView(private val mContext: Context) {

    private var mWindow: WindowManager? = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var mPreviousViewClicked: View? = null
    private var mInflateView: View? = null

    private val windowDefaultLayoutParams: WindowManager.LayoutParams
        get() {
            val params = WindowManager.LayoutParams()
            params.height = WindowManager.LayoutParams.MATCH_PARENT
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
            params.format = PixelFormat.TRANSPARENT
            params.windowAnimations = android.R.style.Animation_InputMethod
            return params
        }

    fun showOneMinuteVideoCategories(listener: CategoryClickListener) {
        val colors = arrayListOf(R.color.color_inspiratif, R.color.color_kelakuan, R.color.color_ohgitu, R.color.color_bangga,
                R.color.color_semesta, R.color.color_kisah, R.color.color_aha, R.color.color_politik)
        val stringArrays = arrayListOf(R.string.string_inspiratif, R.string.string_kelakuan, R.string.string_ohgitu,
                R.string.string_bangga, R.string.string_semesta, R.string.string_kisah, R.string.string_aha, R.string.string_politik)
        val params = windowDefaultLayoutParams
        if (mInflateView != null) {
            mWindow?.addView(mInflateView, params)
        } else {
            val inflateView = LayoutInflater.from(mContext).inflate(R.layout.view_window_categories, null)
            for (index in 0 until colors.size) {
                val cardItemView = LayoutInflater.from(mContext).inflate(R.layout.card_category_item, null)
                cardItemView.category_text.text = mContext.resources.getString(stringArrays[index])
                cardItemView.category_color.setImageResource(colors[index])
                cardItemView.card_category_item_e.setOnClickListener {
                    onCategoryClicked(cardItemView.category_card_item)
                    listener.onCategoryClicked(cardItemView.category_text.text.toString())
                }
                inflateView.cards_container.addView(cardItemView)
            }
            val dismissButton = inflateView.dismiss_window
            dismissButton.setOnClickListener {
                mWindow?.removeView(inflateView)
            }
            mInflateView = inflateView
            mWindow?.addView(inflateView, params)
        }
    }

    private fun onCategoryClicked(view: View) {
        view.category_selected.visibility = View.VISIBLE
        if (mPreviousViewClicked != null) {
            mPreviousViewClicked!!.category_selected.visibility = View.GONE
        }
        mPreviousViewClicked = view
    }

    fun dismiss() {
        mWindow = null
        mPreviousViewClicked = null
    }
}