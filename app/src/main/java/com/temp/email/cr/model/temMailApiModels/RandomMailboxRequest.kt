package com.temp.email.cr.model.temMailApiModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RandomMailboxRequest(
    @SerializedName("action") val action: String,
    @SerializedName("count") val count: Int
)