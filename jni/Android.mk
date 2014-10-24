LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE :=  python2.7
LOCAL_SRC_FILES := ../python-install/lib/libpython2.7.so
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE :=  sqlite3
LOCAL_SRC_FILES := ../python-install/lib/libsqlite3.so
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)
include $(call all-makefiles-under,$(LOCAL_PATH))