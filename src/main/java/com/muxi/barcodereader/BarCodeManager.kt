package com.muxi.barcodereader

import com.muxi.barcodereader.ui.BarCodeListener

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