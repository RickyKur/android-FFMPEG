package com.gvm.demoffmpeg

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.gvm.demoffmpeg.slideitem.*
import com.jakewharton.rxbinding2.widget.RxTextView
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.content_slideshow_item.*
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler
import nl.bravobit.ffmpeg.FFmpeg

/**
 * Brought to you by rickykurniawan on 04/09/18.
 */
class SlideShowActivity : BaseActivity(), SlideItemListener, CategoryClickListener, SongClickListener {

    private val mAdapter = SlideItemAdapter(this)
    private var mCurrentPosition = 0
    private lateinit var mWindow: WindowView
    private var mCategoryName: String? = null
    private var mSongName: String? = null
    private val mCompositeDisposable = CompositeDisposable()
    private var mMediaPlayer: MediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slideshow_create)
        initMaterialDialog()
        setFontType()
        setAdapter()
        addSlides()
        setListeners()
        initViews(0, null)
    }

    private fun initMaterialDialog() {
        mWindow = WindowView(this)
    }

    private fun setFontType() {
        val latoBlack = FontUtil.getFontTypeFace(this, FontUtil.FontType.LATO_BLACK)
        text_preview.typeface = latoBlack
        text_1menit.typeface = latoBlack
        text_1menit2.typeface = latoBlack
        text_category.typeface = latoBlack
        text_category2.typeface = latoBlack
        punchline1.typeface = latoBlack
        punchline2.typeface = latoBlack
    }

    /**
     * Add default slides, minimal 2 items, change this value later
     */
    private fun addSlides() {
        mAdapter.addSlideItem(SlideItemModel(selected = true))
        mAdapter.addSlideItem(SlideItemModel())
    }

    private fun initViews(position: Int, slideItem: SlideItemModel?) {
        setCategoryTextVisibility(position)
        setMusicCardVisibility(position)
        setCategoryButtonVisibility(position)
        setTitleText(position, slideItem)
        setPreviewImageAndText(slideItem)
        setPunchLineItemVisibility(position)
    }

    private fun setMusicCardVisibility(position: Int) {
        val visibility = if (position == 0) View.GONE
        else View.VISIBLE
        music_card.visibility = visibility
    }

    private fun setCategoryTextVisibility(position: Int) {
        val visibility = if (position == 0) View.VISIBLE
        else View.GONE
        text_category_container.visibility = visibility
    }

    private fun setCategoryButtonVisibility(position: Int) {
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

    private fun setPreviewImageAndText(slideItem: SlideItemModel?) {
        if (slideItem != null && slideItem.imagePath.isNotEmpty()) {
            val uri = slideItem.imagePath
            image_preview.visibility = View.VISIBLE
            text_container.visibility = View.VISIBLE
            image_camera.visibility = View.GONE
            Glide.with(this).load(uri).into(image_preview)
        } else {
            image_preview.visibility = View.GONE
            text_container.visibility = View.GONE
            image_camera.visibility = View.VISIBLE
        }
    }

    private fun setPunchLineItemVisibility(position: Int) {
        val isPunchLine = mAdapter.isPunchLine(position)
        if (isPunchLine) {
            punchline_layout.visibility = View.VISIBLE
            punchline_container1.visibility = View.VISIBLE
            punchline_container2.visibility = View.VISIBLE
            edittext_container.visibility = View.GONE

        } else {
            punchline_layout.visibility = View.GONE
            punchline_container1.visibility = View.GONE
            punchline_container2.visibility = View.GONE
            edittext_container.visibility = View.VISIBLE
        }
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerview.layoutManager = layoutManager
        recyclerview.addItemDecoration(SlideItemDecoration(this))
        recyclerview.adapter = mAdapter
    }

    private fun setListeners() {
        cardViewCategory_e.setOnClickListener { mWindow.showOneMinuteVideoCategories(this) }
        music_card_e.setOnClickListener { mWindow.showMusicList(::resetMedia, this) }
        publishButton.setOnClickListener { publishVideo() }
        mCompositeDisposable.add(RxTextView.textChanges(text_edittext).subscribe({
            val currentSlideItem = mAdapter.getSlideItems(mCurrentPosition)
            currentSlideItem.slideText = it.toString()
            text_preview.text = currentSlideItem.slideText
        }, { Log.e("Error", "${it.cause}") }))
        mCompositeDisposable.add(RxTextView.textChanges(text_punchline1).subscribe({
            val currentSlideItem = mAdapter.getSlideItems(mCurrentPosition)
            currentSlideItem.punchLine1 = it.toString()
            punchline1.text = currentSlideItem.punchLine1
        }, { Log.e("Error", "${it.cause}") }))
        mCompositeDisposable.add(RxTextView.textChanges(text_punchline2).subscribe({
            val currentSlideItem = mAdapter.getSlideItems(mCurrentPosition)
            currentSlideItem.punchLine2 = it.toString()
            punchline2.text = currentSlideItem.punchLine2
        }, { Log.e("Error", "${it.cause}") }))

        cardView.setOnClickListener { invokeCropImageActivity() }
    }

    private fun invokeCropImageActivity() {
        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON)
                .setAllowRotation(true)
                .setAllowCounterRotation(true)
                .setAllowFlipping(true)
                .setAspectRatio(1, 1)
                .setActivityTitle("Crop Image")
                .start(this)
    }

    private fun publishVideo() {
        val validate = validateInput()
        when (validate) {
            0 -> Toast.makeText(this, "Please Select Category", Toast.LENGTH_SHORT).show()
            1 -> Toast.makeText(this, "Please input more than 4 slides", Toast.LENGTH_SHORT).show()
            2 -> Toast.makeText(this, "Make sure you input the image for each slides", Toast.LENGTH_SHORT).show()
            3 -> Toast.makeText(this, "Please input punchline", Toast.LENGTH_SHORT).show()
            4 -> Toast.makeText(this, "Please pick music/song for video", Toast.LENGTH_SHORT).show()
            200 -> {
                mWindow.mDurationProgress = OneMinuteCommandVideoBuilder.getTotalDurationInFrameForVideo(mAdapter.mSlideItems.size) * 3
                mWindow.showPassCodeWindowView(::generateSlideVideo)
            }
        }
    }

    private fun validateInput(): Int {
        if (mCategoryName == null) return 0
        if (mSongName == null) return 4
        if (mAdapter.mSlideItems.size < 2) {
            return 1
        } else {
            for (index in 0 until mAdapter.mSlideItems.size) {
                if (mAdapter.mSlideItems[index].imagePath.isEmpty()) return 2
            }
        }
        val punchLine1 = mAdapter.mPunchLine.punchLine1
        val punchLine2 = mAdapter.mPunchLine.punchLine2
        if (punchLine1.isEmpty() && punchLine2.isEmpty()) {
            return 3
        }
        return 200
    }

    private fun generateSlideVideo() {
        val models = mAdapter.mSlideItems
        val punchLine = mAdapter.mPunchLine
        val fontPath = BASE_FONT_DIR + "Lato-Bold.ttf"
        val fontPathTitle = BASE_FONT_DIR + "Lato-Black.ttf"
        val outputName = BASE_OUTPUT_PATH + "_prototype_slide.mp4"
        val command1 = OneMinuteCommandVideoBuilder()
                .withContext(this).loadModels(models).withCategory(mCategoryName!!)
                .generateInputStringArraysWithFilterComplexCommand()
                .generateStringBuilderScaleCommand("scale", "720")
                .generateStringBuilderZoomPanCommand("zoom", "45", "180", "720x720")
                .generateStringBuilderDrawTextBoxCommand("text", punchLine.punchLine1, punchLine.punchLine2, fontPath, fontPathTitle)
                .generateCommandForConcat()
                .generateOutput(outputName)
                .buildString()
        FileUtility.checkFileExists("_prototype_slide.mp4", BASE_OUTPUT_PATH)
        mWindow.updateProgressExplanation("0/3 Embedding images and texts")
        val ffmpeg = FFmpeg.getInstance(this)
        ffmpeg.execute(command1.toTypedArray(), object : ExecuteBinaryResponseHandler() {
            override fun onSuccess(message: String?) {
                generateSlideVideoWithLogoAndText()
            }

            override fun onFailure(message: String?) {
                Log.e("Error", "$message")
                mWindow.dismissPassCodeView()
            }

            override fun onProgress(message: String?) {
                val index: Int = message?.indexOf("fps=")!!
                if (index > -1) {
                    val substringIndex = message.substring(0, index)
                    val replaceEmptyString = substringIndex.replace(" ", "")
                    val frame = replaceEmptyString.substring(replaceEmptyString.lastIndexOf("=") + 1)
                    mWindow.updateProgressBar(frame.toInt())
                }
            }
        })
    }

    private fun generateSlideVideoWithLogoAndText() {
        val models = mAdapter.mSlideItems
        val inputVideo = BaseActivity.BASE_OUTPUT_PATH + "_prototype_slide.mp4"
        val outputName = BaseActivity.BASE_OUTPUT_PATH + "_prototype_one_minute.mp4"
        val command = OneMinuteCommandVideoBuilder()
                .withContext(this).loadModels(models)
                .withCategory(mCategoryName!!)
                .generateCommandForOverlayLogo(inputVideo, outputName)
                .buildString()
        FileUtility.checkFileExists("_prototype_one_minute.mp4", BaseActivity.BASE_OUTPUT_PATH)
        mWindow.updateProgressExplanation("1/3 Creating logo and animations")
        val ffmpeg = FFmpeg.getInstance(this)
        Log.d("FFMPEG", "$command")
        ffmpeg.execute(command.toTypedArray(), object : ExecuteBinaryResponseHandler() {
            override fun onSuccess(message: String?) {
//                mWindow.showSuccessViewLoading()
                addAudio()
            }

            override fun onFailure(message: String?) {
                Log.e("Error", "$message")
                mWindow.dismissPassCodeView()
            }

            override fun onProgress(message: String?) {
                val index: Int = message?.indexOf("fps=")!!
                if (index > -1) {
                    val substringIndex = message.substring(0, index)
                    val replaceEmptyString = substringIndex.replace(" ", "")
                    val frame = replaceEmptyString.substring(replaceEmptyString.lastIndexOf("=") + 1)
                    val totalDuration = OneMinuteCommandVideoBuilder.getTotalDurationInFrameForVideo(mAdapter.mSlideItems.size)
                    mWindow.updateProgressBar(totalDuration + frame.toInt())
                }
            }
        })
    }

    private fun addAudio() {
        val fFmpeg = FFmpeg.getInstance(this)
        val models = mAdapter.mSlideItems
        val inputVideo = BaseActivity.BASE_OUTPUT_PATH + "_prototype_one_minute.mp4"
        val command = OneMinuteCommandVideoBuilder().withContext(this).loadModels(models)
                .generateCommandToEmbedAudio(inputVideo, mSongName!!)
                .buildString()
        mWindow.updateProgressExplanation("2/3 Embedding audio to the video file")
        fFmpeg.execute(command.toTypedArray(), object: ExecuteBinaryResponseHandler() {
            override fun onSuccess(message: String?) {
                mWindow.showSuccessViewLoading()
                FileUtility.checkFileExists("_prototype_slide.mp4", BASE_OUTPUT_PATH)
                FileUtility.checkFileExists("_prototype_one_minute.mp4", BASE_OUTPUT_PATH)
            }

            override fun onFailure(message: String?) {
                Log.e("Error", "$message")
                mWindow.dismissPassCodeView()
            }

            override fun onProgress(message: String?) {
                val index: Int = message?.indexOf("fps=")!!
                if (index > -1) {
                    val substringIndex = message.substring(0, index)
                    val replaceEmptyString = substringIndex.replace(" ", "")
                    val frame = replaceEmptyString.substring(replaceEmptyString.lastIndexOf("=") + 1)
                    val totalDuration = OneMinuteCommandVideoBuilder.getTotalDurationInFrameForVideo(mAdapter.mSlideItems.size)
                    mWindow.updateProgressBar((totalDuration * 2) + frame.toInt())
                }
            }
        })
    }

    private fun setTextCategory(view: TextView) {
        view.setBackgroundColor(OneMinuteCommandVideoBuilder.getColorCategory(this, mCategoryName!!))
        view.text = mCategoryName!!.toUpperCase()
        view.setTextColor(OneMinuteCommandVideoBuilder.getTextColorForCategory(this, mCategoryName!!))
    }

    private fun playSong() {
        resetMedia()
        val songFileName = OneMinuteCommandVideoBuilder.returnSongFileName(this, mSongName!!)
        val descriptor = assets.openFd("songs/$songFileName")
        mMediaPlayer.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
        descriptor.close()

        mMediaPlayer.prepare()
        mMediaPlayer.setVolume(1f, 1f)
        mMediaPlayer.isLooping = false
        mMediaPlayer.start()
    }

    private fun resetMedia() {
        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.stop()
            mMediaPlayer.reset()
        }
    }

    override fun onCategoryClicked(categoryName: String) {
        mCategoryName = categoryName
        setTextCategory(text_category)
        setTextCategory(text_category2)
    }

    override fun onSongClicked(songName: String) {
        mSongName = songName
        playSong()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val uri = result.uri
                if (uri != null) {
                    val pathName = FileUtility.getPath(this, uri)
                    val currentSlideItem = mAdapter.getSlideItems(mCurrentPosition)
                    currentSlideItem.imagePath = pathName!!
                    mAdapter.notifyItemChanged(mCurrentPosition)
                    setPreviewImageAndText(currentSlideItem)
                }
            }
        }
    }

    override fun onDestroy() {
        mCompositeDisposable.dispose()
        mWindow.dismiss()
        mMediaPlayer.stop()
        mMediaPlayer.release()
        super.onDestroy()
    }
}