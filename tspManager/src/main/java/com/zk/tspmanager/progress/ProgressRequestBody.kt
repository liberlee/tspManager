package com.zk.tspmanager.progress

import com.zk.tspmanager.progress.ProgressBean
import com.zk.tspmanager.progress.ProgressHandler
import com.zk.tspmanager.progress.ProgressHelper
import kotlin.Throws
import com.zk.tspmanager.progress.ProgressResponseBody
import com.zk.tspmanager.progress.ProgressHandler.ResponseHandler
import com.zk.tspmanager.progress.UploadProgressHandler
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

class ProgressRequestBody(
    private val requestBody: RequestBody,
    private val progressListener: ProgressListener
) : RequestBody() {
    private var bufferedSink: BufferedSink? = null
    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        if (bufferedSink == null) {
            bufferedSink = sink(sink).buffer()
        }
        requestBody.writeTo(bufferedSink!!)
        bufferedSink!!.flush()
    }

    private fun sink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            var bytesWritten = 0L
            var contentLength = 0L
            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (contentLength == 0L) {
                    contentLength = contentLength()
                }
                bytesWritten += byteCount
                progressListener.onProgress(
                    bytesWritten,
                    contentLength,
                    bytesWritten == contentLength
                )
            }
        }
    }
}