package com.srinathdev.techjaysapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.srinathdev.apiclasses.ApiCall
import com.srinathdev.apiclasses.apiModels.BaseUrl
import com.srinathdev.apiclasses.apiModels.User

class MainActivity : AppCompatActivity() {
    var mBase = BaseUrl()
    var mUser = User()
    lateinit var mClick: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mClick = findViewById(R.id.click)

        mBase.mBaseUrl = "https://sprint.myvidhire.com/api/v1/"
        mBase.mSubDomain = ""
        mBase.mMisc = "users/signin/"
        mBase.mVersion = ""
        mUser.mMobileNumber = "8300093107"
        mUser.mPassword = "123"

        mClick.setOnClickListener {
            ApiCall(this).userSignIn(mBase,mUser)
        }
    }
}