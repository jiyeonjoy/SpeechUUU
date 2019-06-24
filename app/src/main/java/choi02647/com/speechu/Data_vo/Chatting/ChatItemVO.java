package choi02647.com.speechu.Data_vo.Chatting;

import com.google.gson.annotations.SerializedName;

public class ChatItemVO {
    @SerializedName("sendUser")
    private String sendUser;
    @SerializedName("sendUserName")
    private String sendUserName;
    @SerializedName("sendProfile")
    private String sendProfile;
    @SerializedName("message")
    private String message;
    @SerializedName("syncTime")
    private long syncTime;

    public ChatItemVO() {
    }

    public ChatItemVO(String sendUser, String sendUserName, String sendProfile, String message, long syncTime) {
        this.sendUser = sendUser;
        this.sendUserName = sendUserName;
        this.sendProfile = sendProfile;
        this.message = message;
        this.syncTime = syncTime;
    }

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public String getSendProfile() {
        return sendProfile;
    }

    public void setSendProfile(String sendProfile) {
        this.sendProfile = sendProfile;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSyncTime() {
        return syncTime - 8000;
    }

    public void setSyncTime(long syncTime) {
        this.syncTime = syncTime;
    }
}
