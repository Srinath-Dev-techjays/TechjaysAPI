package com.srinathdev.apiclasses

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.srinathdev.apiclasses.apiModels.BaseUrl
import com.srinathdev.apiclasses.apiModels.Response
import com.srinathdev.apiclasses.apiModels.ResponseListener
import com.srinathdev.apiclasses.apiModels.User

class ApiCall(private val mContext: Context):ViewModel(),ResponseListener {
    private var user = MutableLiveData<Response?>()

    fun userSignIn(baseUrl: BaseUrl,mUser: User) {
        ApiServices.signin( baseUrl,mUser, mContext,this)
    }

    override fun onResponse(r: Response?) {

    }
}