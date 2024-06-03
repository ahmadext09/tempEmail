package com.temp.email.cr.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.temp.email.cr.R
import com.temp.email.cr.databinding.FragmentEmailBinding
import com.temp.email.cr.model.temMailApiModels.RandomMailboxRequest
import com.temp.email.cr.repository.HomeRepository
import com.temp.email.cr.utility.AppConstants
import com.temp.email.cr.utility.AppUtility
import com.temp.email.cr.utility.Resource
import com.temp.email.cr.utility.copyText
import com.temp.email.cr.viewmodel.HomeViewModel
import com.temp.email.cr.viewmodel.HomeViewModelProviderFactory

class EmailFragment : BaseFragment() {
    private lateinit var binding: FragmentEmailBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var handler: Handler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEmailBinding.inflate(inflater, container, false)
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
            handler = Handler(Looper.myLooper()!!)
        }
    }

    private fun initViews() {
        getRandomMailbox()
        observeResponse()
        handleRefreshClick()
        handleCopyClick()
    }

    private fun getRandomMailbox() {
        val existingEmailAddress = AppUtility.getStringFromSharedPrefs(AppConstants.SHARED_PREF.CURRENT_EMAIL_ADDRESS, null)
        if (existingEmailAddress.isNullOrEmpty()) {
            val randomMailboxRequest = RandomMailboxRequest(AppConstants.SEC_MAIL.RANDOM_MAILBOX_QUERY_ACTION, 1)
            viewModel.getRandomMailbox(randomMailboxRequest)
        } else {
            setEmailText(existingEmailAddress)
            AppUtility.stopShimmer(binding.displaySuggestionShimmer.root)
        }
    }

    private fun handleRefreshClick() {
        binding.clRefresh.setOnClickListener {
            val randomMailboxRequest = RandomMailboxRequest(AppConstants.SEC_MAIL.RANDOM_MAILBOX_QUERY_ACTION, 1)
            viewModel.getRandomMailbox(randomMailboxRequest)
        }
    }

    private fun handleCopyClick() {
        binding.clCopy.setOnClickListener {
            val existingEmailAddress = AppUtility.getStringFromSharedPrefs(AppConstants.SHARED_PREF.CURRENT_EMAIL_ADDRESS, null)
            existingEmailAddress?.let { text ->
                if (text.isNotEmpty()) {
                    text.copyText()
                    Toast.makeText(requireContext(), requireContext().getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
                    changeBackgroundColorTemporarily(binding.tvMailText, requireContext().getColor(R.color.copied), 5000)
                }
            }
        }
    }

    private fun changeBackgroundColorTemporarily(view: View, color: Int, duration: Long) {
        val originalColor = view.background
        view.setBackgroundColor(color)

        handler.postDelayed({
            view.background = originalColor
        }, duration)
    }

    private fun observeResponse() {
        viewModel.randomMailboxLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    resetAndStopShimmer()
                    it.data?.let { list ->
                        list.get(0).let { email ->
                            setEmailText(email)
                            updateSharedPref(email)
                        }
                    }
                }

                is Resource.Error -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    resetAndStartShimmer()
                }

                else -> {}
            }
        }
    }

    private fun resetAndStartShimmer() {
        binding.tvMailText.text = ""
        binding.clEmailShimmer.visibility = View.VISIBLE
        AppUtility.startShimmer(binding.displaySuggestionShimmer.root)
    }

    private fun resetAndStopShimmer() {
        binding.clEmailShimmer.visibility = View.GONE
        AppUtility.stopShimmer(binding.displaySuggestionShimmer.root)
    }


    private fun updateSharedPref(generatedMail: String) {
        AppUtility.saveStringInSharedPrefs(AppConstants.SHARED_PREF.CURRENT_EMAIL_ADDRESS, generatedMail)
    }

    private fun setEmailText(email: String) {
        binding.tvMailText.text = email
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

}