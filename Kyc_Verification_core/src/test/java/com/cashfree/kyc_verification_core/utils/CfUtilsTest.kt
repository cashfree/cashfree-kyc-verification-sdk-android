package com.cashfree.kyc_verification_core.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class CfUtilsTest {

    @Test
    fun testGetVerificationSuccessResponse() {
        val jsonObject = JSONObject().apply {
            put(Constants.STATUS, "success")
            put(Constants.FORM_ID, "formId123")
        }
        val response = CfUtils.getVerificationSuccessResponse(jsonObject)
        assertEquals("success", response.status)
        assertEquals("formId123", response.form_id)
    }

    @Test
    fun testGetErrorResponse() {
        val jsonObject = JSONObject().apply {
            put(Constants.STATUS_CODE, 400)
            put(Constants.MESSAGE, "Error occurred")
        }
        val response = CfUtils.getErrorResponse(jsonObject)
        assertEquals(400, response.statusCode)
        assertEquals("Error occurred", response.message)
    }

    @Test
    fun testBitmapToBase64() {
        val bitmap = mock(Bitmap::class.java)
        val byteArray = byteArrayOf(1, 2, 3, 4)
        `when`(
            bitmap.compress(
                any(Bitmap.CompressFormat::class.java),
                anyInt(),
                any(ByteArrayOutputStream::class.java)
            )
        )
            .thenAnswer {
                val outputStream = it.arguments[2] as ByteArrayOutputStream
                outputStream.write(byteArray)
                true
            }
        val base64String = CfUtils.bitmapToBase64(bitmap)
        val expectedBase64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

        assertEquals(expectedBase64String, base64String)
    }


    @Test
    fun testGetBase64FromUri() {
        val uri = mock(Uri::class.java)
        val contentResolver = mock(ContentResolver::class.java)
        val byteArray = byteArrayOf(1, 2, 3, 4)
        val inputStream: InputStream = ByteArrayInputStream(byteArray)

        `when`(contentResolver.openInputStream(uri)).thenReturn(inputStream)

        val base64String = CfUtils.getBase64FromUri(uri, contentResolver)
        val expectedBase64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        assertEquals(expectedBase64String, base64String)
    }

    @Test
    fun testGetCancellationResponse() {
        val response = CfUtils.getCancellationResponse()
        assertEquals("User cancelled Verification", response.message)
    }
}
