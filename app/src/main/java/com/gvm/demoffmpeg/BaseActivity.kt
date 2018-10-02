package com.gvm.demoffmpeg

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.afollestad.materialdialogs.MaterialDialog
import nl.bravobit.ffmpeg.FFmpeg

/**
 * Brought to you by rickykurniawan on 30/07/18.
 */
abstract class BaseActivity : AppCompatActivity() {

    companion object {
        private const val mDirectoryName = "/Demo2"
        val mBasePath = Environment.getExternalStorageDirectory().path + mDirectoryName
        val BASE_FONT_DIR = "$mBasePath/fontDemo2/"
        val BASE_AUDIO_DIR = "$mBasePath/songsDemo2/"
        val BASE_TEMPLATE_DIR = "$mBasePath/templateDemo2/"
        val BASE_OUTPUT_PATH = "$mBasePath/output/"

        const val PERMISSION_WRITE = 1
        const val PERMISSION_READ = 2
        const val REQ_CODE_IMAGE = 99
    }

    var mMaterialDialogBuilder: MaterialDialog.Builder? = null

    var mMaterialDialog: MaterialDialog? = null

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        initFFMPEG()
        checkForPermission()
    }

    open fun initFFMPEG() {
        val isSupported = FFmpeg.getInstance(this).isSupported
        val log = if (isSupported) "DEVICE IS SUPPORTED" else "DEVICE NOT SUPPORTED"
        Log.e("FFMPEG init", log)
    }

    open fun setProgressDialog() {
        mMaterialDialogBuilder?.content("Wait")
        mMaterialDialogBuilder?.progressIndeterminateStyle(true)
        mMaterialDialog = mMaterialDialogBuilder?.show()
    }

    open fun checkForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    BaseActivity.PERMISSION_WRITE)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    BaseActivity.PERMISSION_READ)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            BaseActivity.PERMISSION_WRITE -> {
                if ((grantResults.isNotEmpty()) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*Do if success*/
                } else {
                    mMaterialDialogBuilder?.content("Please enable all permission, to use this feature")
                    mMaterialDialogBuilder?.positiveText("Ok")
                    mMaterialDialogBuilder?.onPositive { _, _ -> finish() }
                    mMaterialDialog = mMaterialDialogBuilder?.show()
                }
            }
            BaseActivity.PERMISSION_READ -> {
                if ((grantResults.isNotEmpty()) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*Do if success*/
                } else {
                    mMaterialDialogBuilder?.content("Please enable all permission, to use this feature")
                    mMaterialDialogBuilder?.positiveText("Iye")
                    mMaterialDialogBuilder?.onPositive { _, _ -> finish() }
                    mMaterialDialog = mMaterialDialogBuilder?.show()
                }
            }
        }
    }
}