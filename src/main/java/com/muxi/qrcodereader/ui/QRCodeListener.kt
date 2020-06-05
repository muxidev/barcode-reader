package com.muxi.qrcodereader.ui

interface QRCodeListener {

    fun onSuccess(codeReaded: String)
    fun onError()
}