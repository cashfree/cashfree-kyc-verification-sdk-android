package com.cashfree.kyc_verification_core.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cashfree.kyc_verification_core.WebHelperInterface
import com.cashfree.kyc_verification_core.WebJSInterfaceImpl
import com.cashfree.kyc_verification_core.databinding.ActivityKycVerificationBinding
import com.cashfree.kyc_verification_core.models.CFErrorResponse
import com.cashfree.kyc_verification_core.models.CFVerificationResponse
import com.cashfree.kyc_verification_core.utils.CFCallbackUtil
import com.cashfree.kyc_verification_core.utils.CfUtils
import com.cashfree.kyc_verification_core.utils.Constants
import com.cashfree.kyc_verification_core.utils.Constants.WB_INTENT_BRIDGE
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject

class KycVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKycVerificationBinding
    private var exitDialog: AlertDialog? = null
    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private var fieldName = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        const val REQUEST_CAMERA = 1
        const val LOCATION_PERMISSION_REQUEST_CODE = 2
    }

    private val webJsBridge: WebJSInterfaceImpl by lazy {
        WebJSInterfaceImpl(addWebHelperInterfaceImplementation())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKycVerificationBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(binding.root)
        handleLoader(true)
        registerFilePickerLauncher()
        registerCameraLauncher()
        setWebView()
        loadUrl()
        addBackPressDispatcher()
    }

    private fun registerCameraLauncher() {
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val extras = result.data!!.extras
                val imageBitmap = extras?.get("data") as Bitmap
                val base64String = CfUtils.bitmapToBase64(imageBitmap)
                runOnUiThread {
                    callOnFileSelectedFunction(base64String)
                }

            }
        }
    }


    private fun registerFilePickerLauncher() {
        filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val selectedFileUri: Uri? = result.data?.data
                    if (selectedFileUri != null) {
                        val base64String =
                            CfUtils.getBase64FromUri(selectedFileUri, contentResolver)
                        runOnUiThread {
                            callOnFileSelectedFunction(base64String)
                        }
                    }
                }
            }
    }

    private fun callOnFileSelectedFunction(base64String: String) {
        try {
            binding.kycWebView.post {
                binding.kycWebView.evaluateJavascript(
                    "onFileSelected('$base64String','$fieldName')", null
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    internal fun loadUrl() {
        intent.extras?.let { bundle ->
            bundle.getString(Constants.FORM_URL)?.let { url ->
                binding.kycWebView.loadUrl(url)
            }
        }

    }

    private fun handleLoader(isVisible: Boolean) {
        binding.cfLoader.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun setWebView() {
        with(binding.kycWebView) {
            settings.apply {
                javaScriptEnabled = true
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                cacheMode = WebSettings.LOAD_NO_CACHE
            }
            addJavascriptInterface(webJsBridge, WB_INTENT_BRIDGE)
        }
        setWebViewClient()
    }

    private fun setWebViewClient() {
        binding.kycWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                handleLoader(false)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun shouldInterceptRequest(
                view: WebView?, request: WebResourceRequest?
            ): WebResourceResponse? {
                return super.shouldInterceptRequest(view, request)
            }
        }
    }

    private fun addWebHelperInterfaceImplementation(): WebHelperInterface {
        return object : WebHelperInterface {


            override fun onVerificationResponse(jsonObject: JSONObject) {
                handleVerificationResponse(CfUtils.getVerificationSuccessResponse(jsonObject))
            }

            override fun onWebErrors(jsonObject: JSONObject) {
                handleErrorResponse(CfUtils.getErrorResponse(jsonObject))
            }

            override fun openFilePicker(fieldName: String) {
                this@KycVerificationActivity.fieldName = fieldName
                openFilePicker()
            }

            override fun openCamera(fieldName: String) {
                this@KycVerificationActivity.fieldName = fieldName
                openCamera()
            }

            override fun onGetGeoLocation() {
                getGeoLocation()
            }
        }
    }

    private fun handleErrorResponse(errorResponse: CFErrorResponse) {
        CFCallbackUtil.sendErrorResponse(errorResponse)
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                this@KycVerificationActivity, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@KycVerificationActivity, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA
            )
        } else {
            dispatchTakePictureIntent()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchGeoLocation()
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            cameraLauncher.launch(takePictureIntent)
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/png", "image/jpg", "image/jpeg"))
        }
        filePickerLauncher.launch(intent)
    }

    private fun handleVerificationResponse(response: CFVerificationResponse) {
        finish()
        CFCallbackUtil.sendVerificationResponse(response)
    }

    private fun addBackPressDispatcher() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.kycWebView.canGoBack()) {
                    binding.kycWebView.goBack()
                } else {
                    exitDialog = CFExitDialog(this@KycVerificationActivity) {
                        finish()
                        handleErrorResponse(CfUtils.getCancellationResponse())
                    }
                    if (!isFinishing && !isDestroyed) {
                        exitDialog?.show()
                    }
                }

            }
        })
    }

    fun getGeoLocation() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fetchGeoLocation()
    }

    private fun fetchGeoLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val latitude = location.latitude
                val longitude = location.longitude
                passGeoLocationToWeb(latitude, longitude)
            }
        }.addOnFailureListener { exception ->
            // Handle failure to fetch location
        }
    }

    private fun passGeoLocationToWeb(latitude: Double, longitude: Double) {

        binding.kycWebView.evaluateJavascript(
            "setGeoLocation($latitude, $longitude)", null
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        webJsBridge.clearCallback()
    }
}