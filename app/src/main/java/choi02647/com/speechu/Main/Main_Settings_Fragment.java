package choi02647.com.speechu.Main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import choi02647.com.speechu.FaceDetect.FaceDetectActivity;
import choi02647.com.speechu.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class Main_Settings_Fragment extends Fragment {


    CircleImageView userProfile;
    TextView userName;

    SharedPreferences loginIdShared ;
    String loginId;                                                                                  // 현재 로그인 된 아이디
    String loginName;
    String loginImg;

    String SERVER_URL = "http://15.164.102.182/";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_main_settings, container, false);

        userProfile = view.findViewById(R.id.userProfile);
        userName = view.findViewById(R.id.userName);




        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent go_take_a_picture = new Intent(getContext().getApplicationContext(), FaceDetectActivity.class);
                startActivity(go_take_a_picture);

            }
        });

        return view;
    }  // onCreate 끝



    @Override
    public void onResume() {
        Log.d(this.getClass().getSimpleName(), "onResume()");
        super.onResume();

        // 현재 로그인된 유저 정보
        loginIdShared = getContext().getSharedPreferences("logins", MODE_PRIVATE);
        loginId = loginIdShared.getString("loginId", "");
        loginName = loginIdShared.getString("loginName", "");
        loginImg = loginIdShared.getString("loginImg", "");

        if(loginImg == null) {
            /* 회원가입 시에 사용자가 프로필 사진을 설정 안했을때는 아이콘으로 프로필을 대체한다 */
            userProfile.setImageResource(R.drawable.ic_clickuser);
            Log.d("eeeeeeeeeee", "사진저장안됨");
        }else {
            Glide.with(getContext()).load(SERVER_URL  + loginImg).into(userProfile);
        }
        userName.setText(loginName);



    }









}
