package com.gvm.demoffmpeg.slideitem

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Brought to you by rickykurniawan on 04/09/18.
 */
class SlideItemPlusVH(itemView: View, slideItemListener: SlideItemListener): RecyclerView.ViewHolder(itemView) {

    init {
        itemView.setOnClickListener {
            slideItemListener.addItemClick()
        }
    }
}