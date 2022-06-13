package com.zk.tspmanager.scope

import com.zk.tspmanager.utils.LogUtil
import kotlinx.coroutines.*

/**
 * 协程Tools
 * 各种线程可以在这里使用
 * @author ljl
 */
object ScopeTools {

    const val TAG: String = "ScopeTools"
    private var sScope: CoroutineScope? = null
    var ioScopeMap: MutableMap<String, CoroutineScope>? = mutableMapOf()

    interface IExecutor {
        suspend fun doInScope(): Boolean;
    }

    /**
     * IO线程协程操作
     */
    private fun executeIO(iExecutor: IExecutor?) {

        if (iExecutor == null) {
            LogUtil.ilog(TAG, "iExecutor can't be null")
            return
        }
        if (sScope != null) {
            cancelIO()
        }
        sScope = CoroutineScope(Job() + Dispatchers.IO)
        sScope?.launch {
            val success = iExecutor.doInScope();
            if (!success) {
                this.cancel()
            }
        }
    }

    /**
     * 取消IO线程协程操作
     */
    private fun cancelIO() {
        if (sScope != null)
            sScope!!.cancel()
        else throw NullPointerException("Expression 'scope' must not be null")
    }

    /**
     * IO线程协程操作
     */
    fun executeIO(name: String, iExecutor: IExecutor?) {
        if (iExecutor == null) {
            LogUtil.ilog(TAG, "iExecutor can't be null")
            return
        }
        if (ioScopeMap?.contains(name) == true) {
            cancelIO(name)
        }
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        ioScopeMap?.put(name, scope)
        scope.launch {
            LogUtil.ilog(TAG, "iExecutor start in scope")
            val success = iExecutor.doInScope();
            if (!success) {
                LogUtil.ilog(TAG, "iExecutor.doInScope fail")
                this.cancel()
            }
        }
    }

    /**
     * 取消IO线程协程操作
     */
    fun cancelIO(name: String) {
        if (ioScopeMap == null) {
            LogUtil.ilog(TAG, "cancelIO, sMap is null")
            return
        }
        val scope = ioScopeMap?.get(name)
        if (scope != null) {
            LogUtil.ilog(TAG, "cancelIO, != null")
            if (scope.isActive) {
                LogUtil.ilog(TAG, "cancelIO, scope.cancel()")
                scope.cancel()
            }
            ioScopeMap!!.remove(name)
        } else LogUtil.elog(TAG, "cancelIO, scope is null")
    }

}