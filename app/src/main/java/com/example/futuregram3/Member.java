package com.example.futuregram3;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class Member implements Serializable{
    public int memberNo; // 멤버 번호
    public String phone; // 전화번호
    public String password; // 비밀번호
    public String nickName; // 닉네임
    public String profileImage; // 프로필 이미지
    public ArrayList<Integer> follows; // 팔로우 번호 목록
    public ArrayList<Integer> followings; // 팔로잉 목록
    public String pushToken;

    public Member(){}

    // Member 생성자
    public Member(int memberNo, String phone, String password, String nickName){
        this.memberNo = memberNo;
        this.phone = phone;
        this.password = password;
        this.nickName = nickName;
        this.profileImage = String.valueOf(R.drawable.default_profile); // 기본 이미지로 설정
        this.follows = new ArrayList<>();
        this.followings = new ArrayList<>();
    }

} // Member 클래스
