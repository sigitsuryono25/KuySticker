package com.surelabsid.kuysticker.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.surelabsid.kuysticker.model.StickerPacksItem
import com.surelabsid.stickerkuy.utils.HourToMillis
import com.surelabsid.whatsappapi.utils.ImageUtils
import java.io.*

object ImageHelper {

    fun ImageView.loadImage(context: Context, uri: String) {
        Glide.with(context)
            .load(Uri.parse(uri))
            .apply(RequestOptions.centerCropTransform())
            .into(this)
    }

    fun ImageView.loadImage(context: Context, drawable: Int) {
        Glide.with(context)
            .load(drawable)
            .into(this)
    }

    fun ImageView.loadImageCircle(context: Context, drawable: Int) {
        Glide.with(context)
            .load(drawable)
            .apply(RequestOptions.circleCropTransform())
            .into(this)
    }

    fun ImageView.loadImageCircle(context: Context, uri: String) {
        Glide.with(context)
            .load(Uri.parse(uri))
            .apply(RequestOptions.circleCropTransform())
            .into(this)
    }

    fun base64ImageTest(filePath: String?, bitmap: Bitmap?, quality: Int): String? {
        if (filePath?.isNotEmpty() == true) {
            val imageFile = File(filePath)
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(imageFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val bm = BitmapFactory.decodeStream(fis)
            val baos = ByteArrayOutputStream()
            ImageUtils.overlayBitmapToCenter(Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888), bm)
            bm.compress(Bitmap.CompressFormat.WEBP, quality, baos)
            val b = baos.toByteArray()
            return Base64.encodeToString(b, Base64.DEFAULT)
        } else {
            if (bitmap != null) {
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.WEBP, quality, baos)
                val b = baos.toByteArray()
                print(Base64.encodeToString(b, Base64.DEFAULT))
                return Base64.encodeToString(b, Base64.DEFAULT)
            }
            return null
        }
    }

    fun imageFileToByte(filePath: String?, quality: Int): ByteArray? {
        if (filePath?.isNotEmpty() == true) {
            val imageFile = File(filePath)
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(imageFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val bm = BitmapFactory.decodeStream(fis)
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.WEBP, quality, baos)
            return baos.toByteArray()
        }
        return null
    }

    fun saveFile(context: Context, sourceuri: Uri, quality: Int, path: String) {
        val sourceFilename: String = sourceuri.path.toString()
        val bmp = BitmapFactory.decodeFile(sourceFilename)
        this.saveImageAsFile(context, bmp, quality, path, HourToMillis.millis().toString())
    }

    fun saveImageAsFile(
        context: Context,
        bmp: Bitmap,
        quality: Int = 70,
        path: String, imageFileName: String
    ): String? {
        var savedImagePath: String? = null
        val pathDir = File(path)
        var success = true
        if (!pathDir.exists()) {
            success = pathDir.mkdirs()
        }

        if (success) {
            val imageFile = File(pathDir, imageFileName)
            savedImagePath = imageFile.absolutePath
            try {
                val outputStream: OutputStream = FileOutputStream(imageFile)
                bmp.compress(Bitmap.CompressFormat.WEBP, quality, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            galleryAddPic(context = context, savedImagePath)
            Log.d("saveImageAsFile", "saveImageAsFile: image saved")

        }

        return savedImagePath
    }

    private fun galleryAddPic(context: Context, imagePath: String?) {
        imagePath?.let { path ->
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val f = File(path)
            val contentUri: Uri = Uri.fromFile(f)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
        }
    }

    fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)
    }

    fun convertToBase64(file: File): String {
        return Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
    }

    fun writeStringAsFile(
        fileContents: String?,
        fileName: String
    ) {
        try {
            val out = FileWriter(File(Environment.getExternalStorageDirectory().path, fileName))
            out.write(fileContents)
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}