package com.temp.email.cr.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.temp.email.cr.repository.HomeRepository


class HomeViewModelProviderFactory(
    private val application: Application,
    private val homeRepository: HomeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(application, homeRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
