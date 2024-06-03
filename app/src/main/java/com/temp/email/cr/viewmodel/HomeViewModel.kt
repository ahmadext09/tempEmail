package com.temp.email.cr.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.temp.email.cr.R
import com.temp.email.cr.model.temMailApiModels.EmailContentRequest
import com.temp.email.cr.model.temMailApiModels.EmailContentResponse
import com.temp.email.cr.model.temMailApiModels.InboxMessagesRequest
import com.temp.email.cr.model.temMailApiModels.InboxMessagesResponse
import com.temp.email.cr.model.temMailApiModels.RandomMailboxRequest
import com.temp.email.cr.repository.HomeRepository
import com.temp.email.cr.utility.AppConstants
import com.temp.email.cr.utility.AppUtility
import com.temp.email.cr.utility.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.Query
import java.io.IOException
import java.net.ConnectException

class HomeViewModel(
    val app: Application, private val repository: HomeRepository
) : AndroidViewModel(app) {

    val randomMailboxLiveData: MutableLiveData<Resource<List<String>>> = MutableLiveData()
    val inboxMessagesLiveData: MutableLiveData<Resource<List<InboxMessagesResponse>>> = MutableLiveData()
    val emailContentLiveData: MutableLiveData<Resource<EmailContentResponse>> = MutableLiveData()


    fun getRandomMailbox(query: RandomMailboxRequest) = viewModelScope.launch {
        randomMailboxLiveData.postValue(Resource.Loading())
        try {
            if (AppUtility.hasInternetConnection()) {
                val response = repository.getRandomMailbox(query)
                randomMailboxLiveData.postValue(handleRandomMailboxResponse(response))
            } else {
                randomMailboxLiveData.postValue(Resource.Error(app.getString(R.string.internet_connection), AppConstants.ERROR_CODES.APP_ERROR_CODE))
            }
        } catch (e: Exception) {
            val errorMsg = handleException(e)
            randomMailboxLiveData.postValue(Resource.Error(errorMsg, AppConstants.ERROR_CODES.APP_ERROR_CODE))
        }
    }

    private fun handleRandomMailboxResponse(response: Response<List<String>>): Resource<List<String>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse, response.code())
            }
        }
        val errorMsg = if (TextUtils.isEmpty(response.message())) {
            app.getString(R.string.something_went_wrong)
        } else {
            response.message()
        }
        return Resource.Error(errorMsg, response.code())
    }


    fun getInboxMessages(query: InboxMessagesRequest) = viewModelScope.launch {
        inboxMessagesLiveData.postValue(Resource.Loading())
        try {
            if (AppUtility.hasInternetConnection()) {
                val response = repository.getInboxMessages(query)
                inboxMessagesLiveData.postValue(handleInboxMessagesResponse(response))
            } else {
                inboxMessagesLiveData.postValue(Resource.Error(app.getString(R.string.internet_connection), AppConstants.ERROR_CODES.APP_ERROR_CODE))
            }
        } catch (e: Exception) {
            val errorMsg = handleException(e)
            inboxMessagesLiveData.postValue(Resource.Error(errorMsg, AppConstants.ERROR_CODES.APP_ERROR_CODE))
        }
    }

    private fun handleInboxMessagesResponse(response: Response<List<InboxMessagesResponse>>): Resource<List<InboxMessagesResponse>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse, response.code())
            }
        }
        val errorMsg = if (TextUtils.isEmpty(response.message())) {
            app.getString(R.string.something_went_wrong)
        } else {
            response.message()
        }
        return Resource.Error(errorMsg, response.code())
    }

    fun getEmailContent(query: EmailContentRequest) = viewModelScope.launch {
        emailContentLiveData.postValue(Resource.Loading())
        try {
            if (AppUtility.hasInternetConnection()) {
                val response = repository.getEmailContent(query)
                emailContentLiveData.postValue(handleEmailContentResponse(response))
            } else {
                emailContentLiveData.postValue(Resource.Error(app.getString(R.string.internet_connection), AppConstants.ERROR_CODES.APP_ERROR_CODE))
            }
        } catch (e: Exception) {
            val errorMsg = handleException(e)
            emailContentLiveData.postValue(Resource.Error(errorMsg, AppConstants.ERROR_CODES.APP_ERROR_CODE))
        }
    }

    private fun handleEmailContentResponse(response: Response<EmailContentResponse>): Resource<EmailContentResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse, response.code())
            }
        }
        val errorMsg = if (TextUtils.isEmpty(response.message())) {
            app.getString(R.string.something_went_wrong)
        } else {
            response.message()
        }
        return Resource.Error(errorMsg, response.code())
    }

    private fun handleException(e: Exception): String {
        val errorMsg = when (e) {
            is IOException -> app.getString(R.string.network_failure)
            is ConnectException -> app.getString(R.string.internet_connection)
            else -> app.getString(R.string.something_went_wrong)
        }
        return errorMsg
    }


}