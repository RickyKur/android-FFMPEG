package com.gvm.demoffmpeg.slideitem

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.gvm.demoffmpeg.R

/**
 * Brought to you by rickykurniawan on 04/09/18.
 */
class SlideItemAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSlideItems = arrayListOf<SlideItemModel>()
    var mPunchLine = SlideItemModel()

    private val slideItem = 0
    private var slideItemPlus = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            slideItem -> {
                val slideView = LayoutInflater.from(context).inflate(R.layout.slide_item, parent, false)
                SlideItemVH(slideView, context as SlideItemListener)
            }
            else -> {
                val slidePlusView = LayoutInflater.from(context).inflate(R.layout.slide_item_plus, parent, false)
                SlideItemPlusVH(slidePlusView, context as SlideItemListener)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       if (holder is SlideItemVH) {
           val punchLine = isPunchLine(position)
           val slideItemModel = if (punchLine) mPunchLine
           else mSlideItems[position]
           holder.bind(slideItemModel, punchLine)
       }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == mSlideItems.size) {
            return slideItemPlus
        }
        return slideItem
    }

    override fun getItemCount(): Int {
        return mSlideItems.size + 2
    }

    fun isPunchLine(position: Int): Boolean {
        return (position >= mSlideItems.size)
    }

    fun getSlideItems(position: Int): SlideItemModel {
        return if (isPunchLine(position)) mPunchLine
        else mSlideItems[position]
    }

    fun addSlideItem(slideItem: SlideItemModel) {
        mSlideItems.add(slideItem)
        /*Notify item inserted at position*/
        notifyItemInserted(mSlideItems.size - 1)
    }

    fun setSelected(position: Int) {
        getSlideItems(position).selected = !getSlideItems(position).selected
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        mSlideItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mSlideItems.size - position)
    }
}