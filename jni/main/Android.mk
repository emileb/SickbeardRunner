LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := main

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../python-install/include/python2.7

LOCAL_SRC_FILES := main.cpp

LOCAL_SHARED_LIBRARIES := python2.7
 
LOCAL_LDLIBS =  -ldl -llog -lz


include $(BUILD_SHARED_LIBRARY)

