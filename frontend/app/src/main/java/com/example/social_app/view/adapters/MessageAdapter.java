package com.example.social_app.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_app.R;
import com.example.social_app.model.MessageResponse;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    private List<MessageResponse> messageList;
    private int currentUserId;

    public MessageAdapter(List<MessageResponse> messageList, int currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }


    @Override
    public int getItemViewType(int position) {
        MessageResponse message = messageList.get(position);

        int senderId = (message.getNguoi_gui() != null) ? message.getNguoi_gui().getMaNguoiDung() : message.getMaNguoiGui();
        if (senderId == currentUserId) {
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageResponse message = messageList.get(position);
        if (holder instanceof SentMessageHolder) {
            ((SentMessageHolder) holder).bind(message);
        } else if (holder instanceof ReceivedMessageHolder) {
            ((ReceivedMessageHolder) holder).bind(message);
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView txtMessageSent;

        SentMessageHolder(View itemView) {
            super(itemView);
            txtMessageSent = itemView.findViewById(R.id.txtMessageSent);
        }

        void bind(MessageResponse message) {
            txtMessageSent.setText(message.getNoiDung());
        }
    }

    static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView txtMessageReceived;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            txtMessageReceived = itemView.findViewById(R.id.txtMessageReceived);
        }

        void bind(MessageResponse message) {
            txtMessageReceived.setText(message.getNoiDung());
        }
    }
}
