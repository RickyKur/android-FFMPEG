package com.gvm.demoffmpeg

import android.content.Context
import android.os.Environment
import android.support.v4.content.ContextCompat
import com.gvm.demoffmpeg.slideitem.SlideItemModel
import java.util.*
import kotlin.collections.ArrayList

/**
 * Brought to you by rickykurniawan on 30/08/18.
 */
class OneMinuteCommandVideoBuilder {

    private var mNumberOfInputs = 0
    private var mGeneratedString: MutableList<String> = mutableListOf()
    private var mFilterComplexString: StringBuilder = StringBuilder()
    private var mPreviousLabelName: String = ""
    private var mCategoryName: String = ""

    private lateinit var mSlideModels: List<SlideItemModel>
    private lateinit var mContext: Context

    private val mDirectoryName = "/Demo2"
    val mBasePath = Environment.getExternalStorageDirectory().path + mDirectoryName
    val BASE_FONT_DIR = "$mBasePath/fontDemo2/"
    val BASE_TEMPLATE_DIR = "$mBasePath/templateDemo2/"
    val BASE_OUTPUT_PATH = "$mBasePath/output/"

    var mTotalDuration: Int = 0 /*Accepts total number of slides*/
        set(value) {
            val duration = 80 + ((value - 2) * 180) + 405
            field = duration
        }

    /**
     * Call clear first just to be safe
     */
    fun clear(): OneMinuteCommandVideoBuilder {
        mNumberOfInputs = 0
        mGeneratedString.clear()
        mFilterComplexString = StringBuilder()
        return this
    }
//
//    fun withTotal(total: Int): OneMinuteCommandVideoBuilder {
//        return this
//    }

    fun withContext(context: Context): OneMinuteCommandVideoBuilder {
        mContext = context
        return this
    }

    fun loadModels(slideModels: List<SlideItemModel>): OneMinuteCommandVideoBuilder {
        mSlideModels = slideModels as ArrayList<SlideItemModel>
        mNumberOfInputs = mSlideModels.size
        return this
    }

    fun withCategory(categoryName: String): OneMinuteCommandVideoBuilder {
        mCategoryName = categoryName
        return this
    }

    fun generateInputStringArraysWithFilterComplexCommand(): OneMinuteCommandVideoBuilder {
        for (index in 1..mNumberOfInputs) {
            mGeneratedString.add("-i")
            mGeneratedString.add(mSlideModels[index - 1].imagePath)
        }
        mGeneratedString.add("-f")
        mGeneratedString.add("lavfi")
        mGeneratedString.add("-i")
        mGeneratedString.add("color=${getColorCategoryForFFMPEG(mContext, mCategoryName)}:s=720x720:d=2:sar=1")
        mGeneratedString.add("-filter_complex")
        /*Don't forget to append mfilterComplexString at the end*/
        return this
    }

    fun generateStringBuilderScaleCommand(labelName: String, resolution: String): OneMinuteCommandVideoBuilder {
        val builder = StringBuilder()
        for (index in 1..mNumberOfInputs) {
            val videoIndex = index - 1
            builder.append("[$videoIndex:v]scale=$resolution:$resolution, setsar=1[$labelName$index]; ")
        }
        mFilterComplexString.append(builder.toString())
        mPreviousLabelName = labelName
        return this
    }

    fun generateStringBuilderZoomPanCommand(labelName: String, framePerSecond: String, durationInFrame: String, scale: String):
            OneMinuteCommandVideoBuilder {
        val builder = StringBuilder()
        val defaultEndDuration = "450" /*Duration for last slide that contains punchline*/

        builder.append("[${mPreviousLabelName}1]zoompan=z=1:fps=$framePerSecond:s=$scale:d=80[${labelName}1]; ")
        for (index in 2 until mNumberOfInputs) {
            /*give no zoom effect first*/
            builder.append("[$mPreviousLabelName$index]${generateZoomPanCommand()}:fps=$framePerSecond:s=$scale:d=$durationInFrame[$labelName$index]; ")
        }
        builder.append("[$mPreviousLabelName$mNumberOfInputs]${generateZoomPanCommand()}:fps=$framePerSecond:s=$scale:" +
                "d=$defaultEndDuration[$labelName$mNumberOfInputs]; ")

        mFilterComplexString.append(builder.toString())
        mPreviousLabelName = labelName
        return this
    }

    /*input 0 text is for title*/
    fun generateStringBuilderDrawTextBoxCommand(labelName: String, punchLine1: String, punchLine2: String, fontFile: String, fontFileTitle: String):
            OneMinuteCommandVideoBuilder {
        val title = mSlideModels[0].slideText.replace("\\\\", "\\\\\\\\\\\\\\\\").replace("'", "")
                .replace("%", "persen").replace(":", "")
        val builder = StringBuilder()
        val slideSpeed = "2200"
        val fontSize = "63"
        val fontSize2 = "75"
        val latoBlack = BASE_FONT_DIR + "Lato-Black.ttf"

        /*for title and cover page (page 0)*/
        builder.append("[${mPreviousLabelName}1]drawbox=y=${returnDrawBoxYPosition(title)}:color=black@0.5:width=iw:" +
                "height=${returnDrawBoxHeight(title)}:t=fill, " +
                "drawtext=fontfile=$fontFileTitle:text='$title'" +
                ":x=40:y=${returnDrawTextYPosition(title)}:fontsize=70:line_spacing=12.0:text_shaping=0:fontcolor=white," +
                "drawbox=y=${returnDrawBoxYPositionForTitleSlide(title)}:color=${getColorCategoryForFFMPEG(mContext, mCategoryName)}:width=iw:height=45:t=fill," +
                "drawbox=y=${returnDrawBoxYPositionForTitleSlide(title)}:color=0x00FFD8:width=164:height=45:t=fill," +
                "drawtext=fontfile=$latoBlack:text='1MENIT':fontcolor=black:fontsize=27:x=30:y=${returnDrawTextYPositionForCategoryText(title)}," +
                "drawtext=fontfile=$latoBlack:text='${mCategoryName.toUpperCase()}':" +
                "fontcolor=${getTextColorCategoryForFFMPEG(mContext, mCategoryName)}:fontsize=27:x=195:" +
                "y=${returnDrawTextYPositionForCategoryText(title)}[${labelName}1];")

        for (index in 2 until mNumberOfInputs) {
            val text = mSlideModels[index - 1].slideText.replace("\\\\", "\\\\\\\\\\\\\\\\").replace("'", "")
                    .replace("%", "persen").replace(":", "")
            val drawTextXPosition = "if(lt(t\\,0.1)\\,-tw\\,min(40\\,-tw+($slideSpeed*(t-0.1))))"

            /*For slides page*/
            builder.append("[$mPreviousLabelName$index] drawbox=y=${returnDrawBoxYPosition(text)}:" +
                    "color=black@0.5:width=iw:height=${returnDrawBoxHeight(text)}:t=fill, " +
                    "drawtext=fontfile=$fontFile: text='$text':x=$drawTextXPosition:y=${returnDrawTextYPosition(text)}:fontsize=$fontSize:" +
                    "line_spacing=17.0:fontcolor=white:alpha=if(gte(t\\,7)\\,0\\, 1)[$labelName$index];")
        }
        /*For closing page*/
        val lastIndex = mNumberOfInputs - 1
        val lastText = mSlideModels[lastIndex].slideText.replace("\\\\", "\\\\\\\\\\\\\\\\").replace("'", "")
                .replace("%", "persen").replace(":", "")
        builder.append("[$mPreviousLabelName$mNumberOfInputs]" +

                "drawbox=enable=between(t\\,0\\,3.5):y=${returnDrawBoxYPosition(lastText)}:color=black@0.5:" +
                "width=iw:height=${returnDrawBoxHeight(lastText)}:t=fill, " +
                "drawtext=fontfile=$fontFile:text='$lastText':x=if(lt(t\\,0.1)\\,-tw\\,min(40\\,-tw+($slideSpeed*(t-0.1))))" +
                ":y=${returnDrawTextYPosition(lastText)}:fontsize=$fontSize:line_spacing=17.0:text_shaping=0:fontcolor=white:" +
                "alpha=if(gte(t\\,3.5)\\,0\\, 1), " +

                "drawbox=enable=gt(t\\,4):y=0:x=0:color=black@0.5:width=iw:height=ih:t=fill," +
                "drawtext=fontfile=$fontFile:text='$punchLine2':x=(w-tw)/2:" +
                "y=(-10)+(h-th)/2:fontsize=$fontSize2:" +
                "alpha=if(gte(t\\,4)\\,min((t-4)/2\\, 1)\\, 0):fontcolor=white:line_spacing=20.0, " +

                "drawtext=fontfile=$fontFile:text='$punchLine1':x=(w-tw)/2:" +
                "y=if(gte(t\\,5)\\,min(150\\,((t-5)*500))\\,0):fontcolor=0x00FFD8:fontsize=35:" +
                "alpha=if(gte(t\\,5)\\,min(1\\,(t-5))\\,0):box=1:boxcolor=black:boxborderw=16," +

                "drawtext=fontfile=$fontFile:text='1MENIT':" + /*1Menit*/
                "x=if(gte(t\\,5)\\,max((-73${returnMarginForCategoryText(mContext, mCategoryName)})+(w-tw)/2\\,((75)+(w-tw)/2) - ((t-5)*200))\\,(75)+(w-tw)/2):" +
                "alpha=if(gte(t\\,5)\\,min(1\\,(t-5))\\,0):y=h-210:fontcolor=black:fontsize=30:" +
                "box=1:boxcolor=0x00FFD8:boxborderw=14," +

                "drawtext=fontfile=$fontFile:text='${mCategoryName.toUpperCase()}':" +
                "x=if(gte(t\\,5)\\,min((w/2)+10${returnMarginForCategoryText(mContext, mCategoryName)}\\,((-73)+(w-tw)/2) + ((t-5)*200))\\,(-73)+(w-tw)/2):" +
                "alpha=if(gte(t\\,5)\\,min(1\\,(t-5))\\,0):y=h-210:fontcolor=${getTextColorCategoryForFFMPEG(mContext, mCategoryName)}:fontsize=30:" +
                "box=1:boxcolor=${getColorCategoryForFFMPEG(mContext, mCategoryName)}:boxborderw=14" +

                "[$labelName$mNumberOfInputs];")

        mFilterComplexString.append(builder.toString())
        mPreviousLabelName = labelName
        return this
    }

    private fun generateZoomPanCommand(): String {
        val random = Random().nextInt(2)
        return when (random) {
            0 -> "scale=8000:-1, zoompan=z=min(zoom+0.0008\\, 1.5):y=(ih/2)-(ih/zoom/2)"
            else -> "zoompan=z=if(lte(zoom\\,1.0)\\,1.3\\,max(1.001\\,zoom - 0.0015))"
        }
    }

    fun generateCommandForConcat(): OneMinuteCommandVideoBuilder {
        val builder = StringBuilder()
        for (index in 1..mNumberOfInputs) {
            builder.append("[$mPreviousLabelName$index]")
        }
        builder.append("concat=n=$mNumberOfInputs:v=1:a=0, format=yuv420p[video]")
        mFilterComplexString.append(builder.toString())
        mGeneratedString.add(mFilterComplexString.toString())

        return this
    }

    fun generateOutput(outputName: String): OneMinuteCommandVideoBuilder {
        mGeneratedString.add("-map")
        mGeneratedString.add("[video]")
        mGeneratedString.add(outputName)

        return this
    }

    fun generateCommandForOverlayLogo(inputVideoName: String, outputName: String): OneMinuteCommandVideoBuilder {
        val imageLogo = BaseActivity.BASE_TEMPLATE_DIR + "logo.png"
        val imageLogoBig = BaseActivity.BASE_TEMPLATE_DIR + "logo_black.png"
        val imageFb = BaseActivity.BASE_TEMPLATE_DIR + "fb.png"
        val imageIG = BaseActivity.BASE_TEMPLATE_DIR + "ig.png"
        val imageTwitter = BaseActivity.BASE_TEMPLATE_DIR + "tw.png"
        val imageYoutube = BaseActivity.BASE_TEMPLATE_DIR + "yt.png"
        val imageWhatsapp = BaseActivity.BASE_TEMPLATE_DIR + "wa.png"
        val imageLine = BaseActivity.BASE_TEMPLATE_DIR + "line.png"
        val durationLogoDisappear = (((mNumberOfInputs - 1) * 4) + 1.6).toString()
        val durationOfSlideTransition = (((mNumberOfInputs - 1) * 4) + 6).toString()

        val imageSocMedYPosition = "(H-h)-100"

        mGeneratedString.add("-i"); mGeneratedString.add(inputVideoName) /*[0:v]*/
        mGeneratedString.add("-i"); mGeneratedString.add(imageLogo) /*[1:v]*/

        mGeneratedString.add("-f"); mGeneratedString.add("lavfi"); mGeneratedString.add("-i")
        mGeneratedString.add("color=c=0x00FFD8:s=720x720") /*[2:v] color teal*/
        mGeneratedString.add("-f"); mGeneratedString.add("lavfi"); mGeneratedString.add("-i")
        mGeneratedString.add("color=c=${getColorCategoryForFFMPEG(mContext, mCategoryName)}:s=720x720") /*[3:v]*/
        mGeneratedString.add("-i"); mGeneratedString.add(imageLogoBig) /*[4:v]*/

        mGeneratedString.add("-i"); mGeneratedString.add(imageFb) /*[5:v]*/
        mGeneratedString.add("-i"); mGeneratedString.add(imageIG) /*[6:v]*/
        mGeneratedString.add("-i"); mGeneratedString.add(imageTwitter) /*[7:v]*/
        mGeneratedString.add("-i"); mGeneratedString.add(imageYoutube) /*[8:v]*/
        mGeneratedString.add("-i"); mGeneratedString.add(imageWhatsapp) /*[9:v]*/
        mGeneratedString.add("-i"); mGeneratedString.add(imageLine) /*[10:v]*/
//
        mGeneratedString.add("-filter_complex")

        mFilterComplexString.append("[1:v]scale=(iw*1.25):(ih*1.25)[scl1];" +
                "[0:v][scl1]overlay=x=0:y=if(gte(t\\,$durationLogoDisappear)\\,-200\\,0)," +
                "drawtext=fontfile=" + BaseActivity.BASE_FONT_DIR + "Lato-Black.ttf: text='\\  ${mCategoryName.toUpperCase()}\\                                                                                                       .':fontcolor=" +
                "${getTextColorCategoryForFFMPEG(mContext, mCategoryName)}:fontsize=28:x=192:y=" +
                "if(lte(t\\,3)\\,-th*2\\,if(lte(t\\,6)\\,min(12\\,(-th*2)+((t-3)*100))\\,max(-th*2\\,12-((t-6)*50)))):" +
                "box=1:boxcolor=${getColorCategoryForFFMPEG(mContext, mCategoryName)}:boxborderw=17[logo];" +
                "[4:v]scale=(iw*1.2):(ih*1.2)[scaledImage];" +
                "[2:v][scaledImage]overlay=x=(W-w)/2:y=(H-h)/2[imageLogoOpini];" +

                "[imageLogoOpini][5:v]overlay=x=140:y=$imageSocMedYPosition[imageFB];" +
                "[imageFB][6:v]overlay=x=220:y=$imageSocMedYPosition[imageIG];" +
                "[imageIG][7:v]overlay=x=300:y=$imageSocMedYPosition[imageTW];" +
                "[imageTW][8:v]overlay=x=380:y=$imageSocMedYPosition[imageYT];" +
                "[imageYT][9:v]overlay=x=460:y=$imageSocMedYPosition[imageWA];" +
                "[imageWA][10:v]overlay=x=540:y=$imageSocMedYPosition[imageLN];" +

                "[logo][imageLN]overlay=enable=gt(t\\,$durationOfSlideTransition):shortest=1, format=yuv420p[ovr1];" +
                "[ovr1][3:v]overlay=shortest=1:y=(t-$durationOfSlideTransition)*2100, format=yuv420p")
        mGeneratedString.add(mFilterComplexString.toString())
        mGeneratedString.add(outputName)
        return this
    }

    companion object {

        /**
         * Return total number of lines in text, usually separated by \n
         */
        private fun returnNumberOfEnterInText(currentText: String): Int {
            return "$currentText ".split("\r?\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size
        }

        private fun returnDrawBoxYPosition(currentText: String): String {
            return when {
                returnNumberOfEnterInText(currentText) == 3 -> "(ih-260)"
                returnNumberOfEnterInText(currentText) == 2 -> "(ih-200)"
                else -> "(ih-140)"
            }
        }

        private fun returnDrawBoxYPositionForTitleSlide(title: String): String {
            return when {
                returnNumberOfEnterInText(title) == 3 -> "(ih-305)"
                returnNumberOfEnterInText(title) == 2 -> "(ih-245)"
                else -> "(ih-185)"
            }
        }

        private fun returnDrawTextYPositionForCategoryText(currentText: String): String {
            return when {
                returnNumberOfEnterInText(currentText) == 3 -> "(h-295)"
                returnNumberOfEnterInText(currentText) == 2 -> "(h-235)"
                else -> "(h-175)"
            }
        }

        private fun returnDrawBoxHeight(currentText: String): String {
            return when {
                returnNumberOfEnterInText(currentText) == 3 -> "270"
                returnNumberOfEnterInText(currentText) == 2 -> "210"
                else -> "150"
            }
        }

        private fun returnDrawTextYPosition(currentText: String): String {
            return when {
                returnNumberOfEnterInText(currentText) == 3 -> "(h-230)"
                returnNumberOfEnterInText(currentText) == 2 -> "(h-160)"
                else -> "(h-100)"
            }
        }

        /**
         * Get duration, accepts total slides array
         *
         * 80 for title, (size - 2)*180 for each slides (180 frame each slides), and 450 for end of slides and closing
         */
        fun getTotalDurationInFrameForVideo(size: Int): Int = 80 + ((size - 2) * 180 ) + 450

        /**
         * FPS is set to 45
         */
        fun getTotalDurationInSecondForVideo(size: Int): Double = 80.toDouble().frameToSecond() +
                ((size - 2) * 180.toDouble().frameToSecond() ) + 450.toDouble().frameToSecond()

        private fun Double.frameToSecond(): Double = this / 45

        fun getColorCategory(context: Context, categoryName: String): Int {
            return when (categoryName) {
                context.resources.getString(R.string.string_inspiratif) -> ContextCompat.getColor(context, R.color.color_inspiratif)
                context.resources.getString(R.string.string_kelakuan) -> ContextCompat.getColor(context, R.color.color_kelakuan)
                context.resources.getString(R.string.string_ohgitu) -> ContextCompat.getColor(context, R.color.color_ohgitu)
                context.resources.getString(R.string.string_bangga) -> ContextCompat.getColor(context, R.color.color_bangga)
                context.resources.getString(R.string.string_semesta) -> ContextCompat.getColor(context, R.color.color_semesta)
                context.resources.getString(R.string.string_kisah) -> ContextCompat.getColor(context, R.color.color_kisah)
                context.resources.getString(R.string.string_aha) -> ContextCompat.getColor(context, R.color.color_aha)
                context.resources.getString(R.string.string_politik) -> ContextCompat.getColor(context, R.color.color_politik)
                else -> 0
            }
        }

        fun returnMarginForCategoryText(context: Context, categoryName: String): String {
            return when (categoryName) {
                context.resources.getString(R.string.string_inspiratif) -> "-5"
                context.resources.getString(R.string.string_kelakuan) -> "-1"
                context.resources.getString(R.string.string_ohgitu) -> "+0"
                context.resources.getString(R.string.string_bangga) -> "+0"
                context.resources.getString(R.string.string_semesta) -> "-1"
                context.resources.getString(R.string.string_kisah) -> "+5"
                context.resources.getString(R.string.string_aha) -> "+10"
                context.resources.getString(R.string.string_politik) -> "+2"
                else -> "+0"
            }
        }

        /**
         * For FFMPEG, just use {getColorCategoryForFFMPEG}
         */
        fun getColorCategoryForFFMPEG(context: Context, categoryName: String): String {
            return "0x${Integer.toHexString(getColorCategory(context, categoryName)).substring(2).toUpperCase()}"
        }

        fun getTextColorForCategory(context: Context, categoryName: String): Int {
            return when (categoryName) {
                context.resources.getString(R.string.string_politik),
                context.resources.getString(R.string.string_ohgitu),
                context.resources.getString(R.string.string_bangga),
                context.resources.getString(R.string.string_inspiratif) -> ContextCompat.getColor(context, R.color.white)
                else -> ContextCompat.getColor(context, R.color.black)
            }
        }

        fun getTextColorCategoryForFFMPEG(context: Context, categoryName: String): String {
            return "0x${Integer.toHexString(getTextColorForCategory(context, categoryName)).substring(2).toUpperCase()}"
        }
    }

    fun buildString(): MutableList<String> {
        return mGeneratedString
    }
}