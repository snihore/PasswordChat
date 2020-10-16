package com.passwordchat;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InboxMessageRecyclerViewAdapter extends RecyclerView.Adapter<InboxMessageRecyclerViewAdapter.InboxMessageViewHolder> {

    private Context context;
    private List<Message> messages;
    private InboxMessageClickListener inboxMessageClickListener;

    public InboxMessageRecyclerViewAdapter(Context context, List<Message> messages, InboxMessageClickListener inboxMessageClickListener) {
        this.context = context;
        this.messages = messages;
        this.inboxMessageClickListener = inboxMessageClickListener;


    }


    @NonNull
    @Override
    public InboxMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.inbox_message_layout, parent, false);
        return new InboxMessageViewHolder(view, inboxMessageClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxMessageViewHolder holder, int position) {

        holder.msgTextView.setText(messages.get(position).getMsg());

        //Check isEncrypted
        if(messages.get(position).isEncrypted()){
            //Encrypted
            holder.messageImg.setImageResource(R.drawable.ic_baseline_visibility_24);
            holder.msgTextView.setTextColor(Color.parseColor("#ffffff"));
            holder.constraintLayout.setBackgroundResource(R.drawable.round_layout_bg02);
        }else {
            //Decrypted
            holder.messageImg.setImageResource(R.drawable.logo);
            holder.msgTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.constraintLayout.setBackgroundResource(R.drawable.round_layout_bg);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class InboxMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView msgTextView;
        private InboxMessageClickListener messageClickListener;
        public ImageView messageImg;
        public ConstraintLayout constraintLayout;

        public InboxMessageViewHolder(@NonNull View itemView, InboxMessageClickListener listener) {
            super(itemView);

            msgTextView = (TextView)itemView.findViewById(R.id.message_msg);
            messageImg = (ImageView)itemView.findViewById(R.id.message_img);
            constraintLayout = (ConstraintLayout)itemView.findViewById(R.id.inbox_message_layout);

            messageClickListener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }


        @Override
        public void onClick(View view) {
            messageClickListener.onClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            messageClickListener.onLongClick(view, getAdapterPosition());
            return true;
        }
    }
}
