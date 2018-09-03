package com.gvm.demoffmpeg

/**
 * Brought to you by rickykurniawan on 30/08/18.
 */
object OneMinuteCommandVideoBuilder {

    private var mNumberOfInputs = 0
    private var mGeneratedString: MutableList<String> = mutableListOf()
    private var mFilterComplexString: StringBuilder = StringBuilder()
    private var mPreviousLabelName: String = ""

    /**
     * Call clear first just to be safe
     */
    fun clear(): OneMinuteCommandVideoBuilder {
        mNumberOfInputs = 0
        mGeneratedString.clear()
        mFilterComplexString = StringBuilder()
        return this
    }

    /**
     * Specifying number of inputs, first input will be title cover
     */
    fun withTotal(numberOfInputs: Int): OneMinuteCommandVideoBuilder {
        mNumberOfInputs = numberOfInputs
        return this
    }

    fun generateInputStringArraysWithFilterComplexCommand(imageNames: List<String>, colorString: String?): OneMinuteCommandVideoBuilder {
        for (index in 1 .. mNumberOfInputs) {
            mGeneratedString.add("-i")
            mGeneratedString.add(imageNames[index - 1])
        }
        mGeneratedString.add("-f")
        mGeneratedString.add("lavfi")
        mGeneratedString.add("-i")
        mGeneratedString.add("color=green:s=720x720:d=2:sar=1")
        mGeneratedString.add("-filter_complex")
        /*Don't forget to append mfilterComplexString at the end*/
        return this
    }

    fun generateStringBuilderScaleCommand(labelName: String, resolution: String): OneMinuteCommandVideoBuilder {
        val builder = StringBuilder()
        for (index in 1 .. mNumberOfInputs) {
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
        builder.append("[${mPreviousLabelName}1]zoompan=z=1:fps=$framePerSecond:s=$scale:d=80[${labelName}1]; ")
        for (index in 2 until  mNumberOfInputs) {
            /*give no zoom effect first*/
            builder.append("[$mPreviousLabelName$index]zoompan=z=1:fps=$framePerSecond:s=$scale:d=$durationInFrame[$labelName$index]; ")
        }
        builder.append("[$mPreviousLabelName$mNumberOfInputs]zoompan=z=1:fps=$framePerSecond:s=$scale:d=405[$labelName$mNumberOfInputs]; ")
        mFilterComplexString.append(builder.toString())
        mGeneratedString.add(mFilterComplexString.toString())
        mPreviousLabelName = labelName
        return this
    }

    /*input 0 text is for title*/
    fun generateStringBuilderDrawTextBoxCommand(labelName: String, title: String, texts: List<String>, fontFile: String):
            OneMinuteCommandVideoBuilder {
        val builder = StringBuilder()
        val slideSpeed = "2200"
        val fontSize = "63"
        val fontSize2 = "75"

        /*for title and cover page (page 0)*/
        builder.append("[${mPreviousLabelName}1]drawbox=y=(ih-200):color=black@0.5:width=iw:height=210:t=fill, " +
                "drawtext=fontfile=$fontFile:text='$title'" +
                ":x=40:y=${returnDrawTextYPosition(title)}:fontsize=70:line_spacing=12.0:text_shaping=0:fontcolor=white," +
                "drawbox=y=${returnDrawBoxYPositionForTitleSlide(title)}:color=0x0087EA:width=iw:height=45:t=fill," +
                "drawbox=y=${returnDrawBoxYPositionForTitleSlide(title)}:color=0x00FFD8:width=165:height=45:t=fill," +
                "drawtext=fontfile=$fontFile:text='1MENIT':fontcolor=black:fontsize=27:x=30:y=h-235," +
                "drawtext=fontfile=$fontFile:text='OH GITU!':fontcolor=white:fontsize=27:x=195:y=h-235[${labelName}1]")

        for (index in 2 until mNumberOfInputs) {
            val text = texts[index - 1]
            val drawTextXPosition = "if(lt(t\\,0.1)\\,-tw\\,min(40\\,-tw+($slideSpeed*(t-0.1))))"

            /*For slides page*/
            builder.append("[$mPreviousLabelName$index] drawbox=y=${returnDrawBoxHeight(text)}:" +
            "color=black@0.5:width=iw:height=${returnDrawBoxHeight(text)}:t=fill, " +
            "drawtext=fontFile=$fontFile: text='$text':x=$drawTextXPosition:y=${returnDrawTextYPosition(text)}:fontsize=$fontSize:" +
            "line_spacing=12.0:fontcolor=white:alpha=if(gte(t\\,7)\\,0\\, 1)[$labelName$index];")
        }
        /*For closing page*/
        val lastIndex = mNumberOfInputs - 1
        val lastText = texts[lastIndex]
        builder.append("[$mPreviousLabelName$mNumberOfInputs]" +

                "drawbox=enable=between(t\\,0\\,3.5):y=${returnDrawBoxYPosition(lastText)}:color=black@0.5:" +
                "width=iw:height=${returnDrawBoxHeight(lastText)}:t=fill, " +
                "drawtext=fontfile=$fontFile:text=$lastText:x=if(lt(t\\,0.1)\\,-tw\\,min(40\\,-tw+($slideSpeed*(t-0.1))))" +
                ":y=h-220:fontsize=$fontSize:line_spacing=12.0:text_shaping=0:fontcolor=white:" +
                "alpha=if(gte(t\\,3.5)\\,0\\, 1), " +

                "drawbox=enable=gt(t\\,4):y=0:x=0:color=black@0.5:width=iw:height=ih:t=fill," +
                "drawtext=fontfile=$fontFile:text='\\      SABARRR....\nDEMI PRESTASI':x=(w-tw)/2:" +
                "y=(-10)+(h-th)/2:fontsize=$fontSize2:" +
                "alpha=if(gte(t\\,4)\\,min((t-4)/2\\, 1)\\, 0):fontcolor=white:line_spacing=20.0, " +

                "drawtext=fontfile=$fontFile:text='SHARE KALAU BARU TAHU':x=(w-tw)/2:" +
                "y=if(gte(t\\,5)\\,min(150\\,((t-5)*500))\\,0):fontcolor=0x00FFD8:fontsize=35:" +
                "alpha=if(gte(t\\,5)\\,min(1\\,(t-5))\\,0):box=1:boxcolor=black:boxborderw=16," +

                "drawtext=fontfile=$fontFile:text='1MENIT':" + /*1Menit*/
                "x=if(gte(t\\,5)\\,max((-78)+(w-tw)/2\\,((75)+(w-tw)/2) - ((t-5)*200))\\,(75)+(w-tw)/2):" +
                "alpha=if(gte(t\\,5)\\,min(1\\,(t-5))\\,0):y=h-210:fontcolor=black:fontsize=30:" +
                "box=1:boxcolor=0x00FFD8:boxborderw=14," +

                "drawtext=fontfile=$fontFile:text='OH GITU!':" +
                "x=if(gte(t\\,5)\\,min((74)+(w-tw)/2\\,((-78)+(w-tw)/2) + ((t-5)*200))\\,(-78)+(w-tw)/2):" +
                "alpha=if(gte(t\\,5)\\,min(1\\,(t-5))\\,0):y=h-210:fontcolor=white:fontsize=30:" +
                "box=1:boxcolor=0x0087EA:boxborderw=14" +

                "[$labelName$mNumberOfInputs];")

        mFilterComplexString.append(builder.toString())
        mPreviousLabelName = labelName
        return this
    }

    fun generateCommandForConcat(): OneMinuteCommandVideoBuilder {
        val builder = StringBuilder()
        for (index in 1 .. mNumberOfInputs) {
            builder.append("[$mPreviousLabelName$index]")
        }
        builder.append("concat=n=$mNumberOfInputs:v=1:a=0, format=yuv420p[video]")
        mFilterComplexString.append(builder.toString())
        mGeneratedString.add(mFilterComplexString.toString())

        return this
    }

    fun output(outputName: String): OneMinuteCommandVideoBuilder {
        mGeneratedString.add("-map")
        mGeneratedString.add("[video]")
        mGeneratedString.add(outputName)

        return this
    }

    /**
     * Return total number of lines in text, usually separated by \n
     */
    fun returnNumberOfEnterInText(currentText: String): Int {
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

    private fun returnDrawBoxHeight(currentText: String): String {
        return when {
            returnNumberOfEnterInText(currentText) == 3 -> "270"
            returnNumberOfEnterInText(currentText) == 2 -> "210"
            else -> "150"
        }
    }

    private fun returnDrawTextYPosition(currentText: String): String {
        return when {
            returnNumberOfEnterInText(currentText) == 3 -> "(h-220)"
            returnNumberOfEnterInText(currentText) == 2 -> "(h-160)"
            else -> "(h-100)"
        }
    }

    fun buildString(): MutableList<String> {
        return mGeneratedString
    }
}