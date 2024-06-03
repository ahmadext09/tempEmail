package com.temp.email.cr.receiver


import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.temp.email.cr.utility.AppConstants


class DownloadReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent?.action) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (context != null && downloadId != -1L) {
                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)

                    if (statusIndex != -1 && uriIndex != -1) {
                        val status = cursor.getInt(statusIndex)
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            val uriString = cursor.getString(uriIndex)
                            val fileUri = Uri.parse(uriString)
                            sendDownloadCompleteBroadcast(context, fileUri)
                            Toast.makeText(context, "Download complete", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                cursor.close()
            }
        }
    }

    private fun sendDownloadCompleteBroadcast(context: Context, uri: Uri) {
        val intent = Intent(AppConstants.INTENT_KEY.LOCAL_BROADCAST_INTENT_ACTION)
        intent.putExtra(AppConstants.INTENT_KEY.FILE_URI_INTENT_KEY, uri.toString())
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}