package com.srinathdev.apiclasses

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings

class ApiApplication : Application() {

    var deviceId = ""
    var deviceType = "android"
    var deviceName = ""
    var isDashboard = false

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()

        mContext = this
        deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        deviceName = android.os.Build.MANUFACTURER
    }

    /**
     * For india region base url changes
     */
    /* fun getBaseURL(): String {
         return API_URL
     }
 */

    fun getDp(pixel: Int): Int {
        val density = instance().resources.displayMetrics.density
        val dp = pixel / density
        return dp.toInt()
    }

    fun getPixel(dp: Int): Int {
        val density = instance().resources.displayMetrics.density
        val px = dp * density
        return px.toInt()
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null

        fun instance(): ApiApplication {
            return mContext!!.applicationContext as ApiApplication
        }
    }
}