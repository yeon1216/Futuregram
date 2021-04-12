package com.example.futuregram3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class MemberListActivity extends AppCompatActivity {



    MyAppData myAppData;
    MyAppService myAppService;
    int tempBoardNo;
    Board tempBoard;
    int screenSelect; // 0: 좋아요 목록, 1: 팔로워 목록, 2: 팔로잉 목록
    Member loginMember; // 로그인 멤버

    RecyclerView memberListRecyclerView; // 멤버리스트 리싸이클러뷰
    MemberListAdapter memberListAdapter; // 멤버리스트 어뎁터

    TextView whatListTV; // 어떤 멤버 리스트 텍스트뷰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        myAppService = new MyAppService();
        myAppData = myAppService.readAllData(this);
        Intent intent = getIntent();
        whatListTV = findViewById(R.id.whatListTV);
        screenSelect = intent.getIntExtra("screenSelect",-1);
        switch (screenSelect){
            case 0:
                // 좋아요 목록 보여주기
                tempBoardNo = intent.getIntExtra("tempBoardNo",-1); // 인텐트로 게시글 번호 받기
                tempBoard = myAppService.findBoardByBoardNo(myAppData,tempBoardNo);
                Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"tempBoardNo"+tempBoard.boardNo);


                whatListTV.setText("좋아요    "+tempBoard.likeMembers.size()+" 명");

                ArrayList<Member> tempBoardLikeMembers = new ArrayList<>();
                for(Member tempMember : myAppData.members){
                    for (int tempBoardLikeMemberNo : tempBoard.likeMembers){
                        if(tempMember.memberNo==tempBoardLikeMemberNo){
                            tempBoardLikeMembers.add(tempMember);
                        }
                    }
                }
                memberListRecyclerView = findViewById(R.id.memberListRecyclerView);
                memberListRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // 이건 좀 알아보자 나중에
                memberListAdapter = new MemberListAdapter(tempBoardLikeMembers,this);
                memberListRecyclerView.setAdapter(memberListAdapter);

                break;
            case 1:
                // 팔로워 목록
                loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo);
                whatListTV.setText("팔로워    "+loginMember.follows.size()+" 명");

                // adapter에 들어갈 리스트 생성
                ArrayList<Member> follows = new ArrayList<>();
                for(Member tempMember : myAppData.members){
                    for (int followMemberNo : loginMember.follows){
                        if(tempMember.memberNo==followMemberNo){
                            follows.add(tempMember);
                        }
                    }
                }

                // recyclerView 만들기
                memberListRecyclerView = findViewById(R.id.memberListRecyclerView);
                memberListRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // 이건 좀 알아보자 나중에
                memberListAdapter = new MemberListAdapter(follows,this);
                memberListRecyclerView.setAdapter(memberListAdapter);
                break;
            case 2:
                // 팔로잉 목록
                loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo);
                whatListTV.setText("팔로잉    "+loginMember.followings.size()+" 명");

                // adapter에 들어갈 리스트 생성
                ArrayList<Member> followings = new ArrayList<>();
                for(Member tempMember : myAppData.members){
                    for (int followingMemberNo : loginMember.followings){
                        if(tempMember.memberNo==followingMemberNo){
                            followings.add(tempMember);
                        }
                    }
                }

                // recyclerView 만들기
                memberListRecyclerView = findViewById(R.id.memberListRecyclerView);
                memberListRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // 이건 좀 알아보자 나중에
                memberListAdapter = new MemberListAdapter(followings,this);
                memberListRecyclerView.setAdapter(memberListAdapter);
                break;
        }


    } // onCreate() 메소드

} // MemberListActivity 클래스
