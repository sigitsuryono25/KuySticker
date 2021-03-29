package com.surelabsid.kuysticker.model

import android.os.Parcelable
import com.vlk.multimager.utils.Image
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StickerPackModel(
    var id : Long? = null,
    var packName : String? = null,
    var author: String? = null,
    var stickerList: List<String?>? = null
): Parcelable
