package com.gvm.demoffmpeg

import android.content.Context
import android.graphics.Typeface
import java.util.*

/**
 * Brought to you by rickykurniawan on 07/09/18.
 */
object FontUtil {

    enum class FontType {
        LATO_REGULAR,
        LATO_BOLD,
        LATO_BLACK
    }

    /**
     * Store all the typefaces in cache to avoid recreating font object
     */
    private val mTypeFaces = Hashtable<FontType, Typeface>()

    fun getFontTypeFace(context: Context, fontType: FontType): Typeface? {
        var tempTypeFace: Typeface? = mTypeFaces[fontType]
        if (tempTypeFace == null) {
            val fontName = when (fontType) {
                FontUtil.FontType.LATO_REGULAR -> "fonts/Lato-Regular.ttf"
                FontUtil.FontType.LATO_BOLD -> "fonts/Lato-Bold.ttf"
                FontUtil.FontType.LATO_BLACK -> "fonts/Lato-Black.ttf"
            }
            tempTypeFace = Typeface.createFromAsset(context.assets, fontName)
            mTypeFaces[fontType] = tempTypeFace
        }
        return tempTypeFace
    }

    fun addSpaceForEachCharacter(text: String): String {
        val stringBuffer = StringBuffer()
        val charArray = text.toCharArray()
        for (char in charArray) {
            stringBuffer.append("$char ")
        }
        return stringBuffer.toString()
    }
}