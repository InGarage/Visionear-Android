#include <com_penguinz_testopencvndk_OpencvTest.h>

JNIEXPORT jint JNICALL Java_com_penguinz_testopencvndk_OpencvTest_convertGray
  (JNIEnv * env, jclass, jbyteArray img, jlong addrGray, jint row, jint col){
    //Mat& mRgb = *(Mat*)addrRgba;
    jbyte * cData = env->GetByteArrayElements(img, 0);
    Mat mRgb = Mat(row,col,CV_8UC4,cData);
    Mat& mGray = *(Mat*)addrGray;

    int conv;
    jint retVal;
    conv = toGray(mRgb,mGray);

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