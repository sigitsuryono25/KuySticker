package com.surelabsid.kuysticker.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseStickerPackList(

	@field:SerializedName("iosAppStoreLink")
	val iosAppStoreLink: String? = null,

	@field:SerializedName("stickerPacks")
	val stickerPacks: List<StickerPacksItem?>? = null,

	@field:SerializedName("androidPlayStoreLink")
	val androidPlayStoreLink: String? = null
) : Parcelable

@Parcelize
data class StickersItem(

	@field:SerializedName("size")
	val size: Int? = null,

	@field:SerializedName("imagePath")
	val imagePath: String? = null,

	@field:SerializedName("imageFileName")
	val imageFileName: String? = null
) : Parcelable

@Parcelize
data class StickerPacksItem(

	@field:SerializedName("licenseAgreementWebsite")
	val licenseAgreementWebsite: String? = null,

	@field:SerializedName("identifier")
	val identifier: String? = null,

	@field:SerializedName("publisherWebsite")
	val publisherWebsite: String? = null,

	@field:SerializedName("totalSize")
	val totalSize: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("publisher")
	val publisher: String? = null,

	@field:SerializedName("isWhiteListed")
	val isWhiteListed: Boolean? = null,

	@field:SerializedName("stickers")
	val stickers: List<StickersItem?>? = null,

	@field:SerializedName("publisherEmail")
	val publisherEmail: String? = null,

	@field:SerializedName("trayImageFile")
	val trayImageFile: String? = null,

	@field:SerializedName("trayFile")
	val trayFile: String? = null,

	@field:SerializedName("privacyPolicyWebsite")
	val privacyPolicyWebsite: String? = null
) : Parcelable
