package com.gvm.demoffmpeg

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.provider.OpenableColumns



/**
 * Brought to you by rickykurniawan on 06/07/18.
 */
object FileUtility {


    @Throws(IOException::class)
    fun copyDirOrFileFromAsset(applicationContext: Context, assetDir: String, destinationDir: String) {
        val destDirectory = File(destinationDir)

        createDir(destDirectory)

        val assetManager = applicationContext.assets
        val files = assetManager.list(assetDir)

        for (file in files) {

            val assetFilePath = addTrailingSlash(assetDir) + file
            val subFiles = assetManager.list(assetFilePath)

            if (subFiles.isEmpty()) {
                // It is a file
                val destFilePath = addTrailingSlash(destinationDir) + file
                copyAssetFile(applicationContext, assetFilePath, destFilePath)
            } else {
                // It is a sub directory
                copyDirOrFileFromAsset(applicationContext, assetFilePath, addTrailingSlash(destinationDir) + file)
            }
        }
    }

    @Throws(IOException::class)
    private fun copyAssetFile(applicationContext: Context, assetFilePath: String, destinationFilePath: String) {
        val `in` = applicationContext.assets.open(assetFilePath)
        val out = FileOutputStream(destinationFilePath)

        val buf = ByteArray(1024)
        var len: Int? = 0
        while ((`in`.read(buf).let {
                    len = it
                    it > 0
                })) out.write(buf, 0, len!!)
        `in`.close()
        out.close()
    }

    private fun addTrailingSlash(pathParameter: String): String {
        var path = pathParameter
        if (path[path.length - 1] != '/') {
            path += "/"
        }
        return path
    }

    fun addLeadingSlash(pathParameter: String): String {
        var path = pathParameter
        if (path[0] != '/') {
            path = "/$path"
        }
        return path
    }

    fun checkFileExists(videoName: String, baseVideoPath: String) {
        val path = baseVideoPath + videoName
        val file = File(path)
        when (file.delete()) {
            true -> Log.e("Delete file","File exists, deleting")
            else -> Log.e("Delete file","File does not exist, nothing to delete")
        }
    }

    @Throws(IOException::class)
    private fun createDir(dir: File) {
        if (dir.exists()) {
            if (!dir.isDirectory) {
                throw IOException("Can't create directory, a file is in the way")
            }
        } else {
            dir.mkdirs()
            if (!dir.isDirectory) {
                throw IOException("Unable to create directory")
            }
        }
    }

    fun getImageFromGallery(context: Context) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        (context as BaseActivity).startActivityForResult(Intent.createChooser(intent, "Select Image"), BaseActivity.REQ_CODE_IMAGE)
    }

    fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } catch (ex: Exception) {
                Log.e("Cursor","Error cursor")
                ex.printStackTrace()
            }
            cursor.close()
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    fun getAudioFileLength(applicationContext: Context, pathToAudioFile: String): Int {
        val uri = Uri.parse(pathToAudioFile)
        val mediaMetaDataRetriever = MediaMetadataRetriever()
        mediaMetaDataRetriever.setDataSource(applicationContext, uri)
        val duration = mediaMetaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val milliSecond = duration.toInt()
        return milliSecond / 1000
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Throws(NumberFormatException::class)
    fun getPath(context: Context, uri: Uri): String? {
        // DocumentProvider
        when {
            DocumentsContract.isDocumentUri(context, uri) -> when {
                isExternalStorageDocument(uri) -> { // ExternalStorageProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    return if ("primary".equals(type, ignoreCase = true)) {
                        Environment.getExternalStorageDirectory().toString() + "/" + split[1] //internal storage
                    } else {
                        "/storage/sdcard1/" + split[1] //sd card
                    }
                }
                isDownloadsDocument(uri) -> {  // DownloadsProvider
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                    return getDataColumn(context, contentUri, null, null)
                }
                isMediaDocument(uri) -> { // MediaProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    when (type) {
                        "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])

                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
                else -> {
                }
            }
            "content".equals(uri.scheme, ignoreCase = true) -> {
                println("yiha")
                return getDataColumn(context, uri, null, null)
            }
            "file".equals(uri.scheme, ignoreCase = true) -> return uri.path
            else -> println("here")
        }
        return null
    }

    private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            val resolver = context.contentResolver
            cursor = resolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}