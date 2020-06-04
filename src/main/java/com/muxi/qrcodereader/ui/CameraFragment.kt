package com.muxi.qrcodereader.ui

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
import com.muxi.qrcodereader.R
import java.io.IOException

class CameraFragment: Fragment() {

    private lateinit var barcodeDetector: BarcodeDetector
    lateinit var cameraSource: CameraSource
    lateinit var surfaceView: SurfaceView

    companion object {
        val TAG = CameraFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camera,container,false)
        surfaceView = view.findViewById(R.id.surface_scanner)
        return view
    }


    private fun initReader() {
        barcodeDetector = BarcodeDetector.Builder(requireContext())
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()

        cameraSource = CameraSource.Builder(requireActivity().applicationContext,barcodeDetector)
            .setRequestedFps(60.0f)
            .setAutoFocusEnabled(true)
            .build()

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback{
            override fun surfaceChanged(
                holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                cameraSource.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                try {
                    cameraSource.start(surfaceView.holder)
                } catch (e: IOException) {
                    Log.e(TAG,"Camera start with error: ${Log.getStackTraceString(e)}")
                }
            }

        })
        setBarCodeProcessor()
    }

    private fun setBarCodeProcessor() {

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode>{
            override fun release() {}

            override fun receiveDetections(p0: Detector.Detections<Barcode>?) {
                p0?.let {
                    Log.d(TAG,it.detectedItems.valueAt(0).displayValue)
                }
                val handler = Handler(Looper.getMainLooper())
                handler.post { cameraSource.stop() }
                requireActivity().finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        initReader()
    }
}