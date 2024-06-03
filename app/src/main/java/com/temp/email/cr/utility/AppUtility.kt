package com.temp.email.cr.utility


import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import com.facebook.shimmer.ShimmerFrameLayout

object AppUtility {

    private fun getSharedPrefs(): SharedPreferences {
        return MainApplication.getMyApplicationContext().getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE)
    }

    fun saveStringInSharedPrefs(key: String, value: String) {
        val prefs = getSharedPrefs()
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getStringFromSharedPrefs(key: String, defaultValue: String?): String? {
        val prefs = getSharedPrefs()
        return prefs.getString(key, defaultValue)
    }


    fun hasInternetConnection(): Boolean {
        val connectivityManager = MainApplication.getMyApplicationContext().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    fun startShimmer(shimmerView: ShimmerFrameLayout) {
        shimmerView.apply {
            visibility = View.VISIBLE
            startShimmer()
        }
    }

    fun stopShimmer(shimmerView: ShimmerFrameLayout) {
        shimmerView.apply {
            visibility = View.GONE
            stopShimmer()
        }
    }

    fun getLoginAndDomain(email: String): Pair<String, String>? {
        val parts = email.split("@")
        if (parts.size != 2) {
            return null
        }
        return Pair(parts[0], parts[1])
    }


    fun extractTime(dateTime: String): String? {
        val parts = dateTime.split(" ")
        if (parts.size != 2) {
            return null
        }
        val timeParts = parts[1].split(":")
        if (timeParts.size < 2) {
            return null
        }
        return "${timeParts[0]}:${timeParts[1]}"
    }


}