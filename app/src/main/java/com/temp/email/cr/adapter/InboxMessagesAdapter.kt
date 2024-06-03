package com.temp.email.cr.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.temp.email.cr.databinding.ItemRowInboxLayoutBinding
import com.temp.email.cr.model.temMailApiModels.InboxMessagesResponse
import com.temp.email.cr.utility.AppUtility


class InboxMessagesAdapter(private val listener: InboxMessagesClickListener) : RecyclerView.Adapter<InboxMessagesAdapter.InboxMessagesViewHolder>() {
    private var messageList = ArrayList<InboxMessagesResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxMessagesAdapter.InboxMessagesViewHolder {
        val binding = ItemRowInboxLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InboxMessagesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: InboxMessagesAdapter.InboxMessagesViewHolder, position: Int) {
        holder.updateData(messageList[position], position)
    }

    fun updateData(itemList: List<InboxMessagesResponse>) {
        messageList.clear()
        messageList.addAll(itemList)
        notifyDataSetChanged()
    }

    inner class InboxMessagesViewHolder(private val binding: ItemRowInboxLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        private val senderEmail: AppCompatTextView = binding.tvSenderMAilAddress
        private val sendingTime: AppCompatTextView = binding.tvTime
        private val mailSubject: AppCompatTextView = binding.tvSubject

        fun updateData(item: InboxMessagesResponse, position: Int) {
            senderEmail.text = item.from
            AppUtility.extractTime(item.date)?.let { time ->
                sendingTime.text = time
            }
            mailSubject.text = item.subject
            handleItemClick(item)
        }

        private fun handleItemClick(item: InboxMessagesResponse) {
            binding.root.setOnClickListener {
                listener.onItemClick(item)
            }
        }


    }

    interface InboxMessagesClickListener {
        fun onItemClick(item: InboxMessagesResponse)

    }

}

