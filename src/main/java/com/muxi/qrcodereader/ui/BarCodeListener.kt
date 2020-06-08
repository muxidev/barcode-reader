package com.muxi.qrcodereader.ui

import android.util.SparseArray
import com.google.android.gms.vision.barcode.Barcode
import com.muxi.qrcodereader.data.BarcodeResponse

interface BarCodeListener {

    fun onSuccess(codeReaded: BarcodeResponse)
    fun onError()
    fun onPermissionDenied()
    fun onPermissionGranted()
}