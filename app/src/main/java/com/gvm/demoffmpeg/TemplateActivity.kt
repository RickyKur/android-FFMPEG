package com.gvm.demoffmpeg

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_template.*
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler
import nl.bravobit.ffmpeg.FFmpeg

/**
 * Brought to you by rickykurniawan on 30/07/18.
 */
class TemplateActivity : BaseActivity() {

    private var mPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template)
        setToolbar()
        initMaterialDialog()
        setListener()
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar_activity)
        val actionBar = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
    }

    private fun initMaterialDialog() {
        mMaterialDialogBuilder = MaterialDialog.Builder(this)
                .cancelable(false)
                .theme(Theme.LIGHT)
    }

    private fun setProgressDialog() {
        mMaterialDialogBuilder.content("Wait")
        mMaterialDialogBuilder.progressIndeterminateStyle(false)
        mMaterialDialog = mMaterialDialogBuilder.show()
    }

    private fun setListener() {
        thumbnail.setOnClickListener {
            FileUtility.getImageFromGallery(this)
        }
        generate.setOnClickListener {
            //            concatVideos()
//            generateFFMPEG()
//            zoomExample()
//            addLogoAndOthers()
            addSolidColor()
        }
    }

    private fun loadThumbnail(uri: Uri) {
        Glide.with(this).load(uri).into(thumbnail)
    }

    private fun zoomExample() {
//        https://opini.id/video/113090/larangan-unik-atlet-pesepeda
        setProgressDialog()
        val outputName = BASE_OUTPUT_PATH + "_zoomTest.mp4"
        val fFmpeg = FFmpeg.getInstance(this)
        val imageOpening = BASE_TEMPLATE_DIR + "intro.jpg"
        val image1 = BASE_TEMPLATE_DIR + "bike1.jpg"
        val image2 = BASE_TEMPLATE_DIR + "bike2.jpg"
        val image3 = BASE_TEMPLATE_DIR + "bike8.jpg"
        val image4 = BASE_TEMPLATE_DIR + "bike5.jpg"
        val image5 = BASE_TEMPLATE_DIR + "bike10.jpg"
        val image6 = BASE_TEMPLATE_DIR + "bike11.jpg"
        val image7 = BASE_TEMPLATE_DIR + "bike3.jpg"
        val image8 = BASE_TEMPLATE_DIR + "bike4.jpg"
        val image9 = BASE_TEMPLATE_DIR + "bike7.jpg"
        val image10 = BASE_TEMPLATE_DIR + "bike9.jpg"
        val fontSize = "63"
        val fontSize2 = "75"
        val slideSpeed = "2200"
        FileUtility.checkFileExists("_ZoomTest.mp4", BASE_OUTPUT_PATH)
        /*-78 for 1menit, 75 for oh gitu, h = h-210*/
        val command = arrayOf(
                "-i", imageOpening,
                "-i", image1,
                "-i", image2,
                "-i", image3,
                "-i", image4,
                "-i", image5,
                "-i", image6,
                "-i", image7,
                "-i", image8,
                "-i", image9,
                "-i", image10,

                "-f","lavfi","-i", "color=green:s=720x720:d=2:sar=1",

                "-filter_complex",

                        "[0:v]scale=720:720, setsar=1[scl1];" +
                        "[1:v]scale=720:720, setsar=1[scl2]; [2:v]scale=720:720, setsar=1[scl3]; [3:v]scale=720:720, " +
                        "setsar=1[scl4];[4:v]scale=720:720, setsar=1[scl5]; [5:v]scale=720:720, setsar=1[scl6]; [6:v]scale=720:720, setsar=1[scl7];" +
                        "[7:v]scale=720:720, setsar=1[scl8]; [8:v]scale=720:720, setsar=1[scl9];" +
                        "[9:v]scale=720:720, setsar=1[scl10]; [10:v]scale=720:720, setsar=1[scl11];" +

                        "[scl1]zoompan=z=1:fps=45:s=720x720:d=80[v0];" +
//                "fade=t=in:st=0:d=1,fade=t=out:st=7:d=1[v0];" +

                        "[scl2]scale=8000:-1, zoompan=z=min(zoom+0.0015\\, 1.5):x=iw/2-(iw/zoom/2):fps=45:s=720x720:d=180[v1];" +
//                "fade=t=in:st=0:d=1,fade=t=out:st=7:d=1[v1];" +

                        "[scl3]zoompan=z=1.5:x=0:y=ih/zoom/2:fps=45:s=720x720:d=180[v2];" +
//                "fade=t=in:st=0:d=1,fade=t=out:st=7:d=1[v2];" +

                        "[scl4]zoompan=z=if(lte(zoom\\,1.0)\\,1.5\\,max(1.001\\,zoom - 0.0025)):fps=45:s=720x720:d=180[v3];" +
//                "fade=t=in:st=0:d=1,fade=t=out:st=7:d=1[v3];" +

                        "[scl5]zoompan=z=if(lte(zoom\\,1.0)\\,1.5\\,max(1.001\\,zoom - 0.0025)):fps=45:s=720x720:d=180[v4];" +

                        "[scl6]zoompan=z=if(lte(zoom\\,1.0)\\,1.5\\,max(1.001\\,zoom - 0.0025)):fps=45:s=720x720:d=180[v5];" +

                        "[scl7]zoompan=z=if(lte(zoom\\,1.0)\\,1.5\\,max(1.001\\,zoom - 0.0025)):fps=45:s=720x720:d=180[v6];" +

                        "[scl8]zoompan=z=if(lte(zoom\\,1.0)\\,1.5\\,max(1.001\\,zoom - 0.0025)):fps=45:s=720x720:d=180[v7];" +

                        "[scl9]zoompan=z=if(lte(zoom\\,1.0)\\,1.5\\,max(1.001\\,zoom - 0.0025)):fps=45:s=720x720:d=180[v8];" +

                        "[scl10]zoompan=z=if(lte(zoom\\,1.0)\\,1.5\\,max(1.001\\,zoom - 0.0025)):fps=45:s=720x720:d=180[v9];" +

                        "[scl11]zoompan=z=if(lte(zoom\\,1.0)\\,1.5\\,max(1.001\\,zoom - 0.0025)):fps=45:s=720x720:d=405[v10];" +

                        "[v0]drawbox=y=(ih-200):color=black@0.5:width=iw:height=210:t=fill, " +
                        "drawtext=fontfile=" + BASE_FONT_DIR + "Lato-Black.ttf: text='Larangan Unik\nAtlet Pesepeda'" +
                        ":x=40:y=h-160:fontsize=70:line_spacing=12.0:text_shaping=0:fontcolor=white," +
                        "drawbox=y=(ih-245):color=0x0087EA:width=iw:height=45:t=fill," +
                        "drawbox=y=(ih-245):color=0x00FFD8:width=165:height=45:t=fill," +
                        "drawtext=fontfile="+ BASE_FONT_DIR +"Lato-Black.ttf: text='1MENIT':fontcolor=black:fontsize=27:x=30:y=h-235," +
                        "drawtext=fontfile="+ BASE_FONT_DIR +"Lato-Black.ttf: text='OH GITU!':fontcolor=white:fontsize=27:x=195:y=h-235[text1];"+

                        "[v1]drawbox=y=(ih-260):color=black@0.5:width=iw:height=300:t=fill, " +
                        "drawtext=fontfile=" + BASE_FONT_DIR + "Lato-Bold.ttf: text='Atlet sepeda\nIndonesia punya\naturan ketat'" +
                        ":x=if(lt(t\\,0.1)\\,-tw\\,min(40\\,-tw+($slideSpeed*(t-0.1)))):y=h-220:fontsize=$fontSize:line_spacing=12.0:text_shaping=0:fontcolor=white:" +
                        "alpha=if(gte(t\\,7)\\,0\\, 1)[text2];" +

                        "[v2]drawbox=y=(ih-260):color=black@0.5:width=iw:height=300:t=fill, " +
                        "drawtext=fontfile=" + BASE_FONT_DIR + "Lato-Bold.ttf: text='Tujuannya dongkrak\npenampilan di\nAsian Games 2018'" +
                        ":x=if(lt(t\\,0.1)\\,-tw\\,min(40\\,-tw+($slideSpeed*(t-0.1)))):y=h-220:fontsize=$fontSize:line_spacing=12.0:text_shaping=0:fontcolor=white:" +
                        "alpha=if(gte(t\\,7)\\,0\\, 1)[text3];" +

                        "[v3]drawbox=y=(ih-260):color=black@0.5:width=iw:height=270:t=fill, " +
                        "drawtext=fontfile=" + BASE_FONT_DIR + "Lato-Bold.ttf: text='Salah satunya\nlarangan hubungan\nintim jelang lomba'" +
                        ":x=if(lt(t\\,0.1)\\,-tw\\,min(40\\,-tw+($slideSpeed*(t-0.1)))):y=h-220:fontsize=$fontSize:line_spacing=12.0:text_shaping=0:fontcolor=white:" +
                        "alpha=if(gte(t\\,7)\\,0\\, 1)[text4];" +

                        "[v4]drawbox=y=(ih-200):color=black@0.5:width=iw:height=210:t=fill, " +
                        "drawtext=fontfile=" + BASE_FONT_DIR + "Lato-Bold.ttf: text='Agar gak ganggu\nperforma atlet'" +
                        ":x=if(lt(t\\,0.1)\\,-tw\\,min(40\\,-tw+($slideSpeed*(t-0.1)))):y=h-160:fontsize=$fontSize:line_spacing=12.0:text_shaping=0:fontcolor=white:" +
                        "alpha=if(gte(t\\,7)\\,0\\, 1)[text5];" +

                        "[v5]drawbox=y=(ih-260):color=black@0.5:width=iw:height=270:t=fill, " +
                        "drawtext=fontfile=" + BASE_FONT_DIR + "Lato-Bold.ttf: text='Apalagi yang\nbertanding\njarak pendek'" +
                        ":x=if(lt(t\\,0.1)\\,-tw\\,min(40\\,-tw+($slideSpeed*(t-0.1)))):y=h-220:fontsize=$fontSize:line_spacing=12.0:text_shaping=0:fontcolor=white:" +
                        "alpha=if(gte(t\\,7)\\,0\\, 1)[text6];" +

                        "[v6]drawbox=y=(ih-260):color=black@0.5:width=iw:height=270:t=fill, " +
                        "drawtext=fontfile=" + BASE_FONT_DIR + "Lato-Bold.ttf: text='Ini hasil\npengalaman pelatih\nDadang H.purnomo'" +
                        ":x=if(lt(t\\,0.1)\\,-tw\\,min(40\\,-tw+($slideSpeed*(t-0.1)))):y=h-220:fontsize=$fontSize:line_spacing=12.0:text_shaping=0:fontcolor=white:" +
                        "alpha=if(gte(t\\,7)\\,0\\, 1)[text7];" +

                        "[v7]drawbox=y=(ih-200):color=black@0.5:width=iw:height=210:t=fill, " +
                        "drawtext=fontfile=" + BASE_FONT_DIR + "Lato-Bold.ttf: text='Ketika masih\njadi atlet sepeda'" +
                        ":x=if(lt(t\\,0.1)\\,-tw\\,min(40\\,-tw+($slideSpeed*(t-0.1)))):y=h-160:fontsize=$fontSize:line_spacing=12.0:text_shaping=0:fontcolor=white:" +
                        "alpha=if(gte(t\\,7)\\,0\\, 1)[text8];" +

                        "[v8]drawbox=y=(ih-200):color=black@0.5:width=iw:height=210:t=fill, " +
                        "drawtext=fontfile=" + BASE_FONT_DIR + "Lato-Bold.ttf: text='Puasa ini akan\nberbuah manis'" +
                        ":x=if(lt(t\\,0.1)\\,-tw\\,min(40\\,-tw+($slideSpeed*(t-0.1)))):y=h-160:fontsize=$fontSize:line_spacing=12.0:text_shaping=0:fontcolor=white:" +
                        "alpha=if(gte(t\\,7)\\,0\\, 1)[text9];" +

                        "[v9]drawbox=y=(ih-200):color=black@0.5:width=iw:height=210:t=fill, " +
                        "drawtext=fontfile=" + BASE_FONT_DIR + "Lato-Bold.ttf: text='PB ISSI akan siapkan\nbonus 1 miliar'" +
                        ":x=if(lt(t\\,0.1)\\,-tw\\,min(40\\,-tw+($slideSpeed*(t-0.1)))):y=h-160:fontsize=$fontSize:line_spacing=12.0:text_shaping=0:fontcolor=white:" +
                        "alpha=if(gte(t\\,7)\\,0\\, 1)[text10];" +

                        "[v10]drawbox=enable=between(t\\,0\\,3.5):y=(ih-260):color=black@0.5:width=iw:height=270:t=fill, " +
                        "drawtext=fontfile=" + BASE_FONT_DIR + "Lato-Bold.ttf: text='Untuk atlet\npenyumbang\nmedali emas'" +
                        ":x=if(lt(t\\,0.1)\\,-tw\\,min(40\\,-tw+($slideSpeed*(t-0.1)))):y=h-220:fontsize=$fontSize:line_spacing=12.0:text_shaping=0:fontcolor=white:" +
                        "alpha=if(gte(t\\,3.5)\\,0\\, 1), " +
                        "drawbox=enable=gt(t\\,4):y=0:x=0:color=black@0.5:width=iw:height=ih:t=fill," +
                        "drawtext=fontfile="+ BASE_FONT_DIR+"Lato-Black.ttf: text='\\      SABARRR....\nDEMI PRESTASI':x=(w-tw)/2:" +
                        "y=(-10)+(h-th)/2:fontsize=$fontSize2:" +
                        "alpha=if(gte(t\\,4)\\,min((t-4)/2\\, 1)\\, 0):fontcolor=white:line_spacing=20.0, " +

                        "drawtext=fontfile="+ BASE_FONT_DIR +"Lato-Bold.ttf: text='SHARE KALAU BARU TAHU':x=(w-tw)/2:" +
                        "y=if(gte(t\\,5)\\,min(150\\,((t-5)*500))\\,0):fontcolor=0x00FFD8:fontsize=35:" +
                        "alpha=if(gte(t\\,5)\\,min(1\\,(t-5))\\,0):box=1: boxcolor=black: boxborderw=16," +

                        "drawtext=fontfile="+ BASE_FONT_DIR +"Lato-Black.ttf: text='1MENIT':" +
                        "x=if(gte(t\\,5)\\,max((-78)+(w-tw)/2\\,((75)+(w-tw)/2) - ((t-5)*200))\\,(75)+(w-tw)/2):" +
                        "alpha=if(gte(t\\,5)\\,min(1\\,(t-5))\\,0):y=h-210:fontcolor=black:fontsize=30:" +
                        "box=1: boxcolor=0x00FFD8: boxborderw=14," +

                        "drawtext=fontfile="+ BASE_FONT_DIR +"Lato-Black.ttf: text='OH GITU!':" +
                        "x=if(gte(t\\,5)\\,min((74)+(w-tw)/2\\,((-78)+(w-tw)/2) + ((t-5)*200))\\,(-78)+(w-tw)/2):" +
                        "alpha=if(gte(t\\,5)\\,min(1\\,(t-5))\\,0):y=h-210:fontcolor=white:fontsize=30:" +
                        "box=1: boxcolor=0x0087EA: boxborderw=14[text11];" +


                        "[text1][text2][text3][text4][text5][text6][text7][text8][text9][text10][text11]concat=n=11:v=1:a=0, " +
                        "format=yuv420p[v]", "-map", "[v]", outputName)

        fFmpeg.execute(command, object : ExecuteBinaryResponseHandler() {
            override fun onSuccess(message: String?) {
                mMaterialDialog?.dismiss()
                Toast.makeText(this@TemplateActivity, "Good", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(message: String?) {
                mMaterialDialog?.dismiss()
                Log.e("Error", "$message")
                Toast.makeText(this@TemplateActivity, "Ooops zoom in error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addLogoAndOthers() {
        setProgressDialog()
        FileUtility.checkFileExists("_oneminute.mp4", BASE_OUTPUT_PATH)
        val fFmpeg = FFmpeg.getInstance(this)
        val inputVideo = BASE_OUTPUT_PATH + "_zoomTest.mp4"
        val outputName = BASE_OUTPUT_PATH + "_oneminute.mp4"
        val imageLogo = BASE_TEMPLATE_DIR + "logo.png"
        val command = arrayOf(
                "-i", inputVideo,
                "-i", imageLogo,
                "-filter_complex",
                "[1:v]scale=(iw*1.25):(ih*1.25)[scl1];" +
//                "drawbox=x=130:y=0:color=0x0087EA:width=iw:height=40:t=fill," +
                "[0:v][scl1]overlay=x=0:y=if(gte(t\\,41.5)\\,-200\\,0)," +
                "drawtext=fontfile="+ BASE_FONT_DIR +"Lato-Black.ttf: text='\\  OH GITU!\\                                                                                                       .':fontcolor=white:fontsize=28:x=192:y=" +
                "if(lte(t\\,3)\\,-th*2\\,if(lte(t\\,6)\\,min(12\\,(-th*2)+((t-3)*100))\\,max(-th*2\\,12-((t-6)*50)))):" +
                "box=1:boxcolor=0x0087EA:boxborderw=17",
                 outputName)
        fFmpeg.execute(command, object : ExecuteBinaryResponseHandler() {
            override fun onSuccess(message: String?) {
                mMaterialDialog?.dismiss()
                Toast.makeText(this@TemplateActivity, "Good", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(message: String?) {
                mMaterialDialog?.dismiss()
                Log.e("Error", "$message")
                Toast.makeText(this@TemplateActivity, "Ooops error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addSolidColor() {
        setProgressDialog()
        FileUtility.checkFileExists("_oneMinuteFinal.mp4", BASE_OUTPUT_PATH)
        val fFmpeg = FFmpeg.getInstance(this)
        val outputVideo = BASE_OUTPUT_PATH + "_oneMinuteFinal.mp4"
        val inputVideo = BASE_OUTPUT_PATH + "_oneminute.mp4"
        val imageLogo = BASE_TEMPLATE_DIR + "logo2.png"
        val command = arrayOf(
                "-i", inputVideo, /*[0:v]*/

                "-f", "lavfi", "-i", "color=c=0x00FFD8:s=720x720", /*teal*/ /*[1:v]*/
                "-f", "lavfi", "-i", "color=c=0x0087EA:s=720x720",/*blue*/ /*[2:v]*/

                "-i", imageLogo, /*[3:v]*/

                "-filter_complex",

                "[3:v]scale=(iw*1.2):(ih*1.2)[scaledImage];" +
                "[1:v][scaledImage]overlay=x=(W-w)/2:y=(H-h)/2[image1];" +
                "[0:v][image1]overlay=enable=gt(t\\,45.01):shortest=1, format=yuv420p[ovr1];" +
                "[ovr1][2:v]overlay=shortest=1:y=(t-45)*2100, format=yuv420p"
                , outputVideo)
        fFmpeg.execute(command, object: ExecuteBinaryResponseHandler() {
            override fun onSuccess(message: String?) {
                mMaterialDialog?.dismiss()
                Toast.makeText(this@TemplateActivity, "Good", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(message: String?) {
                mMaterialDialog?.dismiss()
                Log.e("Error","$message")
                Toast.makeText(this@TemplateActivity, "Ooops error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun generateFFMPEG() {
        setProgressDialog()
        val outputName = "_baseVid.mp4"
        val backgroundName = BASE_TEMPLATE_DIR + "Background.png"
        val fFmpeg = FFmpeg.getInstance(this)
        val command = arrayOf("-loop", "1", "-i", mPath, "-filter_complex", "format=yuv420p[fuck]; [fuck]scale=720:720", "-t", "5", BASE_OUTPUT_PATH + outputName)
        FileUtility.checkFileExists(outputName, BASE_OUTPUT_PATH)
        fFmpeg.execute(command, object : ExecuteBinaryResponseHandler() {
            override fun onSuccess(message: String?) {
                mMaterialDialog?.setContent("Wait again")
                addText()
            }

            override fun onFailure(message: String?) {
                mMaterialDialog?.dismiss()
                Log.e("Error", message)
                Toast.makeText(this@TemplateActivity, "Ooops, error conversion to video", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun concatVideos() {
        setProgressDialog()
        val outputName = BASE_OUTPUT_PATH + "_concat.mp4"
        val videoName1 = BASE_OUTPUT_PATH + "_baseVidWithText.mp4"
        val videoName2 = BASE_OUTPUT_PATH + "_baseVidWithText2.mp4"
        val videoName3 = BASE_OUTPUT_PATH + "_baseVidWithText3.mp4"
        val ffmpeg = FFmpeg.getInstance(this)
        val command = arrayOf("-i", videoName1, "-i", videoName2, "-i", videoName3, "-filter_complex", "[0:0] [1:0] [2:0] concat=n=3:v=1:a=0 [v]",
                "-map", "[v]", outputName)
        ffmpeg.execute(command, object : ExecuteBinaryResponseHandler() {
            override fun onSuccess(message: String?) {
                mMaterialDialog?.dismiss()
                Toast.makeText(this@TemplateActivity, "Good man", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(message: String?) {
                mMaterialDialog?.dismiss()
                Log.e("Error", message)
                Toast.makeText(this@TemplateActivity, "Error concat video", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addText() {
        val fontName = "Lato-Bold.ttf"
        val inputName = "_baseVid.mp4"
        val outputName = "_baseVidWithText3.mp4"
        val fFmpeg = FFmpeg.getInstance(this)
        val text = addText.text.toString()
        val command = arrayOf("-i", BASE_OUTPUT_PATH + inputName, "-filter_complex", "color=black@0:100x100,format=yuva444p[c]; [c][0]scale2ref[ct][mv0]; [ct]setsar=1,split=1[t1]; " +

                "[t1]drawtext=fontfile=" + BASE_FONT_DIR + fontName + ": text='$text':x=if(between(t\\,1\\,2.5)\\, w-((t-1)*(w+text_w)/3)\\, if(gte(t\\,2.5)\\, (w-text_w)/2\\, w)): y=(2*text_h)-20: fontsize=80: fontcolor=0xc0f893[text1];" +
                "[mv0][text1]overlay= shortest=1", "-codec:a", "copy", BASE_OUTPUT_PATH + outputName)
        fFmpeg.execute(command, object : ExecuteBinaryResponseHandler() {
            override fun onSuccess(message: String?) {
                mMaterialDialog?.dismiss()
                Toast.makeText(this@TemplateActivity, "Good", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(message: String?) {
                mMaterialDialog?.dismiss()
                Log.e("Error", message)
                Toast.makeText(this@TemplateActivity, "Ooops, please check log", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BaseActivity.REQ_CODE_IMAGE) {
                val uri = data?.data
                if (uri != null) {
                    val fileName = FileUtility.getFileName(this, uri)
                    mPath = FileUtility.getPath(this, uri)
                    Log.d("", "")
                    loadThumbnail(uri)
                }
            }
        }
    }
}