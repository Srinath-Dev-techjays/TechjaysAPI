package com.srinathdev.apiclasses

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object Utilites {

    @SuppressLint("MissingPermission")
    fun isInternetAvailable(context: Context?): Boolean {
        try {
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo: NetworkInfo? = cm.activeNetworkInfo
            if (netInfo != null && netInfo.isConnected) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return false
    }
}