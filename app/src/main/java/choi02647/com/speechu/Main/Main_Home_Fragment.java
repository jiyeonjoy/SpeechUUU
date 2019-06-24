package choi02647.com.speechu.Main;

import android.content.Context;
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
import choi02647.com.speechu.R;
import choi02647.com.speechu.Retrofit.Retrofit_ApiConfig;
import choi02647.com.speechu.Retrofit.Retrofit_Creator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *  [ 메인 화면으로써 Login 액티비티에서 로그인 후 넘어오는 화면이다 ]
 *
 *  - 라이브 방송 목록과 VOD 목록이 표시된다
 *  - 방송 목록들은 데이터베이스의 streamList 테이블에서 가져온다
 *
 */

public class Main_Home_Fragment extends Fragment {


    /* 실시간 방송 목록 RecyclerView 와 어댑터 */
    RecyclerView streamLiveRecycle;
    LinearLayoutManager live_recLayout;
    ArrayList<StreamListVO> liveList_array = new ArrayList<>();
    StreamLive_Adapter streamLive_adapter;

    /* VOD 목록 RecyclerView 와 어댑터 */
    RecyclerView vodRecycle;
    LinearLayoutManager vod_recLayout;
    ArrayList<StreamListVO> vodList_array = new ArrayList<>();
    StreamVOD_Adapter VOD_adapter;

    /* 로그인 유저 아이디, 프로필 저장 변수 */
    String loginId;

    SwipeRefreshLayout swipeRefresh;
    TextView noBroadcast;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_main_home, container, false);

        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        streamLiveRecycle = view.findViewById(R.id.stream_live_Recycle);
        vodRecycle = view.findViewById(R.id.vod_Recycle);
        noBroadcast = view.findViewById(R.id.no_broadcast);

        /* 실시간 방송 목록 당겨서 새로고침 이벤트 리스너 */
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(liveList_array.size() > 0 || vodList_array.size() > 0) {
                    liveList_array.clear();
                    vodList_array.clear();
                    selectLivelist();
                }else {
                    selectLivelist();
                }
                swipeRefresh.setRefreshing(false);
            }
        });

        SharedPreferences loginShared = getContext().getSharedPreferences("logins", Context.MODE_PRIVATE);
        loginId = loginShared.getString("loginId", null);


        //return dataBindings_stream.getRoot();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(liveList_array.size() > 0 || vodList_array.size() > 0) {
            liveList_array.clear();
            vodList_array.clear();
        }

        /* 실시간 방송 목록 Recyclerview 에 layoutmanager 설정 */
        live_recLayout = new LinearLayoutManager(getContext());
        streamLiveRecycle.setLayoutManager(live_recLayout);

        /* VOD 목록 Recyclerview 에 layoutmanager 설정 */
        vod_recLayout = new LinearLayoutManager(getContext());
        vodRecycle.setLayoutManager(vod_recLayout);
        vod_recLayout.setOrientation(LinearLayoutManager.HORIZONTAL);

        /* 실시간 방송 목록 Recyclerview 에 어댑터 바인드 */
        streamLive_adapter = new StreamLive_Adapter(getActivity().getApplicationContext(), liveList_array, loginId);
        streamLiveRecycle.setAdapter(streamLive_adapter);

        /* VOD 목록 Recyclerview 에 어댑터 바인드 */
        VOD_adapter = new StreamVOD_Adapter(getActivity().getApplicationContext(), vodList_array);
        vodRecycle.setAdapter(VOD_adapter);

        selectLivelist();

    }

    /* 데이터베이스의 livestream_list 테이블에서 생방송 목록과 VOD 목록을 가져오기 위한 메소드
     *  - liveList_array : 생방송 목록을 담는 ArrayList
     *  - vodList_array : VOD 목록을 담는 ArrrayList */
    public void selectLivelist() {
        Retrofit_ApiConfig apiConfig = Retrofit_Creator.getApiConfig();
        Call<LiveAndVodArray> call = apiConfig.select_liveList("host_ID");
        call.enqueue(new Callback<LiveAndVodArray>() {
            @Override
            public void onResponse(Call<LiveAndVodArray> call, Response<LiveAndVodArray> response) {
                liveList_array.addAll(response.body().getLivelistArray());
                vodList_array.addAll(response.body().getVodlistArray());

                if(liveList_array.size() > 0) {
                    noBroadcast.setVisibility(View.INVISIBLE);
                }else {
                    noBroadcast.setVisibility(View.VISIBLE);
                }

                streamLive_adapter.updateList(liveList_array);
                VOD_adapter.updateVodlist(vodList_array);
            }
            @Override
            public void onFailure(Call<LiveAndVodArray> call, Throwable t) {
                Log.e("라이브 목록 가져오기", t.getMessage());
            }
        });
    }

}
