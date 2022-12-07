package com.zk.tspmanager.scope

enum class ScopeName(val scopeName: String) {

    SAVE_PROGRESS("saveProgress"),
    GET_TOTAL("getTotal"),
    GET_PROGRESS("getProgress"),
    SAVE_ZIP_FILE("saveZipFile"),

    COPY_UPDATE_FILE("copyUpdateFile"),
    CLEAR_UPDATE_FILE("clearUpdateFile"),
    CLEAR_MCU_UPDATE_FILE("clearMcuUpdateFile"),

    TIMER_TASK("timerTask"),
    TIMER_PERIOD_TASK("timerPeriodTask"),

    LOAD_CAR_SERVICE("loadCarService"),
    UNZIP_FILE("unzipFile"),
    UNZIP_MCU_FILE("unzipMcuFile"),
}
