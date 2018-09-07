package com.gvm.demoffmpeg

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.gvm.demoffmpeg.slideitem.*
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.content_slideshow_item.*

/**
 * Brought to you by rickykurniawan on 04/09/18.
 */
class SlideShowActivity : BaseActivity(), SlideItemListener, CategoryClickListener {

    private val mAdapter = SlideItemAdapter(this)
    private var mCurrentPosition = 0
    private lateinit var mWindow: WindowView
    private lateinit var mTextDisposable: Disposable
    private var mCategoryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slideshow_create)
        mWindow = WindowView(this)
        setAdapter()
        addSlides()
        setListeners()
        initViews(0, null)
    }

    /**
     * Add default slides, minimal 2 items, change this value later
     */
    private fun addSlides() {
        mAdapter.addSlideItem(SlideItemModel(selected = true))
        mAdapter.addSlideItem(SlideItemModel())
    }

    private fun initViews(position: Int, slideItem: SlideItemModel?) {
        setCategoryVisibility(position)
        setTitleText(position, slideItem)
        setPreviewImageAndText(position, slideItem)
    }

    private fun setCategoryVisibility(position: Int) {
        val visibility = if (position == 0) View.VISIBLE
        else View.GONE
        cardViewCategory.visibility = visibility
    }

    private fun setTitleText(position: Int, slideItem: SlideItemModel?) {
        text_input_title.text = if (position == 0) "Title" else "Text"
        if (slideItem != null) {
            text_edittext.setText(slideItem.slideText)
        }
    }

    private fun setPreviewImageAndText(position: Int, slideItem: SlideItemModel?) {
        /*Later*/
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerview.layoutManager = layoutManager
        recyclerview.addItemDecoration(SlideItemDecoration(this))
        recyclerview.adapter = mAdapter
    }

    private fun setListeners() {
        cardViewCategory_e.setOnClickListener { mWindow.showOneMinuteVideoCategories(this) }
        publishButton.setOnClickListener {
            Toast.makeText(this, "Publish", Toast.LENGTH_SHORT).show()
        }
        mTextDisposable = RxTextView.textChanges(text_edittext).subscribe ({
            val currentSlideItem = mAdapter.getSlideItems(mCurrentPosition)
            currentSlideItem.slideText = it.toString()
        }, {
            Log.e("Error","${it.cause}")
        })
    }

    override fun onCategoryClicked(categoryName: String) {
        mCategoryName = categoryName
    }

    override fun onSlideItemClick(position: Int) {
        if (mCurrentPosition == position) return
        val currentSlideItem = mAdapter.getSlideItems(position)
        mAdapter.setSelected(mCurrentPosition)
        mAdapter.setSelected(position)
        mCurrentPosition = position
        initViews(position, currentSlideItem)
    }

    override fun deleteItemClick(position: Int) {
        mAdapter.removeItem(position)
        if (mCurrentPosition == position) {
            mAdapter.setSelected(position - 1)
            mCurrentPosition = position - 1
            val previousSlideItems = mAdapter.getSlideItems(position - 1)
            initViews(position - 1, previousSlideItems)
        }
    }

    override fun addItemClick() {
        mAdapter.addSlideItem(SlideItemModel())
    }

    override fun onDestroy() {
        mTextDisposable.dispose()
        mWindow.dismiss()
        super.onDestroy()
    }
}