    LOCAL_PATH := $(call my-dir)

	include $(CLEAR_VARS)

	#opencv
	OPENCVROOT:= D:\iKringz\Backup\OpenCV-2.4.10-android-sdk
	OPENCV_CAMERA_MODULES:=on
	OPENCV_INSTALL_MODULES:=on
	OPENCV_LIB_TYPE:=SHARED
	include D:\iKringz\Backup\OpenCV-2.4.10-android-sdk\OpenCV-2.4.10-android-sdk\sdk\native\jni\OpenCV.mk

	LOCAL_SRC_FILES := com_penguinz_testopencvndk_OpencvNativeClass.cpp

	LOCAL_LDLIBS += -llog
	LOCAL_MODULE := MyOpencvLibs


	include $(BUILD_SHARED_LIBRARY)