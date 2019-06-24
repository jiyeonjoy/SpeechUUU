package choi02647.com.speechu.Streaming.Viewer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import choi02647.com.speechu.Data_vo.Chatting.ChatItemVO;
import choi02647.com.speechu.Data_vo.ServerResponse;
import choi02647.com.speechu.R;
import choi02647.com.speechu.Retrofit.Retrofit_ApiConfig;
import choi02647.com.speechu.Retrofit.Retrofit_Creator;
import choi02647.com.speechu.Streaming.Broadcaster.BroadCasterActivity;
import choi02647.com.speechu.Streaming.LiveStreamTitle;
import choi02647.com.speechu.Streaming.Rtc_peer.Kurento.KurentoViewerRTCClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *  [ 실시간 방송 시청 화면 ]
 *
 *  - main 의 실시간 방송 목록에서 하나의 방송을 클릭하면 넘어오는 화면이다
 *  - 클릭된 방송에 해당하는 실시간 방송을 시청하는 화면
 *  - 채팅 기능
 */

@EActivity(R.layout.activity_viewer)
public class ViewerActivity extends MvpActivity<ViewerView, ViewerPresenter> implements ViewerView {
    private static final String TAG = ViewerActivity.class.getSimpleName();

    @ViewById(R.id.vGLSurfaceViewCall)
    protected SurfaceViewRenderer vGLSurfaceViewCall;

    private EglBase rootEglBase;
    private ProxyRenderer remoteProxyRenderer;
    private Toast logToast;

    Intent StreamList_adap_intent;

    /* 라이브 방송일때 채팅 RecyclerView */
    RecyclerView chat_recycler;
    LinearLayoutManager chatLayoutManager;
    ArrayList<JSONObject> liveChatArray = new ArrayList<>();
    Chat_Adapter chatAdapter;

    /* VOD 재생할때 해당 VOD 채팅내용 모두 저장하는 ArrayList */
    ArrayList<ChatItemVO> vodChatArray = new ArrayList<>();

    /* 채팅 EditText, 버튼 */
    EditText writeChat_et;
    ImageView chatSend_btn;



    /* 소켓에서 넘어온 메세지 저장 */
    Handler messageHandler;

    /* 로그인 아이디, 이름, 프로필 저장 변수 */
    String loginId, loginName, loginPro = "";



    JSONObject roomSendJSON = new JSONObject();

    //Handler handler;
    String data;
    SocketChannel socketChannel;
    private static final String HOST = "15.164.102.182";
    private static final int PORT = 5001;
    String msg;



    String roomId2, roomId;

    @AfterViews
    protected void init() {
        //config peer
        remoteProxyRenderer = new ProxyRenderer();
        rootEglBase = EglBase.create();

        vGLSurfaceViewCall.init(rootEglBase.getEglBaseContext(), null);
        vGLSurfaceViewCall.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        vGLSurfaceViewCall.setEnableHardwareScaler(true);
        vGLSurfaceViewCall.setMirror(true);
        remoteProxyRenderer.setTarget(vGLSurfaceViewCall);

        writeChat_et = findViewById(R.id.writeChat_et);
        chatSend_btn = findViewById(R.id.chatSend_btn);
        chat_recycler = findViewById(R.id.chat_recycler);

        /* 로그인 정보 불러오기 */
        SharedPreferences loginShared = getSharedPreferences("logins", MODE_PRIVATE);
        loginId = loginShared.getString("loginId", null);
        loginName = loginShared.getString("loginName", null);
        loginPro = loginShared.getString("loginImg", null);

        messageHandler = new Handler();

        /* 채팅 recyclerView */
        chatLayoutManager = new LinearLayoutManager(this);
        chat_recycler.setLayoutManager(chatLayoutManager);
        chatAdapter = new Chat_Adapter(getApplicationContext(), true, liveChatArray);
        chat_recycler.setAdapter(chatAdapter);

        presenter.initPeerConfig();
//        presenter.startCall();

        StreamList_adap_intent = getIntent();

        roomId2 = StreamList_adap_intent.getStringExtra("streamPath");
        roomId = roomId2.substring(0, roomId2.length()-5);

        //handler = new Handler();
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
    protected void onResume() {
        super.onResume();

        presenter.startCall();

        /* 라이브 방송 시청시 roomId 로 채팅방의 고유 아이디를 채팅 서버에 보낸다*/
        try {

            roomSendJSON.put("command", "enter the room");
            roomSendJSON.put("roomId", roomId);

            Log.e("eeeeeeeeeeeeee", roomSendJSON.toString());
            // chatService.sendMessageSocket(roomSendJSON);

            new SendmsgTask().execute(roomSendJSON.toString());


        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void disconnect() {
        remoteProxyRenderer.setTarget(null);
        if (vGLSurfaceViewCall != null) {
            vGLSurfaceViewCall.release();
            vGLSurfaceViewCall = null;
        }

        try{
            JSONObject outSendJSON = new JSONObject();
            outSendJSON.put("command", "watcher exit the room");
            outSendJSON.put("roomId", roomId);
            Log.e("eeeeeeeeeeeeee",outSendJSON.toString());
            /* 서비스를 통해 메세지 보내기 */
            //chatService.sendMessageSocket(sendJSON);

            new SendmsgTask().execute(outSendJSON.toString());


        }catch (Exception e) {
            e.printStackTrace();
            Log.e("eeeeeeeeeeeeee",e.toString());
        }


        finish();
    }

    @NonNull
    @Override
    public ViewerPresenter createPresenter() {
        return new ViewerPresenter(getApplication());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        presenter.disconnect();
    }

    @Override
    public void stopCommunication() {
        onBackPressed();
    }

    @Override
    public void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (logToast != null) {
          //  logToast.cancel();
        }
       // logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
       // logToast.show();
    }

    @Override
    public EglBase.Context getEglBaseContext() {
        return rootEglBase.getEglBaseContext();
    }

    @Override
    public VideoRenderer.Callbacks getRemoteProxyRenderer() {
        return remoteProxyRenderer;
    }


    /* ====================================== 채팅 ===================================== */
    /* 채팅 보내기 */
    public void chatSend(View view) {
        if(writeChat_et.getText().toString().equals("") || writeChat_et.getText().toString() == null) {
            Toast.makeText(this, "메세지를 입력하세요", Toast.LENGTH_SHORT).show();
        }else {
            try {
                JSONObject sendJSON = new JSONObject();
                String message = writeChat_et.getText().toString();
                long mDate = System.currentTimeMillis()-StreamList_adap_intent.getLongExtra("stratTime", 0);
                sendJSON.put("command", "send a message");
                sendJSON.put("sendUser", loginId);
                sendJSON.put("sendUserName", loginName);
                sendJSON.put("sendProfile", loginPro);
                sendJSON.put("roomId", roomId);
                sendJSON.put("message", message);
                sendJSON.put("syncTime", mDate);

//                /* 보낸 채팅 메세지 나한테도 보이기 */
//                liveChatArray.add(sendJSON);
//                Message handlerM = handler.obtainMessage();
//                handler.sendMessage(handlerM);

                Log.e("eeeeeeeeeeeeee", "제이슨 메시지, " + sendJSON.toString());
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
                Log.d("readByteCount", readByteCount + "");
                //서버가 정상적으로 Socket의 close()를 호출했을 경우
                if (readByteCount == -1) {
                    throw new IOException();
                }

                byteBuffer.flip(); // 문자열로 변환
                Charset charset = Charset.forName("UTF-8");
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

    private Runnable showUpdate = new Runnable() {

        public void run() {
            String receive = "Coming word : " + data;
            Log.e("eeeeeeeeeeeeee88", receive);    // 받은거 표시
            //  binding.receiveMsgTv.setText(receive);
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
