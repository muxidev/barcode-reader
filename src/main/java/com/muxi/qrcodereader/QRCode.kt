package com.muxi.qrcodereader

import com.muxi.qrcodereader.ui.QRCodeListener

class QRCode {
    var qrCodeListener: QRCodeListener? = null

    companion object {
        var instance: QRCode? = null

        fun getQRInstance(): QRCode {
            synchronized(this) {
                if(instance == null) {
                    instance = QRCode()
                }
                return instance!!
            }
        }
    }

    fun setListener(qrCodeListener: QRCodeListener) {
        this.qrCodeListener = qrCodeListener
    }
}