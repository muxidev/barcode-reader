package com.muxi.barcodereader.ui

import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.YuvImage
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.muxi.barcodereader.BarCodeManager
import com.muxi.barcodereader.R
import com.muxi.barcodereader.utils.fromYUVToNv21
import com.muxi.barcodereader.utils.navigateTo
import com.muxi.barcodereader.utils.processBarCodeReaded
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

typealias BarcodeListener = (barcode: SparseArray<Barcode>) -> Unit

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class CameraXFragment:Fragment() {
    private lateinit var viewFinder: PreviewView

    private val barCodeManager = BarCodeManager.getManagerInstance()
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    companion object {
        val TAG = CameraXFragment::class.java.simpleName

        private const val RATIO_4_3_VALUE = 4.0/3.0
        private const val RATIO_16_9_VALUE = 16.0/9.0

        const val IMAGE_QUALITY = 50
    }
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camerax,container,false)
        viewFinder = view.findViewById(R.id.view_finder)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        cameraProvider?.unbindAll()
    }

    override fun onResume() {
        super.onResume()
        if(!BarCodeReaderFragment.hasPermissions(requireContext())) {
            navigateTo(BarCodeReaderFragment())
        }
    }

    private fun startCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        viewFinder.post { setUpCamera() }
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()

            lensFacing = CameraSelector.LENS_FACING_BACK

            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases() {

        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        preview = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .build()

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                    if (barcode.size() > 0) {
                        cameraExecutor.shutdown()
                        val handler = Handler(Looper.getMainLooper())
                        handler.post { cameraProvider.unbindAll() }
                        barCodeManager.barCodeListener?.onSuccess(barcode.processBarCodeReaded())
                    } else {
                        Log.d(TAG, "No barcode found")
                    }
                })
            }
        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this , cameraSelector, preview,imageAnalyzer)

            preview?.setSurfaceProvider(viewFinder.createSurfaceProvider())
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }


    inner class BarcodeAnalyzer(listener: BarcodeListener? = null) : ImageAnalysis.Analyzer {
        private val listeners = ArrayList<BarcodeListener>().apply { listener?.let { add(it) } }
        private val barcodeDetector: BarcodeDetector = BarcodeDetector.Builder(requireContext())
            .setBarcodeFormats(Barcode.QR_CODE and Barcode.DATA_MATRIX)
            .build()

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {
            if (listeners.isEmpty()) {
                image.close()
                return
            }
            val nv21 = image.fromYUVToNv21()

            val outputImage = ByteArrayOutputStream()
            val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
            yuvImage.compressToJpeg(image.cropRect, IMAGE_QUALITY, outputImage)

            val convertedByteArray = outputImage.toByteArray()
            val bitmap = BitmapFactory.decodeByteArray(
                convertedByteArray, 0, convertedByteArray.count(), null)
            val frameToProcess = Frame.Builder().setBitmap(bitmap).build()
            val barcodeResults = barcodeDetector.detect(frameToProcess)

            listeners.forEach { it(barcodeResults) }

            image.close()
        }
    }

}
