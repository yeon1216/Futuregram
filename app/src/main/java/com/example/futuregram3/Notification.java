package com.example.futuregram3;

import java.io.Serializable;
import java.util.Calendar;


//    알람이 오는 경우
//            0. 게시글을 작성한 경우
    //            새로운 게시글을 작성하였습니다.
//            1. 누군가가 내 게시글에 좋아요를 누른 경우
    //            님이 회원님의 게시글을 좋아합니다.
//            2. 누군가가 내 게시글에 댓글을 단 경우
    //            님이 회원님의 게시글에 댓글을 남겼습니다.
//            3. 누군가가 나를 팔로우 한 경우
    //            님이 회원님을 팔로우합니다.
public class Notification implements Serializable {
    int notifiedMemberNo; // 알람 받는 멤버 번호
    int notificationNo; // 알람 번호
    int makeNotificationMemberNo; // 알람을 만든 멤버 번호
    int makeNotificationBoardNo; // 알람을 만든 게시글 번호
    int notificationType; // 알람 유형
    String notificationTime; // 알람시간

    public Notification(){}

    public Notification(int notifiedMemberNo,int notificationNo,  int makeNotificationMemberNo, int makeNotificationBoardNo, int notificationType){
        MyAppService myAppService = new MyAppService();
        this.notifiedMemberNo = notifiedMemberNo;
        this.notificationNo = notificationNo;
        this.makeNotificationMemberNo = makeNotificationMemberNo;
        this.makeNotificationBoardNo = makeNotificationBoardNo;
        this.notificationType = notificationType;
        this.notificationTime = myAppService.timeToString(Calendar.getInstance());
    }

//    public Notification(int notifiedMemberNo,int notificationNo, int makeNotificationMemberNo, int notificationType){
//        MyAppService myAppService = new MyAppService();
//        this.notifiedMemberNo = notifiedMemberNo;
//        this.notificationNo = notificationNo;
//        this.makeNotificationMemberNo = makeNotificationMemberNo;
//        this.notificationType = notificationType;
//        this.notificationTime = myAppService.timeToString(Calendar.getInstance());
//    }

}
