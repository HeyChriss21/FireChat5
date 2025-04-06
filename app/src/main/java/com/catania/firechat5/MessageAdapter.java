package com.catania.firechat5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {
    private static final int MSG_TYPE_RIGHT = 0; //messaggio inviato
    private static final int MSG_TYPE_LEFT = 1;// messaggio ricevuto

    private Context context;
    private List<MessageModel> messages;

    public MessageAdapter(Context context) {
        this.context = context;
        this.messages = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel message = messages.get(position);
        if(holder.getClass() == SentViewHolder.class){
            SentViewHolder sentViewHolder = (SentViewHolder) holder;
            sentViewHolder.message.setText(message.getMessage());
        } else{
            ReceivedViewHolder receivedViewHolder = (ReceivedViewHolder) holder;
            receivedViewHolder.message.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void add(MessageModel messageModel) {
        messages.add(messageModel);
        notifyItemInserted(messages.size() - 1);
    }
    public void clear() {
        messages.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return MSG_TYPE_RIGHT;
        } else{
            return MSG_TYPE_LEFT;
        }
    }

    class ReceivedViewHolder extends RecyclerView.ViewHolder{
        TextView message;

        public ReceivedViewHolder(@NonNull View itemView){
            super(itemView);
            message = itemView.findViewById(R.id.message_text_received);
        }
    }
    class SentViewHolder extends RecyclerView.ViewHolder{
        TextView message;

        public SentViewHolder(@NonNull View itemView){
            super(itemView);
            message = itemView.findViewById(R.id.message_text_sent);
        }
    }
}