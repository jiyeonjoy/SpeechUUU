package choi02647.com.speechu.Streaming;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import choi02647.com.speechu.R;
import choi02647.com.speechu.Retrofit.Retrofit_ApiConfig;
import choi02647.com.speechu.Retrofit.Retrofit_Creator;
import choi02647.com.speechu.Data_vo.ServerResponse;
import choi02647.com.speechu.Streaming.Broadcaster.BroadCasterActivity_;
import choi02647.com.speechu.Streaming.Rtc_peer.Kurento.KurentoPresenterRTCClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveStreamTitle extends AppCompatActivity {

    EditText title_et;
    String title;

    //  데이터베이스의 streamList 테이블에 들어갈 경로 저장 변수
    String live_path;

    SharedPreferences loginIdShared ;
    String loginId;                                                                                  // 현재 로그인 된 아이디
    String loginName;
    String loginImg;

    public  static long startTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_stream_title);

        // 현재 로그인된 유저 정보
        loginIdShared = getSharedPreferences("logins", MODE_PRIVATE);
        loginId = loginIdShared.getString("loginId", "");
        loginName = loginIdShared.getString("loginName", "");
        loginImg = loginIdShared.getString("loginImg", "");


        title_et = findViewById(R.id.title_et);
        ImageView start_btn = findViewById(R.id.start_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = title_et.getText().toString();
                live_path = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_" + title;
                if(title.trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해 주세요!", Toast.LENGTH_SHORT).show();
                } else {

                    startTime = System.currentTimeMillis();
                    /* 데이터베이스의 streamList 테이블에 생성된 생방송 저장
                     *  - 테이블 컬럼 : hostID(방송 생성한 사용자 아이디), hostName, hostImg, streamTitle,
                     *               streamPath(.m3u8 파일 경로), vodTag(라이브인지 VOD 인지 설정하는 태그), vodThumbnail(썸네일 경로)
                     *  - vodTag = 0 : 생방송 / vodTag = 1 : VOD */
                    Retrofit_ApiConfig apiConfig = Retrofit_Creator.getApiConfig();
                    Call<ServerResponse> call = apiConfig.insert_liveList(loginId, loginName, loginImg,
                            title, live_path+".m3u8", 0, live_path+".png", startTime);
                    call.enqueue(new Callback<ServerResponse>() {
                        @Override
                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                            Log.e("방송 목록 저장 성공", response.body().getMessage());
                        }

                        @Override
                        public void onFailure(Call<ServerResponse> call, Throwable t) {
                            Log.e("방송 목록 저장 eeeeeee", t.getMessage());
                        }
                    });
                    Log.e("스트리밍 송출", "시작");

                    //  라이브 스트리밍 액티비티 이동
                    Intent go_broadcast = new Intent(getApplication().getApplicationContext(), BroadCasterActivity_.class);
                    go_broadcast.putExtra("liveTitle", title);
                    go_broadcast.putExtra("livePath", live_path);
                    KurentoPresenterRTCClient.P_send_roomId = live_path;
                    startActivity(go_broadcast);
                    finish();
                }
            }
        });
    }
}
