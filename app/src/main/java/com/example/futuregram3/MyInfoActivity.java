package com.example.futuregram3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyInfoActivity extends AppCompatActivity implements BoardAdapter.BoardRecyclerViewClickListener {

    MyAppData myAppData; // 앱 데이터
    MyAppService myAppService; // 앱 서비스
    Member loginMember; // 현재 로그인중인 멤버
    BoardAdapter boardAdapter; // 게시글 adapter
    ArrayList<Board> reBoards; // 필요한 게시글 목록

    TextView followerCountTV; // 팔로워 숫자 텍스트뷰
    TextView followingCountTV; // 팔로잉 숫자 텍스트뷰

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        myAppService = new MyAppService();

        ImageView settingIV = findViewById(R.id.settingIV); // 설정 아이콘 이미지뷰
        // 설정 아이콘 이미지뷰 클릭시 이벤트
        settingIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
            }
        });

        TextView profileUpdateTV = findViewById(R.id.profileUpdateTV); // 프로필 수정 텍스트뷰
        // 프로필 수정 텍스트뷰 클릭시 이벤트
        profileUpdateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ProfileUpdateActivity.class);
                startActivity(intent);
            }
        });

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
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    } // onCreate() 메소드

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onNewIntent() 호출");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onStart() 호출");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onResume() 호출");

        myAppData = myAppService.readAllData(this);
        loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo);

        ImageView myProfileImageIV = findViewById(R.id.memberProfileImageIV); // 내 프로필 사진 이미지뷰
        if(loginMember.profileImage.substring(0,1).equals("c")){
            myProfileImageIV.setImageURI(Uri.parse(loginMember.profileImage));
        }else{
            myProfileImageIV.setImageResource(Integer.parseInt(loginMember.profileImage)); // 프로필 사진 적용
        }

        TextView myNickNameTV = findViewById(R.id.myIdTV);
        myNickNameTV.setText(loginMember.nickName);


        followerCountTV = findViewById(R.id.followerCountTV); // 팔로워 숫자 텍스트뷰
        followerCountTV.setText(loginMember.follows.size()+""); // 팔로워 숫자 텍스트뷰 세팅
        // 팔로워 숫자 텍스트뷰 클릭시 이벤트
        followerCountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MemberListActivity.class);
                intent.putExtra("screenSelect",1); // 0: 좋아요 목록, 1: 팔로워 목록, 2: 팔로잉 목록
                startActivity(intent);
            }
        });

        followingCountTV = findViewById(R.id.followingCountTV); // 팔로잉 숫자 텍스트뷰
        followingCountTV.setText(loginMember.followings.size()+""); // 팔로잉 숫자 텍스트뷰 세팅
        // 팔로잉 숫자 텍스트뷰 클릭시 이벤트
        followingCountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MemberListActivity.class);
                intent.putExtra("screenSelect",2); // 0: 좋아요 목록, 1: 팔로워 목록, 2: 팔로잉 목록
                startActivity(intent);
            }
        });

        // 게시글 adapter에 들어갈 리스트 만들기 (내 화면이므로 작성자==로그인멤버인 게시글)
        reBoards = new ArrayList<>();
        for(Board board : myAppData.boards){
            if(board.writeMemberNo==loginMember.memberNo){
                reBoards.add(board);
            }
        }

        RecyclerView boardRecyclerView = findViewById(R.id.boardRecyclerView);
        boardRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // 이건 좀 알아보자 나중에
        boardAdapter = new BoardAdapter(reBoards,myAppData,this);
        boardAdapter.setOnBoardRecyclerViewClickListener(this);
        boardRecyclerView.setAdapter(boardAdapter); // 게시글 recyclerview adapter 적용

        // 아이템 구분선
        DividerItemDecoration decoration = new DividerItemDecoration(this, new LinearLayoutManager(this).getOrientation());
        boardRecyclerView.addItemDecoration(decoration);

    } // onResume() 메소드

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onPause() 호출");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onStop() 호출");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onDestroy() 호출");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onRestart() 호출");
    }

    @Override
    public void onEditIconClicked(final int tempBoardNo, final int position) {
//        myAppService.boardUpdateRemoveDialogShow(myAppData,tempBoardNo,boardAdapter,this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MyInfoActivity.this);
        builder.setTitle("게시글을 수정하거나 삭제할 수 있습니다.");
        builder.setMessage("");
        builder.setPositiveButton("수정",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),BoardUpdateActivity.class);
                        intent.putExtra("tempBoardNo",tempBoardNo);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("삭제",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(MyInfoActivity.this);
                        deleteBuilder.setTitle("정말로 삭제하시겠습니까?");
                        deleteBuilder.setMessage("");
                        deleteBuilder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Board tempBoard = myAppService.findBoardByBoardNo(myAppData,tempBoardNo);

                                // 알림 객체를 제거해주는 메소드
                                for (Notification notification : myAppData.notifications){
                                    if(notification.makeNotificationBoardNo==tempBoardNo){
                                        myAppService.deleteNotificationData(notification,getApplicationContext());
                                    }
                                }

                                boardAdapter.removeItem(tempBoard,position);
                                myAppData.boards.remove(tempBoard);
                                myAppService.deleteBoardData(tempBoard,getApplicationContext());

                                myAppData = myAppService.readAllData(getApplicationContext());
                            }
                        });
                        deleteBuilder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        deleteBuilder.show();
                    }
                });
        builder.setNeutralButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.show(); // 다이얼로그 보이게 하기
    }
} // MyInfoActivity 클래스
