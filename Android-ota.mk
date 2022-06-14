LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := OtaHmiService

LOCAL_PROGUARD_ENABLED := disabled
LOCAL_PRIVATE_PLATFORM_APIS := true

LOCAL_PROPRIETARY_MODULE := true
LOCAL_CERTIFICATE := platform
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    $(call all-java-files-under, java) \
    $(call all-Iaidl-files-under, java) \
    java/com/excelfore/hmiagent/OtaListener.aidl \
    java/com/zk/car/upgrade/gateway/aidl/GwListener.aidl \
    java/com/zk/car/upgrade/core/aidl/OtaUpdateStatusListener.aidl

LOCAL_AIDL_INCLUDES := $(LOCAL_PATH)/java

LOCAL_STATIC_ANDROID_LIBRARIES := \
      androidx.appcompat_appcompat \
      androidx.core_core \
      androidx.annotation_annotation \
      com.google.android.material_material


LOCAL_STATIC_JAVA_LIBRARIES := \
    libprotobuf-java-lite \
    adapter-rxjava2-2.3.0 \
    converter-gson-2.9.0 \
    datastore-core-1.0.0 \
    kotlin-android-extensions-runtime-1.6.21 \
    kotlin-parcelize-runtime-1.6.21 \
    kotlinx-coroutines-android-1.6.1 \
    kotlinx-coroutines-core-jvm-1.6.1 \
    kotlin-stdlib-1.6.21 \
    kotlin-stdlib-jdk8-1.6.21 \
    kotlin-stdlib-common-1.6.21 \
    logging-interceptor-4.9.3 \
    okhttp-4.9.3 \
    okio-jvm-2.8.0 \
    retrofit-2.9.0 \
    rxjava-2.0.0

LOCAL_STATIC_JAVA_AAR_LIBRARIES:= \
    tspManager \
    datastore-1.0.0

LOCAL_ANNOTATION_PROCESSORS := \
        kotlin-stdlib \
        kotlin-annotations

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
PRE_LIBS_PATH := ../../../../../prebuilts/tools/common/m2/repository
BASE_LIBS_PATH := ../../libs

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES :=  \
    ota-jetbrain-nodeps:$(PRE_LIBS_PATH)/org/jetbrains/annotations/13.0/annotations-13.0.jar \
    tspManager:$(BASE_LIBS_PATH)/tspManager.aar \
    datastore-1.0.0:$(BASE_LIBS_PATH)/datastore-1.0.0.aar \
    adapter-rxjava2-2.3.0:$(BASE_LIBS_PATH)/adapter-rxjava2-2.3.0.jar \
    converter-gson-2.9.0:$(BASE_LIBS_PATH)/converter-gson-2.9.0.jar \
    datastore-core-1.0.0:$(BASE_LIBS_PATH)/datastore-core-1.0.0.jar \
    kotlin-android-extensions-runtime-1.6.21:$(BASE_LIBS_PATH)/kotlin-android-extensions-runtime-1.6.21.jar \
    kotlin-parcelize-runtime-1.6.21:$(BASE_LIBS_PATH)/kotlin-parcelize-runtime-1.6.21.jar \
    kotlinx-coroutines-android-1.6.1:$(BASE_LIBS_PATH)/kotlinx-coroutines-android-1.6.1.jar \
    kotlinx-coroutines-core-jvm-1.6.1:$(BASE_LIBS_PATH)/kotlinx-coroutines-core-jvm-1.6.1.jar \
    kotlin-stdlib-1.6.21:$(BASE_LIBS_PATH)/kotlin-stdlib-1.6.21.jar \
    kotlin-stdlib-jdk8-1.6.21:$(BASE_LIBS_PATH)/kotlin-stdlib-jdk8-1.6.21.jar \
    kotlin-stdlib-common-1.6.21:$(BASE_LIBS_PATH)/kotlin-stdlib-common-1.6.21.jar \
    logging-interceptor-4.9.3:$(BASE_LIBS_PATH)/logging-interceptor-4.9.3.jar \
    okhttp-4.9.3:$(BASE_LIBS_PATH)/okhttp-4.9.3.jar \
    okio-jvm-2.8.0:$(BASE_LIBS_PATH)/okio-jvm-2.8.0.jar \
    retrofit-2.9.0:$(BASE_LIBS_PATH)/retrofit-2.9.0.jar \
    rxjava-2.0.0:$(BASE_LIBS_PATH)/rxjava-2.0.0.jar


include $(BUILD_MULTI_PREBUILT)
