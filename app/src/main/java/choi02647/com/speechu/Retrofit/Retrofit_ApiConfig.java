package choi02647.com.speechu.Retrofit;


import java.util.List;

import choi02647.com.speechu.Data_vo.Broadcast.LiveAndVodArray;
import choi02647.com.speechu.Data_vo.Broadcast.UserVideoArray;
import choi02647.com.speechu.Data_vo.Chatting.ChatArray;
import choi02647.com.speechu.Data_vo.Chatting.ChatItemVO;
import choi02647.com.speechu.Data_vo.ServerResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 *  [ Retrofit 과 통신할 PHP 파일들을 선언하고 어떤 형태의 데이터가 넘어가고, 어떤 데이터를 받는지 지정하는 클래스 파일 ]
 */

public interface Retrofit_ApiConfig {

    /* 로그인 시 입력된 아이디와 비밀번호가 맞는지 확인하기 위해 데이터베이스 이동 */
    @FormUrlEncoded
    @POST("login.php")
    Call<ServerResponse> check_Login(@Field("loginId") String loginId,
                                     @Field("loginPw") String loginPw);


    /* 방송 시작 후 데이터베이스에 방송 추가 */
    @FormUrlEncoded
    @POST("insert_liveList.php")
    Call<ServerResponse> insert_liveList(@Field("loginId") String loginId,
                                         @Field("loginName") String loginName,
                                         @Field("loginImg") String loginImg,
                                         @Field("liveTitle") String liveTitle,
                                         @Field("livePath") String livePath,
                                         @Field("vodTag") int vodTag,
                                         @Field("vod_thumb") String vodThumb,
                                         @Field("liveStart") long liveStart);


    /* 생방송 종료 후에 streamList 테이블에서 해당 방송 vodTag 업데이트 */
    @FormUrlEncoded
    @POST("update_liveList.php")
    Call<ServerResponse> update_liveList(@Field("live_path") String live_path,
                                         @Field("startTime") long startTime,
                                         @Field("endTime") long endTime);

    /* 실시간 방송 목록 가져오기 */
    @FormUrlEncoded
    @POST("select_liveList.php")
    Call<LiveAndVodArray> select_liveList(@Field("hostID") String hostID);


    /* 로그인한 사용자가 직접 방송한 VOD 리스트를 모두 가져온다 */
    @FormUrlEncoded
    @POST("select_userVod.php")
    Call<UserVideoArray> select_userVod(@Field("loginId") String loginId);

    /* DB 에 채팅 내용 insert 시키기 */
    @FormUrlEncoded
    @POST("insert_chat.php")
    Call<ServerResponse> insert_chat(@Field("chatID") String chatID,
                                     @Field("chatName") String chatName,
                                     @Field("chatImg") String chatImg,
                                     @Field("roomName") String roomName,
                                     @Field("chatMsg") String chatMsg,
                                     @Field("syncTime") long syncTime);


    /* DB 에 저장된 채팅 내용 select (VOD 에서 채팅내용 불러오기 위해)
     *  - VOD 에 해당하는 roomId 로 채팅 내용 select 한다 */
    @FormUrlEncoded
    @POST("select_chat.php")
    Call<ChatArray> select_chat(@Field("roomId") String roomId);

}
