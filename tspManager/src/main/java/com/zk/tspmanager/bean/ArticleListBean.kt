package com.zk.tspmanager.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleListBean(
    val errorCode: Int,
    val errorMsg: String,
    val data: ListData,
) : Parcelable

@Parcelize
data class ListData(
    val curPage: Int,
    val offset: Long,
    val over: Boolean,
    val pageCount: Long,
    val size: Long,
    val total: Long,
    val datas: MutableList<DataBean>,
) : Parcelable

@Parcelize
data class DataBean(
        val apkLink: String,
        val audit: Long,
        val author: String,
        val canEdit: Boolean,
        val chapterId: Long,
        val chapterName: String,
        val collect: Boolean,
        val courseId: Long,
        val desc: String,
        val descMd: String,
        val envelopePic: String,
        val fresh: Boolean,
        val host: String,
        val id: Long,
        val link: String,
        val niceDate: String,
        val niceShareDate: String,
        val publishTime: Long,
        val realSuperChapterId: Long,
        val shareDate: Long,
        val shareUser: String,
        val superChapterId: Long,
        val superChapterName: String,
        val title: String,
        val type: Int,
        val userId: Int,
        val visible: Int,
        val zan: Int,
) : Parcelable
