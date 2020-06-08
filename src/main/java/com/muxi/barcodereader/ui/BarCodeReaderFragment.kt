package com.muxi.barcodereader.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.muxi.barcodereader.BarCodeManager
import com.muxi.barcodereader.R
import com.muxi.barcodereader.utils.navigateTo

private val PERMISSIONS_REQUIRED = arrayOf(
    Manifest.permission.CAMERA)
private const val PERMISSIONS_REQUEST_CODE = 10

open class BarCodeReaderFragment: Fragment() {

    private val barCodeManager by lazy {
        BarCodeManager.getManagerInstance()
    }

    companion object {
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(hasPermissions(requireContext())) {
            checkSDKVersion()
        } else {
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_permission,container,false)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSDKVersion()
            } else {
                barCodeManager.barCodeListener?.onPermissionDenied()
            }
        }
    }

    private fun checkSDKVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            navigateTo(CameraXFragment())
        } else {
            navigateTo(Camera1Fragment())
        }
    }

}