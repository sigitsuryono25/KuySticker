package com.surelabsid.kuysticker.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Network {

    val BASEURL = "http://192.168.18.114/ci-test/index.php/"

    // function untuk melihat lalu lintas data yang dikirim dan diterima
    // setiap data akan tercetak ke dalam logcat
    private fun setupOkHttp(): OkHttpClient{
        val logging = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC)
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    // configurasi retrofit client
    private fun setupRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(setupOkHttp())
            .build()
    }

    fun setupService(): ServiceApi {
        return setupRetrofit().create(ServiceApi::class.java)
    }

}