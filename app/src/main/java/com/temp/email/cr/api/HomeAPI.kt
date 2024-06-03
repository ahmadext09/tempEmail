package com.temp.email.cr.api

import com.temp.email.cr.model.temMailApiModels.EmailContentResponse
import com.temp.email.cr.model.temMailApiModels.InboxMessagesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface HomeAPI {

    @Headers("Content-Type: application/json")
    @GET("api/v1/")
    suspend fun getRandomMailbox(
        @Query("action") action: String,
        @Query("count") count: Int
    ): Response<List<String>>


    @Headers("Content-Type: application/json")
    @GET("api/v1/")
    suspend fun getInboxMessages(
        @Query("action") action: String,
        @Query("login") login: String,
        @Query("domain") domain: String
    ): Response<List<InboxMessagesResponse>>


    @Headers("Content-Type: application/json")
    @GET("api/v1/")
    suspend fun getEmailContent(
        @Query("action") action: String,
        @Query("login") login: String,
        @Query("domain") domain: String,
        @Query("id") id: Long
    ): Response<EmailContentResponse>

}