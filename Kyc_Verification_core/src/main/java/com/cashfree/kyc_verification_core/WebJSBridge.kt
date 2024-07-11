package com.cashfree.kyc_verification_core

import android.webkit.JavascriptInterface
import org.json.JSONObject

internal interface WebHelperInterface {
    fun onVerificationResponse(jsonObject: JSONObject)
    fun onWebErrors(jsonObject: JSONObject)
    fun openFilePicker(fieldName: String)
    fun openCamera(fieldName: String)
    fun onGetGeoLocation()
}

internal class WebJSInterfaceImpl(private var callback: WebHelperInterface?) {
    private val TAG: String = "KycVerification"

    @JavascriptInterface
    fun verificationResponse(result: String) {
        callback?.onVerificationResponse(JSONObject(result))
    }

    @JavascriptInterface
    fun webErrors(error: String) {
        callback?.onWebErrors(JSONObject(error))
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