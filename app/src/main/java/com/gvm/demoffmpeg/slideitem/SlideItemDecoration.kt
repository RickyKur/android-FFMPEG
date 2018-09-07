package com.gvm.demoffmpeg.slideitem

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import com.gvm.demoffmpeg.MathUtil

/**
 * Brought to you by rickykurniawan on 05/09/18.
 */
class SlideItemDecoration(private val context: Context): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        val vh = parent?.getChildViewHolder(view)
        if (vh != null) {
            val position = parent.getChildLayoutPosition(view)
            if (position == 0) outRect?.left = MathUtil.returnValueAsDp(context, 15)
            else outRect?.left = 0
        }
    }
}