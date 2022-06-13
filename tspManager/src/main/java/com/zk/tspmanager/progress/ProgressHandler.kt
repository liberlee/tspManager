package com.zk.tspmanager.progress

import android.os.Handler
import android.os.Looper
import android.os.Message

abstract class ProgressHandler {
    abstract fun sendMessage(progressBean: ProgressBean?)
    protected abstract fun handleMessage(message: Message)
    protected abstract fun onProgress(progress: Long, total: Long, done: Boolean)
    protected class ResponseHandler( private val mProgressHandler: ProgressHandler, looper: Looper? ) : Handler(
        looper!!
    ) {
        override fun handleMessage(msg: Message) {
            mProgressHandler.handleMessage(msg)
        }
    }
}