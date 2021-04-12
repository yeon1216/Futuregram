package com.example.futuregram3;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MessagingAdapter extends RecyclerView.Adapter<MessagingAdapter.ViewHolder> {

    Context context;
    private ArrayList<Chat> chats;
    MyAppService myAppService;
    MyAppData myAppData;
    Member loginMember;

    // 생성자에서 데이터 리스트 객체를 전달받음
    MessagingAdapter(ArrayList<Chat> chats, Context context){
        this.context = context;
        this.chats = chats;
        this.myAppService = new MyAppService();
        this.myAppData = myAppService.readAllData(context);
        this.loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo);
    }

    // 뷰홀더를 만들고 그 뷰홀더에 아이템 레이아웃을 만들자
    @NonNull
    @Override
    public MessagingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // inflater 구현
        View view = inflater.inflate(R.layout.recyclerview_messaging_item,parent,false); // inflater로 뷰를 객체로 만듬
        MessagingAdapter.ViewHolder viewHolder = new MessagingAdapter.ViewHolder(view); // 뷰홀더 객체 생성
        return viewHolder; // 뷰홀더 반환
    }

    // 뷰와 데이터를 연결하는 메소드
    @Override
    public void onBindViewHolder(@NonNull MessagingAdapter.ViewHolder holder, int position) {
        if(holder.getAdapterPosition()!=RecyclerView.NO_POSITION){
            Chat tempChat = chats.get(holder.getAdapterPosition());
            if(loginMember.memberNo==tempChat.chatMemberNo){
                holder.loginMemberChatTV.setVisibility(TextView.VISIBLE);
                holder.chatMemberChatTV.setVisibility(TextView.GONE);
                holder.loginMemberChatTimeTV.setVisibility(TextView.VISIBLE);
                holder.chatMemberChatTimeTV.setVisibility(TextView.GONE);
                holder.loginMemberChatTV.setText(tempChat.chatContent);
                holder.loginMemberChatTimeTV.setText(tempChat.chatTime);
            }else{
                holder.loginMemberChatTV.setVisibility(TextView.GONE);
                holder.chatMemberChatTV.setVisibility(TextView.VISIBLE);
                holder.loginMemberChatTimeTV.setVisibility(TextView.GONE);
                holder.chatMemberChatTimeTV.setVisibility(TextView.VISIBLE);
                holder.chatMemberChatTV.setText(tempChat.chatContent);
                holder.chatMemberChatTimeTV.setText(tempChat.chatTime);
            }
        }
    } // onBindViewHolder() 메소드

    // 아이템의 갯수를 반환
    @Override
    public int getItemCount() {
        return chats.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView loginMemberChatTV; // 로그인 멤버 채팅 텍스트뷰
        TextView chatMemberChatTV; // 상대 멤버 채팅 텍스트뷰
        TextView loginMemberChatTimeTV; // 로그인 멤버 시간 텍스트뷰
        TextView chatMemberChatTimeTV; // 로그인 멤버 시간 텍스트뷰

        // ViewHolder 생성자
        ViewHolder(View itemView){
            super(itemView);

            loginMemberChatTV = itemView.findViewById(R.id.loginMemberChatTV); // 로그인 멤버 채팅 텍스트뷰
            chatMemberChatTV = itemView.findViewById(R.id.chatMemberChatTV); // 상대 멤버 채팅 텍스트뷰
            loginMemberChatTimeTV = itemView.findViewById(R.id.loginMemberChatTimeTV); // 로그인멤버 시간 텍스트뷰
            chatMemberChatTimeTV = itemView.findViewById(R.id.chatMemberChatTimeTV); // 상대멤버 시간 텍스트뷰

        } // ViewHolder 생성자

    } // ViewHolder 클래스

    public void addItem(Chat chat){
        chats.add(chat);
//        notifyItemChanged(chats.size());
//        notifyDataSetChanged();
        notifyItemInserted(chats.size()-1);
    }


} // ReplyAdapter 클래스
