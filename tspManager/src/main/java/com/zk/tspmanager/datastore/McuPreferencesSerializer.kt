package com.zk.tspmanager.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.zk.car.upgrade.McuVersion
import java.io.InputStream
import java.io.OutputStream

/**
 * Mcu版本信息序列化
 * @author ljl
 */
object McuPreferencesSerializer : Serializer<McuVersion> {

    override val defaultValue: McuVersion
        get() = McuVersion.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): McuVersion {
        try {
            return McuVersion.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: McuVersion, output: OutputStream) {
        t.writeTo(output)
    }

}