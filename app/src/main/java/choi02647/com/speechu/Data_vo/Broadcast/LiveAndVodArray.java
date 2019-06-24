package choi02647.com.speechu.Data_vo.Broadcast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 *  [ 로그인 후 넘어오는 메인 화면 (Main_Home_Fragment) 에서 라이브 방송 목록과 VOD 목록을 담을 데이터 클래스 ]
 *
 *  - 데이터베이스의 streamList 테이블에서 라이브 방송 목록과 VOD 목록을 모두 가져와서 이 클래스에 담는다
 */

public class LiveAndVodArray {
    @SerializedName("live_list")
    @Expose
    private ArrayList<StreamListVO> livelistArray;  // 라이브 목록
    @SerializedName("vod_list")
    @Expose
    private ArrayList<StreamListVO> vodlistArray;   // VOD 목록

    public LiveAndVodArray() {
    }

    public LiveAndVodArray(ArrayList<StreamListVO> livelistArray, ArrayList<StreamListVO> vodlistArray) {
        this.livelistArray = livelistArray;
        this.vodlistArray = vodlistArray;
    }

    public ArrayList<StreamListVO> getLivelistArray() {
        return livelistArray;
    }

    public void setLivelistArray(ArrayList<StreamListVO> livelistArray) {
        this.livelistArray = livelistArray;
    }

    public ArrayList<StreamListVO> getVodlistArray() {
        return vodlistArray;
    }

    public void setVodlistArray(ArrayList<StreamListVO> vodlistArray) {
        this.vodlistArray = vodlistArray;
    }

}
