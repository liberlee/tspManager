package com.example.ljl;

import android.util.Log;

public class LogUtil {
    private static boolean debuge_v = true;

    private static boolean debuge_d = true;

    private static boolean debuge_i = true;

    private static boolean debuge_w = true;

    private static boolean debuge_e = true;

    private static final String MAINTAG = "OTA:";

    /**
     * vlog.
     * @param tag tag
     * @param message mes
     */
    public static final void vlog(String tag, String message) {
        if (debuge_v) {
            Log.v(MAINTAG + tag, message);
        }
    }

    /**
     * dlog.
     * @param tag tag
     * @param message mes
     */
    public static final void dlog(String tag, String message) {
        if (debuge_d) {
            Log.d(MAINTAG + tag, message);
        }
    }

    /**
     * ilog.
     * @param tag tag
     * @param message mes
     */
    public static final void ilog(String tag, String message) {
        if (debuge_i) {
            Log.i(MAINTAG + tag, message);
        }
    }

    /**
     * wlog.
     * @param tag tag
     * @param message mes
     */
    public static final void wlog(String tag, String message) {
        if (debuge_w) {
            Log.w(MAINTAG + tag, message);
        }
    }

    /**
     * elog.
     * @param tag tag
     * @param message mes
     */
    public static final void elog(String tag, String message) {
        if (debuge_e) {
            Log.e(MAINTAG + tag, message);
        }
    }
}
