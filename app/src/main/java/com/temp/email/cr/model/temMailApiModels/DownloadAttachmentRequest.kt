package com.temp.email.cr.model.temMailApiModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class DownloadAttachmentRequest(
    @SerializedName("action") val action: String,
    @SerializedName("login") val login: String,
    @SerializedName("domain") val domain: String,
    @SerializedName("id") val id: Long,
    @SerializedName("file") val file: String
)
