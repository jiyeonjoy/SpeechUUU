package choi02647.com.speechu.Streaming.Vod;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import choi02647.com.speechu.Adapter.Chatting.Chat_Adapter;
import choi02647.com.speechu.Data_vo.Broadcast.UserVideoArray;
import choi02647.com.speechu.Data_vo.Chatting.ChatArray;
import choi02647.com.speechu.Data_vo.Chatting.ChatItemVO;
import choi02647.com.speechu.R;
import choi02647.com.speechu.Retrofit.Retrofit_ApiConfig;
import choi02647.com.speechu.Retrofit.Retrofit_Creator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 [ VOD 재생 화면 ]
 - main 화면에서 VOD 목록 중 한 건 클릭하면 넘어오는 화면
 - 라이브 방송 시청할때 나눴던 채팅이 영상 시간에 맞춰서 같이 재생된다
 */

public class VodPlayActivity extends AppCompatActivity {

    private static final String TAG = "VodStreamPlay";
    static final String DASH_URI = "http://15.164.102.182/hlsrecording/";

    /* VOD 재생시에 필요한 변수들 */
    Handler mainHandler;
    TrackSelection.Factory videoTrackSelectionFactory;
    TrackSelector trackSelector;
    LoadControl loadControl;
    DataSource.Factory dataSourceFactory;
    MediaSource videoSource;
    Uri dash_uri;                                           //  VOD 영상이 존재하는 파일 경로 변수
    String userAgent;
    final DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

    private PlayerView vodstream_player;
    private SimpleExoPlayer player;

    private Boolean playWhenReady = true;
    private int currentWindow = 0;
    private Long playbackPosition = 0L;

    Intent StreamList_adap_intent;

    /* VOD 방송일때 채팅 RecyclerView */
    RecyclerView vodChat_R;
    LinearLayoutManager chatLayoutManager;
    ArrayList<ChatItemVO> vodChatArray = new ArrayList<>();
    Chat_Adapter chatAdapter;

    /* 방송에 포함된 채팅과 영상 싱크를 맞추기 위해 채팅을 담을 ArrayList */
    ArrayList<ChatItemVO> syncChatArray = new ArrayList<>();

    Handler syncHandler;
    static int second;

    String roomId2, roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_play);

        vodstream_player = findViewById(R.id.vodstream_player);
        vodChat_R = findViewById(R.id.vodChat_R);
        StreamList_adap_intent = getIntent();

        initializePlayer();

        /* RecyclerView */
        chatLayoutManager = new LinearLayoutManager(this);
        vodChat_R.setLayoutManager(chatLayoutManager);
        chatAdapter = new Chat_Adapter(this, syncChatArray, false);
        vodChat_R.setAdapter(chatAdapter);


        roomId2 = StreamList_adap_intent.getStringExtra("streamPath");
        roomId = roomId2.substring(0, roomId2.length()-5);

        PlayerListener playerListener = new PlayerListener();
        player.addListener(playerListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
      //  initializePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void initializePlayer() {
        if (player == null) {
            /* create player */
            mainHandler = new Handler();
            videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            loadControl = new DefaultLoadControl();
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

            //플레이어 연결
            vodstream_player.setPlayer(player);
        }

        Log.e("라이브, VOD 경로", StreamList_adap_intent.getStringExtra("streamPath"));
        dash_uri = Uri.parse(DASH_URI + StreamList_adap_intent.getStringExtra("streamPath"));

        MediaSource mediaSource = buildMediaSource(dash_uri);

        //prepare
        player.prepare(mediaSource, true, false);

        //start,stop
        player.setPlayWhenReady(playWhenReady);
    }



    private MediaSource buildMediaSource(Uri uri) {

        String userAgent = Util.getUserAgent(this, "blackJin");
        if (uri.getLastPathSegment().contains("mp3") || uri.getLastPathSegment().contains("mp4")) {
            return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);
        } else if (uri.getLastPathSegment().contains("m3u8")) {
            //com.google.android.exoplayer:exoplayer-hls 확장 라이브러리를 빌드 해야 합니다.
            return new HlsMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);
        } else {
            return new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(this, userAgent))
                    .createMediaSource(uri);
        }

    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();

            vodstream_player.setPlayer(null);
            player.release();
            player = null;

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        /* VOD 에 해당하는 채팅내용 모두 불러와서 vodChatArray 에 저장한다 */

        Retrofit_ApiConfig apiConfig = Retrofit_Creator.getApiConfig();
        Call<ChatArray> call = apiConfig.select_chat(roomId);
        Log.e("eeeeeeeeeeeeeeroomId", roomId);
        call.enqueue(new Callback<ChatArray>() {
            @Override
            public void onResponse(Call<ChatArray> call, Response<ChatArray> response) {
                vodChatArray.addAll(response.body().getChatArray());
                Log.e("eeeeeeeeeeeeee11", roomId);
                if(second > 0) {
                    second = 0;
                }

                /* Nosql 에서 해당 VOD 의 채팅내용을 받아온 후 영상과 채팅의 싱크를 맞추기 위해 Thread 를 스타트한다 */
                SyncTime syncTime = new SyncTime();
                syncTime.start();
            }

            @Override
            public void onFailure(retrofit2.Call<ChatArray> call, Throwable t) {
                Log.e("eeeeeeeeeeeeee실패", t.getMessage());
            }
        });

    }










    /* 채팅과 영상의 싱크를 맞추기 위한 Thread */
    public class SyncTime extends Thread {
        @Override
        public void run() {
            while(true) {
                try{
                    for(int i=0 ; i<vodChatArray.size() ; i++) {
                        if((vodChatArray.get(i).getSyncTime() / 100) * 100 == second) {
                            syncChatArray.add(vodChatArray.get(i));
                            Message handlerM = handler.obtainMessage();
                            handler.sendMessage(handlerM);
                        }
                    }
                    if(syncChatArray.size() == vodChatArray.size()) {
                        break;
                    }
                    Thread.sleep(100);
                    second = second+100;
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /* 영상 재생에 맞춰서 채팅을 RecyclerView 에 추가하기 위한 핸들러 */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chatAdapter.notifyDataSetChanged();
            vodChat_R.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    };

    /* ExoPlayer 이벤트 리스너
     *  - onSeekProcessed : 사용자가 하단 seekBar 를 조작할 때 불려지는 이벤트 리스너 메소드
     *  - getCurrentPosition 으로 현재 seekBar 의 millis 를 가져와서 채팅의 millis 와 같으면 채팅을 Array 에 추가한다 */
    public class PlayerListener extends Player.DefaultEventListener {

        @Override
        public void onSeekProcessed() {
            super.onSeekProcessed();
            int seekTime = (int)player.getCurrentPosition();
            second = (seekTime/100) * 100;
            if(syncChatArray.size() > 0) {
                syncChatArray.clear();
            }
            for(int i=0 ; i<vodChatArray.size() ; i++) {
                if(vodChatArray.get(i).getSyncTime() <= seekTime) {
                    syncChatArray.add(vodChatArray.get(i));
                    Message handlerM = handler.obtainMessage();
                    handler.sendMessage(handlerM);
                }else if(vodChatArray.get(0).getSyncTime() > seekTime){
                    syncChatArray.clear();
                    Message handlers = handler.obtainMessage();
                    handler.sendMessage(handlers);
                }
            }
        }
    }


}