#include <com_penguinz_testopencvndk_OpencvNativeClass.h>


JNIEXPORT jint JNICALL Java_com_penguinz_testopencvndk_OpencvNativeClass_convertGray
  (JNIEnv *, jclass, jlong addrRgba, jlong addrGray){
    Mat& mRgb = *(Mat*)addrRgba;
    Mat& mGray = *(Mat*)addrGray;

    int conv;
    jint retVal;
    conv = toGray(mRgb,mGray);

    retVal = (jint)conv;

    return retVal;

}

JNIEXPORT jint JNICALL Java_com_penguinz_testopencvndk_OpencvNativeClass_donothing
        (JNIEnv *, jclass, jlong addrRgba, jlong addrGray){
    Mat& mRgb = *(Mat*)addrRgba;
    Mat& mGray = *(Mat*)addrGray;

    int conv;
    jint retVal;
    conv = doNothing(mRgb,mGray);

    retVal = (jint)conv;

    return retVal;
}

int toGray(Mat img, Mat& gray){
    cvtColor(img, gray, CV_RGBA2GRAY);
    if(gray.rows == img.rows && gray.cols == img.cols){
        return 1;
    }
    return 0;
}

int doNothing(Mat img, Mat& gray){
    gray = img.clone();
    return 1;
}