package com.gvm.demoffmpeg

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler
import nl.bravobit.ffmpeg.FFmpeg
import java.io.File

@SuppressLint("Registered")
class MainActivity : BaseActivity() {

    private val mSingleObservable = Single.fromCallable {
        FileUtility.copyDirOrFileFromAsset(applicationContext, "fonts", BASE_FONT_DIR)
        FileUtility.copyDirOrFileFromAsset(applicationContext, "template", BASE_TEMPLATE_DIR)
    }
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()
    private var mDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbar()
        initMaterialDialog()
        setListener()
    }

    private fun initMaterialDialog() {
        mMaterialDialogBuilder = MaterialDialog.Builder(this)
                .cancelable(false)
                .theme(Theme.LIGHT)
    }

    private fun setListener() {
        button.setOnClickListener { doOperations() }
    }

    private fun doOperations() {
        setProgress()
        checkBasePath()
        initDisposableObserver()
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar_activity)
        val actionBar = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
    }

    private fun setProgress() {
        mMaterialDialogBuilder.content("Transporting assets file to sdcard, please wait")
        mMaterialDialogBuilder.progressIndeterminateStyle(true)
        mMaterialDialogBuilder.progress(false, 250)
        mMaterialDialog = mMaterialDialogBuilder.show()
    }

    private fun checkBasePath() {
        val file = File(mBasePath)
        if (!file.exists()) {
            Log.e("CHECKBASEPATH", "File does not exist, creating the directory needed")
            file.mkdirs()
        }
        val fileOutput = File(BASE_OUTPUT_PATH)
        if (!fileOutput.exists()) {
            fileOutput.mkdirs()
        }
    }

    private fun initDisposableObserver() {
        mDisposable = mSingleObservable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                subscribe({
            mMaterialDialog?.incrementProgress(1)
            changeImageToVideo()
        }, { err ->
            Log.e("Observable error", "The error is : $err")
        })
        mCompositeDisposable.add(mDisposable!!)
    }

    private fun changeImageToVideo() {
        mMaterialDialog?.setContent("Generating background template..")

        val outputName = "baseVid.mp4"
        val backgroundName = "Background.png"
        val fFmpeg = FFmpeg.getInstance(this)
        val command = arrayOf("-loop", "1", "-i", BASE_TEMPLATE_DIR + backgroundName, "-vf", "format=yuv420p", "-t", "10", BASE_OUTPUT_PATH + outputName)
        FileUtility.checkFileExists(outputName, BASE_OUTPUT_PATH)
        fFmpeg.execute(command, object : ExecuteBinaryResponseHandler() {
            override fun onProgress(message: String?) {
                mMaterialDialog?.incrementProgress(1)
            }

            override fun onFailure(message: String?) {
                mMaterialDialog?.dismiss()
                Toast.makeText(this@MainActivity, "Ooops, please check log", Toast.LENGTH_SHORT).show()
                Log.e("Error", message)
            }

            override fun onSuccess(message: String?) {
                addImagesToVideo()
            }
        })
    }

    private fun addImagesToVideo() {
        mMaterialDialog?.setContent("Adding images...")

        val inputName = "baseVid.mp4"
        val outputName = "baseVidImages.mp4"
        val fFmpeg = FFmpeg.getInstance(this)
        val command = arrayOf("-i", BASE_OUTPUT_PATH + inputName,
                "-loop", "1", "-i", BASE_TEMPLATE_DIR + "lights.png",
                "-loop", "1", "-i", BASE_TEMPLATE_DIR + "decoration.png",
                "-loop", "1", "-i", BASE_TEMPLATE_DIR + "building1.png",
                "-loop", "1", "-i", BASE_TEMPLATE_DIR + "building2.png",
                "-loop", "1", "-i", BASE_TEMPLATE_DIR + "building3.png",
                "-loop", "1", "-i", BASE_TEMPLATE_DIR + "building4.png",
                "-loop", "1", "-i", BASE_TEMPLATE_DIR + "cloud1.png",
                "-loop", "1", "-i", BASE_TEMPLATE_DIR + "cloud2.png",
                "-c:a", "copy", "-filter_complex",

                //"[0:v]overlay=shortest=1[video1]; "+

                "[1:v]fade=in:st=5:d=2:alpha=1, fade=out:st=7:d=2:alpha=1[image1];" +
                "[0:v][image1]overlay=y=(H-h):shortest=1[video1];" +
                "[video1]overlay=shortest=1[video2];"
                        +
                "[video2]overlay= x=25 : y=if(between(t\\,0\\,0.5)\\, H-(((t-0)*h)/0.5)\\, if(gte(t\\,0.5)\\, (H-h)\\, H)):shortest=1[video3]; " +
                "[video3]overlay= x=272: y=if(between(t\\,0.5\\,1.5)\\, H-(((t-0.5)*h)/1)\\, if(gte(t\\,1.5)\\, (H-h)\\, H)):shortest=1[video4];" +
                "[video4]overlay= x=441: y=if(between(t\\,1.5\\,2.5)\\, H-(((t-1.5)*h)/1)\\, if(gte(t\\,2.5)\\, (H-h)\\, H)):shortest=1[video5];" +
                "[video5]overlay= x=550: y=if(between(t\\,2.5\\,3.2)\\, H-(((t-2.5)*h)/0.7)\\, if(gte(t\\,3.2)\\, (H-h)\\, H)):shortest=1[video6];" +
                "[video6]overlay= x=if(gte(t\\,5)\\,((-w)+((t-5)*125)) \\, (-w)): y=((H-h)/2)+70:shortest=1[video7];" +
                "[video7]overlay= x=if(gte(t\\,5)\\,(W-((t-5)*125)) \\, W): y=((H-h)/2)-20:shortest=1",

                BASE_OUTPUT_PATH + outputName)
        FileUtility.checkFileExists(outputName, BASE_OUTPUT_PATH)
        fFmpeg.execute(command, object : ExecuteBinaryResponseHandler() {
            override fun onProgress(message: String?) {
                mMaterialDialog?.incrementProgress(1)
            }

            override fun onFailure(message: String?) {
                mMaterialDialog?.dismiss()
                Toast.makeText(this@MainActivity, "Ooops, please check log", Toast.LENGTH_SHORT).show()
                Log.e("Error", message)
            }

            override fun onSuccess(message: String?) {
                addText()
            }
        })
    }

    private fun addText() {
        mMaterialDialog?.setContent("Embedding text...")
        val inputName = "baseVidImages.mp4"
        val outputName = "result.mp4"
        val fFmpeg = FFmpeg.getInstance(this)
        val fontName = "Potra.ttf"
        val command = arrayOf("-i", BASE_OUTPUT_PATH + inputName, "-filter_complex", "color=black@0:100x100,format=yuva444p[c]; [c][0]scale2ref[ct][mv0]; [ct]setsar=1,split=2[t1][t2]; " +

                "[t1]drawtext=fontfile=" + BASE_FONT_DIR + fontName + ": text='Whaddup':x=if(between(t\\,3\\,4.5)\\, w-((t-3)*(w+text_w)/3)\\, if(gte(t\\,4.5)\\, (w-text_w)/2\\, w)): y=(2*text_h)-20: fontsize=80: fontcolor=0xc0f893[text1];" +

                "[t2]drawtext=fontfile=" + BASE_FONT_DIR + fontName + ": text='Bitch': x=if(between(t\\,3\\,4.5)\\, (-text_w)+((t-3)*(w+text_w)/3)\\, if(gte(t\\,4.5)\\, (w-text_w)/2\\, (-text_w))): y=(3*text_h)+5: fontsize=80: fontcolor=0xc0f893[text2];" +

                "[mv0][text1]overlay= shortest=1[mv1];" +
                "[mv1][text2]overlay= shortest=1", "-codec:a", "copy", BASE_OUTPUT_PATH + outputName)
        FileUtility.checkFileExists(outputName, BASE_OUTPUT_PATH)
        fFmpeg.execute(command, object : ExecuteBinaryResponseHandler() {
            override fun onProgress(message: String?) {
                mMaterialDialog?.incrementProgress(1)
            }

            override fun onFailure(message: String?) {
                mMaterialDialog?.dismiss()
                Toast.makeText(this@MainActivity, "Ooops, please check log", Toast.LENGTH_SHORT).show()
                Log.e("Error", message)
            }

            override fun onSuccess(message: String?) {
                mMaterialDialog?.dismiss()
                Toast.makeText(this@MainActivity, "Success generating video", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroy() {
        mCompositeDisposable.clear()
        mMaterialDialog?.dismiss()
        super.onDestroy()
    }
}