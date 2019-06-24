package choi02647.com.speechu.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import choi02647.com.speechu.Main.MainActivity;
import choi02647.com.speechu.R;
import choi02647.com.speechu.Retrofit.Retrofit_ApiConfig;
import choi02647.com.speechu.Retrofit.Retrofit_Creator;
import choi02647.com.speechu.Data_vo.ServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *  [ 로그인화면 ]
 *
 *  - 유저의 아이디와 비밀번호를 Retrofit 을 통해 서버로 넘긴다
 *  - 유효성 검사는 PHP 파일에서 확인
 *  - 회원가입 버튼 클릭하면 회원가입 화면으로 넘어간다
 *
 */

public class LoginActivity extends AppCompatActivity {

    /* 로그인 아이디, 비밀번호 EditText */
    EditText login_id_et, login_password_et;

    String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_id_et = findViewById(R.id.login_id_et);
        login_password_et = findViewById(R.id.login_password_et);

    }  // onCreate 끝

    /*
     *  [ 로그인 & 회원가입 버튼 클릭 이벤트 ]
     *   - 로그인 버튼 누르면 입력된 아이디와 비밀번호를 가지고 PHP 파일에서 유효성 검사한다
     *   - 아이디와 비밀번호가 일치하면 PHP 에서 true 를 리턴하여 로그인 OK
     *   - 일치하지 않으면 PHP 에서 false 를 리턴한다
     *   - 회원가입 버튼 누르면 Join 액티비티로 넘어간다
     */
    public void login_onclick(View view) {
        int id = view.getId();
        switch (id) {
            /* 로그인 버튼
             * - 입력된 아이디와 비밀번호를 가지고 login.php 파일로 간다
             * - 데이터베이스에서 아이디와 비밀번호의 유효성을 검사하고 true, false 를 반환한다 */
            case R.id.login_btn:
                Retrofit_ApiConfig apiConfig = Retrofit_Creator.getApiConfig();
                Call<ServerResponse> call = apiConfig.check_Login(login_id_et.getText().toString(), login_password_et.getText().toString());
                Log.d(TAG, "dddddddddddddddddddddd");


                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        ServerResponse serverResponse = response.body();
                        Log.d("dddddddddddddddddddddd", serverResponse.getMessage());
                        if(serverResponse.getMessage().equals("true")) {
                            /* 유효성 검사 확인 - 로그인 성공하면 Main 화면으로 넘어간다 */
                            Toast.makeText(getApplicationContext(), "로그인 성공!", Toast.LENGTH_SHORT).show();
                            //  로그인 아이디 Shared 에 저장
                            SharedPreferences loginIdShared = getSharedPreferences("logins", MODE_PRIVATE);
                            SharedPreferences.Editor loginEdit = loginIdShared.edit();
                            loginEdit.putString("loginId", login_id_et.getText().toString());
                            loginEdit.putString("loginName", serverResponse.getUserName());
                            loginEdit.putString("loginImg", serverResponse.getUserImg());
                            loginEdit.commit();

                            //  Main 화면으로 이동
                            Intent go_stream_main = new Intent(getApplication().getApplicationContext(), MainActivity.class);
                            startActivity(go_stream_main);

                            finish();

                        }else {
                            /* 아이디 또는 비밀번호가 일치하지 않는다 */
                            Log.d(TAG, "ddddddddddddddbbbbbbb");
                            Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
                            login_id_et.setText("");
                            login_password_et.setText("");
                            login_id_et.requestFocus();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Log.d(TAG, "ddddddddddddddbbbbbbb", t);
                    }
                });
                break;

            /* 회원가입 버튼 */
            case R.id.join_btn:
                break;
        }
    }


}
