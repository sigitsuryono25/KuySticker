package com.surelabsid.kuysticker

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.surelabsid.kuysticker.model.StickerPackModel
import com.surelabsid.kuysticker.network.Network
import com.surelabsid.kuysticker.utils.FileUtils
import com.surelabsid.kuysticker.utils.ImageHelper
import com.surelabsid.kuysticker.utils.StickerPacksManager
import com.surelabsid.kuysticker.whatsapp_api.*
import com.vlk.multimager.adapters.GalleryImagesAdapter
import com.vlk.multimager.utils.Image
import com.vlk.multimager.utils.Params
import kotlinx.android.synthetic.main.activity_create_new_sticker_pack.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.random.Random

class CreateNewStickerPackActivity : AppCompatActivity() {
    val uries: MutableList<Uri> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_sticker_pack)

        //check incoming intent
        val incomeSticker = intent.getParcelableArrayListExtra<Image>(INCOMING_IMAGE)
        manageSticker(incomeSticker)

        done.setOnClickListener {
            saveToArray(incomeSticker)
        }
        close.setOnClickListener {
            finish()
        }
    }

    private fun saveToArray(incomeSticker: ArrayList<Image>?) {
        val fileName = StickerPackModel()
        fileName.id = Random.nextLong()
        fileName.author = author.text.toString()
        fileName.packName = stickerPackName.text.toString()

        val base64ListImage = mutableListOf<String?>()
        incomeSticker?.forEach {
            val base64Item = ImageHelper.base64ImageTest(it.imagePath, null, 60)
            base64ListImage.add(base64Item)
            uries.add(it.uri)
        }
        fileName.stickerList = base64ListImage

        sendData(fileName)
    }

    private fun sendData(fileName: StickerPackModel) {
        Network.setupService().uploadFileBatch(fileName)
            .enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    savePack(uries, stickerPackName.text.toString(), author.text.toString())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                }
            })
    }

    private fun savePack(uries: MutableList<Uri>, name: String, author: String) {
        Thread {

            try {
                val intent = Intent(
                    this@CreateNewStickerPackActivity,
                    StickerPackDetailsActivity::class.java
                )
                intent.putExtra(StickerPackDetailsActivity.EXTRA_SHOW_UP_BUTTON, true)

                val identifier = "." + FileUtils.generateRandomIdentifier()
                val stickerPack = StickerPack(
                    identifier,
                    name,
                    author,
                    Objects.requireNonNull<Array<Any>>(uries.toTypedArray())[0].toString(),
                    "",
                    "",
                    "",
                    ""
                )

                //Save the sticker images locally and get the list of new stickers for pack

                //Save the sticker images locally and get the list of new stickers for pack
                val stickerList: List<Sticker> = StickerPacksManager.saveStickerPackFilesLocally(
                    stickerPack.identifier,
                    uries,
                    this@CreateNewStickerPackActivity
                )
                stickerPack.stickers = stickerList

                //Generate image tray icon

                //Generate image tray icon
                val stickerPath: String = Constants.STICKERS_DIRECTORY_PATH.toString() + identifier
                val trayIconFile: String = FileUtils.generateRandomIdentifier().toString() + ".png"
                StickerPacksManager.createStickerPackTrayIconFile(
                    uries[0], Uri.parse(
                        "$stickerPath/$trayIconFile"
                    ), this@CreateNewStickerPackActivity
                )
                stickerPack.trayImageFile = trayIconFile

                //Save stickerPack created to write in json

                //Save stickerPack created to write in json
                StickerPacksManager.stickerPacksContainer.addStickerPack(stickerPack)
                StickerPacksManager.saveStickerPacksToJson(StickerPacksManager.stickerPacksContainer)
                insertStickerPackInContentProvider(stickerPack)

                //Start new activity with stickerpack information

                //Start new activity with stickerpack information
                intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_DATA, stickerPack)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }.start()
    }

    private fun insertStickerPackInContentProvider(stickerPack: StickerPack) {
        val contentValues = ContentValues()
        contentValues.put("stickerPack", Gson().toJson(stickerPack))
        contentResolver.insert(StickerContentProvider.AUTHORITY_URI, contentValues)
    }


    private fun manageSticker(incomeSticker: ArrayList<Image>?) {
        if (incomeSticker != null) {
            if (incomeSticker.size < 1) {
                Glide.with(this)
                    .load(incomeSticker[0].imagePath)
                    .into(stickerPreview)
            } else {
                val adapter =
                    GalleryImagesAdapter(this, incomeSticker, incomeSticker.size, Params())
                stickerList.adapter = adapter
                val layoutManager = StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)
                layoutManager.gapStrategy =
                    StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
                stickerList.layoutManager = layoutManager


            }
        }
    }


    fun saveToFile(uri: String) {
        ImageHelper.saveFile(this, Uri.parse(uri), 60)
    }

    companion object {
        const val INCOMING_IMAGE = "incoming object"
    }
}