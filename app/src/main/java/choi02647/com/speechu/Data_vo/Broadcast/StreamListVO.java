package choi02647.com.speechu.Data_vo.Broadcast;

import com.google.gson.annotations.SerializedName;

/**
 *  [ HOME 화면, MY MENU 화면의 Stream List 한 개의 데이터 클래스 ]
 *
 */

public class StreamListVO {
    @SerializedName("streamList_number")
    private int streamList_number;          // 데이터베이스의 인덱스
    @SerializedName("hostID")
    private String hostID;                  // 영상주인 아이디
    @SerializedName("hostName")
    private String hostName;                // 영상주인 이름
    @SerializedName("hostImg")
    private String hostImg;                 // 영상주인 프로필사진
    @SerializedName("streamTitle")
    private String streamTitle;             // 영상 제목
    @SerializedName("streamPath")
    private String streamPath;              // 서버의 영상 경로
    @SerializedName("vodTag")
    private int vodTag;                     // 0: 라이브 방송, 1: VOD
    @SerializedName("vodThumbnail")
    private String vodThumbnail;            // 영상 썸네일
    @SerializedName("startTime")
    private long startTime;            // 영상 시작 시간

    public StreamListVO() {
    }

    public StreamListVO(int streamList_number, String hostID, String hostName, String hostImg,
                        String streamTitle, String streamPath, int vodTag, String vodThumbnail, long startTime) {
        this.streamList_number = streamList_number;
        this.hostID = hostID;
        this.hostName = hostName;
        this.hostImg = hostImg;
        this.streamTitle = streamTitle;
        this.streamPath = streamPath;
        this.vodTag = vodTag;
        this.vodThumbnail = vodThumbnail;
        this.startTime = startTime;
    }

    public int getStreamList_number() {
        return streamList_number;
    }

    public void setStreamList_number(int streamList_number) {
        this.streamList_number = streamList_number;
    }

    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostImg() {
        return hostImg;
    }

    public void setHostImg(String hostImg) {
        this.hostImg = hostImg;
    }

    public String getStreamTitle() {
        return streamTitle;
    }

    public void setStreamTitle(String streamTitle) {
        this.streamTitle = streamTitle;
    }

    public String getStreamPath() {
        return streamPath;
    }

    public void setStreamPath(String streamPath) {
        this.streamPath = streamPath;
    }

    public int getVodTag() {
        return vodTag;
    }

    public void setVodTag(int vodTag) {
        this.vodTag = vodTag;
    }

    public String getVodThumbnail() {
        return vodThumbnail;
    }

    public void setVodThumbnail(String vodThumbnail) {
        this.vodThumbnail = vodThumbnail;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }


}

