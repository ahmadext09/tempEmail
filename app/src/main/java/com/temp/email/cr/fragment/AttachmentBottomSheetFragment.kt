package com.temp.email.cr.fragment


import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.temp.email.cr.adapter.AttachmentAdapter
import com.temp.email.cr.databinding.AttachmentBottomSheetFragmentBinding
import com.temp.email.cr.model.temMailApiModels.Attachment
import com.temp.email.cr.repository.HomeRepository
import com.temp.email.cr.utility.Resource
import com.temp.email.cr.viewmodel.HomeViewModel
import com.temp.email.cr.viewmodel.HomeViewModelProviderFactory

class AttachmentBottomSheetFragment : BottomSheetDialogFragment(), AttachmentAdapter.AttachmentClickListener {

    private lateinit var binding: AttachmentBottomSheetFragmentBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: AttachmentAdapter
    private lateinit var manager: LinearLayoutManager
    private var listener: AttachmentClickListener? = null
    var messageId: Long? = null


    companion object {
        val TAG = AttachmentBottomSheetFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AttachmentBottomSheetFragmentBinding.inflate(inflater, container, false)
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

        }
    }

    private fun initViews() {
        observeResponse()
        setUpRVAttachment()
        handleArrowClick()
    }


    private fun observeResponse() {
        viewModel.emailContentLiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { emailContentResponse ->
                        if (emailContentResponse.attachments.isNotEmpty()) {
                            messageId = emailContentResponse.id
                            setAttachmentList(emailContentResponse.attachments)
                        }
                    }
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {

                }
            }
        }
    }

    private fun setUpRVAttachment() {
        adapter = AttachmentAdapter(this)
        manager = LinearLayoutManager(context)
        binding.rvAttachment.layoutManager = manager
        binding.rvAttachment.adapter = adapter
    }

    private fun handleArrowClick() {
        binding.cvExpand.setOnClickListener {
            dismiss()
        }
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        messageId = null
        val fragmentManager = requireActivity().supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(TAG)
        fragment?.let {
            fragmentManager.beginTransaction().remove(it).commitAllowingStateLoss()
        }
    }


    private fun setAttachmentList(attachmentList: List<Attachment>) {
        adapter.updateData(attachmentList)
    }


    override fun onItemClick(item: Attachment) {
        messageId?.let { id ->
            listener?.onAttachmentClicked(item, id)
        }
    }

    interface AttachmentClickListener {
        fun onAttachmentClicked(attachment: Attachment, messageId: Long)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AttachmentClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement AttachmentActionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        messageId = null
        listener = null
    }


}