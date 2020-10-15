package com.passwordchat;

import android.view.View;

public interface InboxMessageClickListener {

    public void onClick(View view, int position);

    public void onLongClick(View view, int position);
}
