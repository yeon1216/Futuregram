package com.example.futuregram3;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class MyAppData implements Serializable {

    public int loginMemberNo; // 로그인 멤버 번호 및 로그인 상태('-1'인 경우에는 로그아웃 상태)
    public ArrayList<Member> members; // 멤버 리스트
    public ArrayList<Board> boards; // 게시글 리스트
    public ArrayList<Reply> replies; // 댓글 리스트
    public ArrayList<Notification> notifications; // 알림 리스트
    public ArrayList<ChatRoom> chatRooms; // 채팅 방 리스트
    public ArrayList<Chat> chats; // 채팅 리스트

    public int memberCount; // 멤버 번호를 위한 멤버 숫자
    public int boardCount; // 게시글 번호를 위한 게시글 숫자
    public int replyCount; // 댓글 번호를 위한 댓글 숫자
    public int notificationCount; // 알람 번호를 위한 알림 숫자
    public int chatRoomCount; // 채팅 방 번호를 위한 채팅 방 숫자
    public int chatCount; // 채팅 번호를 위한 채팅 숫자

    // MyAppData 기본 생성자
    public MyAppData(){};

    // MyAppData 생성자
    public MyAppData(int initData){

        this.memberCount = 0;
        this.boardCount = 0;
        this.replyCount = 0;
        this.notificationCount = 0;
        this.chatRoomCount = 0;
        this.chatCount = 0;

        // 로그인 멤버 번호 및 로그인 상태('-1'인 경우에는 로그아웃 상태)
        this.loginMemberNo=-1;

        // 멤버 데이터 생성
        this.members = new ArrayList<>();
        Member member0 = new Member(memberCount,"01000000000","0000","zzangkoo");
        memberCount++;
        member0.profileImage = String.valueOf(R.drawable.zzangkoo);
        members.add(member0);
        Member member1 = new Member(memberCount,"01029412360","0000","yeon1216");
        memberCount++;
        member1.profileImage = String.valueOf(R.drawable.yeon1216_profile);
        members.add(member1);
        Member member2 = new Member(memberCount,"01011111111","0000","horangee");
        memberCount++;
        member2.profileImage = String.valueOf(R.drawable.horanee);
        members.add(member2);
        Member member3 = new Member(memberCount,"01022222222","0000","sajaa");
        memberCount++;
        member3.profileImage = String.valueOf(R.drawable.sajaa);
        members.add(member3);
        Member member4 = new Member(memberCount,"01033333333","0000","monkey");
        memberCount++;
        member4.profileImage = String.valueOf(R.drawable.monkey);
        members.add(member4);
        Member member5 = new Member(memberCount,"01044444444","0000","fish");
        memberCount++;
        member5.profileImage = String.valueOf(R.drawable.fish);
        members.add(member5);
        Member member6 = new Member(memberCount,"01023991733","0000","sola");
        memberCount++;
        member6.profileImage = String.valueOf(R.drawable.sola);
        members.add(member6);

        // 댓글 데이터 생성
        this.replies = new ArrayList<>();

        // 게시글 데이터 생성
        this.boards = new ArrayList<>();
        Board board0 = new Board(boardCount,"저는 실력있는 개발자가 될거에요. 팀노바에서 공부하는 중이에요.",String.valueOf(R.drawable.hope),1);
        boardCount++;
        Reply reply00 = new Reply(0,replyCount,"응원합니다. 화이팅하세요 ~~",4);
        replyCount++;
        replies.add(reply00);
        Reply reply01 = new Reply(0,replyCount,"저도 팀노바 다녀요",3);
        replyCount++;
        replies.add(reply01);
        board0.likeMembers.add(0,0);
        board0.likeMembers.add(0,1);
        board0.likeMembers.add(0,2);
        board0.likeMembers.add(0,3);
        boards.add(0,board0);

        Board board1 = new Board(boardCount,"내일은 화성의 맛집/카페인 엘리스의 정원에 갈거에요 ~~ :) 사진에 있는 강아지 이름은 사랑이에요.",String.valueOf(R.drawable.sarang),0);
        boardCount++;
        Reply reply10 = new Reply(1,replyCount,"좋겠다",2);
        replyCount++;
        replies.add(reply10);
        boards.add(0,board1);

        Board board2 = new Board(boardCount,"호랑이는 멋있어요 다음달에는 동물원에가서 호랑이를 구경할거에요.",String.valueOf(R.drawable.horanee),0);
        boardCount++;
        boards.add(0,board2);

        Board board3 = new Board(boardCount,"아쿠아리움 가고싶다",String.valueOf(R.drawable.fish),1);
        boardCount++;
        boards.add(0,board3);

        Board board4 = new Board(boardCount,"생각중인게 많아요. 오늘 저녁은 머 먹을까?",String.valueOf(R.drawable.lieon),3);
        boardCount++;
        board4.likeMembers.add(0,0);
        board4.likeMembers.add(0,1);
        board4.likeMembers.add(0,2);
        board4.likeMembers.add(0,3);
        boards.add(0,board4);

        // 알람 데이터
        this.notifications = new ArrayList<>();

        // 채팅 방 데이터
        this.chatRooms = new ArrayList<>();

        // 채팅 데이터
        this.chats = new ArrayList<>();

    }// MyAppData 생성자


} // MyAppData 클래스