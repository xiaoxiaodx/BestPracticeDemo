LOCAL_PATH := $(call my-dir)
LOCAL_ALLOW_UNDEFINED_SYMBOLS := true
include $(CLEAR_VARS)
LOCAL_MODULE := ceres
LOCAL_SRC_FILES := libceres.a
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)
LOCAL_C_INCLUDES += /opt/sdk/ndk-bundle/sources/cxx-stl/llvm-libc++
LOCAL_C_INCLUDES += /opt/sdk/ndk-bundle/sources/cxx-stl/llvm-libc++abi
LOCAL_C_INCLUDES += /opt/sdk/ndk-bundle/sources/cxx-stl
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../config
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../include
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../eigen/eigen-eigen-3/
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../internal/ceres/miniglog
LOCAL_MODULE := myproject
LOCAL_SRC_FILES := test.cpp
LOCAL_STATIC_LIBRARIES = ceres
LOCAL_LDLIBS += -llog -ldl
include $(BUILD_SHARED_LIBRARY)
