LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
src_dirs:= src/org/codeaurora/bluetooth/btcservice \
           src/org/codeaurora/bluetooth/ftp \
           src/org/codeaurora/bluetooth/dun \

LOCAL_SRC_FILES := \
        $(call all-java-files-under, $(src_dirs)) \

LOCAL_PACKAGE_NAME := BluetoothExt
LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_CERTIFICATE := platform
LOCAL_JAVA_LIBRARIES := javax.obex
LOCAL_JAVA_LIBRARIES += telephony-common

LOCAL_PROGUARD_ENABLED := disabled
include $(BUILD_PACKAGE)
include $(call all-makefiles-under,$(LOCAL_PATH))
