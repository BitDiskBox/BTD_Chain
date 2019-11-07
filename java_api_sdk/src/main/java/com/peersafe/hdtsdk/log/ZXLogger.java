package com.peersafe.hdtsdk.log;

public class ZXLogger {
    public static final int DEBUG = 2;
    public static final int ERROR = 5;
    public static final int INFO = 3;
    public static final int VERBOSE = 1;
    public static final int WARN = 4;
    private static boolean mIsCloseLog = true;
    private static int mLogLevel = 6;

    public static void v(String tag, String message) {
        if (mLogLevel > 1 || !mIsCloseLog) {
        }
    }

    public static void d(String tag, String message) {
        if (mLogLevel > 2 || !mIsCloseLog) {
        }
    }

    public static void i(String tag, String message) {
        if (mLogLevel > 3 || !mIsCloseLog) {
        }
    }

    public static void w(String tag, String message) {
        if (mLogLevel > 4 || !mIsCloseLog) {
            System.out.println(tag+" : "+message);
        }
    }

    public static void e(String tag, String message) {
        if (mLogLevel > 5 || !mIsCloseLog) {
            System.out.println(tag+" : "+message);
        }
    }

    public void setLogLevel(int level) {
        mLogLevel = level;
    }

    public int getLogLevel() {
        return mLogLevel;
    }

    public void setCloseLog(boolean isClose) {
        mIsCloseLog = isClose;
    }

    public boolean getCloseLog() {
        return mIsCloseLog;
    }
}
