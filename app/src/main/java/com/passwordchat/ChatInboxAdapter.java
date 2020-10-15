package com.passwordchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatInboxAdapter extends RecyclerView.Adapter<ChatInboxAdapter.ChatInboxViewHolder> implements Filterable {

    private Context context;
    private List<String> inboxes;
    private List<String> inboxesFull;
    private ChatInboxClickListener chatInboxClickListener;

    public ChatInboxAdapter(Context context, List<String> inboxes, ChatInboxClickListener chatInboxClickListener) {
        this.context = context;
        this.inboxes = inboxes;
        this.inboxesFull = new ArrayList<>(inboxes);
        this.chatInboxClickListener = chatInboxClickListener;
    }

    @NonNull
    @Override
    public ChatInboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.chat_inbox_layout, parent, false);
        return new ChatInboxViewHolder(view, chatInboxClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatInboxViewHolder holder, int position) {

        holder.title.setText(inboxes.get(position));
    }

    @Override
    public int getItemCount() {
        return inboxes.size();
    }

    public class ChatInboxViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView img;
        public TextView title;

        private ChatInboxClickListener chatInboxClickListener;

        public ChatInboxViewHolder(@NonNull View itemView, ChatInboxClickListener chatInboxClickListener) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.chat_inbox_title);
            img = (ImageView)itemView.findViewById(R.id.chat_inbox_img);
            this.chatInboxClickListener = chatInboxClickListener;

            //Click Event
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            chatInboxClickListener.onClick(view, getAdapterPosition());
        }
    }

    //Filter
    @Override
    public Filter getFilter() {
        return inboxFilter;
    }

    Filter inboxFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<String> filterInboxes = new ArrayList<>();

            if(charSequence == null && charSequence.length() == 0){
                filterInboxes.addAll(inboxesFull);
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(String item: inboxesFull){

                    if(item.toLowerCase().trim().contains(filterPattern)){
                        filterInboxes.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filterInboxes;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            inboxes.clear();
            inboxes.addAll((List<String>)filterResults.values);
            notifyDataSetChanged();
        }
    };
}
