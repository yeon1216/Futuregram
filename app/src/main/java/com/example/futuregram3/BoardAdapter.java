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

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.ViewHolder> {

    Context context;
    private ArrayList<Board> boards = null;
    private BoardRecyclerViewClickListener boardRecyclerViewClickListener;
    MyAppService myAppService;
    MyAppData myAppData;
    Member loginMember;
//    Board tempBoard; // 현재 포지션의 게시글

    // 생성자에서 데이터 리스트 객체를 전달받음
    BoardAdapter(ArrayList<Board> boards, MyAppData myAppData,Context context){
        this.context = context;
        this.boards = boards;
        this.myAppService = new MyAppService();
        this.myAppData = myAppService.readAllData(context);
        int loginMemberNo=myAppData.loginMemberNo;
        this.loginMember = myAppService.findMemberByMemberNo(myAppData,loginMemberNo);
        for(Board board : myAppData.boards){
            Log.w("kkk 전체 데이터","게시글 번호 : "+board.boardNo+", 게시글 내용 : "+board.boardContent);
        }
        for(Board board : boards){
            Log.w("kkk 가공데이터","게시글 번호 : "+board.boardNo+", 게시글 내용 : "+board.boardContent);
        }
    }

    // 뷰홀더를 만들고 그 뷰홀더에 아이템 레이아웃을 만들자
    @NonNull
    @Override
    public BoardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        context = parent.getContext(); // context를 얻어옴
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // inflater 구현
        View view = inflater.inflate(R.layout.recyclerview_board_item,parent,false); // inflater로 뷰를 객체로 만듬
        BoardAdapter.ViewHolder viewHolder = new BoardAdapter.ViewHolder(view); // 뷰홀더 객체 생성
        return viewHolder; // 뷰홀더 반환
    }

    // 뷰와 데이터를 연결하는 메소드
    @Override
    public void onBindViewHolder(@NonNull BoardAdapter.ViewHolder holder, int position) {
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onBindViewHolder() 메소드 호출");
        if(holder.getAdapterPosition()!=RecyclerView.NO_POSITION){
            Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onBindViewHolder() : boards.size() : "+boards.size());
            Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onBindViewHolder() : getAdapterPosition() : "+holder.getAdapterPosition());
            Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),
                    "onBindViewHolder() : boards.size()-1-getAdapterPosition() : "+(boards.size()-1-holder.getAdapterPosition()));
            Board tempBoard = boards.get(holder.getAdapterPosition());
            Log.w("kkk","tempBoard.writeMemberNo : "+tempBoard.writeMemberNo);

//            Member tempBoardMember = tempBoard.writeMember;
            Member tempBoardMember = myAppService.findMemberByMemberNo(myAppData,tempBoard.writeMemberNo);
            Log.w("kkk","tempBoardMember.memberNo : "+tempBoardMember.memberNo);

            // 여기서 내용과 장소 분리하자 시작

            String[] strArr = tempBoard.boardContent.split("¡");

            // 여기서 내용과 장소 분리하자 끝

            String boardContent = strArr[0]+"";

            if(strArr.length>1){
                Log.w("map3","strArr[1] : "+strArr[1]);
                if(strArr[1].trim().length()>0){
                    String registerLocation = strArr[1];
                    String []splitStr = registerLocation.split(",");
                    String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2)+""; // 주소
                    Log.w("map3","address : "+address);
                    if(address.length()!=0){
                        if(address.length()>25){
                            holder.registerLocationTV.setVisibility(View.VISIBLE);
                            holder.registerLocationTV.setText("장소 : "+address.substring(0,25)+"...");
                        }else {
                            holder.registerLocationTV.setVisibility(View.VISIBLE);
                            holder.registerLocationTV.setText("장소 : "+address);
                        }
                    }else{
                        holder.registerLocationTV.setVisibility(View.GONE);
                    }
                }
            }

            int likeCount = tempBoard.likeMembers.size();
            int replyCount = 0;
            for (Reply tempReply : myAppData.replies){
                if(tempReply.replyBoardNo==tempBoard.boardNo){
                    replyCount++;
                }
            }

            if(tempBoardMember.profileImage.substring(0,1).equals("c")){
                holder.memberProfileImageIV.setImageURI(Uri.parse(tempBoardMember.profileImage));
            }else{
                holder.memberProfileImageIV.setImageResource(Integer.parseInt(tempBoardMember.profileImage));
            }
            holder.memberIdTV.setText(tempBoardMember.nickName);
            if(loginMember.memberNo==tempBoardMember.memberNo){
                holder.editIconIV.setVisibility(EditText.VISIBLE);
            }else{
                holder.editIconIV.setVisibility(EditText.GONE);
            }

            if(tempBoard.boardImage.substring(0,1).equals("c")){
                holder.boardImageIV.setImageURI(Uri.parse(tempBoard.boardImage));
            }else{
                holder.boardImageIV.setImageResource(Integer.parseInt(tempBoard.boardImage));
            }

            holder.likeCountTV.setText("좋아요 "+likeCount+"명");

            // 멤버가 이 게시글을 좋아요 했는지 체크하는 코드
            boolean isLike=false;
            ArrayList<Integer> tempBoardLikeMemberList = tempBoard.likeMembers;
            for (int i = 0; i < tempBoardLikeMemberList.size(); i++) {
                if(loginMember.memberNo==tempBoardLikeMemberList.get(i)){
                    isLike=true;
                }
            }

            if(isLike){
                holder.emptyHartIconIV.setVisibility(ImageView.GONE);
                holder.redHartIconIV.setVisibility(ImageView.VISIBLE);
            }else{
                holder.redHartIconIV.setVisibility(ImageView.GONE);
                holder.emptyHartIconIV.setVisibility(ImageView.VISIBLE);
            }

            if(boardContent.trim().length()==0){
                holder.boardContentTV.setVisibility(TextView.GONE);
            }else{
                holder.boardContentTV.setVisibility(TextView.VISIBLE);
                if(boardContent.length()>200){
                    holder.boardContentTV.setText(boardContent.substring(0,200));
                    holder.boardContentDetailTV.setVisibility(TextView.VISIBLE);
                }else{
                    holder.boardContentTV.setText(boardContent);
                    holder.boardContentDetailTV.setVisibility(TextView.GONE);
                }
             }

            if(replyCount==0){
                holder.replyCountTV.setVisibility(TextView.GONE);
            }else{
                holder.replyCountTV.setVisibility(TextView.VISIBLE);
                holder.replyCountTV.setText(replyCount+"개 댓글 모두보기...");
            }

//            holder.boardTimeTV.setText("글 번호 : "+tempBoard.boardNo+"         "+tempBoard.writeTime);
            holder.boardTimeTV.setText(tempBoard.writeTime);
        }
    }

    // 아이템의 갯수를 반환
    @Override
    public int getItemCount() {
        return boards.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView memberProfileImageIV; // 게시글 멤버 프로필 이미지 이미지뷰
        TextView memberIdTV; // 게시글 멤버 아이디 텍스트뷰
        ImageView editIconIV; // 게시글 편집 아이콘 이미지뷰
        ImageView boardImageIV; // 게시글 이미지 이미지뷰
        ImageView emptyHartIconIV; // 빈 하트 아이콘 이미지뷰
        ImageView redHartIconIV; // 빨강 하트 아이콘 이미지뷰
        ImageView replyIconIV; // 댓글 아이콘 이미지뷰;
        TextView likeCountTV; // 좋아요 갯수 텍스트뷰
        TextView boardContentTV; // 게시글 내용 텍스트뷰
        TextView registerLocationTV; // 등록 장소 텍스트뷰
        TextView boardContentDetailTV; // 게시글 자세히보기 텍스트뷰
        TextView replyCountTV; // 댓글 갯수 텍스트뷰
        TextView boardTimeTV; // 게시글 시간 텍스트뷰


        // ViewHolder 생성자
        ViewHolder(View itemView){
            super(itemView);
            memberProfileImageIV = itemView.findViewById(R.id.memberProfileImageIV); // 게시글 멤버 프로필 이미지 이미지뷰
            memberIdTV = itemView.findViewById(R.id.memberIdTV); // 게시글 멤버 아이디 텍스트뷰
            editIconIV = itemView.findViewById(R.id.editIconIV); // 게시글 편집 아이콘 이미지뷰
            boardImageIV = itemView.findViewById(R.id.boardImageIV); // 게시글 이미지 이미지뷰
            emptyHartIconIV = itemView.findViewById(R.id.emptyHartIconIV); // 빈 하트 아이콘 이미지뷰
            redHartIconIV = itemView.findViewById(R.id.redHartIconIV); // 빨강 하트 아이콘 이미지뷰
            replyIconIV = itemView.findViewById(R.id.replyIconIV); // 댓글 아이콘 이미지뷰;
            likeCountTV = itemView.findViewById(R.id.likeCountTV); // 좋아요 갯수 텍스트뷰
            boardContentTV = itemView.findViewById(R.id.boardContentTV); // 게시글 내용 텍스트뷰
            registerLocationTV = itemView.findViewById(R.id.registerLocationTV); // 등록 장소 텍스트뷰
            boardContentDetailTV = itemView.findViewById(R.id.boardContentDetailTV); // 게시글 자세히보기 텍스트뷰
            replyCountTV = itemView.findViewById(R.id.replyCountTV); // 댓글 갯수 텍스트뷰
            boardTimeTV = itemView.findViewById(R.id.boardTimeTV); // 게시글 시간 텍스트뷰


            // 멤버 아이디 클릭시 이벤트
            memberIdTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition()!=RecyclerView.NO_POSITION){
                        Board tempBoard = boards.get(getAdapterPosition());
                        Member selectMember = myAppService.findMemberByMemberNo(myAppData,tempBoard.writeMemberNo);
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


            // 게시글 리싸이클러뷰 클릭 리스너가 있다면
            if(boardRecyclerViewClickListener != null){
                // 편집 아이콘 클릭시 이벤트
                editIconIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(getAdapterPosition()!=RecyclerView.NO_POSITION){
                            Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"편집아이콘 클릭 : boards.get(getAdapterPosition().boardNo) : "+boards.get(getAdapterPosition()).boardNo);
                            int tempBoardNo = boards.get(getAdapterPosition()).boardNo;
                            boardRecyclerViewClickListener.onEditIconClicked(tempBoardNo,getAdapterPosition());
                        }
                    }
                });
            } // 게시글 리싸이클러뷰 클릭 리스너가 있다면 (편집 아이콘 클릭)

            // 좋아요 갯수 클릭시 이벤트
            likeCountTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition()!=RecyclerView.NO_POSITION){
                        int tempBoardNo = boards.get(getAdapterPosition()).boardNo;
                        Intent intent = new Intent(context,MemberListActivity.class);
                        intent.putExtra("tempBoardNo",tempBoardNo);
                        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"좋아요 클릭 : tempBoardNo : "+tempBoardNo);
                        intent.putExtra("screenSelect",0); // 0: 좋아요 목록, 1: 팔로우 목록, 2: 팔로잉 목록
                        context.startActivity(intent);
                    }
                }
            });

            // 게시글 내용 클릭시 이벤트
            boardContentDetailTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition()!=RecyclerView.NO_POSITION){
                        Board tempBoard = boards.get(getAdapterPosition());
                        Intent intent = new Intent(context.getApplicationContext(),BoardSingleActivity.class);
                        intent.putExtra("tempBoard",tempBoard);
                        context.startActivity(intent);

                    }
                }
            });

            // 댓글 아이콘 클릭시 이벤트
            replyIconIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition() != RecyclerView.NO_POSITION){
                        int tempBoardNo = boards.get(getAdapterPosition()).boardNo;
                        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"댓글 아이콘 클릭 : tempBoardNo : "+tempBoardNo);

                        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"댓글 아이콘 클릭 : getAdapterPosition() : "+getAdapterPosition());
                        Intent intent = new Intent(context,ReplyActivity.class);
                        intent.putExtra("tempBoardNo",tempBoardNo);
                        context.startActivity(intent);
                    }
                }
            });

            emptyHartIconIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition()!=RecyclerView.NO_POSITION){
                        Board tempBoard = boards.get(getAdapterPosition());
                        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"빈 하트 : tempBoardNo : "+tempBoard.boardNo);
                        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"빈 하트 : holder.getAdapterPosition() : "+getAdapterPosition());
                        emptyHartIconIV.setVisibility(ImageView.GONE);
                        redHartIconIV.setVisibility(ImageView.VISIBLE);

                        tempBoard.likeMembers.add(0,loginMember.memberNo);

                        if(tempBoard.writeMemberNo!=loginMember.memberNo){
                            // 글 작성자가 로그인멤버가 아니면 알람 추가
                            Member tempMember = myAppService.findMemberByMemberNo(myAppData,tempBoard.writeMemberNo);
                            Notification tempNotification = new Notification(tempMember.memberNo,myAppData.notificationCount,loginMember.memberNo,tempBoard.boardNo,1);
                            myAppData.notificationCount++;
                            myAppData.notifications.add(tempNotification);
                        }
                        notifyItemChanged(getAdapterPosition());

                    }
                }
            });

            redHartIconIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition() != RecyclerView.NO_POSITION){
                        Board tempBoard = boards.get(getAdapterPosition());
                        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"레드 하트 : tempBoardNo : "+tempBoard.boardNo);
                        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"레드 하트 : holder.getAdapterPosition() : "+getAdapterPosition());
                        redHartIconIV.setVisibility(ImageView.GONE);
                        emptyHartIconIV.setVisibility(ImageView.VISIBLE);
                        for (int i = 0; i < tempBoard.likeMembers.size(); i++) {
                            if(loginMember.memberNo==tempBoard.likeMembers.get(i)){
                                tempBoard.likeMembers.remove(i);
                                break;
                            }
                        }

                        // 좋아요 취소시 알람 객체를 제거해주는 메소드
                        for (Notification notification : myAppData.notifications){
                            if(notification.makeNotificationMemberNo==loginMember.memberNo){
                                myAppService.deleteNotificationData(notification,context);
                                myAppData.notifications.remove(notification);
                                break;
                            }
                        }

                        notifyItemChanged(getAdapterPosition());
                    }
                }
            });

            replyCountTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition() != RecyclerView.NO_POSITION){
                        //                        int tempBoardNo = myAppService.findBoardByBoardNo(myAppData,boards.size()-1-getAdapterPosition()).boardNo;
                        int tempBoardNo = boards.get(getAdapterPosition()).boardNo;
                        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"댓글 아이콘 클릭 : tempBoardNo : "+tempBoardNo);
                        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"댓글 아이콘 클릭 : getAdapterPosition() : "+getAdapterPosition());
                        Intent intent = new Intent(context,ReplyActivity.class);
                        intent.putExtra("tempBoardNo",tempBoardNo);
                        context.startActivity(intent);
                    }
                }
            });

            registerLocationTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition()!=RecyclerView.NO_POSITION){
                        Board tempBoard = boards.get(getAdapterPosition());
                        Intent intent = new Intent(context.getApplicationContext(),BoardSingleActivity.class);
                        intent.putExtra("tempBoard",tempBoard);
                        context.startActivity(intent);
                    }
                }
            });

            boardImageIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition()!=RecyclerView.NO_POSITION){
                        Board tempBoard = boards.get(getAdapterPosition());
                        Intent intent = new Intent(context.getApplicationContext(),BoardSingleActivity.class);
                        intent.putExtra("tempBoard",tempBoard);
                        context.startActivity(intent);
                    }
                }
            });

        }
    } // ViewHolder 클래스

    // 리스너를 받아오는 코드
    public void setOnBoardRecyclerViewClickListener(BoardRecyclerViewClickListener boardRecyclerViewClickListener){
        this.boardRecyclerViewClickListener = boardRecyclerViewClickListener;
    }

    public interface BoardRecyclerViewClickListener{
        void onEditIconClicked(int tempBoardNo, int position); // 편집 아이콘 클릭 이벤트
//        void onMemberIdClicked(int tempBoardNo); // 멤버 아이디 클릭 이벤트
    }

    public void removeItem(Board tempBoard,int position){
        Log.w("ccccc","removeItem() 호출됨");
        Log.w("ccccc","position : "+position);
//        int removeBoardPosition = boards.indexOf(tempBoard);
//        Log.w("ccccc","removeBoardPosition : "+removeBoardPosition);
        for (int i = 0; i < boards.size(); i++) {
            Log.w("ccccc","리스트 인덱스 : "+i+", 게시글 번호 : "+boards.get(i).boardNo+", 게시글 내용 : "+boards.get(i).boardContent);
        }
//        boards.remove(removeBoardPosition);
//        notifyItemRemoved(removeBoardPosition);
        boards.remove(position);
        notifyItemRemoved(position);
    }


} // ReplyAdapter 클래스
