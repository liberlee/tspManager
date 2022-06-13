package com.zk.tspmanager.progress

import android.os.Looper
import android.os.Message

abstract class DownloadProgressHandler : ProgressHandler() {
    private var mHandler = ResponseHandler(this, Looper.getMainLooper())
    override fun sendMessage(progressBean: ProgressBean?) {
        mHandler.obtainMessage(DOWNLOAD_PROGRESS, progressBean).sendToTarget()
    }

    override fun handleMessage(message: Message) {
        when (message.what) {
            DOWNLOAD_PROGRESS -> {
                val progressBean = message.obj as ProgressBean
                onProgress(progressBean.bytesRead, progressBean.contentLength, progressBean.isDone)
            }
        }
    }

    companion object {
        private const val DOWNLOAD_PROGRESS = 1
    }
}