package com.example.futuregram3;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Board implements Serializable {
    public int boardNo; // 게시글 번호
    public String boardContent; // 글
    public String boardImage; // 게시글 이미지
    public int writeMemberNo; // 작성자
    public String writeTime; // 작성 시간
    public ArrayList<Integer> likeMembers; // 좋아요한 멤버들
//    public ArrayList<Integer> replies; // 게시글의 댓글

    public Board(){}

    // Board 생성자
    public Board(int boardNo,String boardContent, String boardImage, int writeMemberNo){
        MyAppService myAppService = new MyAppService();
        this.boardNo = boardNo;
        this.boardContent = boardContent;
        this.boardImage = boardImage;
        this.writeMemberNo = writeMemberNo;
        this.writeTime = myAppService.timeToString(Calendar.getInstance());
        this.likeMembers = new ArrayList<>();
//        this.replies = new ArrayList<>();
    }

} // Board 클래스
