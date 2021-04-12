package com.example.futuregram3;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    Context context;
    private ArrayList<Reply> tempBoardReplies;
    MyAppService myAppService;
    MyAppData myAppData;
    Member loginMember;

    int isNeedDetail; // 댓글 더보기가 필요한지 여부 ( 0: 댓글이 100글자 이내임, 1: 댓글 더보기... 상태, 2: 댓글 숨기기... 상태)

    // 생성자에서 데이터 리스트 객체를 전달받음
    ReplyAdapter(ArrayList<Reply> tempBoardReplies, Context context){
        this.context = context;
        this.tempBoardReplies = tempBoardReplies;
        if (tempBoardReplies.size() != 0) {
            Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"댓글 아이콘 클릭 : tempBoardNo : "+tempBoardReplies.get(0).replyBoardNo);
        }
        this.myAppService = new MyAppService();
        this.myAppData = myAppService.readAllData(context);
        this.loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo);
    }

    // 뷰홀더를 만들고 그 뷰홀더에 아이템 레이아웃을 만들자
    @NonNull
    @Override
    public ReplyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // inflater 구현
        View view = inflater.inflate(R.layout.recyclerview_reply_item,parent,false); // inflater로 뷰를 객체로 만듬
        ReplyAdapter.ViewHolder viewHolder = new ReplyAdapter.ViewHolder(view); // 뷰홀더 객체 생성
        return viewHolder; // 뷰홀더 반환
    }

    // 뷰와 데이터를 연결하는 메소드
    @Override
    public void onBindViewHolder(@NonNull ReplyAdapter.ViewHolder holder, int position) {
        if(holder.getAdapterPosition()!=RecyclerView.NO_POSITION){
            Reply tempReply = tempBoardReplies.get(holder.getAdapterPosition());
            Member tempReplyMember = myAppService.findMemberByMemberNo(myAppData,tempReply.replyMemberNo);
            if(tempReplyMember.profileImage.substring(0,1).equals("c")){
                holder.replyMemberProfileImage.setImageURI(Uri.parse(tempReplyMember.profileImage));
            }else{
                holder.replyMemberProfileImage.setImageResource(Integer.parseInt(tempReplyMember.profileImage));
            } // 댓글 멤버 프로필 이미지 적용
            holder.replyMemberIdTV.setText(tempReplyMember.nickName); // 댓글 멤버 아이디

            isNeedDetail=0;
            if(tempReply.replyContent.length()>100){
                holder.replyContentTV.setText(tempReply.replyContent.substring(0,100)+"\n더보기...");
                isNeedDetail=1;
            }else{
                holder.replyContentTV.setText(tempReply.replyContent); // 댓글 내용
                isNeedDetail=0;
            }

            holder.replyTimeTV.setText(tempReply.replyTime); // 댓글 시간 적용
        }
    }

    // 아이템의 갯수를 반환
    @Override
    public int getItemCount() {
        return tempBoardReplies.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView replyMemberProfileImage; // 댓글 멤버 이미지뷰
        TextView replyMemberIdTV; // 댓글 멤버 아이디 텍스트뷰
        TextView replyContentTV; // 댓글 내용 텍스트뷰
        TextView replyTimeTV; // 댓글 시간 텍스트뷰

        // ViewHolder 생성자
        ViewHolder(View itemView){
            super(itemView);
            replyMemberProfileImage = itemView.findViewById(R.id.replyMemberProfileImage);  // 댓글 멤버 이미지뷰
            replyMemberIdTV = itemView.findViewById(R.id.replyMemberIdTV); // 댓글 멤버 아이디 텍스트뷰
            replyContentTV = itemView.findViewById(R.id.replyContentTV); // 댓글 내용 텍스트뷰
            replyTimeTV = itemView.findViewById(R.id.replyTimeTV); // 댓글 시간 텍스트뷰

            // 댓글을 작성한 멤버 닉네임 클릭시 이벤트
            replyMemberIdTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition()!=RecyclerView.NO_POSITION){
                        Reply tempReply = tempBoardReplies.get(getAdapterPosition());
                        Member selectMember = myAppService.findMemberByMemberNo(myAppData,tempReply.replyMemberNo);
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

            replyContentTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition()!=RecyclerView.NO_POSITION){
                        Reply tempReply = tempBoardReplies.get(getAdapterPosition());
                        if(isNeedDetail==1){
                            replyContentTV.setText(tempReply.replyContent +"\n숨기기...");
                            isNeedDetail=2;
                        }else if(isNeedDetail==2){
                            replyContentTV.setText(tempReply.replyContent.substring(0,100)+"\n더보기...");
                            isNeedDetail=1;
                        }
                    }
                }
            });

        } // ViewHolder 생성자

    } // ViewHolder 클래스


} // ReplyAdapter 클래스
