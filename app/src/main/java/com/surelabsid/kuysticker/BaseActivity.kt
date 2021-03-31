package com.surelabsid.kuysticker

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.surelabsid.whatsappapi.identities.StickerPacksContainer
import com.surelabsid.whatsappapi.utils.StickerPacksManager
import com.surelabsid.whatsappapi.whatsapp_api.StickerPack
import com.surelabsid.whatsappapi.whatsapp_api.StickerPackDetailsActivity

abstract class BaseActivity : AppCompatActivity() {


    //insert JSON ke contents.json
    private fun insertStickerPackInContentProvider(stickerPack: StickerPack?) {
        val contentValues = ContentValues()
        contentValues.put("stickerPack", Gson().toJson(stickerPack))
        contentResolver.insert(StickerContentProvider.AUTHORITY_URI, contentValues)
    }

    fun prepareDataForDetail(stickerPack: StickerPack?) {
        val stickerDetailIntent = Intent(
            this,
            StickerPackDetailsActivity::class.java
        )

        val listPack = mutableListOf<StickerPack?>()
        listPack.add(stickerPack)


        StickerPacksManager.stickerPacksContainer =
            StickerPacksContainer("", "", listPack)

        //create JSON filenya
        StickerPacksManager.saveStickerPacksToJson(StickerPacksManager.stickerPacksContainer)

        //convert Class StickerPack ke JSON dan tulis ke contents.json
        insertStickerPackInContentProvider(stickerPack)

        //prepare data untuk kirim ke Sticker Pack Detail Activity
        stickerDetailIntent.apply {
            putExtra(StickerPackDetailsActivity.EXTRA_SHOW_UP_BUTTON, true)
            putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_DATA, stickerPack)
        }
        startActivity(stickerDetailIntent)
    }

}