package com.zk.tspmanager.retrofit

interface DownloadListener {
    fun <T> onSuccess(body: T)
    fun <T> onFail(body: T)
}