package choi02647.com.speechu.Main;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import choi02647.com.speechu.R;

/**
 *  [ 메인 화면 : 프래그먼트 3개 이동 가능 ]
 *
 *  - HOME 버튼 : 현재 실시간 방송 리스트와 VOD 리스트 열람 가능
 *  - MYMENU 버튼 : 실시간 방송 시작 및 내 방송 목록 확인 가능
 *  - SETTINGS 버튼 : 개인정보 및 설정 변경 가능
 */

public class MainActivity extends AppCompatActivity {

    SharedPreferences loginIdShared ;
    String loginId;                                                                                  // 현재 로그인 된 아이디
    String loginName;
    String loginImg;

    ViewPager vp;

    Main_Home_Fragment main_home_fragment;
    Main_Mymenu_Fragment main_mymenu_fragment;
    Main_Settings_Fragment main_settings_fragment;

    ImageView main_home_btn;
    ImageView main_mymenu_btn;
    ImageView main_settings_btn;

    long lastPressed;                                                                                // 한번 더 누르면 종료됩니다.
    private static final int MY_PERMISSION_CAMERA = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();                                                                           // 권한 확인-수정필요!


        // 현재 로그인된 유저 정보 제대로 불러오는지 확인( 잘 불려옴! )
        loginIdShared = getSharedPreferences("logins", MODE_PRIVATE);
        loginId = loginIdShared.getString("loginId", "");
        loginName = loginIdShared.getString("loginName", "");
        loginImg = loginIdShared.getString("loginImg", "");
        Log.d("dddddddddddddddddddddd", "loginId = " + loginId + "loginName = " + loginName  + "loginImg = " + loginImg );


        main_home_fragment = new Main_Home_Fragment();
        main_mymenu_fragment = new Main_Mymenu_Fragment();
        main_settings_fragment = new Main_Settings_Fragment();

        main_home_btn = findViewById(R.id.main_home_btn);
        main_mymenu_btn = findViewById(R.id.main_mymenu_btn);
        main_settings_btn = findViewById(R.id.main_settings_btn);

        vp = findViewById(R.id.vp);

        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);

        main_home_btn.setOnClickListener(movePageListener);
        main_home_btn.setTag(0);
        main_mymenu_btn.setOnClickListener(movePageListener);
        main_mymenu_btn.setTag(1);
        main_settings_btn.setOnClickListener(movePageListener);
        main_settings_btn.setTag(2);


        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                    if (position == 0) {
                        main_home_btn.setImageResource(R.drawable.ic_mainhome);
                        main_mymenu_btn.setImageResource(R.drawable.ic_mainmymenu_unclick);
                        main_settings_btn.setImageResource(R.drawable.ic_mainsettings_unclick);
                    } else if (position == 1) {
                        main_home_btn.setImageResource(R.drawable.ic_mainhome_unclick);
                        main_mymenu_btn.setImageResource(R.drawable.ic_mainmymenu);
                        main_settings_btn.setImageResource(R.drawable.ic_mainsettings_unclick);
                    } else {
                        main_home_btn.setImageResource(R.drawable.ic_mainhome_unclick);
                        main_mymenu_btn.setImageResource(R.drawable.ic_mainmymenu_unclick);
                        main_settings_btn.setImageResource(R.drawable.ic_mainsettings);
                    }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    } // onCreate 끝


    View.OnClickListener movePageListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int tag = (int) v.getTag();
            vp.setCurrentItem(tag);
        }
    };


    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return new Main_Home_Fragment();
                case 1:
                    return new Main_Mymenu_Fragment();
                case 2:
                    return new Main_Settings_Fragment();
                default:
                    return null;
            }
        }
        @Override
        public int getCount()
        {
            return 3;
        }
    }







    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - lastPressed < 1500){
            finish();
        }else {
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show();
        }
        lastPressed = System.currentTimeMillis();
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{ {..}의 요청으로 넘어감
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정해서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" +getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CAMERA:
                for (int i=0; i < grantResults.length; i++) {
                    // grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                    if(grantResults[i] < 0) {
                        Toast.makeText(MainActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // 허용했다면 이부분에서
                break;
        }
    }



}
