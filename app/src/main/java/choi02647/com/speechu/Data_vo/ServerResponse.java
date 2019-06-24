package choi02647.com.speechu.Data_vo;

import com.google.gson.annotations.SerializedName;

/**
 *  [ 데이터베이스에 insert, update, delete 하거나 데이터베이스에 있는 정보를 확인할때 반환해주는 true, false 값 저장 ]
 */

public class ServerResponse {
    @SerializedName("success")
    private int success;
    @SerializedName("message")
    private String message;
    @SerializedName("userImg")
    private String userImg;
    @SerializedName("userName")
    private String userName;


    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getUserImg() {
        return userImg;
    }

    public String getUserName() {
        return userName;
    }

}