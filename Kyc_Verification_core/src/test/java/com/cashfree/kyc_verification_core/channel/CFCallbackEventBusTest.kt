package com.cashfree.kyc_verification_core.channel

import android.content.Context
import com.cashfree.kyc_verification_core.channel.CFCallbackEventBus
import com.cashfree.kyc_verification_core.models.CFErrorResponse
import com.cashfree.kyc_verification_core.models.CFVerificationResponse
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CFCallbackEventBusTest {

    @Mock
    private lateinit var cfVerificationCallback: CFVerificationCallback

    @Mock
    private lateinit var mockContext: Context

    private lateinit var cfCallbackEventBus: CFCallbackEventBus

    private lateinit var executorService: ExecutorService

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Set up main dispatcher for coroutines
        Dispatchers.setMain(Dispatchers.Unconfined)

        executorService = Executors.newSingleThreadExecutor()

        // Initialize and set up mock behavior
        cfCallbackEventBus = CFCallbackEventBus.getInstance() ?: run {
            CFCallbackEventBus.initialize(executorService)
            CFCallbackEventBus.getInstance()!!
        }

        cfCallbackEventBus.setCheckoutCallback(cfVerificationCallback)

        `when`(mockContext.getString(anyInt())).thenReturn("Mocked String")
    }

    @After
    fun tearDown() {
        // Clean up the singleton instance
        val instanceField = CFCallbackEventBus::class.java.getDeclaredField("INSTANCE")
        instanceField.isAccessible = true
        instanceField.set(null, null)

        // Reset main dispatcher
        Dispatchers.resetMain()
        executorService.shutdown()
    }

    @Test
    fun test_triggerCallback_with_Verify_event() {
        val response = CFVerificationResponse("verified", "form123")
        val event = CFPaymentCallbackEvent(CFCallbackEvents.Verify, response, null)

        cfCallbackEventBus.triggerCallback(event)

        verify(cfVerificationCallback).onVerificationResponse(response)
    }

    @Test
    fun test_triggerCallback_with_Error_event() {
        val error = CFErrorResponse(500, "Internal Server Error")
        val event = CFPaymentCallbackEvent(CFCallbackEvents.Error, null, error)

        cfCallbackEventBus.triggerCallback(event)

        verify(cfVerificationCallback).onErrorResponse(error)
    }

    @Test
    fun test_setCheckoutCallback() {
        val newCallback: CFVerificationCallback = mock(CFVerificationCallback::class.java)
        cfCallbackEventBus.setCheckoutCallback(newCallback)

        val response = CFVerificationResponse("verified", "form123")
        val event = CFPaymentCallbackEvent(CFCallbackEvents.Verify, response, null)

        cfCallbackEventBus.triggerCallback(event)

        verify(newCallback).onVerificationResponse(response)
    }

    @Test
    fun test_getString_from_mockContext() {
        val result = mockContext.getString(123)
        assertEquals("Mocked String", result)
    }
}
