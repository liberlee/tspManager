package com.zk.tspmanager.utils

import android.util.Log

object LogUtil {
    private const val debuge_v = true
    private const val debuge_d = true
    private const val debuge_i = true
    private const val debuge_w = true
    private const val debuge_e = true
    private const val MAINTAG = "TspManager:"

    /**
     * vlog.
     * @param tag tag
     * @param message mes
     */
    fun vlog(tag: String, message: String?) {
        if (debuge_v) {
            Log.v(MAINTAG + tag, message!!)
        }
    }

    /**
     * dlog.
     * @param tag tag
     * @param message mes
     */
    fun dlog(tag: String, message: String?) {
        if (debuge_d) {
            Log.d(MAINTAG + tag, message!!)
        }
    }

    /**
     * ilog.
     * @param tag tag
     * @param message mes
     */
    fun ilog(tag: String, message: String?) {
        if (debuge_i) {
            Log.i(MAINTAG + tag, message!!)
        }
    }

    /**
     * wlog.
     * @param tag tag
     * @param message mes
     */
    fun wlog(tag: String, message: String?) {
        if (debuge_w) {
            Log.w(MAINTAG + tag, message!!)
        }
    }

    /**
     * elog.
     * @param tag tag
     * @param message mes
     */
    fun elog(tag: String, message: String?) {
        if (debuge_e) {
            Log.e(MAINTAG + tag, message!!)
        }
    }
}