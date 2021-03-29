package com.surelabsid.kuysticker.network

import com.surelabsid.kuysticker.model.StickerPackModel
import com.surelabsid.kuysticker.model.UploadModel
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ServiceApi {

    @POST("Uploadfile/index")
    fun uploadFile(@Body uploadModel: UploadModel): retrofit2.Call<ResponseBody>


    @POST("Uploadfile/index")
    fun uploadFileBatch(@Body uploadModel: StickerPackModel): retrofit2.Call<ResponseBody>
}
