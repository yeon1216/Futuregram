package com.example.futuregram3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    MyAppData myAppData;
    MyAppService myAppService;
    Member loginMember;
    RecyclerView memberListRecyclerView; // 멤버리스트 리싸이클러뷰
    MemberListAdapter memberListAdapter; // 멤버리스트 어뎁터
    DatabaseReference chatRoomsDatabaseReference; // 채팅방 접근하는 디비 레퍼런스


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        myAppService = new MyAppService();

        ImageView homeIconIV = findViewById(R.id.homeIconIV); // 홈아이콘 이미지뷰
        // 홈아이콘 이미지뷰 클릭시 이벤트
        homeIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        ImageView searchIconIV = findViewById(R.id.searchIconIV); // 검색 아이콘 이미지뷰
        // 검색 아이콘 이미지뷰 클릭시 이벤트
        searchIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        ImageView addBoardIconIV = findViewById(R.id.addBoardIconIV); // 게시글 추가 아이콘 이미지뷰
        // 게시글 추가 아이콘 이미지뷰 클릭시 이벤트
        addBoardIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),WriteBoardActivity.class);
                startActivity(intent);
            }
        });

        ImageView notificationIconIV = findViewById(R.id.notificationIconIV); // 알림 아이콘 이미지뷰
        // 알림 아이콘 이미지뷰 클릭시 이벤트
        notificationIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NotificationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        ImageView messageIconIV = findViewById(R.id.messageIconIV); // 메시지 아이콘 이미지뷰
        // 메시지 아이콘 이미지뷰 클릭시 이벤트
        messageIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MessageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

    } // onCreate() 메소드

    @Override
    protected void onResume() {
        super.onResume();
        myAppData = myAppService.readAllData(this);
        loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo);

        ImageView myProfileImageIV = findViewById(R.id.memberProfileImageIV); // 로그인 멤버 프로필사진 이미지뷰
        if(loginMember.profileImage.substring(0,1).equals("c")){
            myProfileImageIV.setImageURI(Uri.parse(loginMember.profileImage));
        }else{
            myProfileImageIV.setImageResource(Integer.parseInt(loginMember.profileImage));
        }

        TextView myNickNameTV = findViewById(R.id.myIdTV); // 로그인 멤버 닉네임 텍스트뷰
        myNickNameTV.setText(loginMember.nickName);
        // 내 아이디 텍스트뷰 클릭시 이벤트
        myNickNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MyInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // adapter에 들어갈 리스트 생성
        ArrayList<Member> chatRooms = new ArrayList<>();
//        for(ChatRoom chatRoom : myAppData.chatRooms){
//            if(chatRoom.chatMemberNo0==loginMember.memberNo){
//                chatRooms.add(myAppService.findMemberByMemberNo(myAppData,chatRoom.chatMemberNo1));
//            }else if(chatRoom.chatMemberNo1==loginMember.memberNo){
//                chatRooms.add(myAppService.findMemberByMemberNo(myAppData,chatRoom.chatMemberNo0));
//            }
//        }



        // recyclerView 만들기
        memberListRecyclerView = findViewById(R.id.memberListRecyclerView);
        memberListRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // 이건 좀 알아보자 나중에
        memberListAdapter = new MemberListAdapter(chatRooms,this,true);
        memberListRecyclerView.setAdapter(memberListAdapter);

        // 아이템 구분선
        DividerItemDecoration decoration = new DividerItemDecoration(this, new LinearLayoutManager(this).getOrientation());
        memberListRecyclerView.addItemDecoration(decoration);

        chatRoomsDatabaseReference = FirebaseDatabase.getInstance().getReference("myAppData").child("chatRooms");
        chatRoomsDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                if((0<=chatRoom.chatMemberNo0 && chatRoom.chatMemberNo0<7 && 0<=chatRoom.chatMemberNo1 && chatRoom.chatMemberNo1<7)){
                    if(chatRoom.chatMemberNo0==loginMember.memberNo){
                        memberListAdapter.addItem(myAppService.findMemberByMemberNo(myAppData,chatRoom.chatMemberNo1));
                    }else if(chatRoom.chatMemberNo1==loginMember.memberNo){
                        memberListAdapter.addItem(myAppService.findMemberByMemberNo(myAppData,chatRoom.chatMemberNo0));
                    }
                }
            }
            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    } // onResume() 메소드
} // MessagingActivity 클래스
