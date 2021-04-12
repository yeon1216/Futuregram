package com.example.futuregram3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    MyAppData myAppData; // 앱 데이터
    MyAppService myAppService; // 앱 서비스
    Member loginMember; // 로그인 멤버

    TextView withDrawMemberTV; // 회원탈퇴 텍스트뷰
    TextView passwordUpdateTV; // 비밀번호 수정 텍스트뷰

    DatabaseReference chatRoomDatabaseReference;
    DatabaseReference chatDatabaseReference;
    int chatRoomNo;
    ArrayList<String> deleteChatRooms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        myAppService = new MyAppService();
        myAppData = myAppService.readAllData(this);
        loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo);
        deleteChatRooms = new ArrayList<>();


        TextView logoutTV = findViewById(R.id.logoutTV);
        logoutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loginMember.password.equals("카kaO123!@#")){
                    onClickLogout(); // 카카오로그인시 로그아웃 메소드드
                    myAppData.loginMemberNo=-1;
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("loginMemberNo",0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("loginMemberNo","-1");
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finishAffinity();
                }else {
                    if(loginMember.memberNo>=0 && loginMember.memberNo<7){
                        FirebaseDatabase.getInstance().getReference("myAppData").child("members").child(String.valueOf(loginMember.memberNo)).child("pushToken").removeValue();
                    }
                    myAppData.loginMemberNo=-1;
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("loginMemberNo",0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("loginMemberNo","-1");
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finishAffinity();
                }
            }
        });



        TextView syncDataTV = findViewById(R.id.initDataWhenKakaoLoginTV); // 데이터 동기화 텍스트뷰
        // 데이터 동기화 텍스트뷰 클릭시 이벤트
        syncDataTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        myAppData = myAppService.initData(SettingActivity.this);
                        myAppService.writeMyAppDataInRealTimeDataBase(myAppService.readAllData(SettingActivity.this));
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finishAffinity();
                    }
                });

            }
        });

//        TextView initDataTV = findViewById(R.id.initDataTV); // 데이터 초기화, 동기화 텍스트뷰
//        // 데이터 초기화, 동기화 텍스트뷰 클릭시 이벤트
//        initDataTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                myAppData = myAppService.initData(SettingActivity.this);
//                myAppService.writeMyAppDataInRealTimeDataBase(myAppService.readAllData(SettingActivity.this));
//                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finishAffinity();
//            }
//        });

        withDrawMemberTV = findViewById(R.id.withDrawMemberTV); // 회원탈퇴 텍스트뷰
        // 회원탈퇴 텍스트뷰 클릭시 이벤트
        withDrawMemberTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loginMember.password.equals("카kaO123!@#")){
                    onClickUnlink();

                }else{
                    new AlertDialog.Builder(SettingActivity.this)
                    .setTitle("정말로 회원을 탈퇴하시겠습니까?")
                    .setMessage("")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(loginMember.memberNo>=0 && loginMember.memberNo<7){
                                FirebaseDatabase.getInstance().getReference("myAppData").child("members").child(String.valueOf(loginMember.memberNo)).child("pushToken").removeValue();
                            }

                            chatRoomDatabaseReference = FirebaseDatabase.getInstance().getReference("myAppData").child("chatRooms");
                            chatRoomDatabaseReference.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                                    if(chatRoom.chatMemberNo1 == loginMember.memberNo || chatRoom.chatMemberNo0==loginMember.memberNo){
                                        chatRoomNo = chatRoom.chatRoomNo;

                                        chatRoomDatabaseReference.child(dataSnapshot.getKey()).removeValue();
                                    }
                                }
                                @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
                                @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
                                @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
                                @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
                            });



                            for(Member member : myAppData.members){
                                if(member.memberNo==loginMember.memberNo){
                                    myAppService.deleteMemberData(member,getApplicationContext());
                                    break;
                                }
                            }
                            myAppData = myAppService.readAllData(getApplicationContext());
                            // 멤버 탈퇴시 : 게시글, 댓글, 알림, 좋아하는 게시글의 좋아요 목록, 팔로잉 하는 멤버들의 팔로워 목록, 채팅

                            // 탈퇴하는 멤버가 작성한 게시글 제거해주기
                            for(Board board : myAppData.boards){
                                if(board.writeMemberNo ==loginMember.memberNo){
                                    myAppService.deleteBoardData(board,getApplicationContext());
                                }
                            }
                            myAppData = myAppService.readAllData(getApplicationContext());

                            // 탈퇴하는 멤버가 작성한 댓글 제거해주기
                            for(Reply reply : myAppData.replies){
                                if(reply.replyMemberNo == loginMember.memberNo){
                                    myAppService.deleteReplyData(reply,getApplicationContext());
                                }
                            }

                            myAppData = myAppService.readAllData(getApplicationContext());

                            // 탈퇴하는 멤버가 만든 알림 제거
                            for (Notification notification : myAppData.notifications){
                                if(notification.makeNotificationMemberNo==loginMember.memberNo){
                                    myAppService.deleteNotificationData(notification,getApplicationContext());
                                }
                            }

                            myAppData = myAppService.readAllData(getApplicationContext());

                            // 탈퇴하는 멤버가 남긴 좋아요 제거
                            for(Board board : myAppData.boards){
                                for(int j=0;j<board.likeMembers.size();j++){
                                    if(board.likeMembers.get(j)==loginMember.memberNo){
                                        board.likeMembers.remove(j);
                                        myAppService.updateBoardData(board,getApplicationContext());
                                        break;
                                    }
                                }
                            }

                            myAppData = myAppService.readAllData(getApplicationContext());

                            // 탈퇴하는 멤버가 팔로잉하는 멤버들의 팔로워목록 제거
                            for(Member member : myAppData.members){
                                for(int j=0;j<member.follows.size();j++){
                                    if(member.follows.get(j)==loginMember.memberNo){
                                        member.follows.remove(j);
                                        myAppService.updateMemberData(member,getApplicationContext());
                                        break;
                                    }
                                }
                            }

                            myAppData = myAppService.readAllData(getApplicationContext());

                            myAppData.loginMemberNo=-1;
                            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finishAffinity();
                        }
                    })
                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
                }

            }
        });

        passwordUpdateTV = findViewById(R.id.passwordUpdateTV); // 비밀번호 수정 텍스트뷰
        if(loginMember.password.equals("카kaO123!@#")){
            passwordUpdateTV.setVisibility(View.GONE);
        }else{
            passwordUpdateTV.setVisibility(View.VISIBLE);
        }
        // 비밀번호 수정 텍스트뷰 클릭시 이벤트
        passwordUpdateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PasswordUpdateActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });



    } // onCreate() 메소드

    @Override
    protected void onResume() {
        super.onResume();



    } // onResume() 메소드

    @Override
    protected void onPause() {
        super.onPause();
        myAppService.writeAllData(myAppData,this);
    }

    // 카카오 회원 로그아웃 메소드
    private void onClickLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {


            }
        });
    }

    // 카카오 회원 탈퇴 메소드
    private void onClickUnlink() {
        new AlertDialog.Builder(this)
                .setTitle("정말로 회원을 탈퇴하시겠습니까?")
                .setMessage("")
                .setPositiveButton("네",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Logger.e(errorResult.toString());
                                        Toast.makeText(getApplicationContext(),"오류 : onFailure()",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        Toast.makeText(getApplicationContext(),"오류 : onSessionClosed()",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        Toast.makeText(getApplicationContext(),"오류 : onNotSignedUp()",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {
                                        if(loginMember.memberNo>=0 && loginMember.memberNo<7){
                                            FirebaseDatabase.getInstance().getReference("myAppData").child("members").child(String.valueOf(loginMember.memberNo)).child("pushToken").removeValue();
                                        }

                                        chatRoomDatabaseReference = FirebaseDatabase.getInstance().getReference("myAppData").child("chatRooms");
                                        chatRoomDatabaseReference.addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                                                if(chatRoom.chatMemberNo1 == loginMember.memberNo || chatRoom.chatMemberNo0==loginMember.memberNo){
                                                    chatRoomNo = chatRoom.chatRoomNo;

                                                    chatRoomDatabaseReference.child(dataSnapshot.getKey()).removeValue();
                                                }
                                            }
                                            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
                                            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
                                            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
                                            @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
                                        });



                                        for(Member member : myAppData.members){
                                            if(member.memberNo==loginMember.memberNo){
                                                myAppService.deleteMemberData(member,getApplicationContext());
                                                break;
                                            }
                                        }
                                        myAppData = myAppService.readAllData(getApplicationContext());
                                        // 멤버 탈퇴시 : 게시글, 댓글, 알림, 좋아하는 게시글의 좋아요 목록, 팔로잉 하는 멤버들의 팔로워 목록, 채팅

                                        // 탈퇴하는 멤버가 작성한 게시글 제거해주기
                                        for(Board board : myAppData.boards){
                                            if(board.writeMemberNo ==loginMember.memberNo){
                                                myAppService.deleteBoardData(board,getApplicationContext());
                                            }
                                        }
                                        myAppData = myAppService.readAllData(getApplicationContext());

                                        // 탈퇴하는 멤버가 작성한 댓글 제거해주기
                                        for(Reply reply : myAppData.replies){
                                            if(reply.replyMemberNo == loginMember.memberNo){
                                                myAppService.deleteReplyData(reply,getApplicationContext());
                                            }
                                        }

                                        myAppData = myAppService.readAllData(getApplicationContext());

                                        // 탈퇴하는 멤버가 만든 알림 제거
                                        for (Notification notification : myAppData.notifications){
                                            if(notification.makeNotificationMemberNo==loginMember.memberNo){
                                                myAppService.deleteNotificationData(notification,getApplicationContext());
                                            }
                                        }

                                        myAppData = myAppService.readAllData(getApplicationContext());

                                        // 탈퇴하는 멤버가 남긴 좋아요 제거
                                        for(Board board : myAppData.boards){
                                            for(int j=0;j<board.likeMembers.size();j++){
                                                if(board.likeMembers.get(j)==loginMember.memberNo){
                                                    board.likeMembers.remove(j);
                                                    myAppService.updateBoardData(board,getApplicationContext());
                                                    break;
                                                }
                                            }
                                        }

                                        myAppData = myAppService.readAllData(getApplicationContext());

                                        // 탈퇴하는 멤버가 팔로잉하는 멤버들의 팔로워목록 제거
                                        for(Member member : myAppData.members){
                                            for(int j=0;j<member.follows.size();j++){
                                                if(member.follows.get(j)==loginMember.memberNo){
                                                    member.follows.remove(j);
                                                    myAppService.updateMemberData(member,getApplicationContext());
                                                    break;
                                                }
                                            }
                                        }

                                        myAppData = myAppService.readAllData(getApplicationContext());

                                        myAppData.loginMemberNo=-1;
                                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finishAffinity();
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("아니요",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

    }



} // 설정 Activity 클래스
