package com.surelabsid.kuysticker

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.gabrielbb.cutout.CutOut
import com.surelabsid.kuysticker.adapter.AdapterSticker
import com.surelabsid.kuysticker.model.ResponseStickerPackList
import com.surelabsid.kuysticker.model.StickerPacksItem
import com.surelabsid.kuysticker.network.Network
import com.surelabsid.whatsappapi.whatsapp_api.Sticker
import com.surelabsid.whatsappapi.whatsapp_api.StickerPack
import com.vlk.multimager.activities.GalleryActivity
import com.vlk.multimager.utils.Constants
import com.vlk.multimager.utils.Image
import com.vlk.multimager.utils.Params
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.random.Random

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createSticker.setOnClickListener {
            GlobalScope.launch {
                imagePicker()
            }
        }

        GlobalScope.launch {
            getAllSticker()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            //something wrong
            return
        }

        when (requestCode) {
            Constants.TYPE_MULTI_PICKER -> handleIntentPicker(data)
            CutOut.CUTOUT_ACTIVITY_REQUEST_CODE.toInt() -> {
                val uri = CutOut.getUri(data)

                //do something with this uri
                val imageList = arrayListOf<Image>()
                val image = Image(Random.nextLong(), uri, uri.path, false)
                imageList.add(image)

                //send to new pack activity
                val newStickerPackActivity = Intent(this, CreateNewStickerPackActivity::class.java)
                newStickerPackActivity.apply {
                    putExtra(CreateNewStickerPackActivity.INCOMING_IMAGE, imageList)
                    startActivity(this)
                }
            }
        }
    }

    // ini dipakai untuk menghandle intent/data dari file picker
    private fun handleIntentPicker(data: Intent?) {
        val imagesList: ArrayList<Image>? =
            data?.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST)

        if (imagesList != null) {
            if (imagesList.size > 1) {
                // ini pasti dia pilih lebih dari satu gambar
                val newStickerPackActivity = Intent(this, CreateNewStickerPackActivity::class.java)
                newStickerPackActivity.apply {
                    putExtra(CreateNewStickerPackActivity.INCOMING_IMAGE, imagesList)
                    startActivity(this)
                }
            } else {
                // satu gambar aja yang dipilih
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Kesalahan")
                    .setMessage("WhatsApp hanya mengizinkan minimal 3 foto")
                    .setPositiveButton("Oke") { d, _ ->
                        d.dismiss()
                    }
                    .create().show()
            }
        }
    }

    private fun imagePicker() {
        val intent = Intent(this, GalleryActivity::class.java)
        val params = Params()
        params.pickerLimit = 10
        params.toolbarColor = resources.getColor(R.color.purple_700)
        params.actionButtonColor = resources.getColor(R.color.teal_200)
        params.buttonTextColor = resources.getColor(R.color.teal_200)
        intent.putExtra(Constants.KEY_PARAMS, params)
        startActivityForResult(intent, Constants.TYPE_MULTI_PICKER)
    }

    private fun getAllSticker() {
        Network.setupService().getAllStickerList()
            .enqueue(object : retrofit2.Callback<ResponseStickerPackList> {
                override fun onResponse(
                    call: Call<ResponseStickerPackList>,
                    response: Response<ResponseStickerPackList>
                ) {
                    val data = response.body()
                    setToAdapter(data)
                }

                override fun onFailure(call: Call<ResponseStickerPackList>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                    t.printStackTrace()
                }

            })
    }

    private fun setToAdapter(data: ResponseStickerPackList?) {
        val adapter = AdapterSticker(data?.stickerPacks) { stickerPackItems ->
            initialItemClick(stickerPackItems)
        }
        listSticker.adapter = adapter
        listSticker.itemAnimator = DefaultItemAnimator()
        listSticker.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
    }

    private fun initialItemClick(stickerPackItems: StickerPacksItem?) {
        //create stickerpack
        val stickerPack = StickerPack(
            stickerPackItems?.identifier.toString(),
            stickerPackItems?.name,
            stickerPackItems?.publisher,
            stickerPackItems?.trayImageFile,
            stickerPackItems?.publisherEmail,
            stickerPackItems?.publisherWebsite,
            stickerPackItems?.privacyPolicyWebsite,
            stickerPackItems?.licenseAgreementWebsite
        )

        val sticker = mutableListOf<Sticker>()
        stickerPackItems?.stickers?.forEach { stickersItem ->
            val s = Sticker(
                stickersItem?.imageFileName.toString(), null
            )
            sticker.add(s)
        }
        stickerPack.stickers = sticker
        stickerPack.trayImageFile = stickerPackItems?.trayImageFile

        prepareDataForDetail(stickerPack)

    }


}