package choi02647.com.speechu.Streaming.Broadcaster;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.nhancv.webrtcpeer.rtc_plugins.ProxyRenderer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import choi02647.com.speechu.Adapter.Chatting.Chat_Adapter;
import choi02647.com.speechu.R;
import choi02647.com.speechu.Retrofit.Retrofit_ApiConfig;
import choi02647.com.speechu.Retrofit.Retrofit_Creator;
import choi02647.com.speechu.Data_vo.ServerResponse;
import choi02647.com.speechu.Streaming.LiveStreamTitle;
import choi02647.com.speechu.Streaming.Rtc_peer.Kurento.KurentoPresenterRTCClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *  [ 라이브 스트리밍 화면 ]
 */

@EActivity(R.layout.activity_broadcaster)
public class BroadCasterActivity extends MvpActivity<BroadCasterView, BroadCasterPresenter>
        implements BroadCasterView{
    private static final String TAG = BroadCasterActivity.class.getSimpleName();

    @ViewById(R.id.vGLSurfaceViewCall)
    protected SurfaceViewRenderer vGLSurfaceViewCall;

    private EglBase rootEglBase;
    private ProxyRenderer localProxyRenderer;
    private Toast logToast;
    private boolean isGranted;

    EditText writeChat_et;
    ImageView chatSend_btn;

    /* 채팅 RecyclerView */
    RecyclerView chat_recycler;
    LinearLayoutManager chatLayoutManager;
    ArrayList<JSONObject> liveChatArray = new ArrayList<>();
    Chat_Adapter chatAdapter;


    /* 로그인 아이디, 이름, 프로필 저장 변수 */
    String loginId, loginName, loginPro = "";

    Intent titleIntent;


    /* 소켓에서 넘어온 메세지 저장 */
    Handler messageHandler;

    String data;
    SocketChannel socketChannel;
    private static final String HOST = "15.164.102.182";
    private static final int PORT = 5001;
    JSONObject roomSendJSON = new JSONObject();



    @AfterViews
    protected void init() {

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        //config peer
        localProxyRenderer = new ProxyRenderer();
        rootEglBase = EglBase.create();

        vGLSurfaceViewCall.init(rootEglBase.getEglBaseContext(), null);
        vGLSurfaceViewCall.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        vGLSurfaceViewCall.setEnableHardwareScaler(true);
        vGLSurfaceViewCall.setMirror(true);
        localProxyRenderer.setTarget(vGLSurfaceViewCall);

        messageHandler = new Handler();

        chat_recycler = findViewById(R.id.chat_recycler);
        writeChat_et = findViewById(R.id.writeChat_et);
        chatSend_btn = findViewById(R.id.chatSend_btn);

        /* 채팅 RecyclerView 선언 */
        chatLayoutManager = new LinearLayoutManager(getApplicationContext());
        chat_recycler.setLayoutManager(chatLayoutManager);
        chatAdapter = new Chat_Adapter(getApplicationContext(), true, liveChatArray);
        chat_recycler.setAdapter(chatAdapter);

        titleIntent = getIntent();

        /* 로그인 정보 불러오기 */
        SharedPreferences loginShared = getSharedPreferences("logins", MODE_PRIVATE);
        loginId = loginShared.getString("loginId", null);
        loginName = loginShared.getString("loginName", null);
        loginPro = loginShared.getString("loginImg", null);

        presenter.initPeerConfig();
        Log.e(TAG,"ddddddddaaaaaaaaa");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress(HOST, PORT));
                } catch (Exception ioe) {
                    Log.d("asd", ioe.getMessage() + "a");
                    ioe.printStackTrace();

                }
                checkUpdate.start();
            }
        }).start();


    }

    @Override
    public void disconnect() {
        localProxyRenderer.setTarget(null);
        if (vGLSurfaceViewCall != null) {
            vGLSurfaceViewCall.release();
            vGLSurfaceViewCall = null;
        }

        try{
            roomSendJSON.put("command", "broadcaster exit the room");
            roomSendJSON.put("roomId", titleIntent.getStringExtra("livePath"));
            Log.e("eeeeeeeeeeeeee11",roomSendJSON.toString());

            new SendmsgTask().execute(roomSendJSON.toString());

        }catch (Exception e) {
            e.printStackTrace();
            Log.e("eeeeeeeeeeeeee22",e.toString());
        }



        /* 영상 송출 중지
         *  - 데이터베이스의 streamList 테이블에서 해당 영상의 vodTag 값을 1로 업데이트
         *  - 시작 시간과 끝나는 시간을 데이터베이스로 보내서 둘의 시간 차이를 업데이트한다 */
        Retrofit_ApiConfig apiConfig = Retrofit_Creator.getApiConfig();
        retrofit2.Call<ServerResponse> call = apiConfig.update_liveList(KurentoPresenterRTCClient.P_send_roomId+".m3u8",
                LiveStreamTitle.startTime, System.currentTimeMillis());
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ServerResponse> call, Response<ServerResponse> response) {
                Log.e("방송 종료 VOD 업데이트", response.body().getMessage());
            }
            @Override
            public void onFailure(retrofit2.Call<ServerResponse> call, Throwable t) {
                Log.e("방송 종료 VOD 업데이트", t.getMessage());
            }
        });
        Log.e("스트리밍 송출","정지");
        finish();

    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.startCall();

        Log.e("eeeeeeeeeeeeee","ddddddddbbbbbbbbbb");

        try{
            roomSendJSON.put("command", "make a room");
            roomSendJSON.put("roomId", titleIntent.getStringExtra("livePath"));
            Log.e("eeeeeeeeeeeeee33",roomSendJSON.toString());

            new SendmsgTask().execute(roomSendJSON.toString());

        }catch (Exception e) {
            e.printStackTrace();
            Log.e("eeeeeeeeeeeeee44",e.toString());
        }

    }


    @NonNull
    @Override
    public BroadCasterPresenter createPresenter() {
        return new BroadCasterPresenter(getApplication());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        presenter.disconnect();
    }

    @Override
    public void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (logToast != null) {
          //  logToast.cancel();
        }
      //  logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
      //  logToast.show();
    }

    @Override
    public VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer;
        if (useCamera2()) {
            if (!captureToTexture()) {
                return null;
            }
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        } else {
            videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
        }
        if (videoCapturer == null) {
            return null;
        }
        return videoCapturer;
    }

    @Override
    public EglBase.Context getEglBaseContext() {
        return rootEglBase.getEglBaseContext();
    }

    @Override
    public VideoRenderer.Callbacks getLocalProxyRenderer() {
        return localProxyRenderer;
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this) && presenter.getDefaultConfig().isUseCamera2();
    }

    private boolean captureToTexture() {
        return presenter.getDefaultConfig().isCaptureToTexture();
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();
        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    /* ====================================== 채팅 ===================================== */

    /* 채팅 메시지 보내기 */
    public void chatSend(View view) {
        if(writeChat_et.getText().toString() == null || writeChat_et.getText().toString().equals("")) {
            Toast.makeText(this, "메세지를 입력하세요", Toast.LENGTH_SHORT).show();
        }else {
            try{
                JSONObject sendJSON = new JSONObject();
                String roomId = titleIntent.getStringExtra("livePath");
                String message = writeChat_et.getText().toString();
                long mDate = System.currentTimeMillis()-LiveStreamTitle.startTime;
                sendJSON.put("command", "send a message");
                sendJSON.put("sendUser", loginId);
                sendJSON.put("sendUserName", loginName);
                sendJSON.put("sendProfile", loginPro);
                sendJSON.put("roomId", roomId);
                sendJSON.put("message", message);
                sendJSON.put("syncTime", mDate);

                /* 보낸 채팅 메세지 나한테도 보이기 */
//                liveChatArray.add(sendJSON);
//                Message handlerM = handler.obtainMessage();
//                handler.sendMessage(handlerM);

                Log.e("eeeeeeeeeeeeee55", "제이슨 메시지, " + sendJSON.toString());
                new SendmsgTask().execute(sendJSON.toString());


                /* 채팅 내용 DB 에 저장 */
                Retrofit_ApiConfig apiConfig = Retrofit_Creator.getApiConfig();
                Call<ServerResponse> call = apiConfig.insert_chat(loginId, loginName, loginPro,
                        roomId, message, mDate);
                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        Log.e("채팅 내용 저장", response.body().getMessage());
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Log.e("채팅 내용 저장", t.getMessage());
                    }
                });

            }catch (Exception e) {
                e.printStackTrace();
            }
            writeChat_et.setText("");
        }
    }









    private class SendmsgTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            try {
                socketChannel
                        .socket()
                        .getOutputStream()
                        .write(strings[0].getBytes("UTF-8")); // 서버로
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //    binding.sendMsgEditText.setText("");
                }
            });
        }
    }

    void receive() {
        while (true) {
            try {

                    ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                    //서버가 비정상적으로 종료했을 경우 IOException 발생
                    int readByteCount = socketChannel.read(byteBuffer); //데이터받기

                    //서버가 정상적으로 Socket의 close()를 호출했을 경우
                    if (readByteCount == -1) {
                        throw new IOException();
                    }

                    byteBuffer.flip(); // 문자열로 변환
                    Charset charset = Charset.forName("UTF-8");

                    // if 문 data가 낫널일시 하기
                    data = charset.decode(byteBuffer).toString();
                    Log.d("eeeeeeeeeeeeee77", "msg :" + data);
                    //  handler.post(showUpdate);

                    messageHandler.post(showMessage);


            } catch (IOException e) {
                Log.d("getMsg", e.getMessage() + "");
                try {
                    socketChannel.close();
                    break;
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    private Thread checkUpdate = new Thread() {

        public void run() {
            try {
                receive();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



    /* 받은 채팅 메세지 리사이클러뷰에 출력 */
    private Runnable showMessage = new Runnable() {
        JSONObject receiveJSON = new JSONObject();
        @Override
        public void run() {
            try{
                receiveJSON = new JSONObject(data);
                Log.e("livestreamstart 받은 메세지", receiveJSON.toString());
                liveChatArray.add(receiveJSON);
                Message message = handler.obtainMessage();
                handler.sendMessage(message);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chatAdapter.notifyDataSetChanged();
            chat_recycler.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






}
