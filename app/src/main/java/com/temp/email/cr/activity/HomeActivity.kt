package com.temp.email.cr.activity

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.temp.email.cr.R
import com.temp.email.cr.adapter.HomePagerAdapter
import com.temp.email.cr.databinding.ActivityHomeBinding
import com.temp.email.cr.databinding.CustomTabLayoutBinding
import com.temp.email.cr.repository.HomeRepository
import com.temp.email.cr.utility.AppConstants
import com.temp.email.cr.utility.AppUtility
import com.temp.email.cr.viewmodel.HomeViewModel
import com.temp.email.cr.viewmodel.HomeViewModelProviderFactory

class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: HomePagerAdapter
    private lateinit var viewModel: HomeViewModel

    private val tabTitles = listOf(
        R.string.menu_email,
        R.string.menu_inbox
    )

    private val tabIcons = listOf(
        R.drawable.mail_icon_selector,
        R.drawable.inbox_icon_selector

    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initViews()
    }

    private fun init() {
        application?.let { application ->
            val repository = HomeRepository()
            val viewModelProviderFactory = HomeViewModelProviderFactory(application, repository)
            viewModel = ViewModelProvider(this, viewModelProviderFactory)[HomeViewModel::class.java]
        }
        adapter = HomePagerAdapter(supportFragmentManager, lifecycle)
    }

    private fun initViews() {
        setupTabs()
    }


    private fun setupTabs() {
        binding.homeViewPager.adapter = adapter
        binding.tabLayout.tabRippleColor = null

        addTabs()

        TabLayoutMediator(binding.tabLayout, binding.homeViewPager) { tab, position ->
            tab.customView = setCustomTabLayout(position)
        }.attach()

        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = binding.tabLayout.getTabAt(i)
            setTabColor(tab)
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    binding.homeViewPager.setCurrentItem(it.position, true)
                }
                setTabColor(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                setTabColor(tab)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                setTabColor(tab)
            }
        })

        binding.homeViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
                handleToolbarContent(position)

            }
        })
    }

    private fun addTabs() {
        for (i in tabTitles.indices) {
            binding.tabLayout.addTab(
                binding.tabLayout.newTab()
            )
        }
    }

    private fun setCustomTabLayout(position: Int): View {
        val customTabBinding = CustomTabLayoutBinding.inflate(layoutInflater)
        customTabBinding.tabIcon.setImageResource(tabIcons[position])
        customTabBinding.tabTitle.text = getString(tabTitles[position])
        return customTabBinding.root
    }

    private fun setTabColor(tab: TabLayout.Tab?) {
        tab?.let { tab ->
            val color = if (tab.isSelected) R.color.white else R.color.medium_grey
            val tabIconColor = ContextCompat.getColor(this@HomeActivity, color)
            val customTabBinding = tab.customView?.let { CustomTabLayoutBinding.bind(it) }
            customTabBinding?.tabTitle?.setTextColor(tabIconColor)
            val style = if (tab.isSelected) Typeface.BOLD else Typeface.NORMAL
            customTabBinding?.tabTitle?.setTypeface(null, style)
            val textSize = resources.getDimensionPixelSize(if (tab.isSelected) R.dimen.selected_tab_text_size else R.dimen.normal_tab_text_size).toFloat()


            val layoutParamsIcon = customTabBinding?.tabIcon?.layoutParams as? LinearLayout.LayoutParams
            val margin = if (tab.isSelected) resources.getDimensionPixelOffset(R.dimen.selected_icon_margin_bottom) else 0
            layoutParamsIcon?.bottomMargin = margin
            customTabBinding?.tabIcon?.layoutParams = layoutParamsIcon
            customTabBinding?.tabTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)

        }
    }

    private fun handleToolbarContent(position: Int) {
        if (position == 1) {
            val existingEmailAddress = AppUtility.getStringFromSharedPrefs(AppConstants.SHARED_PREF.CURRENT_EMAIL_ADDRESS, null)
            binding.appToolbarLayout.tvTempMailTitle.text = getString(R.string.inbox_text)
            binding.appToolbarLayout.tvTempMailTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            existingEmailAddress?.let { email ->
                binding.appToolbarLayout.tvTempMailSubtitle.visibility = View.VISIBLE
                binding.appToolbarLayout.tvTempMailSubtitle.text = email
            }
        } else {
            binding.appToolbarLayout.tvTempMailSubtitle.visibility = View.GONE
            binding.appToolbarLayout.tvTempMailTitle.text = getString(R.string.app_name)
            binding.appToolbarLayout.tvTempMailTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
        }
    }

}