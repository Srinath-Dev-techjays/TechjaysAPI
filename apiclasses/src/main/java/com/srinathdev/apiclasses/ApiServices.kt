package com.srinathdev.apiclasses

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.srinathdev.apiclasses.apiModels.BaseUrl
import com.srinathdev.apiclasses.apiModels.Response
import com.srinathdev.apiclasses.apiModels.ResponseListener
import okhttp3.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

class ApiServices {

    object API {
        const val sign_in = "users/signin/"
    }

    private interface ApiInterface {

        //        ----------------- POST Request ---------------
        @POST
        fun POST(
            @Url url: String,
            @HeaderMap headerMap: Map<String, String>,
            @Body body: JsonObject
        ): Call<ResponseBody>

        @POST
        fun POST(
            @Url url: String,
            @HeaderMap headerMap: Map<String, String>
        ): Call<ResponseBody>

        @Multipart
        @POST
        fun MULTIPART(
            @Url url: String,
            @PartMap file: HashMap<String, RequestBody>,
            @HeaderMap headerMap: Map<String, String>
        ): Call<ResponseBody>

        @Multipart
        @POST
        fun MULTIPART(
            @Url url: String,
            @PartMap file: HashMap<String, RequestBody>
        ): Call<ResponseBody>

//      ----------------- GET Request ---------------

        @GET
        fun GET(
            @Url url: String,
            @HeaderMap headerMap: Map<String, String>,
            @QueryMap param: Map<String, String>
        ): Call<ResponseBody>

        @GET
        fun GET(
            @Url url: String,
            @HeaderMap headerMap: Map<String, String>
        ): Call<ResponseBody>

        @GET
        fun GET(
            @Url url: String
        ): Call<ResponseBody>

//      ----------------- PUT Request ---------------

        @PUT
        fun PUT(
            @Url url: String,
            @HeaderMap headerMap: Map<String, String>,
            @Body body: JsonObject
        ): Call<ResponseBody>

        @Multipart
        @PUT
        fun PUT(
            @Url url: String,
            @PartMap file: HashMap<String, RequestBody>,
            @HeaderMap headerMap: Map<String, String>
        ): Call<ResponseBody>

//      ----------------- DELETE Request ---------------

        @DELETE
        fun DELETE(
            @Url url: String,
            @HeaderMap headerMap: Map<String, String>
        ): Call<ResponseBody>

        @HTTP(method = "DELETE", path = API.sign_in, hasBody = true)
        fun deleteNotification(
            @Body body: JsonObject,
            @HeaderMap headerMap: Map<String, String>
        ): Call<ResponseBody>
    }

    companion object {
        private var retrofit: Retrofit? = null
        private var okHttpClient: OkHttpClient? = null

        private fun getClient(url: String): Retrofit {

            if (okHttpClient == null) {
                okHttpClient = OkHttpClient.Builder()
                    .cookieJar(CookieJar.NO_COOKIES)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()
            }

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClient!!)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit as Retrofit
        }

        /**
         * Sign in API
         * Method - POST
         */
        fun signin(baseUrl: BaseUrl, c: Context, listener: ResponseListener) {
            var constructUrl = baseUrl.mBaseUrl
            try {
                val apiService = getClient(baseUrl.mBaseUrl).create(ApiInterface::class.java)
                val mHashCode = baseUrl.mSubDomain
                val mURL = constructUrl

                val mObject = JsonObject()
                mObject.addProperty("mobile_number", "")
                mObject.addProperty("password", "")

                val call = apiService.POST(mURL, getHeader(), mObject)
                initService(c, call, ResponseListener::class.java, mHashCode, listener)
                Log.d("Param --> ", mObject.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // ################################################################################################

        /**
         * Create RequestBody - text/plain
         */

        private fun requestBody(string: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"), string)
        }

        /**
         * Get Error Msg
         * return - Response
         */

        private fun getErrorMsg(
            t: Throwable,
            hash: Int
        ): com.srinathdev.apiclasses.apiModels.Response {
            val r = com.srinathdev.apiclasses.apiModels.Response()
            r.responseStatus = false
            r.responseMessage = t.message!!
            r.requestType = hash

            Log.d("failure", t.message!!)

            return r
        }

        /**
         * Initiating the api call
         */
        private fun initService(
            c: Context,
            call: Call<ResponseBody>,
            mSerializable: Type,
            mHashCode: String,
            listener: ResponseListener
        ) {
            Log.d("URL --> ", call.request().url().toString())
            Log.d("METHOD --> ", call.request().method())
            call.enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>
                ) {
                    listener.onResponse(getResponse(c, response, mSerializable, mHashCode))
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.onResponse(getErrorMsg(t, mHashCode.hashCode()))
                }
            })
        }

        /**
         * Get Success and Failure Msg
         * @return - Response
         */

        private fun getResponse(
            context: Context,
            mResponse: retrofit2.Response<ResponseBody>,
            mSerializable: Type,
            mHashCode: String
        ): Response? {
            val response: com.srinathdev.apiclasses.apiModels.Response?

            if (!Utilites.isInternetAvailable(context)) {
                okHttpClient?.dispatcher()?.cancelAll()
                return null
            }

            if (mResponse.isSuccessful) {
                val body = mResponse.body()?.string()!!
                Log.d("success", body)
                response = Gson().fromJson(body, mSerializable)
            } else {
                try {
                    if (mResponse.code() == 401) { // Unauthorized User / Invalid Token
                        Log.e("unauthorized", mResponse.errorBody()!!.string())
                        Log.e("unauthorized url", mResponse.raw().request().url().toString())
                        okHttpClient?.dispatcher()?.cancelAll()
                        return null
                    } else {
                        val errorBody = mResponse.errorBody()?.string()!!
                        Log.e("fail", errorBody)
                        response = Gson().fromJson(errorBody, mSerializable)
                        response?.responseStatus = false
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, e.message!!, Toast.LENGTH_SHORT).show()
                    return null
                }
            }
            response?.requestType = mHashCode.hashCode()
            return response
        }

        /**
         * Get Common Header
         * @return - HashMap
         */

        private fun getHeader(): HashMap<String, String> {
            val mHeader = HashMap<String, String>()
            mHeader["Content-Type"] = "application/json"
            mHeader["device"] = ApiApplication.instance().deviceId
            mHeader["platform"] = ApiApplication.instance().deviceType

            Log.d("Header --> ", mHeader.toString())

            return mHeader
        }


        private fun getlogoutHeader(): HashMap<String, String> {
            val mHeader = HashMap<String, String>()

            mHeader["device"] = ApiApplication.instance().deviceId
            mHeader["platform"] = ApiApplication.instance().deviceType

            Log.d("Header --> ", mHeader.toString())

            return mHeader
        }

        private fun getHeaderContent(): HashMap<String, String> {
            val mHeader = HashMap<String, String>()
            mHeader["Content-Type"] = "application/json"
            Log.d("Header --> ", mHeader.toString())

            return mHeader
        }


        /**
         * Get Auth Header
         * return - HashMap<String, String>
         */

        private fun getCustomAuthHeader(c: Context, token: String): HashMap<String, String> {
            val mHeader = HashMap<String, String>()
            mHeader["Content-Type"] = "application/json"
            mHeader["Authorization"] = "Token $token"

            Log.d("Auth Header --> ", mHeader.toString())

            return mHeader
        }
    }
}