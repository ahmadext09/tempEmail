package com.temp.email.cr.model.temMailApiModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class EmailContentResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("from") val from: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("date") val date: String,
    @SerializedName("attachments") val attachments: List<Attachment>,
    @SerializedName("body") val body: String,
    @SerializedName("textBody") val textBody: String,
    @SerializedName("htmlBody") val htmlBody: String
)


