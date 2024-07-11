package com.cashfree.kyc_verification_core

import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Config.OLDEST_SDK])
class WebJSInterfaceImplTest {

    @Mock
    private lateinit var mockCallback: WebHelperInterface

    private lateinit var webJSInterface: WebJSInterfaceImpl

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        webJSInterface = WebJSInterfaceImpl(mockCallback)
    }


    @Test
    fun testOpenFilePicker() {
        val fieldName = "sampleField"
        val result = webJSInterface.openFilePicker(fieldName)
        verify(mockCallback).openFilePicker(fieldName)
        assertEquals(true, result)
    }

    @Test
    fun testOpenCamera() {
        val fieldName = "sampleField"
        webJSInterface.openCamera(fieldName)
        verify(mockCallback).openCamera(fieldName)
    }

    @Test
    fun testGetGeoLocation() {
        webJSInterface.getGeoLocation()
        verify(mockCallback).onGetGeoLocation()
    }

    @Test
    fun testClearCallback() {
        webJSInterface.clearCallback()
    }
}
