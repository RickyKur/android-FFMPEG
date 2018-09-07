package com.gvm.demoffmpeg.slideitem

import android.support.v7.widget.RecyclerView
import android.view.View
import com.gvm.demoffmpeg.R
import kotlinx.android.synthetic.main.slide_item.view.*

/**
 * Brought to you by rickykurniawan on 04/09/18.
 */
class SlideItemVH(itemView: View, private val listener: SlideItemListener) : RecyclerView.ViewHolder(itemView) {

    fun bind(slideItemModel: SlideItemModel, isPunchLine: Boolean) {
        setTitle(isPunchLine)
        setText(isPunchLine)
        setDeleteButtonVisibility(isPunchLine)
        setBackground(slideItemModel.selected)
        setClickEvent()
    }

    private fun setDeleteButtonVisibility(isPunchLine: Boolean) {
        val visibility = when {
            (isPunchLine || adapterPosition < 2) -> View.GONE
            else -> View.VISIBLE
        }
        itemView.deleteButton.visibility = visibility
    }

    private fun setTitle(isPunchLine: Boolean) {
        val text = when {
            (adapterPosition == 0) -> "Cover"
            isPunchLine -> "Punchline"
            else -> String.format("Slide %s", adapterPosition.toString())
        }
        itemView.slide_title.text = text
    }

    private fun setText(isPunchLine: Boolean) {
        val text = when {
            (adapterPosition == 0) || isPunchLine -> ""
            else -> adapterPosition.toString()
        }
        itemView.slide_item_page_number.text = text
    }

    private fun setBackground(isSelected: Boolean) {
        val background = if (isSelected) R.drawable.cardview_bg_selected
        else R.drawable.cardview_bg

        itemView.slide_item_bg.setBackgroundResource(background)
    }

    private fun setClickEvent() {
        itemView.slide_item_bg.setOnClickListener { listener.onSlideItemClick(adapterPosition) }
        itemView.deleteButton.setOnClickListener { listener.deleteItemClick(adapterPosition) }
    }
}