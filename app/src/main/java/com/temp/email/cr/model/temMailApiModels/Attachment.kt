package com.temp.email.cr.model.temMailApiModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class Attachment(
    @SerializedName("filename") val filename: String,
    @SerializedName("contentType") val contentType: String,
    @SerializedName("size") val size: Long
)
