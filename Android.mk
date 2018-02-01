LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

TMP_LOCAL_PATH := $(LOCAL_PATH)
include $(TMP_LOCAL_PATH)/tools/Android.mk
include $(TMP_LOCAL_PATH)/bthost_ipc/Android.mk
