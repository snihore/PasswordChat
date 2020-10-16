package com.passwordchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChatSettingsBottomsheetDialog extends BottomSheetDialogFragment {

    private Context context;

    public ChatSettingsBottomsheetDialog(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.chat_activity_bottom_sheet,
                container, false);

        TextView logout = (TextView) view.findViewById(R.id.chat_bottom_sheet_logout);
        TextView extraSecurity = (TextView) view.findViewById(R.id.chat_bottom_sheet_extra_security);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                dismiss();
                Intent intent = new Intent(context, SignupActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

        extraSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                Intent intent = new Intent(context, ExtraSecurityActivity.class);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }
}
