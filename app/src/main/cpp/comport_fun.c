//
// Created by Admin on 2019-09-13.
//
#include <poll.h>
#include <fcntl.h>
#include <strings.h>
#include <termios.h>
#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <malloc.h>
#include <string.h>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "_WM_",__VA_ARGS__)
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "_WM_",__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "_WM_",__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "_WM_",__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "_WM_",__VA_ARGS__)

#define RESULT_OK 	 			0
#define RESULT_ERROR 			0xFFF0
#define RESULT_NULL				RESULT_ERROR + 1
#define RESULT_OPEN_ERROR 		RESULT_ERROR + 2
#define RESULT_SCAN_ERROR 		RESULT_ERROR + 3
#define RESULT_TIMEOUT_ERROR 	RESULT_ERROR + 4
#define RESULT_SETTING_ERROR 	RESULT_ERROR + 5
#define RESULT_BYTE_SIZE		2


int serial_port_open(JNIEnv* env, jstring nodePath, int bRate);
int serial_port_write(JNIEnv* env, int fd, jbyteArray cmd, int len);
int serial_port_setting(JNIEnv* env, int fd, int bRate);

jbyteArray serial_port_read(JNIEnv* env, int fd, int delayTime);
jbyteArray CharAry2JbArray(JNIEnv* env, char* pResult, int length);

static struct pollfd pfd;

/*********************************************
 * Java to C
 *********************************************/

//node open
JNIEXPORT jint JNICALL Java_com_allentownblower_communication_SerialPortConversion_node_1open
        (JNIEnv * env, jobject thiz, jstring nodePath, jint baud_rate) {
    //Serial_Port_Open
    return serial_port_open(env, nodePath, baud_rate);
}

//node close
JNIEXPORT jint JNICALL Java_com_allentownblower_communication_SerialPortConversion_node_1close
        (JNIEnv * env, jobject thiz, jint fd) {
    //Serial_Port_Close
    return serial_port_close(fd);
}

//node read
JNIEXPORT jbyteArray JNICALL Java_com_allentownblower_communication_SerialPortConversion_node_1read
        (JNIEnv *env, jobject thiz, jint fd, jint delay)
{
    //Read data from serial port
    return serial_port_read(env, fd, delay);
}

//node write
JNIEXPORT jint JNICALL Java_com_allentownblower_communication_SerialPortConversion_node_1write
        (JNIEnv * env, jobject thiz, jint fd, jbyteArray cmd, jint len) {
    //Write command to serial port
    return serial_port_write(env, fd, cmd, len);
}


/*********************************************
 * C code about Comport
 *********************************************/

//open
int serial_port_open(JNIEnv* env, jstring nodePath, int bRate) {

    const char *port;

    port = (*env)->GetStringUTFChars(env, nodePath, NULL);	//convert jstring "nodePath" to character pointer

    //Open serial port
    int fd = open(port, O_RDWR | O_NOCTTY | O_NDELAY | O_NONBLOCK);

    //Open serial port error
    if (fd < 0) {
        (*env)->ReleaseStringUTFChars(env, nodePath, port); // release character pointer
        close(fd);
        return NULL;
    }
    //Serial port configuration
    serial_port_setting(env, fd, bRate);

    (*env)->ReleaseStringUTFChars(env, nodePath, port); // release character pointer

    return fd;
}

//close
int serial_port_close(int fd) {
    return close(fd);
}


//read
jbyteArray serial_port_read(JNIEnv* env, int fd, int delayTime) {
    //Read data from serial port
    int data_len = 0, size = 0;
    char tmp[1024];
    char* Result;
    memset(tmp, 0, sizeof(tmp));

    bzero(&pfd, sizeof(pfd));
    pfd.fd = fd;
    pfd.events = POLLIN;
    int ret = poll(&pfd, 1, delayTime * 1000);

    if (pfd.revents == 1) {
        do {
            usleep(20000);
            size = read(fd, tmp, sizeof(tmp));
            if (size <= 0) break;

            LOGI("JNI --- serial_port_read , size = %d, Result = %s, HexValue = %x, Dec = %d", size, tmp, tmp, tmp);

            char *Result_temp = NULL;
            int pt = 0;
            if( data_len != 0 ) {
                Result_temp = malloc((data_len) * sizeof(char));
                for ( pt = 0; pt < (data_len); pt++ ) {
                    Result_temp[pt]=Result[pt];
                }
                free(Result);
            }

            Result = malloc((data_len+size) * sizeof(char));
            if ( data_len != 0 ) {
                for ( pt = 0; pt < (data_len); pt++ ) {
                    Result[pt] = Result_temp[pt];
                }
            }

            int tmpPt = 0;
            for ( pt = data_len; pt < (data_len+size); pt++ ) {
                Result[pt] = tmp[tmpPt];
                tmpPt++;
            }

            data_len += size;

            memset(tmp, 0, sizeof(tmp));
            if ( Result_temp != NULL ) {
                free(Result_temp);
            }
        } while (size > 0);
        pfd.revents = 0;
    } else {
        jbyte jdata[] =  { 0xFF, 0xF4 };
        jbyteArray result = (*env)->NewByteArray(env, RESULT_BYTE_SIZE);
        (*env)->SetByteArrayRegion(env, result, 0, RESULT_BYTE_SIZE, jdata);
        return result;
    }
    int i = 0;
    for (i = 0;i < data_len; i++){
        LOGV("JNI --- serial_port_read, len:%d, Result[%d]:%02X",data_len, i, Result[i]);
    }

    return CharAry2JbArray(env, Result, data_len);
}

//write
int serial_port_write(JNIEnv* env, int fd, jbyteArray data, int len) {

    jbyte jdata[len];
    jbyte* bytedata = (*env)->GetByteArrayElements(env, data, 0);

    memset(&jdata, 0, sizeof(jdata));
    memcpy(&jdata, bytedata, len);

    int i = 0;
    for (i = 0; i < len; i++) {
        LOGI("JNI --- serial_port_write, len:%d, data[%d]:%02X", len, i, jdata[i]);
    }

    int res = write(fd, &jdata, len);
//    int res1 = pwrite64(fd, &jdata, len,0);
//    int res1 = pwrite(fd, &jdata, len,0);
//    int res1 = __pwrite64_real(fd, &jdata, len,0);

    (*env)->ReleaseByteArrayElements(env, data, bytedata, 0);

    return len;
}


/*********************************************
 * Comport & Function Settings
 *********************************************/

int _speed(int speed) {
    switch (speed) {
        case 0: return B0;
        case 50: return B50;
        case 75: return B75;
        case 110: return B110;
        case 134: return B134;
        case 150: return B150;
        case 200: return B200;
        case 300: return B300;
        case 600: return B600;
        case 1200: return B1200;
        case 1800: return B1800;
        case 2400: return B2400;
        case 4800: return B4800;
        case 9600: return B9600;
        case 19200: return B19200;
        case 38400: return B38400;
        case 57600: return B57600;
        case 115200: return B115200;
        case 230400: return B230400;
        case 460800: return B460800;
        case 500000: return B500000;
        case 576000: return B576000;
        case 921600: return B921600;
        case 1000000: return B1000000;
        case 1152000: return B1152000;
        case 1500000: return B1500000;
        case 2000000: return B2000000;
        case 2500000: return B2500000;
        case 3000000: return B3000000;
        case 3500000: return B3500000;
        case 4000000: return B4000000;
        default: return B9600;
    }
}

int serial_port_setting(JNIEnv* env, int fd, int bRate) // configure the port
{
    struct termios port_settings; // structure to store the port settings in

    bzero(&port_settings, sizeof(port_settings));
    tcflush(fd, TCIOFLUSH);

    int speed = _speed(bRate);
    cfsetispeed(&port_settings, speed); // set baud rates
    cfsetospeed(&port_settings, speed);

    port_settings.c_cflag &= ~CRTSCTS;
    port_settings.c_cflag &= ~PARENB; // set no parity, stop bits, data bits
    port_settings.c_cflag &= ~CSTOPB;
    port_settings.c_cflag &= ~CSIZE;
    port_settings.c_cflag = speed | CS8 | CLOCAL | CREAD;
    port_settings.c_oflag = 0;
    port_settings.c_iflag = 0;
    port_settings.c_iflag = BRKINT | IGNPAR;
    port_settings.c_lflag = 0;
    port_settings.c_line = 0;

    tcflush(fd, TCIOFLUSH);
    tcsetattr(fd, TCSANOW, &port_settings); // apply the settings to the serial port
    tcsetattr(fd, TCSANOW, &port_settings); // set again for FA33_LightBar issue
    return fd;
}

jbyteArray CharAry2JbArray(JNIEnv* env, char* pResult, int length) {
    int i = 0;
    jbyteArray jbAry = (*env)->NewByteArray(env, length);
    jbyte *bytes = (*env)->GetByteArrayElements(env, jbAry, 0);
    for (i = 0; i < length; i++) {bytes[i] = pResult[i];}
    (*env)->SetByteArrayRegion(env, jbAry, 0, length, bytes);
    (*env)->ReleaseByteArrayElements(env, jbAry, bytes, 0);
    return jbAry;
}


