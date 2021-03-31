package com.surelabsid.kuysticker

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.surelabsid.kuysticker.model.StickerPackModel
import com.surelabsid.kuysticker.network.Network
import com.surelabsid.kuysticker.utils.ImageHelper
import com.surelabsid.whatsappapi.utils.FileUtils
import com.surelabsid.whatsappapi.utils.StickerPacksManager
import com.surelabsid.whatsappapi.whatsapp_api.*
import com.vlk.multimager.adapters.GalleryImagesAdapter
import com.vlk.multimager.utils.Image
import com.vlk.multimager.utils.Params
import kotlinx.android.synthetic.main.activity_create_new_sticker_pack.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.util.*

class CreateNewStickerPackActivity : BaseActivity() {
    val uries: MutableList<HashMap<String?, Uri?>> = mutableListOf()
    var pd: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_sticker_pack)

        //check incoming intent
        val incomeSticker = intent.getParcelableArrayListExtra<Image>(INCOMING_IMAGE)
        manageSticker(incomeSticker)

        done.setOnClickListener {
            pd =
                ProgressDialog.show(this@CreateNewStickerPackActivity, "", "Tunggu...", true, false)
            GlobalScope.launch {
                saveToArray(incomeSticker)
            }
        }
        close.setOnClickListener {
            finish()
        }
    }

    @WorkerThread
    private fun saveToArray(incomeSticker: ArrayList<Image>?) {
        val fileName = StickerPackModel()
        fileName.identifier = FileUtils.generateRandomIdentifier()
        fileName.author = author.text.toString()
        fileName.name = stickerPackName.text.toString()

        //file name untuk Tray Image. Ini dipake buat nama filenya ketika sudah di upload
        fileName.trayImageFileName = FileUtils.generateRandomIdentifier().toString() + ".png"

        val fileNameAndBase64 = mutableListOf<HashMap<String?, String?>?>()
        val maps: HashMap<String?, String?> = hashMapOf()
        val uri: HashMap<String?, Uri?> = hashMapOf()

        incomeSticker?.forEachIndexed { index, it ->
            //filename untuk masing-masing sticker
            val fileNames = FileUtils.generateRandomIdentifier()

            //convert filenya yang dipilih dari step sebelumnya jadi base64 dengan quality 20%
            // whatsapp sticker hanya memperbolehkan ukuran sticker 100kb, jadi atur sendiri
            val base64Item = ImageHelper.base64ImageTest(it.imagePath, null, 20)

            //letakkan filename dengan base64 nya, supaya nggak ketuker
            //pahami lagi konsep dari hashmap kalo bingung
            maps.put(fileNames, base64Item)

            //add hashmaps dari filename dan base64 tadi
            fileNameAndBase64.add(maps)

            //sama, letakkan uri dari masing masing image dengan key nya itu nama file
            uri[fileNames] = it.uri

            //add uri hashmaps ke mutable list uries
            uries.add(uri)

            //setiap sticker di WhatsApp itu harusnya ada cover atau Tray Image File.
            // disini secara default, gambar yang pertama kali dipilih akan jadi cover atau Tray Image Filenya
            if (index == 0) {
                //tray Image File (ini base64 nya ya)
                fileName.trayImageFile = base64Item
            }
        }
        //sticker listnya
        fileName.stickerList = fileNameAndBase64

        //panggil kirim data keserver
        sendData(fileName)
    }

    private fun sendData(fileName: StickerPackModel) {
        Network.setupService().uploadFileBatch(fileName)
            .enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    //harusnya ada pengecekan dulu, kalo berhasil baru panggil save pack
                    savePack(
                        fileName,
                        uries,
                        stickerPackName.text.toString(),
                        author.text.toString()
                    )
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    pd?.dismiss()
                    Toast.makeText(this@CreateNewStickerPackActivity, t.message, Toast.LENGTH_SHORT)
                        .show()
                    t.printStackTrace()
                }
            })
    }

    /**
    * @see savePack()
     *
     * requirements dari WhatsApp Sticker itu
     * a. Uri dari setiap gambar yang udah dipilih
     * b. identifier, packname, author(publisher), file name tray image
     * savepack() itu dipake untuk
     * 1. menyimpan data sticker ke gallery
     * 2. integrasi ke detail sticker yang nantinya disana ada proses untuk nambahkan sticker ke whatsapp
    * */

    private fun savePack(
        model: StickerPackModel,
        data: MutableList<HashMap<String?, Uri?>>,
        name: String,
        author: String
    ) {
        try {
            val uries = mutableListOf<Uri?>()
            data.forEach { hashMap ->
                for (key in hashMap.keys) {
                    uries.add(hashMap[key])
                }
            }

            // sticker pack ini dipake untuk JSON nya ya
            val stickerPack = StickerPack(
                model.identifier.toString(),
                name,
                author,
                //ambil nama file berdasarkan uri nya
                Objects.requireNonNull(uries.toTypedArray())[0].toString(),
                "",
                "",
                "",
                ""
            )

            //ini proses untuk save gambar yang terpilih tadi ke gallery (udah sekalian di format ke WEBP)
            val stickerList = StickerPacksManager.saveStickerPackFilesLocally(
                stickerPack.identifier,
                data,
                this@CreateNewStickerPackActivity
            )
            //ini untuk stickerList nya
            stickerPack.stickers = stickerList

            //lokasi simpan tray icon (cover) nya. Formatnya akan diubah jadi PNG
            val stickerPath = Constants.STICKERS_DIRECTORY_PATH.toString() + model.identifier
            val trayIconFile = model.trayImageFileName
            StickerPacksManager.createStickerPackTrayIconFile(
                uries[0], Uri.parse(
                    "$stickerPath/$trayIconFile"
                ), this@CreateNewStickerPackActivity
            )
            stickerPack.trayImageFile = trayIconFile

            //ini panggil activity Sticker Detail
            /**
             *
             * @see prepareDataForDetail()
             *
             * */
            prepareDataForDetail(stickerPack)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        pd?.dismiss()
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

    companion object {
        const val INCOMING_IMAGE = "incoming object"
    }
}