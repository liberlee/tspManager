package com.zk.tspmanager.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.zk.car.upgrade.McuVersion
import com.zk.tspmanager.utils.LogUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Mcu版本DataStore
 * 使用前请调用init初始化
 * 方法调用请放在协程中
 * @author ljl
 */
object McuDataStore {

    const val TAG: String = "McuDataStore"
    private val Context.mcuDataStore: DataStore<McuVersion> by dataStore(
        fileName = "mcu_version.pb",
        serializer = McuPreferencesSerializer
    )

    private var globalDataStore: DataStore<McuVersion>? = null

    private fun isNotInit(): Boolean {
        LogUtil.ilog(TAG, "globalDataStore: $globalDataStore")
        return globalDataStore == null
    }

    /**
     * 请初始化，要不不样用
     */
    fun init(context: Context) {
        if (globalDataStore == null) {
            globalDataStore = context.mcuDataStore
        }
    }

    /**
     * 清空数据
     */
    suspend fun clear() {
        if (isNotInit()) return
        globalDataStore!!.updateData {
            it.toBuilder().clear().build()
        }
    }

    /**
     * 存储版本号
     * for Java use
     */
    fun updateRunPositionNow(position: Int): McuDataStore {
        runBlocking {
            globalDataStore!!.updateData {
                it.toBuilder().setMcuRunPosition(position).build()
            }
            LogUtil.ilog(DownloadDataStore.TAG, "updateRunPositionNow: $position")
        }
        return this
    }

    /**
     * 获取版本号
     */
    fun getRunPosition(): Int {
        if (isNotInit()) return -1

        var version: Int = -1
        runBlocking {
            version = globalDataStore?.data?.map { mcu ->
                mcu.mcuRunPosition
            }?.first() ?: -1
        }

        return version
    }

    /**
     * 存储版本号
     * for Java use
     */
    fun updatePblVersionNow(version: String): McuDataStore {
        runBlocking {
            globalDataStore!!.updateData {
                it.toBuilder().setMcuPblVersion(version).build()
            }
            LogUtil.ilog(DownloadDataStore.TAG, "updatePblVersionNow: $version")
        }
        return this
    }

    /**
     * 获取版本号
     */
    fun getPblVersion(): String {
        if (isNotInit()) return "0"

        var version: String = "0"
        runBlocking {
            version = globalDataStore?.data?.map { mcu ->
                mcu.mcuPblVersion
            }?.first() ?: "0"
        }

        return version
    }

    /**
     * 存储版本号
     * for Java use
     */
    fun updateAVersionNow(version: String): McuDataStore {
        runBlocking {
            globalDataStore!!.updateData {
                it.toBuilder().setMcuAVersion(version).build()
            }
            LogUtil.ilog(DownloadDataStore.TAG, "updateAVersionNow: $version")
        }
        return this
    }

    /**
     * 获取版本号
     */
    fun getAVersion(): String {
        if (isNotInit()) return "0"

        var version: String = "0"
        runBlocking {
            version = globalDataStore?.data?.map { mcu ->
                mcu.mcuAVersion
            }?.first() ?: "0"
        }

        return version
    }

    /**
     * 存储版本号
     * for Java use
     */
    fun updateBVersionNow(version: String): McuDataStore {
        runBlocking {
            globalDataStore!!.updateData {
                it.toBuilder().setMcuBVersion(version).build()
            }
            LogUtil.ilog(DownloadDataStore.TAG, "updateBVersionNow: $version")
        }
        return this
    }

    /**
     * 获取版本号
     */
    fun getBVersion(): String {
        if (isNotInit()) return "0"

        var version: String = "0"
        runBlocking {
            version = globalDataStore?.data?.map { mcu ->
                mcu.mcuBVersion
            }?.first() ?: "0"
        }

        return version
    }

    /**
     * 存储版本号
     * for Java use
     */
    fun updateNvmVersionNow(version: String): McuDataStore {
        runBlocking {
            globalDataStore!!.updateData {
                it.toBuilder().setMcuNvmVersion(version).build()
            }
            LogUtil.ilog(DownloadDataStore.TAG, "setMcuNvmVersion: $version")
        }
        return this
    }

    /**
     * 获取版本号
     */
    fun getNvmVersion(): String {
        if (isNotInit()) return "0"

        var version: String = "0"
        runBlocking {
            version = globalDataStore?.data?.map { mcu ->
                mcu.mcuNvmVersion
            }?.first() ?: "0"
        }

        return version
    }

    /**
     * 存储升级数据Bean
     * for Java use
     */
    fun updateMcuUpdateBeanNow(bean: String): McuDataStore {
        runBlocking {
            globalDataStore!!.updateData {
                it.toBuilder().setMcuUpdateBean(bean).build()
            }
            LogUtil.ilog(DownloadDataStore.TAG, "updateMcuUpdateBeanNow: $bean")
        }
        return this
    }

    /**
     * 获取升级数据Bean
     */
    fun getMcuUpdateBean(): String {
        if (isNotInit()) return ""

        var bean: String = ""
        runBlocking {
            bean = globalDataStore?.data?.map { mcu ->
                mcu.mcuUpdateBean
            }?.first() ?: ""
        }

        return bean
    }

}