package choi02647.com.speechu.FaceDetect;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import choi02647.com.speechu.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 *
 *  [ FaceDetectActivity 화면에서 사진 찍은 후 사진을 저장하기 위한 화면 ]
 *  - 사진을 저장하거나 취소 할 수 있다
 *
 */

public class AfterTakePicture extends AppCompatActivity {
    private static final String TAG = "AfterTakePicture";

    CircleImageView captureIV;
    Intent intent;

    String faceImagePath, imageFileName;

    /* 로그인 아이디, 이름, 프로필 저장 변수 */
    String loginId, loginName, loginPro = "";

    Uri uri;

    ProgressDialog dialog = null;

    String upLoadServerUri1 = null;
    int serverResponseCode = 0;

    String faceImgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_take_picture);


        upLoadServerUri1 = "http://ec2-15-164-102-182.ap-northeast-2.compute.amazonaws.com/profileImg.php";       // 서버컴퓨터의 ip주소

        /* 로그인 정보 불러오기 */
        SharedPreferences loginShared = getSharedPreferences("logins", MODE_PRIVATE);
        loginId = loginShared.getString("loginId", null);
        loginName = loginShared.getString("loginName", null);
        loginPro = loginShared.getString("loginImg", null);

        captureIV = findViewById(R.id.captureIV);

        intent = getIntent();
        faceImagePath = intent.getStringExtra("captureAddress");
        captureIV.setImageURI(Uri.parse(intent.getStringExtra("captureAddress")));
    }

    public void afterPictureOnclick(View view) {
        switch (view.getId()) {
            /* 이전 화면으로 가기 */
            case R.id.backBtn:
                finish();
                break;

            /* 프로필사진 저장하기
             * 쉐어드 주소저장 사진 업로드
             */
            case R.id.downloadBtn:
                Log.d("eeeeeeeeeeedddd", faceImagePath);
                faceImgPath = faceImagePath.substring(41);
                Log.d("eeeeeeeeeeedddd", faceImgPath);
                dialog = ProgressDialog.show(AfterTakePicture.this, "", "Uploading file...", true);

                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {

                            }
                        });
                        uploadFile(faceImagePath);
                        Log.i("eeeeeeeeeee", "업로드시작");

                    }
                }).start();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                ProfileImgUpdateRequest profileImgUpdateRequest = new ProfileImgUpdateRequest(loginId, faceImgPath, responseListener);
                RequestQueue queue = Volley.newRequestQueue(AfterTakePicture.this);
                queue.add(profileImgUpdateRequest);



                //  로그인 아이디 Shared 에 저장
                SharedPreferences loginIdShared = getSharedPreferences("logins", MODE_PRIVATE);
                SharedPreferences.Editor loginEdit = loginIdShared.edit();
                loginEdit.putString("loginImg", faceImgPath);
                loginEdit.commit();



                finish();
                break;
        }
    }





    // 이미지 서버에 업로드

    public int uploadFile(String sourceFileUri) {
        Log.i("eeeeeeeeeee", "sssssssssssssssssssssssss");
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        Log.i("eeeeeeeeeee", "업로드시작sssssssssssssssss");
        if (!sourceFile.isFile()) {

            dialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {
                    Log.i("eeeeeeeeeee", "업로드시작sdddddddddddddddssssssssssssssss");
                }
            });

            return 0;

        }
        else
        {
            try {
                Log.i("eeeeeeeeeee", "되고있엉");
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri1);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("eeeeeeeeeee", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(AfterTakePicture.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(AfterTakePicture.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("eeeeeeeeeee", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {

//                        Toast.makeText(Adopt2Activity.this, "Got Exception : see logcat ",
//                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("eeeeeeeeeee", "Exception : "
                        + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }





}
