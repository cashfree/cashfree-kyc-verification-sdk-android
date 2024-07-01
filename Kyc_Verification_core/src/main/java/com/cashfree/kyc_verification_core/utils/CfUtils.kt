package com.cashfree.kyc_verification_core.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import com.cashfree.kyc_verification_core.models.CFErrorResponse
import com.cashfree.kyc_verification_core.models.CFVerificationResponse

import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream

internal class CfUtils {

    companion object {

        fun getVerificationSuccessResponse(jsonObject: JSONObject): CFVerificationResponse {
            return CFVerificationResponse(
                jsonObject.getString(Constants.STATUS),
                jsonObject.getString(Constants.FORM_ID),

                )
        }

        fun getErrorResponse(jsonObject: JSONObject): CFErrorResponse {
            return CFErrorResponse(
                jsonObject.getInt(Constants.STATUS_CODE),
                jsonObject.getString(Constants.MESSAGE),


                )
        }

        fun bitmapToBase64(imageBitmap: Bitmap): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

        fun getBase64FromUri(selectedFileUri: Uri, contentResolver: ContentResolver): String {
            val inputStream: InputStream? = contentResolver.openInputStream(selectedFileUri)
            val byteArrayOutputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var bytesRead: Int

            try {
                while (inputStream?.read(buffer).also { bytesRead = it ?: -1 } != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead)
                }
            } finally {
                inputStream?.close()
            }

            val bytes = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(bytes, Base64.NO_WRAP)
        }

        fun getCancellationResponse(): CFErrorResponse {
            return CFErrorResponse(message = "User cancelled Verification")
        }

    }
}