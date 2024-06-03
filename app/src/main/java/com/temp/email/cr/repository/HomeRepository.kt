package com.temp.email.cr.repository

import com.temp.email.cr.api.HomeAPI
import com.temp.email.cr.api.RetrofitInstance
import com.temp.email.cr.model.temMailApiModels.EmailContentRequest
import com.temp.email.cr.model.temMailApiModels.InboxMessagesRequest
import com.temp.email.cr.model.temMailApiModels.RandomMailboxRequest

class HomeRepository {

    private val SEC_MAIL_BASE_URL = "https://www.1secmail.com/"

    suspend fun getRandomMailbox(query: RandomMailboxRequest) =
        RetrofitInstance.buildServiceWithURL(HomeAPI::class.java, SEC_MAIL_BASE_URL).getRandomMailbox(action = query.action, count = query.count)

    suspend fun getInboxMessages(query: InboxMessagesRequest) =
        RetrofitInstance.buildServiceWithURL(HomeAPI::class.java, SEC_MAIL_BASE_URL).getInboxMessages(action = query.action, login = query.login, domain = query.domain)

    suspend fun getEmailContent(query: EmailContentRequest) =
        RetrofitInstance.buildServiceWithURL(HomeAPI::class.java, SEC_MAIL_BASE_URL)
            .getEmailContent(action = query.action, login = query.login, domain = query.domain, id = query.id)
}