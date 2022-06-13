package com.zk.tspmanager.progress

import android.os.Looper
import android.os.Message
import com.zk.tspmanager.progress.ProgressBean
import com.zk.tspmanager.progress.ProgressHandler
import com.zk.tspmanager.progress.ProgressHelper
import kotlin.Throws
import com.zk.tspmanager.progress.ProgressResponseBody
import com.zk.tspmanager.progress.ProgressHandler.ResponseHandler
import com.zk.tspmanager.progress.UploadProgressHandler

abstract class UploadProgressHandler : ProgressHandler() {
    private var mHandler = ResponseHandler(this, Looper.getMainLooper())
    override fun sendMessage(progressBean: ProgressBean?) {
        mHandler.obtainMessage(UPLOAD_PROGRESS, progressBean).sendToTarget()
    }

    override fun handleMessage(message: Message) {
        when (message.what) {
            UPLOAD_PROGRESS -> {
                val progressBean = message.obj as ProgressBean
                onProgress(progressBean.bytesRead, progressBean.contentLength, progressBean.isDone)
            }
        }
    }

    companion object {
        private const val UPLOAD_PROGRESS = 0
    }
}