package com.caldremch.logboard;

/**
 * Created by Leon on 2022/10/21
 */
public enum DebugLogLevel {
    OFF,
    FATAL,
    ERROR,
    WARN,
    INFO,
    DEBUG,
    TRACE,
    ALL;


    public static DebugLogLevel getLogLevel(int level){
        DebugLogLevel rt = DebugLogLevel.DEBUG;
        switch (level){
            case 0:
                rt = OFF;
                break;
            case 1:
                rt = FATAL;
                break;
            case 2:
                rt = ERROR;
                break;
            case 3:
                rt = WARN;
                break;
            case 4:
                rt = INFO;
                break;
            case 6:
                rt = TRACE;
                break;
            case 7:
                rt = ALL;
                break;
            default:
        }

        return rt;
    }
}
