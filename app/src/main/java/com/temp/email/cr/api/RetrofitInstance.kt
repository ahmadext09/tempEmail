package com.temp.email.cr.api


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.temp.email.cr.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    private val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


    private var okHttp: OkHttpClient.Builder = if (BuildConfig.DEBUG) {
        OkHttpClient.Builder().addInterceptor(logging)
    } else {
        OkHttpClient.Builder()
    }


    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    fun <T> buildServiceWithURL(serviceType: Class<T>, baseURL: String): T {

        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttp.build())
            .build()
            .create(serviceType)
    }
}