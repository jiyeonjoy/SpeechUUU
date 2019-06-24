package choi02647.com.speechu.FaceDetect;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ProfileImgUpdateRequest extends StringRequest {

    final static private String URL = "http://ec2-15-164-102-182.ap-northeast-2.compute.amazonaws.com/profileupdate.php";
    private Map<String, String> parameters;

    public ProfileImgUpdateRequest(String nowId, String profileImg,  Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("nowId", nowId);
        parameters.put("profileImg", profileImg);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
