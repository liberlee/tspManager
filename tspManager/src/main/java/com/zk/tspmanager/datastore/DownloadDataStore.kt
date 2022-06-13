package com.zk.tspmanager.datastore

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.zk.car.upgrade.DownloadProgress
import com.zk.tspmanager.scope.ScopeName
import com.zk.tspmanager.scope.ScopeTools
import com.zk.tspmanager.utils.LogUtil
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * 下载进度DataStore
 * 使用前请调用init初始化
 * 方法调用请放在协程中
 * @author ljl
 */
object DownloadDataStore {

    const val TAG: String = "DownloadDataStore"
    private val Context.downloadDataStore: DataStore<DownloadProgress> by dataStore(
        fileName = "download_progress.pb",
        serializer = DownloadPreferencesSerializer
    )

    private var globalDataStore: DataStore<DownloadProgress>? = null

    private fun isNotInit(): Boolean {
        LogUtil.ilog(TAG, "globalDataStore: $globalDataStore")
        return globalDataStore == null
    }

    /**
     * 请初始化，要不不样用
     */
    fun init(context: Context) {
        if (globalDataStore == null) {
            globalDataStore = context.downloadDataStore
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
     * suspend 请在协程中使用改方法
     */
    suspend fun updateProgress(pro: Long): DownloadDataStore {
        if (!isNotInit()) {
            globalDataStore!!.updateData {
                it.toBuilder().setProgress(pro).build()
            }
            LogUtil.ilog(TAG, "updateProgress: $pro")
        }
        return this
    }

    /**
     * for Java use
     */
    fun updateProgressAsync(pro: Long): DownloadDataStore {
        ScopeTools.executeIO(ScopeName.SAVE_PROGRESS.scopeName, object : ScopeTools.IExecutor {
            override suspend fun doInScope(): Boolean {
                globalDataStore!!.updateData {
                    it.toBuilder().setProgress(pro).build()
                }
                LogUtil.ilog(TAG, "updateProgressAsync: $pro")
                return true
            }

        })
        return this
    }

    /**
     * for Java use
     */
    fun updateProgressNow(pro: Long): DownloadDataStore {
        runBlocking {
            globalDataStore!!.updateData {
                it.toBuilder().setProgress(pro).build()
            }
            LogUtil.ilog(TAG, "updateProgressAsync: $pro")
        }
        return this
    }

    /**
     * suspend 请在协程中使用改方法
     */
    fun getProgress(): Long {
        if (isNotInit()) return 0

        var progress: Long = 0
        runBlocking {
            progress = globalDataStore?.data?.map { download ->
                download.progress
            }?.first() ?: 0
            LogUtil.ilog(TAG, "doInScope getProgress: $progress")
        }
        LogUtil.ilog(TAG, "getProgress: $progress")
//        return globalDataStore?.data?.first()?.progress!!
        return progress
    }

    /**
     * suspend 请在协程中使用改方法
     */
    suspend fun updateTotal(total: Long): DownloadDataStore {
        if (!isNotInit()) {
            globalDataStore!!.updateData {
                it.toBuilder().setTotal(total).build()
            }
            LogUtil.ilog(TAG, "updateTotal: $total")
        }
        return this
    }

    /**
     * for Java use
     */
    fun updateTotalAsync(total: Long): DownloadDataStore {
        ScopeTools.executeIO(ScopeName.SAVE_PROGRESS.scopeName, object : ScopeTools.IExecutor {
            override suspend fun doInScope(): Boolean {
                globalDataStore!!.updateData {
                    it.toBuilder().setTotal(total).build()
                }
                LogUtil.ilog(TAG, "updateProgressAsync: $total")
                return true
            }

        })
        return this
    }

    /**
     * suspend 请在协程中使用改方法
     */
    fun getTotal(): Long {
        if (isNotInit()) return 0

        var total: Long = 0
        runBlocking {
            total = globalDataStore?.data?.map { download ->
                download.total
            }?.first() ?: 0
        }

        return total
    }

    /**
     * suspend 请在协程中使用改方法
     */
    suspend fun updateIsDone(isDone: Boolean): DownloadDataStore {
        if (!isNotInit()) {
            globalDataStore!!.updateData {
                it.toBuilder().setIsDone(isDone).build()
            }
            LogUtil.ilog(TAG, "updateIsDone: $isDone")
        }
        return this
    }

    /**
     * for Java use
     */
    fun updateIsDoneAsync(isDone: Boolean): DownloadDataStore {
        ScopeTools.executeIO(ScopeName.SAVE_PROGRESS.scopeName, object : ScopeTools.IExecutor {
            override suspend fun doInScope(): Boolean {
                globalDataStore!!.updateData {
                    it.toBuilder().setIsDone(isDone).build()
                }
                LogUtil.ilog(TAG, "updateIsDoneAsync: $isDone")
                return true
            }

        })
        return this
    }

    /**
     * suspend 请在协程中使用改方法
     */
    fun getIsDone(): Boolean {
        if (isNotInit()) return false

        var isDone: Boolean = false
        runBlocking {
            isDone = globalDataStore?.data?.map { download ->
                download.isDone
            }?.first() ?: false
        }
        return isDone
    }

}