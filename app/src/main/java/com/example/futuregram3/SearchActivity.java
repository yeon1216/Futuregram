package com.example.futuregram3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements BoardAdapter.BoardRecyclerViewClickListener{

    MyAppData myAppData; // 앱 데이터
    MyAppService myAppService; // 앱 서비스
    Member loginMember; // 로그인 멤버

    BoardAdapter boardAdapter; // 게시글 어댑터

    EditText searchET; // 검색창

    InputMethodManager inputMethodManager; // 키보드 내리기 위해서
    ArrayList<Board> reBoards; // adapter에 넣을 게시글 리스트

    PullRefreshLayout pullRefreshLayout; // 끌어당겨 로딩하기

    private Handler handler;
    boolean isClickedSearchET;
    RecomendSearchThread recomendSearchThread; // 추천검색어 쓰레드

    String getSearchContent; // 입력받은 검색어


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Log.w("kkk","검색 화면 onCreate() 호출됨");

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
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
        getSearchContent = intent.getStringExtra("searchContent");
        Log.w("ttt","여기는 onNewIntent() : "+getSearchContent);
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


        Log.w("ttt","여기는 onResume() : "+getSearchContent);
        if(getSearchContent==null){
            getSearchContent="";
        }

        myAppData = myAppService.readAllData(this);
        loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo);

        isClickedSearchET=false;

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

        // 어댑터에 넣을 게시글 리스트
        reBoards = new ArrayList<>();
        if(getSearchContent.length()==0){
            Log.w("ttt","검색 안한경우");
            for(Board board : myAppData.boards){
                if(board.writeMemberNo!=loginMember.memberNo){
                    reBoards.add(board);
                }
            }
        }else{
            Log.w("ttt","검색한경우");
            for(Board board : myAppData.boards){
                if(board.writeMemberNo!=loginMember.memberNo){
                    if(board.boardContent.contains(getSearchContent)){
                        reBoards.add(board);
                    }
                }
            }
        }

        for(Board board : myAppData.boards){
            Log.w("kkk 검색 화면 전체 데이터","게시글 번호 : "+board.boardNo+", 게시글 내용 : "+board.boardContent);
        }
        for(Board board : reBoards){
            Log.w("kkk 검색 화면 가공데이터","게시글 번호 : "+board.boardNo+", 게시글 내용 : "+board.boardContent);
        }

        if(reBoards.size()==0){
            Toast.makeText(getApplicationContext(),"검색내용 없음",Toast.LENGTH_SHORT).show();
        }

        searchET = findViewById(R.id.searchET); // 검색 입력창
        searchET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                recomendSearchThread.isClickSearchET(true);
            }
        });
        // 키보드 검색 버튼 클릭시 이벤트
        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchContent = searchET.getText().toString();
                    inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(searchET.getWindowToken(),0);
                    Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                    intent.putExtra("searchContent",searchContent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        RecyclerView boardRecyclerView = findViewById(R.id.boardRecyclerView);
        boardRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // 이건 좀 알아보자 나중에
        boardAdapter = new BoardAdapter(reBoards,myAppData,this);
        boardAdapter.setOnBoardRecyclerViewClickListener(this);
        boardRecyclerView.setAdapter(boardAdapter); // 게시글 recyclerview adapter 적용

        // 아이템 구분선
//        DividerItemDecoration decoration = new DividerItemDecoration(this, new LinearLayoutManager(this).getOrientation());
//        boardRecyclerView.addItemDecoration(decoration);

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
                        myAppData = myAppService.readAllData(SearchActivity.this);
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

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what==0){
                    searchET.setHint(" 개발자");
                }else if(msg.what==1){
                    searchET.setHint(" 팀노바");
                }else if(msg.what==2){
                    searchET.setHint(" 여행");
                }else if(msg.what==3){
                    searchET.setHint(" 제주도");
                }else if(msg.what==4){
                    searchET.setHint(" 글 내용을 검색해보세요");
                }
            }
        };

        recomendSearchThread = new RecomendSearchThread(handler,searchET);
        recomendSearchThread.start();
    } // onResume() 메소드

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onPause() 호출");
        myAppService.writeAllData(myAppData,this);

        // 쓰레드를 죽이는 메소드
        recomendSearchThread.threadStop(true);
        recomendSearchThread.interrupt();
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
//        myAppService.boardUpdateRemoveDialogShow(myAppData,tempBoardNo,boardAdapter,this);

        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
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
                        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(SearchActivity.this);
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

} // SearchActivity 클래스


class RecomendSearchThread extends Thread{
    private boolean stop; // 쓰레드를 멈추기 위한 변수
    private Handler handler;
    private boolean isClickedSearchET; // 검색창을 클릭했는지 여부
    private EditText searchET; // 입력창

    RecomendSearchThread(Handler handler, EditText searchET){
        this.stop = false;
        this.handler = handler;
        this.isClickedSearchET = false;
        this.searchET = searchET;
    }
    public void threadStop(boolean stop){
        this.stop=stop;
    }
    public void isClickSearchET(boolean isClickedSearchET){
        this.isClickedSearchET = isClickedSearchET;
    }

    @Override
    public void run() {
        Log.w("qqq", Thread.currentThread().getName() + "  생성");
        while (!stop) {
            if(!isClickedSearchET){
                try{sleep(6000);}catch (InterruptedException e){break;}
                handler.sendEmptyMessage(0);
            }if(!isClickedSearchET){
                try{sleep(3000);}catch (InterruptedException e){break;}
                handler.sendEmptyMessage(1);
            }if(!isClickedSearchET){
                try{sleep(3000);}catch (InterruptedException e){break;}
                handler.sendEmptyMessage(2);
            }if(!isClickedSearchET){
                try{sleep(3000);}catch (InterruptedException e){break;}
                handler.sendEmptyMessage(3);
            }if(!isClickedSearchET){
                try{sleep(3000);}catch (InterruptedException e){break;}
                handler.sendEmptyMessage(4);
            }
//            if(searchET.getText().toString().trim().length()==0){
//                isClickedSearchET=false;
//            }
        }
        Log.w("qqq", Thread.currentThread().getName() + "  종료");

    } // run() 메소드

} // RecomendSearchThread 클래스