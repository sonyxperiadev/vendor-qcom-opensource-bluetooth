LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES:=     \
    btsnoop_dump.c

LOCAL_MODULE:= btsnoop
LOCAL_SYSTEM_EXT_MODULE := true

LOCAL_SHARED_LIBRARIES += libcutils

include $(BUILD_EXECUTABLE)
