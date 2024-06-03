package com.temp.email.cr.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.temp.email.cr.BuildConfig
import com.temp.email.cr.R
import com.temp.email.cr.databinding.ActivityEmailContentBinding
import com.temp.email.cr.fragment.AttachmentBottomSheetFragment
import com.temp.email.cr.model.temMailApiModels.Attachment
import com.temp.email.cr.model.temMailApiModels.DownloadAttachmentRequest
import com.temp.email.cr.model.temMailApiModels.EmailContentRequest
import com.temp.email.cr.model.temMailApiModels.EmailContentResponse
import com.temp.email.cr.model.temMailApiModels.InboxMessagesRequest
import com.temp.email.cr.receiver.DownloadReceiver
import com.temp.email.cr.repository.HomeRepository
import com.temp.email.cr.utility.AppConstants
import com.temp.email.cr.utility.AppUtility
import com.temp.email.cr.utility.Resource
import com.temp.email.cr.viewmodel.HomeViewModel
import com.temp.email.cr.viewmodel.HomeViewModelProviderFactory
import java.io.File
import java.util.Locale

class EmailContentActivity : BaseActivity(), AttachmentBottomSheetFragment.AttachmentClickListener {

    private lateinit var binding: ActivityEmailContentBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var attachmentBottomSheetFragment: AttachmentBottomSheetFragment
    private lateinit var storageActivityResultLauncher: ActivityResultLauncher<Intent>
    private val STORAGE_PERMISSION_CODE = 23
    private var attachmentDownloadQuery: DownloadAttachmentRequest? = null


    private val downloadCompleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AppConstants.INTENT_KEY.LOCAL_BROADCAST_INTENT_ACTION) {
                val fileUriString = intent.getStringExtra(AppConstants.INTENT_KEY.FILE_URI_INTENT_KEY)
                val fileUri = Uri.parse(fileUriString)
                if (fileUri != null) {
                    openDownloadedFile(fileUri)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailContentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initViews()
    }

    private fun init() {
        application?.let { application ->
            val repository = HomeRepository()
            val viewModelProviderFactory = HomeViewModelProviderFactory(application, repository)
            viewModel = ViewModelProvider(this, viewModelProviderFactory)[HomeViewModel::class.java]
            attachmentBottomSheetFragment = AttachmentBottomSheetFragment()
            setStorageActivityResultLauncher()
            registerLocalDownloadReceiver()

        }
    }

    private fun initViews() {
        setupToolbar()
        getEmailContent()
        setKeyText()
        observeResponse()
    }

    private fun registerLocalDownloadReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            downloadCompleteReceiver,
            IntentFilter(AppConstants.INTENT_KEY.LOCAL_BROADCAST_INTENT_ACTION)
        )
    }


    private fun setStorageActivityResultLauncher() {
        storageActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result: ActivityResult ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        startDownload()
                    } else {
                        Toast.makeText(this, getString(R.string.storage_permission_denied), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

                    if (read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED) {
                        startDownload()
                    } else {
                        Toast.makeText(this, getString(R.string.storage_permission_denied), Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun fetchDtaFromIntent(): Long? {
        val messageId = intent?.getLongExtra(AppConstants.INTENT_KEY.MESSAGE_ID_INTENT_KEY, 0)
        messageId?.let {
            if (messageId != 0L)
                return messageId
        }
        return null
    }

    private fun getEmailContent() {
        fetchDtaFromIntent()?.let { messageId ->
            AppUtility.getStringFromSharedPrefs(AppConstants.SHARED_PREF.CURRENT_EMAIL_ADDRESS, null)?.let { email ->
                AppUtility.getLoginAndDomain(email)?.let {
                    val (login, domain) = it
                    val query = EmailContentRequest(AppConstants.SEC_MAIL.EMAIL_CONTENT_QUERY_ACTION, login, domain, messageId)
                    viewModel.getEmailContent(query)
                }
            }
        }
    }


    private fun observeResponse() {
        viewModel.emailContentLiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    startShimmer(false)
                    it.data?.let { emailContentResponse ->
                        setKeyText()
                        if (emailContentResponse.attachments.isEmpty()) {
                            setResponseContent(emailContentResponse)
                        } else {
                            setResponseWithAttachment(emailContentResponse)
                        }
                    }
                }

                is Resource.Error -> {
                    startShimmer(false)
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    startShimmer(true)
                }
            }
        }
    }

    private fun setKeyText() {
        binding.layoutSenderEmail.tvParamKey.text = getString(R.string.email)
        binding.layoutDate.tvParamKey.text = getString(R.string.date)
        binding.layoutSubject.tvParamKey.text = getString(R.string.subject)
    }

    private fun setResponseContent(emailContentResponse: EmailContentResponse) {
        binding.layoutSenderEmail.tvParamValue.text = emailContentResponse.from
        binding.layoutDate.tvParamValue.text = emailContentResponse.date
        binding.layoutSubject.tvParamValue.text = emailContentResponse.subject
        binding.layoutTextMessage.tvMessageText.text = emailContentResponse.textBody
    }

    private fun setResponseWithAttachment(emailContentResponse: EmailContentResponse) {
        binding.layoutAttachmentRow.root.visibility = View.VISIBLE
        binding.layoutAttachmentRow.root.setOnClickListener {
            openAttachmentBottomSheet()
        }

        setResponseContent(emailContentResponse)
        val attachmentNames = mutableListOf<String>()
        for (attachment in emailContentResponse.attachments) {
            attachmentNames.add(attachment.filename)
        }
        val attachmentNamesString = attachmentNames.joinToString(separator = ", ")
        binding.layoutAttachmentRow.tvAttachmentFiles.text = attachmentNamesString

    }

    private fun startShimmer(isEnabled: Boolean) {
        if (isEnabled) {
            binding.contentShimmerLayout.visibility = View.VISIBLE
            binding.clEmailContent.visibility = View.GONE
            binding.contentShimmerLayout.startShimmer()
        } else {
            binding.contentShimmerLayout.stopShimmer()
            binding.contentShimmerLayout.visibility = View.GONE
            binding.clEmailContent.visibility = View.VISIBLE
        }
    }

    private fun setupToolbar() {
        val existingEmailAddress = AppUtility.getStringFromSharedPrefs(AppConstants.SHARED_PREF.CURRENT_EMAIL_ADDRESS, null)
        binding.appToolbarLayout.tvTempMailTitle.text = getString(R.string.inbox_text)
        binding.appToolbarLayout.tvTempMailTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.appToolbarLayout.ivToolbarLeftIcon.visibility = View.VISIBLE
        binding.appToolbarLayout.ivToolbarLeftIcon.setImageResource(R.drawable.arrow_back_24)
        binding.appToolbarLayout.ivToolbarLeftIcon.setOnClickListener {
            if (!isFinishing) {
                finish()
            }
        }
        existingEmailAddress?.let { email ->
            binding.appToolbarLayout.tvTempMailSubtitle.visibility = View.VISIBLE
            binding.appToolbarLayout.tvTempMailSubtitle.text = email
        }
    }


    private fun openAttachmentBottomSheet() {
        val fragmentTag = AttachmentBottomSheetFragment.TAG
        attachmentBottomSheetFragment.show(supportFragmentManager, fragmentTag)
    }


    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                startDownload()
            } else {
                requestForStoragePermissions()
            }
        } else if (Build.VERSION_CODES.R > Build.VERSION.SDK_INT && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

            if (read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED) {
                startDownload()
            } else {
                requestForStoragePermissions()
            }

        } else {
            startDownload()
        }
    }

    private fun requestForStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                storageActivityResultLauncher.launch(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED

                if (read && write) {
                    startDownload()
                } else {
                    Toast.makeText(this, getString(R.string.storage_permission_denied), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startDownload() {
        attachmentDownloadQuery?.let { query ->
            val url = getDownloadAttachmentUrl(query)
            val request = DownloadManager.Request(Uri.parse(url))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle("Downloading File")
            request.setDescription("Downloading ${query.file}")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, query.file)

            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            Toast.makeText(this, getString(R.string.download_start), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAttachmentClicked(attachment: Attachment, messageId: Long) {
        val existingEmailAddress = AppUtility.getStringFromSharedPrefs(AppConstants.SHARED_PREF.CURRENT_EMAIL_ADDRESS, null)
        existingEmailAddress?.let { email ->
            AppUtility.getLoginAndDomain(email)?.let {
                val (login, domain) = it
                attachmentDownloadQuery = DownloadAttachmentRequest(AppConstants.SEC_MAIL.ATTACHMENT_DOWNLOAD, login, domain, messageId, attachment.filename)
                checkPermission()
            }
        }
    }

    private fun getDownloadAttachmentUrl(query: DownloadAttachmentRequest): String {
        return "https://www.1secmail.com/api/v1/?action=${query.action}&login=${query.login}&domain=${query.domain}&id=${query.id}&file=${query.file}"
    }


    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadCompleteReceiver)
        super.onDestroy()
    }

    private fun openDownloadedFile(uri: Uri) {
        val context = this
        val file = uri.path?.let { File(it) }
        val fileUri: Uri? = file?.let { FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.file-provider", it) }
        fileUri?.let {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, getMimeType(it))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, "Share with")
            try {
                startActivity(chooser)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, getString(R.string.no_application_found), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getMimeType(uri: Uri): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        return if (extension != null) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.getDefault()))
        } else {
            "application/octet-stream"
        }
    }

}