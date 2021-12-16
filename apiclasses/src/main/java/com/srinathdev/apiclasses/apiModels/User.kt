package com.srinathdev.apiclasses.apiModels

import com.google.gson.annotations.SerializedName

class User:Response() {

    @SerializedName("data")
    var mUser: User? = null

    @SerializedName("user_id")
    var mUserId = -1

    @SerializedName("token")
    var mUserToken = ""

    @SerializedName("username")
    var mUserName = ""

    @SerializedName("mobile_number")
    var mMobileNumber = ""

    @SerializedName("email")
    var mEmail = ""

    var mPassword = ""
}