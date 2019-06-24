package choi02647.com.speechu.Adapter.FaceDetect;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import choi02647.com.speechu.FaceDetect.FaceDetectActivity;
import choi02647.com.speechu.R;

/**
 *
 *  [ 얼굴인식 화면에서 하단 마스크 RecyclerView 어댑터 클래스 ]
 *
 */

public class EmoticonAdapter extends RecyclerView.Adapter<EmoticonAdapter.ViewHolder>{
    Context context;
    ArrayList<Integer> emoticonArray;

    public EmoticonAdapter(Context context, ArrayList<Integer> emoticonArray) {
        this.context = context;
        this.emoticonArray = emoticonArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.emoticon_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.emoticonImg.setImageResource(emoticonArray.get(position));

        holder.emoticonImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FaceDetectActivity)context).setMask(emoticonArray.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return emoticonArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView emoticonImg;

        public ViewHolder(View itemView) {
            super(itemView);
            emoticonImg = (ImageView)itemView.findViewById(R.id.emoticonImg);
        }
    }
}
