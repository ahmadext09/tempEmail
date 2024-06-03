package com.temp.email.cr.utility

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun String.copyText() {
    val clipboardManager: ClipboardManager? = MainApplication.getMyApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clipData = ClipData.newPlainText("Tools", this)
    clipboardManager?.setPrimaryClip(clipData)
}