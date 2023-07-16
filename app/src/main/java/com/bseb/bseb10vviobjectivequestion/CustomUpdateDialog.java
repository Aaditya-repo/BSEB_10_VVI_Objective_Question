package com.bseb.bseb10vviobjectivequestion;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class CustomUpdateDialog extends Dialog {

    Context context;
    FirebaseRemoteConfig config;

    public CustomUpdateDialog( Context context, FirebaseRemoteConfig config) {
        super(context);
        this.context = context;
        this.config = config;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.update_dialog);
        TextView textTitle = findViewById(R.id.title_1);
        TextView textBody = findViewById(R.id.dialog_body);
        Button buttonSkip = findViewById(R.id.skip);
        Button buttonUpdate = findViewById(R.id.Update);

        textTitle.setText(config.getString(RemoteUtil.TITLE));
        textBody.setText(config.getString(RemoteUtil.WHATNEW));

        if (config.getBoolean(RemoteUtil.ISFORCE))
        {
            buttonSkip.setVisibility(View.GONE);
            setCancelable(false);

        }
        else buttonSkip.setVisibility(View.VISIBLE);


        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName()));
                context.startActivity(intent);
            }
        });



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (config.getBoolean(RemoteUtil.ISFORCE)) ((Activity)context).finish();

    }
}
