package com.zk.tspmanager.progress

import com.zk.tspmanager.utils.LogUtil
import kotlin.Throws
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException

object ProgressHelper {
    private val progressBean = ProgressBean()
    private var mProgressHandler: ProgressHandler? = null

    @JvmStatic
    fun addProgress(builder: OkHttpClient.Builder?): OkHttpClient.Builder? {
        var innerBuilder: OkHttpClient.Builder? = builder
        if (innerBuilder == null) {
            innerBuilder = OkHttpClient.Builder()
        }
        val progressListener = object : ProgressListener {
            override fun onProgress(progress: Long, total: Long, done: Boolean) {
                //该方法在子线程中运行
                LogUtil.dlog(
                    "progressHelper",
                    "progress: " + progress + " total: " + total + " "
                            + String.format("%d%% done\n", 100 * progress / total )
                )
                if (mProgressHandler == null) {
                    return
                }
//                ScopeTools.executeIO(ScopeName.SAVE_PROGRESS.scopeName, object : ScopeTools.IExecutor {
//                    override suspend fun doInScope(): Boolean {
//                        DownloadDataStore
//                            .updateProgress(progress)
//                            .updateTotal(total)
//                            .updateIsDone(done)
//                        return true
//                    }
//                })
                progressBean.bytesRead = progress
                progressBean.contentLength = total
                progressBean.isDone = done
                mProgressHandler!!.sendMessage(progressBean)
            }
        }

        //添加拦截器，自定义ResponseBody，添加下载进度
        innerBuilder.networkInterceptors().add(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val originalResponse: Response = chain.proceed(chain.request())
                return originalResponse.newBuilder().body(
                    ProgressResponseBody(originalResponse.body, progressListener)
                )
                    .build()
            }
        })
        return innerBuilder
    }

    @JvmStatic
    fun setProgressHandler(progressHandler: ProgressHandler?) {
        mProgressHandler = progressHandler
    }
}