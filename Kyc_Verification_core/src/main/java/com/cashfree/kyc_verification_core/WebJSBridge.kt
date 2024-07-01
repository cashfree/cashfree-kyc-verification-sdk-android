package com.cashfree.subscription.coresdk.payment

import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.util.Log
import android.webkit.JavascriptInterface
import com.cashfree.pg.base.logger.CFLoggerService
import org.json.JSONArray
import org.json.JSONObject

internal interface WebHelperInterface {
    fun onVerificationResponse(jsonObject: JSONObject)
    fun openFilePicker(fieldName: String)
    fun openCamera(fieldName: String)
    fun onGetGeoLocation()
}

internal class WebJSInterfaceImpl(private var callback: WebHelperInterface?) {
    private val TAG: String = "KycVerification"

    @JavascriptInterface
    fun verificationResponse(result: String) {
        Log.d("testResult", result)
        callback?.onVerificationResponse(JSONObject(result))
    }

    @JavascriptInterface
    fun openFilePicker(fieldName: String): Boolean {
        callback?.openFilePicker(fieldName)
        return true
    }

    @JavascriptInterface
    fun openCamera(fieldName: String) {
        callback?.openCamera(fieldName)

    }

    @JavascriptInterface
    fun getGeoLocation() {
        callback?.onGetGeoLocation()
    }
    fun clearCallback() {
        callback = null
    }
}