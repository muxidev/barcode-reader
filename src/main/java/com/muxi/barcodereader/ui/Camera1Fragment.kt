package com.muxi.barcodereader.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.muxi.barcodereader.BarCodeManager
import com.muxi.barcodereader.R
import com.muxi.barcodereader.utils.navigateTo
import com.muxi.barcodereader.utils.processBarCodeReaded
import java.io.IOException

class Camera1Fragment: Fragment() {

    private lateinit var barcodeDetector: BarcodeDetector
    lateinit var cameraSource: CameraSource
    lateinit var surfaceView: SurfaceView
    var barCodeManager: BarCodeManager = BarCodeManager.getManagerInstance()

    companion object {
        val TAG = Camera1Fragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camera1,container,false)
        surfaceView = view.findViewById(R.id.surface_scanner)
        barCodeManager = BarCodeManager.getManagerInstance()
        return view
    }

    private fun initReader() {
        barcodeDetector = BarcodeDetector.Builder(requireContext())
            .setBarcodeFormats(Barcode.QR_CODE and Barcode.DATA_MATRIX)
            .build()

        cameraSource = CameraSource.Builder(requireActivity().applicationContext,barcodeDetector)
            .setAutoFocusEnabled(true)
            .build()

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                cameraSource.stop()
            }

            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder?) {
                try {
                    cameraSource.start(surfaceView.holder)
                } catch (e: IOException) {
                    Log.e(TAG,"Camera start with error: ${Log.getStackTraceString(e)}")
                    barCodeManager.barCodeListener?.onError()
                    requireActivity().finish()
                }
            }
        })
        setBarCodeProcessor()
    }

    private fun setBarCodeProcessor() {

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode>{
            override fun release() {}

            override fun receiveDetections(detector: Detector.Detections<Barcode>?) {
                if(detector?.detectedItems?.size()!! > 0) {
                    val handler = Handler(Looper.getMainLooper())
                    handler.post { cameraSource.stop() }
                    barCodeManager.barCodeListener?.onSuccess(detector.detectedItems.processBarCodeReaded())
                } else {
                    Log.d(TAG,"no barcode found")
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if(!BarCodeReaderFragment.hasPermissions(requireContext())) {
            cameraSource.stop()
            navigateTo(BarCodeReaderFragment())
        } else {
            initReader()
        }
    }
}