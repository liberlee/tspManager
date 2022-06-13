package com.zk.tspmanager

import android.annotation.SuppressLint
import java.lang.Exception
import java.lang.RuntimeException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*


object SSLSocketClient {

    /**
     * @return 获取这个SSLSocketFactory
     */
    val sSLSocketFactory: SSLSocketFactory
        get() = try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustManager, SecureRandom())
            sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

//    fun getSslSocketFactory(): SSLSocketFactory {
//        val sslContext = SSLContext.getInstance("SSL")
//        sslContext.init(null, trustManager, SecureRandom())
//        return sslContext.socketFactory
//    }

    /**
     * @return 获取TrustManager
     */
    val trustManager: Array<TrustManager>
        get() = arrayOf(
            @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            })

    /**
     * @return 获取HostnameVerifier
     */
    val hostnameVerifier: HostnameVerifier
        get() = HostnameVerifier { _, _ -> true }
}