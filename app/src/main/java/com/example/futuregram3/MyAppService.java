package com.example.futuregram3;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class MyAppService {
    
    public MyAppService(){} // MyAppService 기본 생성자

//    static MyHandler myHandler = new MyHandler();


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 실시간 데이터베이스 관련 메소드 시작 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

    // 앱 데이터 실시간 데이터베이스에 저장
    public void writeMyAppDataInRealTimeDataBase(MyAppData myAppData){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("myAppData");
        databaseReference.setValue(myAppData);
        databaseReference.child("loginMemberNo").removeValue();
    }

    // 실시간 데이터베이스에서 앱 데이터 읽기
    public MyAppData readMyAppDataInRealTimeDataBase(MyAppData myAppData){
//        MyAppData myAppData=new MyAppData();
        DatabaseReference databaseReference;// ...
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                myAppData = dataSnapshot.getValue(MyAppData.class);
//                myAppData = dataSnapshot.getValue(MyAppData.class);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return myAppData;
    }

    // 실시간 디비에서 가지고온 데이터 확인

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 실시간 데이터베이스 관련 메소드 끝 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 쉐어드 관련 메소드 시작 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // MyAppData 객체에 저장되있는 모든 데이터 로그로 확인하는 메소드
    public void checkMyAppData(MyAppData myAppData){
//        MyAppData myAppData = readAllData(context);
        Log.w("객체를 바로","=================");
        Log.w("myAppData/loginMemberNo",myAppData.loginMemberNo+"");
        for (Member tempMember:myAppData.members) {
            Log.w("myAppData / member/"+tempMember.memberNo,memberToString(tempMember));
        }
        for(Board tempBoard : myAppData.boards){
            Log.w("myAppData / board/"+tempBoard.boardNo,boardToString(tempBoard));
        }
        for(Reply tempReply : myAppData.replies){
            Log.w("myAppData / reply/"+tempReply.replyNo,replyToString(tempReply));
        }
        for(Notification tempNotification : myAppData.notifications){
            Log.w("mAD / notification/"+tempNotification.notificationNo,notificationToString(tempNotification));
        }
        Log.w("mAD/notifis.size()", myAppData.notifications.size()+"");
//        Log.w("mAD / chatRooms.size()", myAppData.chatRooms.size()+"");
        Log.w("mAD / chats.size()", myAppData.chats.size()+"");

    }

    // 쉐어드에 저장되있는 모든 데이터 로그로 확인하는 메소드
    public void checkMyAppData(Context context){
        MyAppData myAppData = readAllData(context);
        Log.w("쉐어드에서 가지고 온것","=================");
        Log.w("myAppData/loginMemberNo",myAppData.loginMemberNo+"");
        for (Member tempMember:myAppData.members) {
//            Log.w("myAppData / members",memberToString(tempMember));
            Log.w("shared / member/"+tempMember.memberNo,memberToString(tempMember));
        }
        for(Board tempBoard : myAppData.boards){
//            Log.w("myAppData / boards",boardToString(tempBoard));
            Log.w("shared / board/"+tempBoard.boardNo,boardToString(tempBoard));
        }

        for(Reply tempReply : myAppData.replies){
//            Log.w("myAppData / replies",replyToString(tempReply));
            Log.w("shared / reply/"+tempReply.replyNo,replyToString(tempReply));
        }
        for(Notification tempNotification : myAppData.notifications){
//            Log.w("myAppData/notifications",notificationToString(tempNotification));
            Log.w("shared/notification/"+tempNotification.notificationNo,notificationToString(tempNotification));
        }
    }

    // 데이터 초기화
    public MyAppData initData(Context context){
        MyAppData myAppData = new MyAppData(0);

        Log.w("하하하","loginMemberNo  "+myAppData.loginMemberNo);
        SharedPreferences sharedPreferences = context.getSharedPreferences("myAppData",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor; // editor : 데이터 저장에 필요하다
        editor = sharedPreferences.edit();
        editor.clear(); // 데이터 완전히 바꾸고 싶을 때
        editor.putString("loginMemberNo",String.valueOf(myAppData.loginMemberNo));
        editor.putInt("memberCount",myAppData.memberCount);
        editor.putInt("boardCount",myAppData.boardCount);
        editor.putInt("replyCount",myAppData.replyCount);
        editor.putInt("notificationCount",myAppData.notificationCount);
        editor.putInt("chatRoomCount",myAppData.chatRoomCount);
        editor.putInt("chatCount",myAppData.chatCount);
        for (int i = 0; i < myAppData.members.size(); i++) {
            editor.putString("member/"+myAppData.members.get(i).memberNo,this.memberToString(myAppData.members.get(i)));
        }
        for (int i = 0; i < myAppData.boards.size(); i++) {
            editor.putString("board/"+myAppData.boards.get(i).boardNo,this.boardToString(myAppData.boards.get(i)));
        }
        for (int i = 0; i < myAppData.replies.size(); i++) {
            editor.putString("reply/"+myAppData.replies.get(i).replyNo,this.replyToString(myAppData.replies.get(i)));
        }
        for (int i = 0; i < myAppData.notifications.size(); i++) {
            editor.putString("notification/"+myAppData.notifications.get(i).notificationNo,this.notificationToString(myAppData.notifications.get(i)));
        }
        for (int i = 0; i < myAppData.chatRooms.size(); i++) {
            editor.putString("chatRoom/"+myAppData.chatRooms.get(i).chatRoomNo,this.chatRoomToString(myAppData.chatRooms.get(i)));
        }
        for (int i = 0; i < myAppData.chats.size(); i++) {
            editor.putString("chat/"+myAppData.chats.get(i).chatNo,this.chatToString(myAppData.chats.get(i)));
        }
        editor.commit();
//        sorting(myAppData);
        return myAppData;
    }

    // 모든 데이터 저장
    public void writeAllData(MyAppData myAppData,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("myAppData",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit(); // editor : 데이터 저장에 필요하다
        editor.putString("loginMemberNo",String.valueOf(myAppData.loginMemberNo));
        editor.putInt("memberCount",myAppData.memberCount);
        editor.putInt("boardCount",myAppData.boardCount);
        editor.putInt("replyCount",myAppData.replyCount);
        editor.putInt("notificationCount",myAppData.notificationCount);
        editor.putInt("chatRoomCount",myAppData.chatRoomCount);
        editor.putInt("chatCount",myAppData.chatCount);
        for (int i = 0; i < myAppData.members.size(); i++) {
            editor.putString("member/"+myAppData.members.get(i).memberNo,this.memberToString(myAppData.members.get(i)));
        }
        for (int i = 0; i < myAppData.boards.size(); i++) {
            editor.putString("board/"+myAppData.boards.get(i).boardNo,this.boardToString(myAppData.boards.get(i)));
        }
        for (int i = 0; i < myAppData.replies.size(); i++) {
            editor.putString("reply/"+myAppData.replies.get(i).replyNo,this.replyToString(myAppData.replies.get(i)));
        }
        if(myAppData.notifications!=null){
            for (int i = 0; i < myAppData.notifications.size(); i++) {
                editor.putString("notification/"+myAppData.notifications.get(i).notificationNo,this.notificationToString(myAppData.notifications.get(i)));
            }
        }
        if(myAppData.chatRooms!=null){
            for (int i = 0; i < myAppData.chatRooms.size(); i++) {
                editor.putString("chatRoom/"+myAppData.chatRooms.get(i).chatRoomNo,this.chatRoomToString(myAppData.chatRooms.get(i)));
            }

        }
        for (int i = 0; i < myAppData.chats.size(); i++) {
            editor.putString("chat/"+myAppData.chats.get(i).chatNo,this.chatToString(myAppData.chats.get(i)));
        }
        editor.commit();
    }

    // 모든 데이터 읽기
    public MyAppData readAllData(Context context){
        MyAppData myAppData = new MyAppData(); // 껍데기 앱 데이터 객체 생성
        int loginMemberNo=-1;
        int memberCount = -1;
        int boardCount = -1;
        int replyCount = -1;
        int notificationCount = -1;
        int chatRoomCount = -1;
        int chatCount = -1;
        ArrayList<Member> members = new ArrayList<>();
        ArrayList<Board> boards = new ArrayList<>();
        ArrayList<Reply> replies = new ArrayList<>();
        ArrayList<Notification> notifications = new ArrayList<>();
        ArrayList<ChatRoom> chatRooms = new ArrayList<>();
        ArrayList<Chat> chats = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("myAppData",MODE_PRIVATE);
        String dataList = "";
        Map<String,?> totalValue = sharedPreferences.getAll();

        for(Map.Entry<String,?> entry : totalValue.entrySet()){
            dataList += entry.getKey()+" : "+entry.getValue().toString();
//            Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"키 : "+entry.getKey()+" ~ 값 : "+entry.getValue());
            String tempKey = entry.getKey();
            String[] tempKeyArr = tempKey.split("/");
            if(tempKeyArr[0].equals("member")){
                Member member = this.stringToMember(entry.getValue().toString());
                members.add(member);
            }else if(tempKeyArr[0].equals("board")){
                Board board = this.stringToBoard(entry.getValue().toString());
                boards.add(board);
            }else if(tempKeyArr[0].equals("reply")){
                Reply reply = this.stringToReply(entry.getValue().toString());
                replies.add(reply);
            }else if(tempKeyArr[0].equals("notification")){
                Notification notification = this.stringToNotification(entry.getValue().toString());
                notifications.add(notification);
            }else if(tempKeyArr[0].equals("chatRoom")){
                ChatRoom chatRoom = this.stringToChatRoom(entry.getValue().toString());
                chatRooms.add(chatRoom);
            }else if(tempKeyArr[0].equals("chat")){
                Chat chat = this.stringToChat(entry.getValue().toString());
                chats.add(chat);
            }else if(tempKeyArr[0].equals("loginMemberNo")){
                loginMemberNo = Integer.parseInt(entry.getValue().toString());
            }else if(tempKeyArr[0].equals("memberCount")){
                memberCount = Integer.parseInt(entry.getValue().toString());
            }else if(tempKeyArr[0].equals("boardCount")){
                boardCount = Integer.parseInt(entry.getValue().toString());
            }else if(tempKeyArr[0].equals("replyCount")){
                replyCount = Integer.parseInt(entry.getValue().toString());
            }else if(tempKeyArr[0].equals("notificationCount")){
                notificationCount = Integer.parseInt(entry.getValue().toString());
            }else if(tempKeyArr[0].equals("chatRoomCount")){
                chatRoomCount = Integer.parseInt(entry.getValue().toString());
            }else if(tempKeyArr[0].equals("chatCount")){
                chatCount = Integer.parseInt(entry.getValue().toString());
            }
        }

        myAppData.loginMemberNo = loginMemberNo;
        myAppData.members = members;
        myAppData.boards = boards;
        myAppData.replies = replies;
        myAppData.notifications = notifications;
        myAppData.chatRooms = chatRooms;
        myAppData.chats = chats;

        myAppData.memberCount = memberCount;
        myAppData.boardCount = boardCount;
        myAppData.replyCount = replyCount;
        myAppData.notificationCount = notificationCount;
        myAppData.chatRoomCount = chatRoomCount;
        myAppData.chatCount = chatCount;

        myAppData = sorting(myAppData);

        return myAppData;
    }

    // 목록 데이터 정렬 메소드
    public MyAppData sorting(MyAppData myAppData){
        ArrayList<Member> tempMembers = myAppData.members;
        for (int i = 0; i < tempMembers.size()-1; i++) {
            for (int j = 1; j < tempMembers.size(); j++) {
                if(tempMembers.get(j-1).memberNo > tempMembers.get(j).memberNo){
                    Member tempMember = tempMembers.get(j-1);
                    tempMembers.set(j-1,tempMembers.get(j));
                    tempMembers.set(j,tempMember);
                }
            }
        }
        ArrayList<Board> tempBoards = myAppData.boards;
        for (int i = 0; i < tempBoards.size()-1; i++) {
            for (int j = 1; j < tempBoards.size(); j++) {
                if(tempBoards.get(j-1).boardNo < tempBoards.get(j).boardNo){
                    Board tempBoard = tempBoards.get(j-1);
                    tempBoards.set(j-1,tempBoards.get(j));
                    tempBoards.set(j,tempBoard);
                }
            }
        }
        ArrayList<Reply> tempReplies = myAppData.replies;
        for (int i = 0; i < tempReplies.size()-1; i++) {
            for (int j = 1; j < tempReplies.size(); j++) {
                if(tempReplies.get(j-1).replyNo > tempReplies.get(j).replyNo){
                    Reply tempReply = tempReplies.get(j-1);
                    tempReplies.set(j-1,tempReplies.get(j));
                    tempReplies.set(j,tempReply);
                }
            }
        }
        ArrayList<Notification> tempNotifications = myAppData.notifications;
        for (int i = 0; i < tempNotifications.size()-1; i++) {
            for (int j = 1; j < tempNotifications.size(); j++) {
                if(tempNotifications.get(j-1).notificationNo < tempNotifications.get(j).notificationNo){
                    Notification tempNotification = tempNotifications.get(j-1);
                    tempNotifications.set(j-1,tempNotifications.get(j));
                    tempNotifications.set(j,tempNotification);
                }
            }
        }

        ArrayList<Chat> tempChats = myAppData.chats;
        for (int i = 0; i < tempChats.size()-1; i++) {
            for (int j = 1; j < tempChats.size(); j++) {
                if(tempChats.get(j-1).chatNo > tempChats.get(j).chatNo){
                    Chat tempChat = tempChats.get(j-1);
                    tempChats.set(j-1,tempChats.get(j));
                    tempChats.set(j,tempChat);
                }
            }
        }
        return myAppData;
    }

    // 멤버 객체를 저장하는 메소드
    public void writeMemberData(Member member,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("myAppData",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("member/"+member.memberNo,this.memberToString(member)).commit();
    }

    // 멤버를 수정하는 메소드
    public void updateMemberData(Member member, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("myAppData",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("member/"+member.memberNo,this.memberToString(member)).commit();
    }

    // 멤버를 삭제하는 메소드
    public void deleteMemberData(Member member,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("myAppData",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("member/"+member.memberNo).commit();
    }

    // 멤버 객체를 문자열로 바꾸는 메소드
    public String memberToString(Member member){
        String memberToString="";
        String followsMemberNoToString="";
        String followingsMemberNoToString="";
        memberToString = member.memberNo +"|"+member.phone+"|"+member.password+"|"+member.nickName+"|"+member.profileImage;
        if(member.follows!=null){
            for (int i = 0; i < member.follows.size(); i++) {
                if(member.follows.size()-1!=i){
                    followsMemberNoToString = followsMemberNoToString + member.follows.get(i) + ",";
                }else{
                    followsMemberNoToString = followsMemberNoToString + member.follows.get(i);
                }
            }
            memberToString = memberToString + "| "+followsMemberNoToString;
        }else{
            memberToString = memberToString + "| ";
        }
        if(member.followings!=null){
            for (int i = 0; i < member.followings.size(); i++) {
                if(member.followings.size()-1!=i){
                    followingsMemberNoToString = followingsMemberNoToString + member.followings.get(i) + ",";
                }else{
                    followingsMemberNoToString = followingsMemberNoToString + member.followings.get(i);
                }
            }
            memberToString = memberToString +"| "+followingsMemberNoToString;
        }else {
            memberToString = memberToString + "| ";
        }

//        memberToString = memberToString + "| "+followsMemberNoToString+"| "+followingsMemberNoToString;
        return memberToString;
    }

    // 멤버 문자열을 멤버 객체로 바꾸는 메소드
    public Member stringToMember(String memberStr){
        String[] strArr = memberStr.split("\\|");
        String followsStr;
        String followingsStr;
        ArrayList<Integer> follows = new ArrayList<>();
        ArrayList<Integer> followings = new ArrayList<>();

        followsStr = strArr[5];
        if(!strArr[5].equals(" ")){
            String[] followsStrArr = followsStr.split(",");
            for (int i = 0; i < followsStrArr.length; i++) {
                follows.add(Integer.parseInt(followsStrArr[i].trim()));
            }
        }
        followingsStr = strArr[6];
        if(!strArr[6].equals(" ")){
            String[] followingsStrArr = followingsStr.split(",");
            for (int i = 0; i < followingsStrArr.length; i++) {
                followings.add(Integer.parseInt(followingsStrArr[i].trim()));
            }
        }
        Member findMember
                = new Member(Integer.parseInt(strArr[0]),strArr[1],strArr[2],strArr[3]);
        findMember.profileImage = strArr[4];
        findMember.follows = follows;
        findMember.followings = followings;
        return findMember;
    }

    // 게시글 객체를 저장하는 메소드
    public void writeBoardData(Board board,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("myAppData",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("board/"+board.boardNo,this.boardToString(board)).commit();
        editor.putInt("boardCount",board.boardNo+1);
    }

    // 게시글을 삭제하는 메소드
    public void deleteBoardData(Board board,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("myAppData",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("board/"+board.boardNo).commit();
    }

    // 게시글을 수정하는 메소드
    public void updateBoardData(Board board, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("myAppData",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("board/"+board.boardNo,this.boardToString(board)).commit();
    }

    // 게시글 객체를 문자열로 바꾸는 메소드
    public String boardToString(Board board){
        String boardToString="";
        String likeMembers="";
        boardToString = board.boardNo +"|"+board.boardContent+"|"+board.boardImage+"|"+board.writeMemberNo+"|"+board.writeTime;
        if(board.likeMembers!=null){
            for (int i = 0; i < board.likeMembers.size(); i++) {
                if(board.likeMembers.size()-1!=i){
                    likeMembers = likeMembers + board.likeMembers.get(i) + ",";
                }else{
                    likeMembers = likeMembers + board.likeMembers.get(i);
                }
            }
            boardToString = boardToString+"| "+likeMembers;
        }else {
            boardToString = boardToString+"| ";
        }
        return boardToString;
    }

    // 게시글 문자열을 게시글 객체로 바꾸는 메소드
    public Board stringToBoard(String boardStr){
        String[] strArr = boardStr.split("\\|");
        ArrayList<Integer> likeMembers = new ArrayList<>();
        String likeMembersStr = strArr[5];
        if(!strArr[5].equals(" ")){
            String[] likeMembersStrArr = likeMembersStr.split(",");
            for (int i = 0; i < likeMembersStrArr.length; i++) {
                likeMembers.add(Integer.parseInt(likeMembersStrArr[i].trim()));
            }
        }
        Board findBoard
                = new Board(Integer.parseInt(strArr[0]),strArr[1],strArr[2],Integer.parseInt(strArr[3]));
        findBoard.writeTime = strArr[4];
        findBoard.likeMembers = likeMembers;
        return findBoard;
    }

    // 댓글 객체를 저장하는 메소드
    public void writeReplyData(Reply reply,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("myAppData",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("reply/"+reply.replyNo,this.replyToString(reply)).commit();
    }

    // 댓글 객체를 삭제하는 메소드
    public void deleteReplyData(Reply reply,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("myAppData",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("reply/"+reply.replyNo).commit();
    }

    // 댓글 객체를 문자열로 바꾸는 메소드
    public String replyToString(Reply reply){
        String replyToString=reply.replyBoardNo +"|"+reply.replyNo+"|"+reply.replyContent+
                "|"+reply.replyMemberNo+"|"+reply.replyTime;
        return replyToString;
    }

    // 댓글 문자열을 댓글 객체로 바꾸는 메소드
    public Reply stringToReply(String replyStr){
        String[] strArr = replyStr.split("\\|");
        Reply findReply
                = new Reply(Integer.parseInt(strArr[0]),Integer.parseInt(strArr[1]),
                    strArr[2],Integer.parseInt(strArr[3]));
        findReply.replyTime = strArr[4];
        return findReply;
    }

    // 알람 객체를 저장하는 메소드
    public void writeNotificationData(Notification notification,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("myAppData",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("notification/"+notification.notificationNo,this.notificationToString(notification)).commit();
    }

    // 알람 객체를 삭제하는 메소드
    public void deleteNotificationData(Notification notification,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("myAppData",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("notification/"+notification.notificationNo).commit();
    }

    // 알람 객체를 문자열로 바꾸는 메소드
    public String notificationToString(Notification notification){
        String notificationToString=notification.notifiedMemberNo +"|"+notification.notificationNo+
                "|"+notification.makeNotificationMemberNo+"|"+notification.makeNotificationBoardNo+
                "|"+notification.notificationType+"|"+notification.notificationTime;
        return notificationToString;
    }

    // 알람 문자열을 알람 객체로 바꾸는 메소드
    public Notification stringToNotification(String notificationStr){
        String[] strArr = notificationStr.split("\\|");
        Notification findNotification
                = new Notification(Integer.parseInt(strArr[0]),Integer.parseInt(strArr[1]),
                                    Integer.parseInt(strArr[2]),Integer.parseInt(strArr[3]),Integer.parseInt(strArr[4]));
        findNotification.notificationTime = strArr[5];
        return findNotification;
    }

    // 채팅 방 객체를 문자열로 바꾸는 메소드
    public String chatRoomToString(ChatRoom chatRoom){
        String chatRoomToString = chatRoom.chatRoomNo+"|"+chatRoom.chatMemberNo0+"|"+chatRoom.chatMemberNo1;
        return chatRoomToString;
    }

    // 채팅 방 문자열을 채팅 방 객체로 바꾸는 메소드
    public ChatRoom stringToChatRoom(String chatRoomStr){
        String[] strArr = chatRoomStr.split("\\|");
//        int[] chatMemberNoArr = {Integer.parseInt(strArr[1]),Integer.parseInt(strArr[2])};
        ChatRoom findChatRoom = new ChatRoom(Integer.parseInt(strArr[0]),Integer.parseInt(strArr[1]),Integer.parseInt(strArr[2]));
        return findChatRoom;
    }

    // 채팅 객체를 문자열로 바꾸는 메소드
    public String chatToString(Chat chat){
        String chatToString =chat.chatNo+"|"+chat.chatRoomNo+"|"+chat.chatMemberNo+"|"+chat.chatContent+"|"+chat.chatTime;
        return chatToString;
    }

    // 채팅 문자열을 채팅 객체로 바꾸는 메소드
    public Chat stringToChat(String chatStr){
        String[] strArr = chatStr.split("\\|");
        Chat findChat = new Chat(Integer.parseInt(strArr[0]),Integer.parseInt(strArr[1]),Integer.parseInt(strArr[2]),strArr[3]);
        findChat.chatTime = strArr[4];
        return findChat;
    }

    // 자동로그인 체크 메소드
    public int autoLoginCheck(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("myAppData",Context.MODE_PRIVATE);
        String loginMemberNo = sharedPreferences.getString("loginMemberNo","-1");
        return Integer.parseInt(loginMemberNo);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 쉐어드 관련 메소드 끝 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

    // 게시글 번호로 게시글 객체를 찾는 메소드
    public Board findBoardByBoardNo(MyAppData myAppData, int boardNo){
//        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"findBoardByBoardNo() : myAppData.boards.size() : "+myAppData.boards.size());
//        for (int i = 0; i < myAppData.boards.size(); i++) {
//            Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"findBoardByBoardNo() : 보드들 다 뽑아봐 : "+myAppData.boards.get(i).boardNo);
//        }
        Board findBoard=null;
        for (Board tempBoard : myAppData.boards){
            if(tempBoard.boardNo==boardNo){
                findBoard=tempBoard;
            }
        }
//        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"findBoardByBoardNo() : findBoard.boardNo : "+findBoard.boardNo);
        return findBoard;
    }

    // 멤버번호로 멤버 찾는 메소드 (멤버객체 리턴)
    public Member findMemberByMemberNo(MyAppData myAppData, int memberNo){
        Member findMember=null;
        for (Member tempMember: myAppData.members) {
            if(tempMember.memberNo==memberNo){
                findMember = tempMember;
            }
        }
        return findMember;
    }

    // 핸드폰번호로 멤버 찾는 메소드 (멤버객체 리턴)
    public Member findMemberByPhoneNum(MyAppData myAppData, String phoneNum){
        Member findMember=null;
        for (Member tempMember: myAppData.members) {
            if(tempMember.phone.equals(phoneNum)){
                findMember = tempMember;
            }
        }
        return findMember;
    }

    // 닉네임으로 멤버 찾는 메소드 (멤버객체 리턴)
    public Member findMemberByNickName(MyAppData myAppData, String nickName){
        Member findMember=null;
        for (Member tempMember: myAppData.members) {
            if(tempMember.nickName.equals(nickName)){
                findMember = tempMember;
            }
        }
        return findMember;
    }

    // 로그인 메소드 (멤버 번호 리턴)
    public int login(MyAppData myAppData, String inputPhoneNum, String password){
        int loginMemberNo=-1;
        Member tempMember = findMemberByPhoneNum(myAppData,inputPhoneNum);
        if(tempMember !=null){
            if(inputPhoneNum.equals(tempMember.phone) && password.equals(tempMember.password)){
                loginMemberNo = tempMember.memberNo;
            }
        }
        return loginMemberNo;
    }

    // 휴대전화로 로그인하기 메소드 (멤버 번호 리턴)
    public int loginByPhone(MyAppData myAppData, String inputPhoneNum){
        int loginMemberNo=-1;
        Member tempMember = findMemberByPhoneNum(myAppData,inputPhoneNum);
        if(tempMember !=null){
            if(inputPhoneNum.equals(tempMember.phone)){
                loginMemberNo = tempMember.memberNo;
            }
        }
        return loginMemberNo;
    }

    // 인증번호 생성 메소드
    public String makeAuthenticationNum(){
        Random r = new Random();
        String result="";
        for (int i = 0; i < 4; i++) {
            result = result + (String.valueOf(r.nextInt(10)));
        }
        return result;
    }

    // 전화번호 검사와 이미 계정이 있는지 체크 메소드
    public int checkThisPhoneNum(MyAppData myAppData, String inputPhoneNum){
        int phoneNumCheck=0;

        if(findMemberByPhoneNum(myAppData,inputPhoneNum) != null){
            // 이미 회원이 있는 경우
            phoneNumCheck=1;
            return phoneNumCheck;
        }
        if(inputPhoneNum.length()!=11){
            // 전화번호가 11자리가 아닌경우
            phoneNumCheck=2;
        }else{
            if(!inputPhoneNum.substring(0,3).equals("010")){
                phoneNumCheck=2;
            }
        }
        return phoneNumCheck;
    }

    // 닉네임 영문(소문자), 숫자조합인지 체크
    public boolean checkNickNameInputOnlyNumberAndAlphabet(String textInput) {
        char chrInput;
        for (int i = 0; i < textInput.length(); i++) {
            chrInput = textInput.charAt(i); // 입력받은 텍스트에서 문자 하나하나 가져와서 체크
            if (chrInput >= 0x61 && chrInput <= 0x7A) {
                // 영문(소문자) OK!
            }else if (chrInput >= 0x30 && chrInput <= 0x39) {
                // 숫자 OK!
            }else {
                return false;   // 영문자도 아니고 숫자도 아님!
            }
        }
        return true;
    }

    // 닉네임 중복 검사 메소드
    public boolean checkDuplicateNickName(MyAppData myAppData, String inputNickName){
        boolean isDuplicate = false;
        if(findMemberByNickName(myAppData,inputNickName) != null){
            isDuplicate=true;
        }
        return isDuplicate;
    }

    // 닉네임 글자수 검사 메소드 (닉네임은 3 ~ 8자)
    public boolean checkNickNameLength(String inputNickName){
        boolean isOk = true;
        if(inputNickName.length()<3){
            isOk=false;
        }
        if(inputNickName.length()>8){
            isOk = false;
        }
        return isOk;
    }

    // 비밀번호 조건 검사 메소드
    public boolean checkPassword(String inputPassword){
        boolean isPasswordOk = true;
        // 비밀번호는 3자리 이상 8자리 이하
        if(inputPassword.length()<3){
            isPasswordOk=false;
        }
        if(inputPassword.length()>8){
            isPasswordOk=false;
        }
        return isPasswordOk;
    }

    // 비밀번호와 비밀번호 확인 일치 검사 메소드
    public boolean checkPasswordCheck(String inputPassword, String inputPasswordCheck){
        boolean isPasswordCheckOk = true;
        if(!inputPasswordCheck.equals(inputPassword)){
            isPasswordCheckOk=false;
        }
        return isPasswordCheckOk;
    }

    // 회원가입 메소드
    public void join(MyAppData myAppData, String phoneNum, String password, String nickName,int memberCount,Context context){
        Member joinMember = new Member(myAppData.memberCount,phoneNum,password,nickName);
        myAppData.memberCount++;
        myAppData.members.add(joinMember);
        writeAllData(myAppData,context);
    }

    // Calendar를 년월일시분초로 반환 메소드
    public String timeToString(Calendar time) {
		String timeToString = (time.get(Calendar.YEAR)) + "." + (time.get(Calendar.MONTH) + 1) + "."
				+ (time.get(Calendar.DAY_OF_MONTH)) + "  " + (time.get(Calendar.HOUR_OF_DAY)) + "시 "
				+ (time.get(Calendar.MINUTE))+"분";
//        String timeToString = (time.get(Calendar.MONTH) + 1) + "월 "
//                + (time.get(Calendar.DAY_OF_MONTH)) + "일 " + (time.get(Calendar.HOUR_OF_DAY)) + "시 "
//                + (time.get(Calendar.MINUTE)) + "분 " + (time.get(Calendar.SECOND)) + "초";
        return timeToString.substring(2);
    }


    // 게시글 수정/삭제 다이얼로그 보여주는 메소드
//    public void boardUpdateRemoveDialogShow(final MyAppData myAppData, final int tempBoardNo, final BoardAdapter boardAdapter,final Context context) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("게시글을 수정하거나 삭제할 수 있습니다.");
//        builder.setMessage("");
//        builder.setPositiveButton("수정",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(context.getApplicationContext(),BoardUpdateActivity.class);
//                        intent.putExtra("tempBoardNo",tempBoardNo);
//                        context.startActivity(intent);
//                    }
//                });
//        builder.setNegativeButton("삭제",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(context);
//                        deleteBuilder.setTitle("정말로 삭제하시겠습니까?");
//                        deleteBuilder.setMessage("");
//                        deleteBuilder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Board tempBoard = MyAppService.this.findBoardByBoardNo(myAppData,tempBoardNo);
//
//                                // 알림 객체를 제거해주는 메소드
//                                for (Notification notification : myAppData.notifications){
//                                    if(notification.makeNotificationBoardNo==tempBoardNo){
//                                        deleteNotificationData(notification,context);
////                                        myAppData.notifications.remove(notification);
//                                    }
//                                }
//
//                                boardAdapter.removeItem(tempBoard,position);
//                                myAppData.boards.remove(tempBoard);
//                                deleteBoardData(tempBoard,context);
////                                a(myAppData,context);
//                            }
//                        });
//                        deleteBuilder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        });
//                        deleteBuilder.show();
//                    }
//                });
//        builder.setNeutralButton("취소",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
//        builder.show(); // 다이얼로그 보이게 하기
//
//    }
//
//    public void a(MyAppData myAppData,Context context){
//        myAppData = readAllData(context);
//    }


    public void boardUpdateRemoveInSingleBoardDialogShow(final MyAppData myAppData, final int tempBoardNo, final Context context, final Activity boardSingleActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("게시글을 수정하거나 삭제할 수 있습니다.");
        builder.setMessage("");
        builder.setPositiveButton("수정",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context.getApplicationContext(),BoardUpdateActivity.class);
                        intent.putExtra("isBoardSingleActivity",true);
                        intent.putExtra("tempBoardNo",tempBoardNo);
                        context.startActivity(intent);
                    }
                });
        builder.setNegativeButton("삭제",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(context);
                        deleteBuilder.setTitle("정말로 삭제하시겠습니까?");
                        deleteBuilder.setMessage("");
                        deleteBuilder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Board tempBoard = MyAppService.this.findBoardByBoardNo(myAppData,tempBoardNo);
                                myAppData.boards.remove(tempBoard);

                                // 알람 객체를 제거해주는 메소드
                                for (Notification notification : myAppData.notifications){
                                    if(notification.makeNotificationBoardNo==tempBoardNo){
                                        deleteNotificationData(notification,context);
                                    }
                                }
//                                int tempNotificationsSize = myAppData.notifications.size();
//                                for (int j = 0; j < tempNotificationsSize; j++) {
//                                    if(myAppData.notifications.get(j).makeNotificationBoardNo==tempBoardNo){
//                                        myAppData.notifications.remove(j);
//                                    }
//                                }

                                deleteBoardData(tempBoard,context);
                                boardSingleActivity.finish();
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

    public int isChatRoom(MyAppData myAppData, int memberNo0, int memberNo1){
        for(ChatRoom chatRoom : myAppData.chatRooms){
            if( ((chatRoom.chatMemberNo0==memberNo0) || (chatRoom.chatMemberNo0==memberNo1))
            && ((chatRoom.chatMemberNo1==memberNo0) || (chatRoom.chatMemberNo1==memberNo1))){
                return chatRoom.chatRoomNo;
            }
        }
        return -1;
    }





} // MyAppService 클래스
