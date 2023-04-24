package com.ivan.work.qrcodescanner

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.SurfaceHolder
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import java.util.*

class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val AUTO_FOCUS_INTERVAL = 2000L

    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var scannedCodeQr : TextView
    private lateinit var captureManager: CaptureManager
    private lateinit var handler: Handler
    private lateinit var autofocusTask: Runnable
    private var savedInst: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scannedCodeQr = findViewById(R.id.scanned_qr_code)

        savedInst = savedInstanceState

        // Check camera permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initScanner()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initScanner()
            } else {
                Toast.makeText(
                    this,
                    "Camera permission is required to scan QR codes",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun initScanner() {
        barcodeView = findViewById(R.id.barcode_view)
        barcodeView.barcodeView.decoderFactory =
            com.journeyapps.barcodescanner.DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE))
        barcodeView.initializeFromIntent(intent)
        // surface view
        barcodeView.setStatusText("")
        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let { processQRCode(it.text) }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
                resultPoints?.let {
                    for (point in it) {
                        val circle = Circle(
                            point.x,
                            point.y,
                            10f,
                            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                                color = Color.RED
                                style = Paint.Style.FILL
                            }
                        )
                        val overlayView = OverlayView(applicationContext)
                        overlayView.setCircles(listOf(circle))
                        val overlay = barcodeView.overlay
                        overlay.add(overlayView)

                    }
                }
            }

        })

        captureManager = CaptureManager(this, barcodeView)
        captureManager.initializeFromIntent(intent, savedInst)
        captureManager.decode()
        handler = Handler(Looper.getMainLooper())
        autofocusTask = Runnable {
            barcodeView.barcodeView?.let {
                it.setTorch(false)
                it.isFocusable = true
                it.resume()
            }
        }
        handler.postDelayed(autofocusTask, AUTO_FOCUS_INTERVAL)
    }

    private fun processQRCode(qrCodeText: String) {
        // Process the scanned QR code text here
        scannedCodeQr.setText(qrCodeText)
        Log.i(TAG, "QRCode : $qrCodeText");
        //Toast.makeText(this, qrCodeText, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        captureManager.onSaveInstanceState(outState)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Do nothing for now
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Do nothing for now
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Do nothing for now
    }
}
