package com.temp.email.cr.model.temMailApiModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Keep
data class InboxMessagesResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("from") val from: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("date") val date: String
) : Serializable
