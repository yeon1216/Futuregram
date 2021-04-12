package com.example.futuregram3;

import java.io.Serializable;

public class ChatRoom implements Serializable {
    public int chatRoomNo;
    public int chatMemberNo0;
    public int chatMemberNo1;

    public ChatRoom(){}

    public ChatRoom(int chatRoomNo, int chatMemberNo0, int chatMemberNo1){
        this.chatRoomNo = chatRoomNo;
        this.chatMemberNo0 = chatMemberNo0;
        this.chatMemberNo1 = chatMemberNo1;
    }

}
