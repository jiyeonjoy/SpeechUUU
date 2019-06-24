package choi02647.com.speechu.Data_vo.Chatting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import choi02647.com.speechu.Data_vo.Broadcast.StreamListVO;

/**
 */
public class ChatArray {
    @SerializedName("chat_list")
    @Expose
    private ArrayList<ChatItemVO> chatArray;   // 내가 방송한 영상 목록

    public ChatArray() {
    }

    public ChatArray(ArrayList<ChatItemVO> chatArray) {
        this.chatArray = chatArray;
    }

    public ArrayList<ChatItemVO> getChatArray() {
        return chatArray;
    }

    public void setChatArray(ArrayList<ChatItemVO> chatArray) {
        this.chatArray = chatArray;
    }
}

