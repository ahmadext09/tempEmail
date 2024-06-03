package com.temp.email.cr.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.temp.email.cr.databinding.ItemRowAttachmentLayoutBinding
import com.temp.email.cr.databinding.ItemRowAttachmentRecyclerLayoutBinding
import com.temp.email.cr.model.temMailApiModels.Attachment


class AttachmentAdapter(private val listener: AttachmentClickListener) : RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder>() {
    private var attachmentList = ArrayList<Attachment>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentAdapter.AttachmentViewHolder {
        val binding = ItemRowAttachmentRecyclerLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttachmentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return attachmentList.size
    }

    override fun onBindViewHolder(holder: AttachmentAdapter.AttachmentViewHolder, position: Int) {
        holder.updateData(attachmentList[position], position)
    }

    fun updateData(itemList: List<Attachment>) {
        attachmentList.clear()
        attachmentList.addAll(itemList)
        notifyDataSetChanged()
    }

    inner class AttachmentViewHolder(private val binding: ItemRowAttachmentRecyclerLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        private val attachmentFileName: AppCompatTextView = binding.tvAttachmentFilesName


        fun updateData(item: Attachment, position: Int) {
            attachmentFileName.text = item.filename
            handleItemClick(item)
        }


        private fun handleItemClick(item: Attachment) {
            binding.root.setOnClickListener {
                listener.onItemClick(item)
            }
        }


    }

    interface AttachmentClickListener {
        fun onItemClick(item: Attachment)

    }
}

