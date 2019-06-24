package choi02647.com.speechu.Main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import choi02647.com.speechu.Adapter.Broadcast.StreamLive_Adapter;
import choi02647.com.speechu.Adapter.Broadcast.StreamVOD_Adapter;
import choi02647.com.speechu.Data_vo.Broadcast.LiveAndVodArray;
import choi02647.com.speechu.Data_vo.Broadcast.StreamListVO;
import choi02647.com.speechu.Data_vo.Broadcast.UserVideoArray;
import choi02647.com.speechu.R;
import choi02647.com.speechu.Retrofit.Retrofit_ApiConfig;
import choi02647.com.speechu.Retrofit.Retrofit_Creator;
import choi02647.com.speechu.Streaming.LiveStreamTitle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *  [ 하단 버튼 세개중에서 가운데 화면 ]
 *
 *  - 라이브방송 버튼으로 실시간 스트리밍을 시작할 수 있다
 *  - 내가 직접 방송했던 VOD 목록이 있다
 *
 */
public class Main_Mymenu_Fragment extends Fragment {

    /* VOD 목록 RecyclerView 와 어댑터 */
    RecyclerView vodRecycle;
    LinearLayoutManager vod_recLayout;
    ArrayList<StreamListVO> vodList_array = new ArrayList<>();
    StreamVOD_Adapter VOD_adapter;

    /* 로그인 유저 아이디, 프로필 저장 변수 */
    String loginId;

    SwipeRefreshLayout swipeRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_main_mymenu, container, false);

        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        /* 실시간 방송 목록 당겨서 새로고침 이벤트 리스너 */
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(vodList_array.size() > 0) {
                    vodList_array.clear();
                    selectLivelist();
                }else {
                    selectLivelist();
                }
                swipeRefresh.setRefreshing(false);
            }
        });


        TextView start_btn = view.findViewById(R.id.start_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_broadcast_setting = new Intent(getContext().getApplicationContext(), LiveStreamTitle.class);
                startActivity(go_broadcast_setting);
            }
        });


        vodRecycle = view.findViewById(R.id.stream_myVod_Recycle);

        SharedPreferences loginShared = getContext().getSharedPreferences("logins", Context.MODE_PRIVATE);
        loginId = loginShared.getString("loginId", null);

        //return dataBindings_stream.getRoot();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(vodList_array.size() > 0) {
            vodList_array.clear();
        }


        /* VOD 목록 Recyclerview 에 layoutmanager 설정 */
        vod_recLayout = new LinearLayoutManager(getContext());
        vodRecycle.setLayoutManager(vod_recLayout);
        vod_recLayout.setOrientation(LinearLayoutManager.HORIZONTAL);

        /* VOD 목록 Recyclerview 에 어댑터 바인드 */
        VOD_adapter = new StreamVOD_Adapter(getActivity().getApplicationContext(), vodList_array);
        vodRecycle.setAdapter(VOD_adapter);

        selectLivelist();

    }

    /* 데이터베이스의 streamList 테이블에서 내 VOD 목록을 가져오기 위한 메소드
     *  - vodList_array : VOD 목록을 담는 ArrrayList */

    public void selectLivelist() {
        Retrofit_ApiConfig apiConfig = Retrofit_Creator.getApiConfig();
        Call<UserVideoArray> call = apiConfig.select_userVod(loginId);
        Log.e("dddddddddddddddddddd", loginId);
        call.enqueue(new Callback<UserVideoArray>() {
            @Override
            public void onResponse(Call<UserVideoArray> call, Response<UserVideoArray> response) {
                vodList_array.addAll(response.body().getMyVideoArray());
                VOD_adapter.updateVodlist(vodList_array);
                Log.e("dddddddddddddddddddd", "aaaaaaaaaaa");
            }
            @Override
            public void onFailure(Call<UserVideoArray> call, Throwable t) {
                Log.e("vod 목록 가져오기", t.getMessage());
            }
        });
    }

}
