package choi02647.com.speechu.Adapter.Broadcast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import choi02647.com.speechu.Data_vo.Broadcast.StreamListVO;
import choi02647.com.speechu.R;
import choi02647.com.speechu.Retrofit.Retrofit_ApiConfig;
import choi02647.com.speechu.Retrofit.Retrofit_Creator;
import choi02647.com.speechu.Streaming.Broadcaster.BroadCasterActivity_;
import choi02647.com.speechu.Streaming.Rtc_peer.Kurento.KurentoPresenterRTCClient;
import choi02647.com.speechu.Streaming.Rtc_peer.Kurento.KurentoViewerRTCClient;
import choi02647.com.speechu.Streaming.Viewer.ViewerActivity_;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 *  [ 실시간 방송 목록 Recyclerview 어댑터 클래스 ]
 *
 *  - Main_Home_Fragment 화면에 있는 실시간 방송 목록(Recyclerview) 의 데이터를 담을 어댑터 클래스이다
 *  - 보고싶은 방송을 클릭했을때 클릭 된 방송의 streamList_number와 경로를 가지고 플레이어 화면으로 이동한다
 *
 */

public class StreamLive_Adapter extends RecyclerView.Adapter<StreamLive_Adapter.ViewHolder>{
    Context context;
    ArrayList<StreamListVO> streamlive_array;
    String SERVER_URL = "http://15.164.102.182/";
    String loginId, loginName;

    public StreamLive_Adapter(Context context, ArrayList<StreamListVO> streamlive_array, String loginId) {
        this.context = context;
        this.streamlive_array = streamlive_array;
        this.loginId = loginId;
    }

    @Override
    public StreamLive_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stream_live_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if(streamlive_array.get(position).getHostImg().equals("")) {
            /* 회원가입 시에 사용자가 프로필 사진을 설정 안했을때는 아이콘으로 프로필을 대체한다 */
            holder.live_host_profile.setImageResource(R.drawable.ic_clickuser);
        }else {
            Glide.with(context).load(SERVER_URL + streamlive_array.get(position).getHostImg()).into(holder.live_host_profile);
        }

        /* 생방송 썸네일 */
//        if(!streamlive_array.get(position).getVodThumbnail().equals(null)) {
//            Glide.with(context).load(SERVER_URL + "vodthumbnails/" + streamlive_array.get(position).getVodThumbnail()).into(holder.live_thumbnail);
//
//        }
        holder.live_title.setText(streamlive_array.get(position).getStreamTitle());
        holder.live_userName.setText(streamlive_array.get(position).getHostName());

        /* 생방송 목록 클릭 이벤트
         * - 클릭 된 방송의 idx 와 경로를 가지고 플레이 화면으로 이동한다
         * - vod_tag = 0 : 라이브 방송이면 play 화면에서 컨트롤러 보이지 않는다.
         * - liveStart : 생방송 시작 시간 (milliSecond)*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String streamPath = streamlive_array.get(position).getStreamPath();
                Intent go_live_play = new Intent(context.getApplicationContext(), ViewerActivity_.class);
                go_live_play.putExtra("streamList_number", streamlive_array.get(position).getStreamList_number());
                go_live_play.putExtra("streamPath", streamPath);
                go_live_play.putExtra("vodTag", streamlive_array.get(position).getVodTag());
                go_live_play.putExtra("stratTime", streamlive_array.get(position).getStartTime());
                KurentoViewerRTCClient.V_send_roomId = streamPath.substring(0, streamPath.length()-5);
                go_live_play.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(go_live_play);

            }
        });

    }

    @Override
    public int getItemCount() {
        return streamlive_array.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView live_thumbnail;
        public CircleImageView live_host_profile;
        public TextView live_title, live_userName;

        public ViewHolder(View view) {
            super(view);
            live_thumbnail = view.findViewById(R.id.live_thumbnail);
            live_host_profile = (CircleImageView)view.findViewById(R.id.live_host_profile);
            live_title = (TextView)view.findViewById(R.id.live_title);
            live_userName = (TextView)view.findViewById(R.id.live_userName);
        }
    }

    public void updateList(ArrayList<StreamListVO> updatelist) {
        this.streamlive_array = updatelist;
        notifyDataSetChanged();
    }

    public String loginShared() {
        SharedPreferences loginShared = context.getSharedPreferences("logins", Context.MODE_PRIVATE);
        return loginShared.getString("loginId", null);
    }


}
