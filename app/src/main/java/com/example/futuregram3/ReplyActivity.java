package com.example.futuregram3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ReplyActivity extends AppCompatActivity {

    EditText replyET; // 댓글 입력 창

    boolean isBackPressed = false; // 백키를 눌렀는지 확인

    MyAppData myAppData; // 앱 데이타
    MyAppService myAppService; // 앱 서비스
    Member loginMember; // 로그인 멤버
    int tempBoardNo; // 현재 게시글의 번호
    Board tempBoard; // 현재 게시글
    ArrayList<Reply> tempBoardReplies; // 현재 게시글의 댓글 목록


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        myAppService = new MyAppService(); // 서비스 객체 생성

        Intent intent = getIntent();
        tempBoardNo = intent.getIntExtra("tempBoardNo",-1);

    } // onCreate() 메소드

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        tempBoardNo = intent.getIntExtra("tempBoardNo",-1);
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
        loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo); // 로그인 멤버 객체 찾기
        tempBoard = myAppService.findBoardByBoardNo(myAppData,tempBoardNo);

        // 어댑터에 들어갈 댓글 리스트 생성
        tempBoardReplies = new ArrayList<>();
        for(Reply tempReply : myAppData.replies){
            if(tempReply.replyBoardNo==tempBoardNo){
                tempBoardReplies.add(tempReply);
            }
        }

        RecyclerView replyRecyclerView = findViewById(R.id.replyRecyclerView);
        replyRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // 이건 좀 알아보자 나중에
        replyRecyclerView.setAdapter(new ReplyAdapter(tempBoardReplies,this)); // 댓글 recyclerview adapter 적용

        replyET = findViewById(R.id.replyET); // 댓글 입력 창

        ImageView registerReplyIconIV = findViewById(R.id.registerReplyIconIV); // 댓글 등록 아이콘
        registerReplyIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String replyContent = replyET.getText().toString();
                if(replyContent.length()!=0){
                    Reply addReply = new Reply(tempBoardNo,myAppData.replyCount,replyContent,loginMember.memberNo);
                    myAppData.replyCount++;
                    myAppData.replies.add(addReply);
                    if(tempBoard.writeMemberNo!=loginMember.memberNo){
                        // 글 작성자가 로그인멤버가 아니면 알람 추가
                        myAppData.notifications.add(new Notification(tempBoard.writeMemberNo,myAppData.notificationCount,loginMember.memberNo,tempBoardNo,2));
                        myAppData.notificationCount++;
                    }
                    replyET.setText("");
                    Intent intent = new Intent(getApplicationContext(),ReplyActivity.class);
                    intent.putExtra("myAppData",myAppData);
                    intent.putExtra("tempBoardNo",tempBoard.boardNo);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"댓글을 입력해주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });


        // 자신이 작성한 댓글 롱클릭시 댓글 수정, 삭제
//        ReplyRecyclerViewItemClickSupport.addTo(replyRecyclerView).setOnItemLongClickListener(new ReplyRecyclerViewItemClickSupport.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
//                if(loginMember.memberNo==tempBoard.replies.get(position).replyMember.memberNo){
//                    replyUpdateRemoveDialogShow(myAppData,position);
//                }
//                return true;
//            }
//        });

        // 자신이 작성한 댓글 클릭시 댓글 수정, 삭제
        ReplyRecyclerViewItemClickSupport.addTo(replyRecyclerView).setOnItemClickListener(new ReplyRecyclerViewItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if(loginMember.memberNo==tempBoardReplies.get(position).replyMemberNo){
                    replyUpdateRemoveDialogShow(myAppData,position);
                }
            }
        });

        // 아이템 구분선
//        DividerItemDecoration decoration = new DividerItemDecoration(this, new LinearLayoutManager(this).getOrientation());
//        replyRecyclerView.addItemDecoration(decoration);

        // 댓글 초점 맞추기 (마지막으로)
        replyRecyclerView.scrollToPosition(tempBoardReplies.size()-1);

    } // onResume() 메소드

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onPause() 호출");
        myAppService.writeAllData(myAppData,this);
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
    public void onBackPressed() {
        super.onBackPressed();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onBackPressed() 호출");
        isBackPressed=true;

    }

    // 다이얼로그 띄우기
    void replyUpdateRemoveDialogShow(final MyAppData myAppData, final int currentPosition) {

        Reply tempReply = tempBoardReplies.get(currentPosition);
        final EditText editText = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(tempReply.replyMember.nickName+"님의 댓글");
        builder.setTitle("댓글을 수정하거나 삭제할 수 있습니다.");
        builder.setMessage("");
        builder.setView(editText);
        editText.setText(tempReply.replyContent);

        builder.setPositiveButton("수정",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(editText.getText().toString().length()==0){
                            Toast.makeText(getApplicationContext(),"수정 할 댓글을 입력해주세요",Toast.LENGTH_SHORT);
                        }else{
                            Reply updateReply = tempBoardReplies.get(currentPosition);
                            for(Reply tempReply : myAppData.replies){
                                if(updateReply.replyNo==tempReply.replyNo){
                                    updateReply.replyContent = editText.getText().toString();
                                    break;
                                }
                            }
//                            tempBoardReplies.get(currentPosition).replyContent = editText.getText().toString();
                            Toast.makeText(getApplicationContext(),"댓글이 수정되었습니다.",Toast.LENGTH_SHORT);
                            Intent intent = new Intent(getApplicationContext(),ReplyActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("myAppData",myAppData);
                            intent.putExtra("tempBoardNo",tempBoard.boardNo);
                            startActivity(intent);
                        }
                    }
                });
        builder.setNegativeButton("삭제",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(ReplyActivity.this);
                        deleteBuilder.setTitle("정말로 삭제하시겠습니까?");
                        deleteBuilder.setMessage("");
                        deleteBuilder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Reply removeReply = tempBoardReplies.get(currentPosition);
                                myAppData.replies.remove(removeReply);
                                myAppService.deleteReplyData(removeReply,ReplyActivity.this);


                                Toast.makeText(getApplicationContext(),"댓글이 삭제되었습니다.",Toast.LENGTH_SHORT);
                                Intent intent = new Intent(getApplicationContext(),ReplyActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.putExtra("myAppData",myAppData);
                                intent.putExtra("tempBoardNo",tempBoard.boardNo);
                                startActivity(intent);
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

} // ReplyActivity 클래스