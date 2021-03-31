package com.surelabsid.kuysticker.adapter

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.surelabsid.kuysticker.R
import com.surelabsid.kuysticker.model.StickerPacksItem
import com.surelabsid.kuysticker.utils.ImageHelper
import com.surelabsid.whatsappapi.utils.ImageUtils
import com.surelabsid.whatsappapi.whatsapp_api.Constants
import kotlinx.android.synthetic.main.item_adapter_sticker.view.*
import java.io.File


class AdapterSticker(
    private val mListStickerItem: List<StickerPacksItem?>?,
    private val addToWaClick: (StickerPacksItem?) -> Unit
) :
    RecyclerView.Adapter<AdapterSticker.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val containerStickerItem = itemView.containerStickerItems
        private val stickerName = itemView.stickerName
        private val author = itemView.author
        private val addToWA = itemView.addToWhatsApp

        fun onBindItem(stickersItems: StickerPacksItem?) {
            stickersItems?.stickers?.forEachIndexed { index, stickersItem ->
                if (index <= 3) {
                    val img = ImageView(itemView.context)
                    val lp = LinearLayout.LayoutParams(200, 200)
                    lp.setMargins(8, 0, 8, 0)
                    img.layoutParams = lp
                    img.scaleType = ImageView.ScaleType.CENTER_CROP

                    val imageSavingPath =
                        Constants.STICKERS_DIRECTORY_PATH + stickersItems.identifier

                    createNoMedia("$imageSavingPath/")
                    Glide.with(itemView.context)
                        .asBitmap()
                        .load(stickersItem?.imagePath)
                        .into(object : SimpleTarget<Bitmap?>(512, 512) {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap?>?
                            ) {
                                ImageHelper.saveImageAsFile(
                                    itemView.context,
                                    ImageUtils.overlayBitmapToCenter(
                                        Bitmap.createBitmap(
                                            512,
                                            512,
                                            Bitmap.Config.ARGB_8888
                                        ), Bitmap.createScaledBitmap(resource, 512, 512, false)
                                    ),
                                    40,
                                    imageSavingPath,
                                    stickersItem?.imageFileName.toString()
                                )
                            }
                        })
                    Glide.with(itemView.context).load(stickersItem?.imagePath)
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
                        .error(android.R.drawable.stat_notify_error)
                        .into(img)

                    containerStickerItem.addView(img, index)

                    Log.d("onBindItem", "onBindItem: $index")
                }
            }

            val traySavingPath = Constants.STICKERS_DIRECTORY_PATH + stickersItems?.identifier
            Glide.with(itemView.context)
                .asBitmap()
                .load(stickersItems?.trayFile)
                .into(object : SimpleTarget<Bitmap?>(100, 100) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        ImageHelper.saveImageAsFile(
                            itemView.context,
                            resource,
                            60,
                            traySavingPath,
                            stickersItems?.trayImageFile.toString()
                        )
                    }
                })

            stickerName.text = stickersItems?.name
            author.text = stickersItems?.publisher

            addToWA.setOnClickListener {
                addToWaClick(stickersItems)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_adapter_sticker, parent, false)
        )
    }

    fun createNoMedia(path: String) {
        try {
            val noMedia = File("$path.nomedia")
            if (noMedia.createNewFile()) {
                println("$noMedia File Created")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindItem(mListStickerItem?.get(position))
    }

    override fun getItemCount(): Int {
        return mListStickerItem?.size ?: 0
    }

}