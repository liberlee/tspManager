package com.zk.tspmanager.api

import com.zk.tspmanager.bean.ArticleListBean
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming
import retrofit2.http.Url

interface Api {

    /** 测试网络数据获取 */
    @GET("article/list/{page}/json")
    suspend fun getArticleList(@Path("page") page: Int): Response<ArticleListBean>;

    /** 测试网络数据下载 */
    @Streaming
    @GET
    fun getDownloadFile(@Url fileUrl: String): Call<ResponseBody>;
}