package com.surelabsid.kuysticker.model

import android.os.Parcelable
import com.vlk.multimager.utils.Image
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StickerPackModel(
    var id : Long? = null,
    var name : String? = null,
    var author: String? = null,
    var stickerList: List<HashMap<String?, String?>?>? = null,
    var trayImageFileName : String? = null,
    var trayImageFile: String? = null

): Parcelable
