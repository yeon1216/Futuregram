package com.example.futuregram3;

public class NotificationModel {
    public String to;
//    public String data;
    public Data data = new Data();

    public static class Data{
        public String title;
        public String text;
    }

} // NotificationModel 클래스
