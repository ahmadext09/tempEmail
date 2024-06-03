package com.temp.email.cr.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.temp.email.cr.fragment.BaseFragment
import com.temp.email.cr.fragment.EmailFragment
import com.temp.email.cr.fragment.InboxFragment


class HomePagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val tabList = arrayListOf(EmailFragment(), InboxFragment())

    override fun getItemCount(): Int {
        return tabList.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabList[position]
    }

    fun getTabList(): ArrayList<BaseFragment> {
        return tabList
    }
}