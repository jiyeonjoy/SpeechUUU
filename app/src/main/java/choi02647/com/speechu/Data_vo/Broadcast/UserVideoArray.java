package choi02647.com.speechu.Data_vo.Broadcast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 *  [ MYMENU 화면의 내가 방송한 영상 목록을 저장할 데이터 클래스 ]
 *
 */

public class UserVideoArray {
    @SerializedName("myVideo")
    @Expose
    private ArrayList<StreamListVO> myVideoArray;   // 내가 방송한 영상 목록

    public UserVideoArray() {
    }

    public UserVideoArray(ArrayList<StreamListVO> myVideoArray) {
        this.myVideoArray = myVideoArray;
    }

    public ArrayList<StreamListVO> getMyVideoArray() {
        return myVideoArray;
    }

    public void setMyVideoArray(ArrayList<StreamListVO> myVideoArray) {
        this.myVideoArray = myVideoArray;
    }
}
