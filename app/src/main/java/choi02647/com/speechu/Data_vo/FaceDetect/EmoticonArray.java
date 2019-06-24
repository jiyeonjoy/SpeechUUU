package choi02647.com.speechu.Data_vo.FaceDetect;

import java.util.ArrayList;

import choi02647.com.speechu.R;

public class EmoticonArray {
    public EmoticonArray() {
    }
    public ArrayList<Integer> setEmoticon() {
        ArrayList<Integer> eArray = new ArrayList<>();
        eArray.add(R.drawable.flower_crown);
        eArray.add(R.drawable.unicorn_mask);
        eArray.add(R.drawable.prince);
        eArray.add(R.drawable.princess);
        eArray.add(R.drawable.dogs_mask);
        eArray.add(R.drawable.hoho_mask);
        return eArray;
    }
}
