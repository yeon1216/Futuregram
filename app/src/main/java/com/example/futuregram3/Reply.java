package com.example.futuregram3;

import java.io.Serializable;
import java.util.Calendar;

public class Reply implements Serializable {

    public int replyBoardNo; // 댓글 게시글 번호
    public int replyNo; // 댓글 번호
    public String replyContent; // 댓글 내용
    public int replyMemberNo; // 댓글 멤버 번호
    public String replyTime; // 댓글 시간


    public Reply(){}
    // 생성자
    public Reply(int replyBoardNo,int replyNo,  String replyContent, int replyMemberNo){
        MyAppService myAppService = new MyAppService();
        this.replyBoardNo = replyBoardNo;
        this.replyNo = replyNo;
        this.replyContent = replyContent;
        this.replyMemberNo = replyMemberNo;
        this.replyTime = myAppService.timeToString(Calendar.getInstance());
    }

} // Reply 클래스
