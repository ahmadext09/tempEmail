package com.temp.email.cr.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.temp.email.cr.activity.EmailContentActivity
import com.temp.email.cr.adapter.InboxMessagesAdapter
import com.temp.email.cr.databinding.FragmentInboxBinding
import com.temp.email.cr.model.temMailApiModels.InboxMessagesRequest
import com.temp.email.cr.model.temMailApiModels.InboxMessagesResponse
import com.temp.email.cr.repository.HomeRepository
import com.temp.email.cr.utility.AppConstants
import com.temp.email.cr.utility.AppUtility
import com.temp.email.cr.utility.Resource
import com.temp.email.cr.viewmodel.HomeViewModel
import com.temp.email.cr.viewmodel.HomeViewModelProviderFactory


class InboxFragment : BaseFragment(), InboxMessagesAdapter.InboxMessagesClickListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentInboxBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: InboxMessagesAdapter
    private lateinit var manager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentInboxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initViews()
    }

    private fun init() {
        activity?.application?.let { application ->
            val repository = HomeRepository()
            val viewModelProviderFactory = HomeViewModelProviderFactory(application, repository)
            viewModel = ViewModelProvider(requireActivity(), viewModelProviderFactory)[HomeViewModel::class.java]
            binding.inboxMessageSwipeRefreshLayout.setOnRefreshListener(this)
        }
    }

    private fun initViews() {
        getInboxMessages()
        setUpRVInboxMessage()
        observeResponse()
        handleRefreshClick()
    }

    private fun setUpRVInboxMessage() {
        adapter = InboxMessagesAdapter(this)
        manager = LinearLayoutManager(context)
        binding.rvInboxMessages.layoutManager = manager
        binding.rvInboxMessages.adapter = adapter
    }

    private fun getInboxMessages() {
        val existingEmailAddress = AppUtility.getStringFromSharedPrefs(AppConstants.SHARED_PREF.CURRENT_EMAIL_ADDRESS, null)
        existingEmailAddress?.let { email ->
            AppUtility.getLoginAndDomain(email)?.let {
                val (login, domain) = it
                val query = InboxMessagesRequest(AppConstants.SEC_MAIL.INBOX_MAILBOX_QUERY_ACTION, login, domain)
                viewModel.getInboxMessages(query)
            }
        }
    }

    private fun handleRefreshClick() {
        binding.layoutEmptyInbox.clEmptyInboxRoot.setOnClickListener {
            getInboxMessages()
        }
    }

    private fun observeResponse() {
        viewModel.inboxMessagesLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    startShimmer(false)
                    it.data?.let { list ->
                        if (list.isEmpty()) {
                            setEmptyInboxVisibility(true)
                        } else {
                            setEmptyInboxVisibility(false)
                            adapter.updateData(list)
                        }
                    }
                }

                is Resource.Error -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    startShimmer(true)
                }
            }
        }
    }

    private fun setEmptyInboxVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.layoutEmptyInbox.clEmptyInboxRoot.visibility = View.VISIBLE
            binding.rvInboxMessages.visibility = View.GONE
        } else {
            binding.layoutEmptyInbox.clEmptyInboxRoot.visibility = View.GONE
            binding.rvInboxMessages.visibility = View.VISIBLE
        }
    }

    override fun onItemClick(item: InboxMessagesResponse) {
        val intent = Intent(requireContext(), EmailContentActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEY.MESSAGE_ID_INTENT_KEY, item.id)
        startActivity(intent)
    }

    private fun startShimmer(isEnabled: Boolean) {
        binding.inboxMessageSwipeRefreshLayout.isRefreshing = isEnabled
        if (isEnabled) {
            binding.inboxMessageShimmerLayout.startShimmer()
            binding.inboxMessageShimmerLayout.visibility = View.VISIBLE
            binding.layoutEmptyInbox.clEmptyInboxRoot.visibility = View.GONE
            binding.rvInboxMessages.visibility = View.GONE
        } else {
            binding.inboxMessageShimmerLayout.stopShimmer()
            binding.inboxMessageShimmerLayout.visibility = View.GONE
        }
    }


    override fun onRefresh() {
        getInboxMessages()
    }

}