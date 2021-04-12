package com.example.futuregram3;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.content.Intent;
        import android.net.Uri;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.google.gson.Gson;

        import org.jetbrains.annotations.NotNull;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.Iterator;

        import okhttp3.Call;
        import okhttp3.Callback;
        import okhttp3.MediaType;
        import okhttp3.OkHttpClient;
        import okhttp3.Request;
        import okhttp3.RequestBody;
        import okhttp3.Response;

public class MessagingActivity extends AppCompatActivity {

    MyAppService myAppService;
    MyAppData myAppData;
    Member loginMember;
    Member selectMember;
    int chatRoomNo;

    TextView chatMemberTV;
    ImageView chatMemberProfileImageIV;
    EditText chatET;
    ImageView registerChatIconIV;

    ArrayList<Chat> dbChats;
    ArrayList<Chat> reChats;
    RecyclerView chatRecyclerView;
    MessagingAdapter messagingAdapter;

    Chat chat;
    DatabaseReference chatsDatabaseReference;
    DatabaseReference chatCountDatabaseReference;
    int chatCount;

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        myAppService = new MyAppService();







    } // onCreate() 메소드

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        selectMember = (Member)intent.getSerializableExtra("selectMember");
        chatRoomNo = intent.getIntExtra("chatRoomNo",-1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        myAppData = myAppService.readAllData(this);

        loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo);
        Intent intent = getIntent();
        selectMember = (Member)intent.getSerializableExtra("selectMember");
        chatRoomNo = intent.getIntExtra("chatRoomNo",-1);

        if(0<=loginMember.memberNo && loginMember.memberNo<7){
        }else {
            Toast.makeText(getApplicationContext(),"회원님은 채팅이 불가합니다.",Toast.LENGTH_SHORT).show();
            finish();
        }
        if(0<=selectMember.memberNo && selectMember.memberNo<7){
        }else {
            Toast.makeText(getApplicationContext(),"해당 멤버는 채팅이 불가합니다.",Toast.LENGTH_SHORT).show();
            finish();
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("myAppData").child("members").child(String.valueOf(selectMember.memberNo));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                while(child.hasNext()){
                    DataSnapshot tempDataSnapshot = child.next();
                    if(tempDataSnapshot.getKey().equals("pushToken")){
                        Log.w("fff","여기는 파이어베이스에서 받아오는 곳   "+tempDataSnapshot.getValue().toString());
                        token = tempDataSnapshot.getValue().toString();
                        return;
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
        });




        ImageView myProfileImageIV = findViewById(R.id.myProfileImageIV); // 로그인 멤버 프로필사진 이미지뷰
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

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        chatMemberProfileImageIV = findViewById(R.id.chatMemberProfileImageIV);
        if(selectMember.profileImage.substring(0,1).equals("c")){
            chatMemberProfileImageIV.setImageURI(Uri.parse(selectMember.profileImage));
        }else{
            chatMemberProfileImageIV.setImageResource(Integer.parseInt(selectMember.profileImage));
        }

        chatMemberTV = findViewById(R.id.chatMemberTV); // 누구랑 대화 텍스트뷰
        chatMemberTV.setText(selectMember.nickName+"와(과)의 대화");

        reChats = new ArrayList<>();
//        for(Chat chat : myAppData.chats){
//            if(chatRoomNo == chat.chatRoomNo){
//                reChats.add(chat);
//            }
//        }

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(MessagingActivity.this)); // 이건 좀 알아보자 나중에
        messagingAdapter =new MessagingAdapter(reChats,MessagingActivity.this);
        chatRecyclerView.setAdapter(messagingAdapter); // 댓글 recyclerview adapter 적용

        chatET = findViewById(R.id.chatET);
        registerChatIconIV = findViewById(R.id.registerChatIconIV);
        registerChatIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chatET.getText().toString().trim().length()!=0){
                    chat = new Chat(chatCount,chatRoomNo,loginMember.memberNo,chatET.getText().toString());
                    chatCount++;
                    chatET.setText("");
                    chatsDatabaseReference.child(String.valueOf(chatCount-1)).setValue(chat);
                    chatCountDatabaseReference.setValue(chatCount);

                    Log.w("fff","여기는 등록하기 전   "+token);
                    if(chat.chatContent.length()>20){
                        sendGcm(loginMember.nickName,token,chat.chatContent.substring(0,20)+"...", String.valueOf(chatRoomNo), String.valueOf(loginMember.memberNo));
                    }else{
                        sendGcm(loginMember.nickName,token,chat.chatContent, String.valueOf(chatRoomNo), String.valueOf(loginMember.memberNo));
                    }

                }else {
                    Toast.makeText(getApplicationContext(),"대화를 입력해주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });


        chatsDatabaseReference = FirebaseDatabase.getInstance().getReference("myAppData").child("chats");
        chatsDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.w("aaa  mAD.chatCount-1" ,myAppData.chatCount-1+"");
                Log.w("aaa  dataSnap.getValue" ,dataSnapshot.getValue()+"");

                Chat chat = dataSnapshot.getValue(Chat.class);
                if(chat!=null){
                    Log.w("aaa","채팅번호 : "+chat.chatNo+" / 채팅내용 : "+chat.chatContent +" / 채팅보낸 멤버 : "+chat.chatMemberNo+"   채팅방 번호 : "+chat.chatRoomNo);
                    if(chat.chatRoomNo==chatRoomNo){
                        messagingAdapter.addItem(chat);
                    }
//                    myAppData.chats.add(chat); //
//                    myAppService.writeAllData(myAppData,MessagingActivity.this); // 쉐어드에 저장
                    chatRecyclerView.scrollToPosition(reChats.size()-1);
                }
            }
            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        //~~~~~~~~~~~~~~~~~~~~~~

        chatCountDatabaseReference = FirebaseDatabase.getInstance().getReference("myAppData").child("chatCount");
        chatCountDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.w("bbb addValue",dataSnapshot.getValue().toString());
                chatCount = Integer.parseInt(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    } // onResume() 메소드

    @Override
    protected void onPause() {
        super.onPause();
        myAppService.writeAllData(myAppData,this);
    }

    void sendGcm(String chatMemberNickName,String token, String chatContent, String chatRoomNo, String selectMemberNo){

        Log.w("fcm","sendGcm()메소드 호출됨");
        Log.w("fcm","chatMemberNickName : "+chatMemberNickName);
        Log.w("fcm","token : "+token);
        Log.w("fcm","chatContent : "+chatContent);

        Gson gson = new Gson();

        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = token;
        notificationModel.data.title = chatMemberNickName;
        notificationModel.data.text = chatContent + "¡"+chatRoomNo+ "¡"+selectMemberNo;

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));

        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization","key=AAAAYiJcoiw:APA91bE-rL31YUgy9T4RTMuN-z3XjKXFeBFNM3RoYrdch6JHs4E4N_q6APotb9tMP8Epsl9Fp_f29Y04hILArWLclYrb4NouyvYsRP4PHqkswlieU4OpaUlJqHZdJ4eMfbUrhpmKsBDt")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();
//                .url("http://gcm-http.googleapis.com/gcm/send")


        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });
    }
} // MessagingActivity 클래스
