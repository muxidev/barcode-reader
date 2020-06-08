package com.muxi.qrcodereader

import com.muxi.qrcodereader.ui.BarCodeListener

class BarCodeManager {
    var barCodeListener: BarCodeListener? = null

    companion object {
        var instance: BarCodeManager? = null

        fun getManagerInstance(): BarCodeManager {
            synchronized(this) {
                if(instance == null) {
                    instance = BarCodeManager()
                }
                return instance!!
            }
        }
    }

    fun setListener(barCodeListener: BarCodeListener) {
        this.barCodeListener = barCodeListener
    }
}