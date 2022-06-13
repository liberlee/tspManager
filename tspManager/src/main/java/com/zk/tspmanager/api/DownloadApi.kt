package com.zk.tspmanager.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface DownloadApi {

    /** 测试网络数据下载 */
    @Streaming
    @GET
    fun getDownloadFile(@Url fileUrl: String): Call<ResponseBody>;

    /** 测试网络数据下载 */
    @Streaming
    @GET
    suspend fun getDownloadFileObservable(@Url fileUrl: String): Observable<ResponseBody>;

    /** 测试网络数据下载 name = 360MobileSafe_6.2.3.1060.apk */
    @Streaming
    @GET("/mobilesafe/shouji360/360safesis/{name}")
    fun getDownloadFileTest(@Path("name") name: String): Call<ResponseBody>;

    /** 测试网络数据下载 name = 360MobileSafe_6.2.3.1060.apk */
    @Streaming
    @GET
    fun getDownloadFileUrlTest(
        @Header("Range") range: String,
        @Url url: String
    ): Call<ResponseBody>;
}