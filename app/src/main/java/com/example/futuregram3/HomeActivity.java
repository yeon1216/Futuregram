package com.example.futuregram3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;

import java.util.ArrayList;
import java.util.Random;

public class HomeActivity extends AppCompatActivity implements BoardAdapter.BoardRecyclerViewClickListener{

    MyAppData myAppData; // 앱의 데이터
    MyAppService myAppService; // 내 앱의 시스템
    Member loginMember; // 현재 로그인 멤버
    Member selectMember; // 추천 멤버

    ImageView myProfileImageIV; // 내 프로필 이미지
    TextView myIdTV; // 내 아이디 텍스트 뷰
    ImageView memberProfileImageIV; // 내 프로필 이미지
    TextView memberIdTV; // 내 아이디 텍스트 뷰
    LinearLayout homeIfNoBoardLL; // 게시글이 없는 경우 리니어 레이아웃

    BoardAdapter boardAdapter; // 게시글 어댑터

    private Handler handler;

    PullRefreshLayout pullRefreshLayout; // 끌어당겨 로딩하기

    RecomendMemberThread recomendMemberThread; // 멤버추천 쓰레드

    AlertDialog dialog; // 닉네임을 입력 받을 다이얼로그


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onCreate() 호출");
        Log.w("kkk","홈 화면 onCreate() 호출됨");
        myAppService = new MyAppService();





        ImageView homeIconIV = findViewById(R.id.homeIconIV); // 홈아이콘 이미지뷰
        // 홈아이콘 이미지뷰 클릭시 이벤트
        homeIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        ImageView searchIconIV = findViewById(R.id.searchIconIV); // 검색 아이콘 이미지뷰
        // 검색 아이콘 이미지뷰 클릭시 이벤트
        searchIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                intent.putExtra("myAppData",myAppData);
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
                intent.putExtra("myAppData",myAppData);
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
    } // onCreate 메소드

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

        myIdTV = findViewById(R.id.myIdTV); // 내 아이디 텍스트뷰

        int loginMemberNo = myAppData.loginMemberNo;
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"loginMemberNo : "+loginMemberNo);
        loginMember = myAppService.findMemberByMemberNo(myAppData,loginMemberNo); // 현재 로그인 멤버 객체 찾기

        myIdTV.setText(loginMember.nickName);
        myProfileImageIV = findViewById(R.id.myProfileImageIV);
        if(loginMember.profileImage.substring(0,1).equals("c")){
            myProfileImageIV.setImageURI(Uri.parse(loginMember.profileImage));
        }else{
            myProfileImageIV.setImageResource(Integer.parseInt(loginMember.profileImage)); // 프로필 사진 적용
        }

        // 내 아이디 텍스트뷰 클릭시 이벤트
        myIdTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MyInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        memberIdTV = findViewById(R.id.memberIdTV);
        memberProfileImageIV = findViewById(R.id.memberProfileImageIV);



        memberIdTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MemberInfoActivity.class);
                intent.putExtra("selectMember",selectMember);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });




        // 팔로우 중인 멤버의 게시글만 선택
        ArrayList<Board> reBoards = new ArrayList<>();
        for(Board board:myAppData.boards){
            if(board.writeMemberNo==loginMember.memberNo){
                reBoards.add(board);
            }else{
                for(int followingMemberNo : loginMember.followings){
                    if(board.writeMemberNo==followingMemberNo){
                        reBoards.add(board);
                    }
                }
            }
        }

        homeIfNoBoardLL = findViewById(R.id.homeIfNoBoardLL);
        if(reBoards.size()==0){
            homeIfNoBoardLL.setVisibility(View.VISIBLE);
        }else{
            homeIfNoBoardLL.setVisibility(View.GONE);
            RecyclerView boardRecyclerView = findViewById(R.id.boardRecyclerView);
            boardRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // 이건 좀 알아보자 나중에
            boardAdapter = new BoardAdapter(reBoards,myAppData,this);
            boardAdapter.setOnBoardRecyclerViewClickListener(this);
            boardRecyclerView.setAdapter(boardAdapter); // 게시글 recyclerview adapter 적용

            // 끌어당겨 동기화화
            pullRefreshLayout = findViewById(R.id.pullRefreshLayout); // 리프레시
            pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
            pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    new AsyncTask<Integer,Integer,Integer>(){
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                        }

                        @Override
                        protected Integer doInBackground(Integer... integers) {
                            publishProgress(0);
                            myAppData = myAppService.readAllData(HomeActivity.this);
                            try {Thread.sleep(500);}catch (InterruptedException e){}
                            return 0;
                        }

                        @Override
                        protected void onProgressUpdate(Integer... values) {
                            super.onProgressUpdate(values);
                        }

                        @Override
                        protected void onPostExecute(Integer integer) {
                            super.onPostExecute(integer);
                            pullRefreshLayout.setRefreshing(false);
                        }

                    }.execute(0);

                }
            });
        }



        // 추천멤버를 보여주기 위해서
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                selectMember = myAppService.findMemberByMemberNo(myAppData,msg.what);
                memberIdTV.setText(selectMember.nickName);
                memberIdTV.setVisibility(View.VISIBLE);
                if(selectMember.profileImage.substring(0,1).equals("c")){
                    memberProfileImageIV.setImageURI(Uri.parse(selectMember.profileImage));
                }else{
                    memberProfileImageIV.setImageResource(Integer.parseInt(selectMember.profileImage)); // 프로필 사진 적용
                }
                memberProfileImageIV.setVisibility(View.VISIBLE);

            }
        };

        recomendMemberThread = new RecomendMemberThread(handler,this);
        recomendMemberThread.start();

    } // onResume() 메소드


   @Override
    protected void onPause() {
        super.onPause();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onPause() 호출");
        myAppService.writeAllData(myAppData,this);
        recomendMemberThread.threadStop(true);
        recomendMemberThread.interrupt();

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
    public void onEditIconClicked(final int tempBoardNo, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
                        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(HomeActivity.this);
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

} // HomeActivity 클래스


class RecomendMemberThread extends Thread{
    private boolean stop; // 쓰레드를 멈추기 위한 변수
    private MyAppData myAppData;
    private Handler handler;
    private MyAppService myAppService;
    private Context context;
    private Member loginMember;

    RecomendMemberThread(Handler handler, Context context){
        this.stop = false;
        this.context = context;
        this.handler = handler;
        this.myAppService = new MyAppService();
        this.myAppData = myAppService.readAllData(context);
        this.loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo);
    }
    public void threadStop(boolean stop){
        this.stop=stop;
    }

    @Override
    public void run() {
        Log.w("qqq", Thread.currentThread().getName() + "  생성");
        while (!stop) {
            Log.w("qqq", Thread.currentThread().getName() + "  멤버를 찾아라");
            Log.w("qqq", Thread.currentThread().getName() + " : " + isInterrupted());
            boolean isOkAddRecommendMember = true;
            Random random = new Random();
            int memberNo = random.nextInt(myAppData.members.size());
            if (memberNo == loginMember.memberNo) {
                isOkAddRecommendMember = false;
            }
            for (int followingMemberNo : loginMember.followings) {
                if (followingMemberNo == memberNo) {
                    isOkAddRecommendMember = false;
                }
            }
            boolean isMember=false;
            for(Member member : myAppData.members){
                if(member.memberNo==memberNo){
                    isMember=true;
                }
            }
            if (isOkAddRecommendMember && isMember) {
                handler.sendEmptyMessage(memberNo);
                try { sleep(5000); } catch (InterruptedException e) { break; }
            }


        }
    }

}