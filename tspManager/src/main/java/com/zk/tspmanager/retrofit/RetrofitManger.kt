package com.zk.tspmanager.retrofit

import com.zk.tspmanager.SSLSocketClient
import com.zk.tspmanager.api.Api
import com.zk.tspmanager.api.DownloadApi
import com.zk.tspmanager.progress.ProgressHandler
import com.zk.tspmanager.progress.ProgressHelper
import com.zk.tspmanager.utils.LogUtil
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit
import javax.net.ssl.X509TrustManager

/**
 * RetrofitManger单例
 * @author ljl
 */
object RetrofitManger {

    @JvmStatic
    var mApi : Api? = null

    private const val CONNECTION_TIME_OUT = 10L
    private const val READ_TIME_OUT = 10L


    //测试下载接口
    private const val TEST_DOWNLOAD_API_URL = "http://msoftdl.360.cn"
    //测试接口
    private const val TEST_API_URL = "https://www.wanandroid.com"
    //正式接口
    private const val API_URL = "https://www.wanandroid.com"

    /**
     * 下载文件测试
     * 只能下载，无进度Interceptors
     */
    private fun downloadFileTest(file: String? = "360MobileSafe_6.2.3.1060.apk", listener: DownloadListener?) {
        val responseBodyCall = getApiServiceWithBaseUrl(TEST_DOWNLOAD_API_URL , DownloadApi::class.java).getDownloadFileTest(
            file!!
        )
        responseBodyCall.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (listener != null) {
                    if (response.isSuccessful) {
                        listener.onSuccess(response.body())
                    } else {
                        LogUtil.elog("RetrofitManager", "onResponse 异常:$response")
                        listener.onFail<Any?>(null)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                LogUtil.elog("RetrofitManager", "onFailure 异常:$t")
                listener?.onFail<Any?>(null)
            }
        })
    }

    /**
     * 获取自封装的网络接口retrofit
     * 可以有进度
     * @param progressHandler 下载进度handler
     */
    @JvmStatic
    fun <T> getApiServiceWithBaseUrl(baseUrl: String, progressHandler: ProgressHandler?,  tClazz: Class<T>): T {
        ProgressHelper.setProgressHandler(progressHandler)
        val builder: OkHttpClient.Builder? = if (progressHandler != null) {
            ProgressHelper.addProgress(null)
        } else {
            buildOkHttpsClient()
        }
        val mService: T
        val okHttpsClient: OkHttpClient.Builder? = builder
        mService = buildRetrofit(baseUrl, okHttpsClient).create(tClazz)
        return mService!!
    }

    /**
     * 获取自封装的网络接口retrofit
     * @param baseUrl base-url
     * @param builder OkHttpClient.Builder
     */
    @JvmStatic
    fun <T> getApiServiceWithBaseUrl(baseUrl: String, builder: OkHttpClient.Builder?,  tClazz: Class<T>): T {
        val mService: T
        val okHttpsClient: OkHttpClient.Builder = builder ?: buildOkHttpsClient()
        mService = buildRetrofit(baseUrl, okHttpsClient).create(tClazz)
        return mService!!
    }

    /**
     * 获取自封装的网络接口retrofit
     * @param baseUrl base-url
     */
    @JvmStatic
    fun <T> getApiServiceWithBaseUrl(baseUrl: String, tClazz: Class<T>): T {
        val mService: T
        val okHttpsClient = buildOkHttpsClient()
        mService = buildRetrofit(baseUrl, okHttpsClient).create(tClazz)
        return mService!!
    }

    /**
     * 获取自封装的网络接口retrofit
     * 使用默认baseUrl请求网络
     */
    fun <T> getApiService(tClazz: Class<T>): T {
        val mService: T
        val okHttpClient = buildOkHttpClient()
        mService = buildRetrofit(API_URL, okHttpClient).create(tClazz)
        return mService!!
    }

    /**
     * 测试接口
     * mApi可能为空，
     * @exception NullPointerException
     */
    fun getApiService(): Api {
        if (mApi == null) {
            synchronized(this) {
                if (mApi == null) {
                    val okHttpClient = buildOkHttpClient()
                    mApi = buildRetrofit(TEST_API_URL, okHttpClient).create(Api::class.java)
                }
            }
        }
        return mApi!!
    }

    /**
     * HTTPS
     * 添加证书
     */
    private fun buildOkHttpsClient(): OkHttpClient.Builder {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .sslSocketFactory(
                SSLSocketClient.sSLSocketFactory,
                SSLSocketClient.trustManager[0] as X509TrustManager
            ).hostnameVerifier(SSLSocketClient.hostnameVerifier)
            .addInterceptor(interceptor)
            .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .proxy(Proxy.NO_PROXY)

    }

    /**
     * HTTP
     */
    private fun buildOkHttpClient(): OkHttpClient.Builder {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .proxy(Proxy.NO_PROXY)

    }

    private fun buildRetrofit(
        baseUrl: String,
        builder: OkHttpClient.Builder?
    ): Retrofit {
        val client = builder?.build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .client(client!!)
            .build()
    }

}