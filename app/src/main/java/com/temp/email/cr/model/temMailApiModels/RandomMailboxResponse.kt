package com.temp.email.cr.model.temMailApiModels

import androidx.annotation.Keep


@Keep
data class RandomMailboxResponse(
    val emails: List<String>
)
