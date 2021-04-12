package com.example.futuregram3;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    Context context;
    private ArrayList<Notification> notifications = null;
    MyAppService myAppService;
    MyAppData myAppData;
    Member loginMember;

    // 생성자에서 데이터 리스트 객체를 전달받음
    NotificationAdapter(ArrayList<Notification> notifications, Context context){
        this.context = context;
        this.notifications = notifications;
        this.myAppService = new MyAppService();
        this.myAppData = myAppService.readAllData(context);
        this.loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo);

    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // inflater 구현
        View view = inflater.inflate(R.layout.recyclerview_notification_item,parent,false); // 뷰 객체 생성
        NotificationAdapter.ViewHolder viewHolder = new NotificationAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        if(holder.getAdapterPosition() != RecyclerView.NO_POSITION){
            Notification tempNotification = notifications.get(holder.getAdapterPosition());
            Member makeNotificationMember=myAppService.findMemberByMemberNo(myAppData,tempNotification.makeNotificationMemberNo);
            Board makeNotificationBoard=myAppService.findBoardByBoardNo(myAppData,tempNotification.makeNotificationBoardNo);
            String profileImage = makeNotificationMember.profileImage;
            String memberId = makeNotificationMember.nickName;
            int notificationType = tempNotification.notificationType;
            String boardImage=null;
            if(makeNotificationBoard!=null){
                boardImage = makeNotificationBoard.boardImage;
            }
            if(profileImage.substring(0,1).equals("c")){
                holder.profileImageIV.setImageURI(Uri.parse(profileImage));
            }else{
                holder.profileImageIV.setImageResource(Integer.parseInt(profileImage));
            }
            holder.memberIdTV.setText(memberId);
            switch (notificationType){
                case 0: // 0. 게시글을 작성한 경우
                    holder.notificationTypeTV.setText("새로운 게시글을 작성하였습니다.");
                    if(boardImage.substring(0,1).equals("c")){
                        holder.boardImageIV.setImageURI(Uri.parse(boardImage));
                    }else{
                        holder.boardImageIV.setImageResource(Integer.parseInt(boardImage));
                    }
                    break;
                case 1: // 1. 누군가가 내 게시글에 좋아요를 누른 경우
                    holder.notificationTypeTV.setText("님이 회원님의 게시글을 좋아합니다.");
                    if(boardImage.substring(0,1).equals("c")){
                        holder.boardImageIV.setImageURI(Uri.parse(boardImage));
                    }else{
                        holder.boardImageIV.setImageResource(Integer.parseInt(boardImage));
                    }
                    break;
                case 2: // 2. 누군가가 내 게시글에 댓글을 단 경우
                    holder.notificationTypeTV.setText("님이 댓글을 남겼습니다.");
                    if(boardImage.substring(0,1).equals("c")){
                        holder.boardImageIV.setImageURI(Uri.parse(boardImage));
                    }else{
                        holder.boardImageIV.setImageResource(Integer.parseInt(boardImage));
                    }
                    break;
                case 3: // 3. 누군가가 나를 팔로우 한 경우
                    holder.notificationTypeTV.setText("님이 회원님을 팔로우 합니다.");
                    break;
            }

            holder.notificationTimeTV.setText(tempNotification.notificationTime);

        }
    } // onBindViewHolder() 메소드

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView profileImageIV; // 프로필 이미지 이미지뷰
        TextView memberIdTV; // 멤버 아이디 텍스트뷰
        TextView notificationTypeTV; // 알람 유형 텍스트뷰
        ImageView boardImageIV; // 게시글 이미지 이비지뷰
        TextView notificationTimeTV; // 알람 시간 텍스트뷰

        // ViewHolder 생성자
        ViewHolder(View itemView){
            super(itemView);

            profileImageIV = itemView.findViewById(R.id.profileImageIV); // 프로필 이미지 이미지뷰
            memberIdTV = itemView.findViewById(R.id.memberIdTV); // 멤버 아이디 텍스트뷰
            notificationTypeTV = itemView.findViewById(R.id.notificationTypeTV); // 알람 유형 텍스트뷰
            boardImageIV = itemView.findViewById(R.id.boardImageIV); // 게시글 이미지 이비지뷰
            notificationTimeTV = itemView.findViewById(R.id.notificationTimeTV); // 알람 시간 텍스트뷰

            memberIdTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition()!=RecyclerView.NO_POSITION){
                        Notification tempNotification = notifications.get(getAdapterPosition());
                        Member selectMember = myAppService.findMemberByMemberNo(myAppData,tempNotification.makeNotificationMemberNo);
                        if(selectMember.memberNo!=loginMember.memberNo){
                            Intent intent = new Intent(context.getApplicationContext(),MemberInfoActivity.class);
                            intent.putExtra("selectMember",selectMember);
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

            boardImageIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition()!=RecyclerView.NO_POSITION){
                        Notification tempNotification = notifications.get(getAdapterPosition());
                        Board tempBoard = myAppService.findBoardByBoardNo(myAppData,tempNotification.makeNotificationBoardNo);
                        Intent intent = new Intent(context.getApplicationContext(),BoardSingleActivity.class);
                        intent.putExtra("tempBoard",tempBoard);
                        context.startActivity(intent);
                    }
                }
            });

        }

    } // ViewHolder 클래스

}
