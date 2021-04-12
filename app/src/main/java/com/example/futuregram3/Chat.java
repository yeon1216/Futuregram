package com.example.futuregram3;

import java.io.Serializable;
import java.util.Calendar;

public class Chat implements Serializable {
    public int chatNo; // 채팅 번호
    public int chatRoomNo; // 채팅 룸 번호
    public int chatMemberNo; // 작성 멤버 번호
    public String chatContent; // 채팅 내용
    public String chatTime; // 채팅 시간

    public Chat(){}

    public Chat(int chatNo, int chatRoomNo, int chatMemberNo, String chatContent){
        MyAppService myAppService = new MyAppService();
        this.chatNo=chatNo;
        this.chatRoomNo=chatRoomNo;
        this.chatMemberNo = chatMemberNo;
        this.chatContent = chatContent;
        this.chatTime = myAppService.timeToString(Calendar.getInstance());
    } // Chat 생성자
} // Chat 클래스
