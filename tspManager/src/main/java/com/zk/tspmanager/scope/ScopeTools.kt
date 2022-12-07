package com.zk.tspmanager.scope

import com.zk.tspmanager.utils.LogUtil
import kotlinx.coroutines.*
import java.util.*
import kotlin.concurrent.schedule

/**
 * 协程Tools
 * 各种线程可以在这里使用
 * @author ljl
 */
object ScopeTools {

    const val TAG: String = "ScopeTools"
    //IO scope
    var ioScopeMap: MutableMap<String, CoroutineScope>? = mutableMapOf()
    //Main scope
    var mainScopeMap: MutableMap<String, CoroutineScope>? = mutableMapOf()
    //Unconfined scope
    var unconfinedScopeMap: MutableMap<String, CoroutineScope>? = mutableMapOf()
    //Timer scope
    var timerScopeMap: MutableMap<String, TimerTask>? = mutableMapOf()

    interface IExecutor {
        suspend fun doInScope(): Boolean;
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

    /**
     * Unconfined线程协程操作
     */
    fun executeUnconfined(name: String, iExecutor: IExecutor?) {
        if (iExecutor == null) {
            LogUtil.ilog(TAG, "iExecutor can't be null")
            return
        }
        if (unconfinedScopeMap?.contains(name) == true) {
            cancelUnconfined(name)
        }
        val scope = CoroutineScope(Job() + Dispatchers.Unconfined)
        unconfinedScopeMap?.put(name, scope)
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
     * 取消Unconfined线程协程操作
     */
    fun cancelUnconfined(name: String) {
        if (unconfinedScopeMap == null) {
            LogUtil.ilog(TAG, "cancelUnconfined, sMap is null")
            return
        }
        val scope = unconfinedScopeMap?.get(name)
        if (scope != null) {
            LogUtil.ilog(TAG, "cancelUnconfined, != null")
            if (scope.isActive) {
                LogUtil.ilog(TAG, "cancelUnconfined, scope.cancel()")
                scope.cancel()
            }
            unconfinedScopeMap!!.remove(name)
        } else LogUtil.elog(TAG, "cancelUnconfined, scope is null")
    }

    /**
     * Main线程协程操作
     */
    fun executeMain(name: String, iExecutor: IExecutor?) {
        if (iExecutor == null) {
            LogUtil.ilog(TAG, "iExecutor can't be null")
            return
}
        if (mainScopeMap?.contains(name) == true) {
            cancelMain(name)
        }
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        mainScopeMap?.put(name, scope)
        scope.launch {
            LogUtil.ilog(TAG, "iExecutor start in main scope")
            val success = iExecutor.doInScope();
            if (!success) {
                LogUtil.ilog(TAG, "iExecutor.doInScope main fail")
                this.cancel()
            }
        }
    }

    /**
     * 取消Main线程协程操作
     */
    fun cancelMain(name: String) {
        if (mainScopeMap == null) {
            LogUtil.ilog(TAG, "cancelMain, sMap is null")
            return
        }
        val scope = mainScopeMap?.get(name)
        if (scope != null) {
            LogUtil.ilog(TAG, "cancelMain, != null")
            if (scope.isActive) {
                LogUtil.ilog(TAG, "cancelMain, scope.cancel()")
                scope.cancel()
            }
            mainScopeMap!!.remove(name)
        } else LogUtil.elog(TAG, "cancelMain, scope is null")
    }

    private var task: TimerTask = object : TimerTask() {
        override fun run() {
        }
    }
    private var periodTask: TimerTask = object : TimerTask() {
        override fun run() {
        }
    }

    /**
     * 带名字的延迟TimerTask
     */
    fun timerScopeWithName(name: String, delay: Long, iExecutor: IExecutor?) {
        if (iExecutor == null) {
            LogUtil.ilog(TAG, "iExecutor can't be null")
            return
        }
        if (timerScopeMap?.contains(name) == true) {
            cancelTimer(name)
        }
        val timerTask = Timer().schedule(delay) {
            executeUnconfined(name, iExecutor)
        }
        timerScopeMap?.put(name, timerTask)
    }

    fun cancelTimer(name: String) {
        if (timerScopeMap == null) {
            LogUtil.ilog(TAG, "cancelTimer with name, timerScopeMap is null")
            return
        }
        val task = timerScopeMap?.get(name)
        if (task != null) {
            LogUtil.ilog(TAG, "cancelTimer, task != null")
            task.cancel()
            timerScopeMap!!.remove(name)
        } else LogUtil.elog(TAG, "cancelTimer, task is null")
    }

    /**
     * 延迟TimerTask
     */
    fun timerScope(delay: Long, iExecutor: IExecutor?) {
        if (iExecutor == null) {
            LogUtil.ilog(TAG, "iExecutor can't be null")
            return
        }
        cancelTimer()
        task = Timer().schedule(delay) {
            executeUnconfined(ScopeName.TIMER_TASK.scopeName, iExecutor)
        }
    }

    fun cancelTimer() {
        task.cancel()
    }

    /**
     * 延迟循环TimerTask
     */
    fun timerScope(delay: Long, period: Long, iExecutor: IExecutor?) {
        if (iExecutor == null) {
            LogUtil.ilog(TAG, "iExecutor can't be null")
            return
        }
        cancelPeriodTimer()
        periodTask = Timer().schedule(delay, period) {
            executeIO(ScopeName.TIMER_PERIOD_TASK.scopeName, iExecutor)
        }
    }

    fun cancelPeriodTimer() {
        periodTask.cancel()
    }

}
