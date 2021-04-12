package com.example.futuregram3;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> {

    Context context;
    private ArrayList<Member> members = null;
    MyAppService myAppService;
    MyAppData myAppData;
    Member loginMember;
    boolean isMessageActivity; // 메시지 Activity인지 확인하는 곳
    int chatRoomCount; // 파이어베이스에서 받아 올 채팅 방 갯수
    DatabaseReference chatRoomCountDatabaseReference; // 채팅방 갯수를 가지고올 디비레퍼런스
    int chatRoomNo; // 채팅 방 번호
    Member tempMember; // 현재 멤버

    // 생성자에서 데이터 리스트 객체를 전달받음
    MemberListAdapter(ArrayList<Member> members,Context context){
        this.context = context;
        this.members = members;
        this.myAppService = new MyAppService();
        myAppData = myAppService.readAllData(context);
        loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo);
    }

    // 생성자에서 데이터 리스트 객체를 전달받음
    MemberListAdapter(ArrayList<Member> members,Context context, boolean isMessageActivity){
        this.isMessageActivity = isMessageActivity;
        this.context = context;
        this.members = members;
        this.myAppService = new MyAppService();
        myAppData = myAppService.readAllData(context);
        loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo);
    }

    @NonNull
    @Override
    public MemberListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // inflater 구현
        View view = inflater.inflate(R.layout.recyclerview_member_list_item,parent,false); // 뷰 객체 생성
        MemberListAdapter.ViewHolder viewHolder = new MemberListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(holder.getAdapterPosition() != RecyclerView.NO_POSITION){
            Member tempMember = members.get(holder.getAdapterPosition());
            if(tempMember.profileImage.substring(0,1).equals("c")){
                holder.memberProfileImageIV.setImageURI(Uri.parse(tempMember.profileImage));
            }else{
                holder.memberProfileImageIV.setImageResource(Integer.parseInt(tempMember.profileImage));
            }
            holder.memberIdTV.setText(tempMember.nickName);
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView memberProfileImageIV; // 멤버의 프로필 사진 이미지뷰
        TextView memberIdTV; // 멤버 아이디 텍스트뷰
        TextView startChatTV; // 대화시작 텍스트뷰

        // ViewHolder 생성자
        ViewHolder(View itemView){
            super(itemView);

            memberProfileImageIV = itemView.findViewById(R.id.memberProfileImageIV); // 멤버의 프로필 사진 이미지뷰
            memberIdTV = itemView.findViewById(R.id.memberIdTV); // 멤버 아이디 텍스트뷰
            startChatTV = itemView.findViewById(R.id.startChatTV); // 대화시작 텍스트뷰

            // 멤버 아이디 텍스트뷰 클릭시 이벤트 (해당 멤버 화면으로 이동)
            memberIdTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition()!=RecyclerView.NO_POSITION){
                        Member tempMember = members.get(getAdapterPosition());
                        if(tempMember.memberNo!=loginMember.memberNo){
                            Intent intent = new Intent(context.getApplicationContext(),MemberInfoActivity.class);
                            intent.putExtra("selectMember",tempMember);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            context.startActivity(intent);
                        }else{
                            Intent intent = new Intent(context.getApplicationContext(),MyInfoActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            context.startActivity(intent);
                        }
                    }
                }
            });

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




            if(isMessageActivity){
                startChatTV.setVisibility(View.VISIBLE);
                startChatTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(getAdapterPosition()!=RecyclerView.NO_POSITION){
                            tempMember = members.get(getAdapterPosition());

                            Intent intent = new Intent(context.getApplicationContext(),MemberInfoActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            intent.putExtra("selectMember",tempMember);
                            intent.putExtra("isStartChat",true);
                            context.startActivity(intent);

//                            DatabaseReference chatRoomsDatabaseReference = FirebaseDatabase.getInstance().getReference("myAppData").child("chatRooms");
//                            chatRoomsDatabaseReference.addChildEventListener(new ChildEventListener() {
//                                @Override
//                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                                    Log.w("cccc","어ㅏ어랑너리얼");
//                                    Log.w("cccloginMember.memberNo",loginMember.memberNo+"");
//                                    Log.w("ccctempMember.memberNo",tempMember.memberNo+"");
//                                    ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
//                                    if(loginMember.memberNo>tempMember.memberNo){
//                                        if(chatRoom.chatMemberNo0==tempMember.memberNo && chatRoom.chatMemberNo1==loginMember.memberNo){
//                                            chatRoomNo= chatRoom.chatRoomNo;
//                                        }
//                                    }else{
//                                        if(chatRoom.chatMemberNo0==loginMember.memberNo && chatRoom.chatMemberNo1==tempMember.memberNo){
//                                            chatRoomNo= chatRoom.chatRoomNo;
//                                        }
//                                    }
//                                }
//                                @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
//                                @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
//                                @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
//                                @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
//                            });
//
//                            Intent intent = new Intent(context.getApplicationContext(),MessagingActivity.class);
//                            intent.putExtra("selectMember",tempMember);
//                            intent.putExtra("chatRoomNo",chatRoomNo);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                            context.startActivity(intent);
                        }
                    }
                });
            }



        } // ViewHolder 생성자

    } // ViewHolder 클래스

    public void addItem(Member member){
        members.add(member);
        notifyItemInserted(members.size()-1);
    }

} // MemberListAdapter 클래스
