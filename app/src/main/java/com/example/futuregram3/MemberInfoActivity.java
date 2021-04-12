package com.example.futuregram3;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.baoyz.widget.PullRefreshLayout;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;

public class MemberInfoActivity extends AppCompatActivity implements BoardAdapter.BoardRecyclerViewClickListener {



    MyAppService myAppService; // 앱 서비스
    MyAppData myAppData; // 앱 데이터
    Member loginMember; // 로그인 멤버
    Member selectMember; // 선택한 멤버 객체

    ImageView myProfileImageIV; // 내 프로필 사진 이미지뷰
    TextView myIdTV; // 내 닉네임 텍스트뷰
    ImageView memberProfileImageIV; // 멤버 프로필 사진 이미지뷰
    TextView memberIdTV; // 멤버 닉네임 텍스트뷰
    TextView followTV; // 팔로우 유무 텍스트뷰
    ImageView startChatIV; // 대화하기 아이콘 이미지뷰

    BoardAdapter boardAdapter; // 게시글 adapter

    boolean isFollow; // 팔로우중인 멤버인지 여부

    PullRefreshLayout pullRefreshLayout; // 끌어당겨 로딩

    int chatRoomCount; // 파이어베이스에서 받아 올 채팅 방 갯수
    DatabaseReference chatRoomCountDatabaseReference; // 채팅방 갯수를 가지고올 디비레퍼런스
    int chatRoomNo; // 채팅방
    boolean isStartChat; // 바로 채팅방으로 가나요?



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);

        myAppService = new MyAppService();

        Intent intent =getIntent();
        selectMember = (Member)intent.getSerializableExtra("selectMember");
        isStartChat = intent.getBooleanExtra("isStartChat",false);

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
        myAppData = myAppService.readAllData(this);
        selectMember = (Member)intent.getSerializableExtra("selectMember");
        isStartChat = intent.getBooleanExtra("isStartChat",false);
        for(Member member:myAppData.members){
            if(selectMember.memberNo == member.memberNo){
                selectMember = member;
            }
        }
    } // onNewIntent() 메소드

    @Override
    protected void onResume() {
        super.onResume();
        myAppData = myAppService.readAllData(this);
        loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo);

        if(isStartChat){
            chatRoomNo=-1;

//                    ChatRoom chatRoom = new ChatRoom(chatRoomCount,loginMember.memberNo,selectMember.memberNo);

            ChatRoom chatRoom;
            if(loginMember.memberNo>selectMember.memberNo){
                chatRoom = new ChatRoom(Integer.parseInt(selectMember.memberNo+""+loginMember.memberNo),selectMember.memberNo,loginMember.memberNo);
            }else{
                chatRoom = new ChatRoom(Integer.parseInt(loginMember.memberNo+""+selectMember.memberNo),loginMember.memberNo,selectMember.memberNo);
            }
//            chatRoomCount++;
//            chatRoomCountDatabaseReference.setValue(chatRoomCount);
            myAppData.chatRooms.add(chatRoom);
            myAppService.writeAllData(myAppData,MemberInfoActivity.this);
            chatRoomNo = chatRoom.chatRoomNo;
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("myAppData");
            databaseReference.child("chatRooms").child(chatRoom.chatMemberNo0+""+chatRoom.chatMemberNo1).setValue(chatRoom);

            Intent intent = new Intent(getApplicationContext(),MessagingActivity.class);
            intent.putExtra("selectMember",selectMember);
            intent.putExtra("chatRoomNo",chatRoomNo);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }


        for(Member member:myAppData.members){
            if(selectMember.memberNo == member.memberNo){
                selectMember = member;
            }
        }

        myProfileImageIV = findViewById(R.id.myProfileImageIV); // 내 프로필 사진 이미지뷰
        if(loginMember.profileImage.substring(0,1).equals("c")){
            myProfileImageIV.setImageURI(Uri.parse(loginMember.profileImage));
        }else{
            myProfileImageIV.setImageResource(Integer.parseInt(loginMember.profileImage));
        }
        myIdTV = findViewById(R.id.myIdTV); // 내 닉네임 텍스트뷰
        myIdTV.setText(loginMember.nickName);

        // 내 아이디 텍스트뷰 클릭시 이벤트
        myIdTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MyInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        memberProfileImageIV = findViewById(R.id.memberProfileImageIV); // 멤버 프로필 사진 이미지뷰
        if(selectMember.profileImage.substring(0,1).equals("c")){
            memberProfileImageIV.setImageURI(Uri.parse(selectMember.profileImage));
        }else{
            memberProfileImageIV.setImageResource(Integer.parseInt(selectMember.profileImage));
        }
        memberIdTV = findViewById(R.id.memberIdTV); // 멤버 닉네임 텍스트뷰
        memberIdTV.setText(selectMember.nickName);

        // 팔로우중인 멤버인지 여부 판단하기
        isFollow=false;
        for(int i=0;i<loginMember.followings.size();i++){
            if(selectMember.memberNo==loginMember.followings.get(i)){
                isFollow=true;
            }
        }

        followTV = findViewById(R.id.followTV); // 팔로우 유무 텍스트뷰
        if(isFollow){

            followTV.setText("팔로우중\n\n팔로우 취소");
            followTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int i=0;i<loginMember.followings.size();i++){
                        if(selectMember.memberNo==loginMember.followings.get(i)){
                            loginMember.followings.remove(i);
                        }
                    }
                    for(int i=0;i<selectMember.follows.size();i++){
                        if(loginMember.memberNo==selectMember.follows.get(i)){
                            selectMember.follows.remove(i);
                        }
                    }
                    Intent intent1 = new Intent(getApplicationContext(),MemberInfoActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent1.putExtra("selectMember",selectMember);
                    startActivity(intent1);
                }
            });
        }else{
            // 팔로우하기 클릭
            followTV.setText("팔로우하기");
            followTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginMember.followings.add(selectMember.memberNo);
                    selectMember.follows.add(loginMember.memberNo);
                    followTV.setText("팔로우중\n\n팔로우 취소");
                    Intent intent1 = new Intent(getApplicationContext(),MemberInfoActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent1.putExtra("selectMember",selectMember);
                    startActivity(intent1);
                }
            });
        }

        chatRoomCountDatabaseReference = FirebaseDatabase.getInstance().getReference("myAppData").child("chatRoomCount");
        chatRoomCountDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.w("bbb chatRoomCount",dataSnapshot.getValue().toString());
                chatRoomCount = Integer.parseInt(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        startChatIV = findViewById(R.id.startChatIV);
        startChatIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatRoomNo=-1;

//                    ChatRoom chatRoom = new ChatRoom(chatRoomCount,loginMember.memberNo,selectMember.memberNo);

                ChatRoom chatRoom;
                if(loginMember.memberNo>selectMember.memberNo){
                    chatRoom = new ChatRoom(Integer.parseInt(selectMember.memberNo+""+loginMember.memberNo),selectMember.memberNo,loginMember.memberNo);
                }else{
                    chatRoom = new ChatRoom(Integer.parseInt(loginMember.memberNo+""+selectMember.memberNo),loginMember.memberNo,selectMember.memberNo);
                }
                chatRoomCount++;
                chatRoomCountDatabaseReference.setValue(chatRoomCount);
                myAppData.chatRooms.add(chatRoom);
                myAppService.writeAllData(myAppData,MemberInfoActivity.this);
                chatRoomNo = chatRoom.chatRoomNo;
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("myAppData");
                databaseReference.child("chatRooms").child(chatRoom.chatMemberNo0+""+chatRoom.chatMemberNo1).setValue(chatRoom);

                Intent intent = new Intent(getApplicationContext(),MessagingActivity.class);
                intent.putExtra("selectMember",selectMember);
                intent.putExtra("chatRoomNo",chatRoomNo);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        ArrayList<Board> reBoards = new ArrayList<>();
        for(Board board : myAppData.boards){
            if(board.writeMemberNo==selectMember.memberNo){
                reBoards.add(board);
            }
        }

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
                        myAppData = myAppService.readAllData(MemberInfoActivity.this);
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


    } // onResume() 메소드

    @Override
    protected void onPause() {
        super.onPause();
        myAppService.writeAllData(myAppData,this);
    }

    @Override
    public void onEditIconClicked(final int tempBoardNo, final int position) {
//        myAppService.boardUpdateRemoveDialogShow(myAppData,tempBoardNo,boardAdapter,this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MemberInfoActivity.this);
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
                        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(MemberInfoActivity.this);
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

} // MemberInfoActivity 클래스
