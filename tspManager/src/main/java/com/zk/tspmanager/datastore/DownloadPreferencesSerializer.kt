package com.zk.tspmanager.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.zk.car.upgrade.DownloadProgress
import java.io.InputStream
import java.io.OutputStream

/**
 * 下载进度序列化
 * @author ljl
 */
object DownloadPreferencesSerializer : Serializer<DownloadProgress> {

    override val defaultValue: DownloadProgress
        get() = DownloadProgress.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): DownloadProgress {
        try {
            return DownloadProgress.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: DownloadProgress, output: OutputStream) {
        t.writeTo(output)
    }

}