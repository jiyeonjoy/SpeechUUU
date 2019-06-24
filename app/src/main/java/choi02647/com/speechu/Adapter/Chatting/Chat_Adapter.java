package choi02647.com.speechu.Adapter.Chatting;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;

import choi02647.com.speechu.Data_vo.Chatting.ChatItemVO;
import choi02647.com.speechu.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 *  [ 채팅 메세지 출력하는 RecyclerView 의 어댑터 클래스 ]
 *
 *  - liveChatArray : 소켓에서 넘어오는 메세지나 보낸 메세지를 저장하는 ArrayList
 *
 */

public class Chat_Adapter extends RecyclerView.Adapter<Chat_Adapter.ViewHolder>{
    Context context;

    /* 라이브 방송일때 채팅 저장 Array */
    ArrayList<JSONObject> livechatArray;

    /* VOD 재생할때 해당 VOD 에 해당하는 채팅 저장 Array */
    ArrayList<ChatItemVO> vodChatArray;

    /* 라이브 방송인지, VOD 재생인지 판단하는 Flag */
    boolean isLive;

    String imagePath = "http://15.164.102.182/";


    /* VOD 재생시에 보일 채팅 목록 받는 생성자 */
    public Chat_Adapter(Context context, ArrayList<ChatItemVO> vodChatArray, boolean isLive) {
        this.context = context;
        this.vodChatArray = vodChatArray;
        this.isLive = isLive;
    }

    /* 라이브 영상시에 보일 채팅 목록 받는 생성자 */
    public Chat_Adapter(Context context, boolean isLive, ArrayList<JSONObject> livechatArray) {
        this.context = context;
        this.livechatArray = livechatArray;
        this.isLive = isLive;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.chatting_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(isLive) {
            /* 라이브 방송일때 채팅 아이템 */
            try {
                if(livechatArray.get(position).getString("sendProfile").equals("null")) {
                    holder.chat_profile.setImageResource(R.drawable.ic_clickuser);
                }else {
                    Glide.with(context.getApplicationContext())
                            .load(imagePath + livechatArray.get(position).getString("sendProfile"))
                            .into(holder.chat_profile);
                }

                holder.chat_Name.setText(livechatArray.get(position).getString("sendUserName"));
                holder.chat_message.setText(livechatArray.get(position).getString("message"));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            /* VOD 재생할때 채팅 아이템 */
            if(vodChatArray.get(position).getSendProfile().equals("null")) {
                holder.chat_profile.setImageResource(R.drawable.ic_clickuser);
            }else {
                Glide.with(context.getApplicationContext())
                        .load(imagePath + vodChatArray.get(position).getSendProfile())
                        .into(holder.chat_profile);
            }

            holder.chat_Name.setText(vodChatArray.get(position).getSendUserName());
            holder.chat_message.setText(vodChatArray.get(position).getMessage());
        }
    }

    @Override
    public int getItemCount() {
        int chatsize = 0;
        if(isLive) {
            chatsize = livechatArray.size();
        }else {
            chatsize = vodChatArray.size();
        }
        return chatsize;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView chat_profile;
        public TextView chat_time, chat_Name, chat_message;

        public ViewHolder(View view) {
            super(view);
            chat_profile = (CircleImageView)view.findViewById(R.id.chat_profile);
            chat_Name = (TextView)view.findViewById(R.id.userName);
            chat_message = (TextView)view.findViewById(R.id.userMessage);
        }
    }
}
