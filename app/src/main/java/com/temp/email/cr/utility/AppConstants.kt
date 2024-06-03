package com.temp.email.cr.utility

object AppConstants {

    const val LOG_TAG = "ANKUSH"
    const val SHARED_PREFS = "AppSharedPrefs"

    object ERROR_CODES {
        const val APP_ERROR_CODE = 900
        const val C_UNAUTHORIZED = 403
    }

    object SHARED_PREF {
        const val CURRENT_EMAIL_ADDRESS = "current_email_address"
    }

    object SEC_MAIL {
        const val RANDOM_MAILBOX_QUERY_ACTION = "genRandomMailbox"
        const val INBOX_MAILBOX_QUERY_ACTION = "getMessages"
        const val EMAIL_CONTENT_QUERY_ACTION = "readMessage"
        const val ATTACHMENT_DOWNLOAD = "download"
    }

    object INTENT_KEY {
        const val MESSAGE_ID_INTENT_KEY = "messageIdKey"
        const val LOCAL_BROADCAST_INTENT_ACTION = "com.temp.email.cr.DOWNLOAD_COMPLETE"
        const val FILE_URI_INTENT_KEY = "fileUri"
    }

}