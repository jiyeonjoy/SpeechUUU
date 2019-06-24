package choi02647.com.speechu.Adapter.Broadcast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import java.util.ArrayList;

import choi02647.com.speechu.Data_vo.Broadcast.StreamListVO;
import choi02647.com.speechu.R;
import choi02647.com.speechu.Retrofit.Retrofit_ApiConfig;
import choi02647.com.speechu.Retrofit.Retrofit_Creator;
import choi02647.com.speechu.Streaming.Vod.VodPlayActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *  [ 메인화면 VOD 목록 RecyclerView 어댑터 ]
 *
 */

public class StreamVOD_Adapter extends RecyclerView.Adapter<StreamVOD_Adapter.ViewHolder>{
    Context context;
    ArrayList<StreamListVO> vodArray;
    String SERVER_URL = "http://15.164.102.182/";

    public StreamVOD_Adapter(Context context, ArrayList<StreamListVO> vodArray) {
        this.context = context;
        this.vodArray = vodArray;
    }

    @Override
    public StreamVOD_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stream_vod_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(vodArray.get(position).getHostImg() == null) {
            /* 회원가입 시에 사용자가 프로필 사진을 설정 안했을때는 아이콘으로 프로필을 대체한다 */
            holder.vodHost_profile.setImageResource(R.drawable.ic_clickuser);
        }else {
            Glide.with(context).load(SERVER_URL  + vodArray.get(position).getHostImg()).into(holder.vodHost_profile);
        }

        /* VOD 영상 썸네일 넣기
         *  - 데이터베이스에 썸네일 경로가 null 이면 아이콘을 넣는다 */
        if(vodArray.get(position).getVodThumbnail() == null) {
            holder.vod_thumbnail.setImageResource(R.drawable.ic_no_vodthumb);
            holder.vod_thumbnail.setBackgroundResource(R.color.thumbnailback);
        }else {
            Glide.with(context.getApplicationContext())
                    .load(SERVER_URL  + "vodthumbnails/" +  vodArray.get(position).getVodThumbnail())
                    .override(500, 170)
                    .into(holder.vod_thumbnail);
        }

        holder.vod_userName.setText(vodArray.get(position).getHostName());
        holder.vod_title.setText(vodArray.get(position).getStreamTitle());


        /* VOD 목록 클릭 이벤트
         *  - 클릭 된 VOD의 idx 와 경로를 가지고 플레이 화면으로 이동한다 */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_vodPlay = new Intent(context.getApplicationContext(), VodPlayActivity.class);
                go_vodPlay.putExtra("streanList_number", vodArray.get(position).getStreamList_number());
                go_vodPlay.putExtra("streamPath", vodArray.get(position).getStreamPath());
                go_vodPlay.putExtra("vodTag", vodArray.get(position).getVodTag());
//                go_vodPlay.putExtra("liveStart", vodArray.get(position).getLive_startTime());
//                go_vodPlay.putExtra("duration", vodArray.get(position).getDuration());
                go_vodPlay.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(go_vodPlay);
            }
        });



    }

    @Override
    public int getItemCount() {
        return vodArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView vod_thumbnail;
        public CircleImageView vodHost_profile;
        public TextView vod_title, vod_userName;

        public ViewHolder(View itemView) {
            super(itemView);
            vod_thumbnail = (ImageView)itemView.findViewById(R.id.vod_thumbnail);
            vodHost_profile = (CircleImageView)itemView.findViewById(R.id.vodHost_profile);
            vod_title = (TextView)itemView.findViewById(R.id.vod_title);
            vod_userName = (TextView)itemView.findViewById(R.id.vod_userName);
        }
    }

    public void updateVodlist(ArrayList<StreamListVO> vodlistArray) {
        this.vodArray = vodlistArray;
        notifyDataSetChanged();
    }

    public String loginShared() {
        SharedPreferences loginShared = context.getSharedPreferences("logins", Context.MODE_PRIVATE);
        return loginShared.getString("loginId", null);
    }

}
