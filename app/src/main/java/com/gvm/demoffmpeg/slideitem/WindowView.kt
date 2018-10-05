package com.gvm.demoffmpeg.slideitem

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.gvm.demoffmpeg.R
import com.gvm.demoffmpeg.SlideShowActivity
import kotlinx.android.synthetic.main.card_category_item.view.*
import kotlinx.android.synthetic.main.view_passcode_view.view.*
import kotlinx.android.synthetic.main.view_window_categories.view.*

/**
 * Brought to you by rickykurniawan on 06/09/18.
 */
@SuppressLint("InflateParams")
class WindowView(private val mContext: Context) {

    private var mWindow: WindowManager? = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var mPreviousCategoryViewClicked: View? = null
    private var mPreviousSongViewClicked: View? = null
    private var mCategoryView: View? = null
    private var mPassCodeView: View? = null
    private var mSongView: View? = null

    var mDurationProgress: Int = 0

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

    fun showPassCodeWindowView(generateVideo: () -> Unit) {
        val params = windowDefaultLayoutParams
        if (mPassCodeView != null) {
            mPassCodeView!!.passcode_view.clearText()
            mWindow?.addView(mPassCodeView, params)
        } else {
            val inflateView = LayoutInflater.from(mContext).inflate(R.layout.view_passcode_view, null)
            inflateView.close.setOnClickListener { dismissPassCodeView() }
            inflateView.passcode_view.requestToShowKeyboard()
            inflateView.passcode_view.setPasscodeEntryListener {
                if (it.length == 4) {
                    if (it == "1234") {
                        generateVideo()
                        showPassCodeViewLoading(inflateView as ViewGroup)
                    } else {
                        inflateView.passcode_label.text = "Invalid pin, please try again"
                        inflateView.passcode_view.clearText()
                    }
                }
            }
            mPassCodeView = inflateView
            mWindow?.addView(mPassCodeView, params)
        }
    }

    fun showOneMinuteVideoCategories(listener: CategoryClickListener) {
        val colors = arrayListOf(R.color.color_inspiratif, R.color.color_kelakuan, R.color.color_ohgitu, R.color.color_bangga,
                R.color.color_semesta, R.color.color_kisah, R.color.color_aha, R.color.color_politik)
        val stringArrays = arrayListOf(R.string.string_inspiratif, R.string.string_kelakuan, R.string.string_ohgitu,
                R.string.string_bangga, R.string.string_semesta, R.string.string_kisah, R.string.string_aha, R.string.string_politik)
        val params = windowDefaultLayoutParams
        if (mCategoryView != null) {
            mWindow?.addView(mCategoryView, params)
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
            dismissButton.setOnClickListener { mWindow?.removeView(mCategoryView) }
            mCategoryView = inflateView
            mWindow?.addView(inflateView, params)
        }
    }

    fun showMusicList(actionDismiss: () -> Unit, listener: SongClickListener) {
        val songs = arrayListOf(R.string.awed, R.string.ca, R.string.hiasom,
                R.string.jr, R.string.jb, R.string.lil, R.string.tms)
        val params = windowDefaultLayoutParams
        if (mSongView != null) {
            mWindow?.addView(mSongView, params)
        } else {
            val inflateView = LayoutInflater.from(mContext).inflate(R.layout.view_window_categories, null)
            for (index in 0 until songs.size) {
                val cardItemView = LayoutInflater.from(mContext).inflate(R.layout.card_category_item, null)
                cardItemView.category_text.text = mContext.resources.getString(songs[index])
                cardItemView.card_category_item_e.setOnClickListener {
                    onSongClicked(cardItemView.category_card_item)
                    listener.onSongClicked(cardItemView.category_text.text.toString())
                }
                inflateView.cards_container.addView(cardItemView)
            }
            val dismissButton = inflateView.dismiss_window
            dismissButton.setOnClickListener {
                mWindow?.removeView(mSongView)
                actionDismiss()
            }
            mSongView = inflateView
            mWindow?.addView(inflateView, params)
        }
    }

    private fun onCategoryClicked(view: View) {
        if (view.category_selected.visibility == View.VISIBLE) return
        view.category_selected.visibility = View.VISIBLE
        if (mPreviousCategoryViewClicked != null) {
            mPreviousCategoryViewClicked!!.category_selected.visibility = View.GONE
        }
        mPreviousCategoryViewClicked = view
    }

    private fun onSongClicked(view: View) {
        if (view.category_selected.visibility == View.VISIBLE) return
        view.category_selected.visibility = View.VISIBLE
        if (mPreviousSongViewClicked != null) {
            mPreviousSongViewClicked!!.category_selected.visibility = View.GONE
        }
        mPreviousSongViewClicked = view
    }

    private fun showPassCodeViewLoading(inflateView: ViewGroup) {
        val inputMethod = (mContext as SlideShowActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethod.hideSoftInputFromWindow(inflateView.passcode_view.windowToken, 0)

        inflateView.passcode_container.visibility = View.GONE
        inflateView.loading_container.visibility = View.VISIBLE
        inflateView.close.visibility = View.GONE
        inflateView.cup_image.setBackgroundResource(R.drawable.cup_background)

        val animationDrawable: AnimationDrawable? = inflateView.cup_image.background as AnimationDrawable
        kotlin.run {
            animationDrawable?.start()
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateProgressBar(currentProgress: Int) {
        Log.d("Current Progress", currentProgress.toString())
        val progress = (currentProgress * 100) / mDurationProgress
        mPassCodeView!!.progress_linear.progress = progress
        mPassCodeView!!.progress_percentage.text = "$progress%"
    }

    fun updateProgressExplanation(explanation: String) {
        mPassCodeView!!.progress_explanation.text = explanation
    }

    fun showSuccessViewLoading() {
        mPassCodeView!!.success_container.visibility = View.VISIBLE
        mPassCodeView!!.close.visibility = View.VISIBLE
        mPassCodeView!!.loading_container.visibility = View.GONE
        mPassCodeView!!.passcode_container.visibility = View.GONE
    }

    fun dismissPassCodeView() {
        mPassCodeView!!.close.visibility = View.VISIBLE
        mPassCodeView!!.passcode_container.visibility = View.VISIBLE
        mPassCodeView!!.loading_container.visibility = View.GONE
        mPassCodeView!!.success_container.visibility = View.GONE
        mWindow?.removeView(mPassCodeView)
    }

    fun dismiss() {
        mCategoryView = null
        mPassCodeView = null
        mWindow = null
        mPreviousCategoryViewClicked = null
    }
}