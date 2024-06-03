package com.temp.email.cr.utility

import android.app.Application
import android.content.Context

class MainApplication : Application() {

    companion object {
        private lateinit var mContext: Context
        fun getMyApplicationContext(): Context {
            return mContext
        }
    }


    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        mContext = this
    }

}