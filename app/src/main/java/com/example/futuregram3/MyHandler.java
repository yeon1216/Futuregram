package com.example.futuregram3;

import android.os.Message;
import android.widget.LinearLayout;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MyHandler extends Handler {

    static MyHandler myHandler;

    public MyHandler(){}

    @Override
    public void publish(LogRecord logRecord) { }

    @Override
    public void flush() { }

    @Override
    public void close() throws SecurityException { }

    // 핸들러 객체 얻기
    public static MyHandler getInstance(){
        if(myHandler==null){
            myHandler = new MyHandler();
        }
        return myHandler;
    }

} // 클래스
