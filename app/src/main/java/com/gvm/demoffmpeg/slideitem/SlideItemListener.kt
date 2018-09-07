package com.gvm.demoffmpeg.slideitem

/**
 * Brought to you by rickykurniawan on 05/09/18.
 */
interface SlideItemListener {

    fun onSlideItemClick(position: Int)

    fun deleteItemClick(position: Int)

    fun addItemClick()
}