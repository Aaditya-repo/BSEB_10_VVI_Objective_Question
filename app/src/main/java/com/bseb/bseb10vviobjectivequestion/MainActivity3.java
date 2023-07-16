package com.bseb.bseb10vviobjectivequestion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;
import com.mahfa.dnswitch.DayNightSwitch;
import com.mahfa.dnswitch.DayNightSwitchListener;

import java.io.File;
import java.time.DayOfWeek;

public class MainActivity3 extends AppCompatActivity {

    private PDFView pdfView;
    private String url;
    String title;
    TextView textView;
    ImageView back;

    DayNightSwitch dayNightSwitch;
    Custom_pdf_load load;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    AdView mAdView;
    AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main3);
        pdfView = findViewById(R.id.pdf);
        textView = findViewById(R.id.setTitle2);
        dayNightSwitch = findViewById(R.id.day_night_switch);
        back = findViewById(R.id.back_btn1);
        load = new Custom_pdf_load(this);
        load.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSupportNavigateUp();
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();


        url = getIntent().getStringExtra("links");
        title = getIntent().getStringExtra("title");
        textView.setText(title);
        load.show();
        loadFile(url);
        setDayNightSwitch();
    }


    private void loadFile(String url) {
        FileLoader.with(this)
                .load(url)
                .fromDirectory("beta", FileLoader.DIR_EXTERNAL_PRIVATE)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {

                        File loadedFile = response.getBody();
                        load.dismiss();
                        pdfView.fromFile(loadedFile)
                                .password(null)
                                .defaultPage(0)
                                .enableSwipe(true)
                                .swipeHorizontal(false)
                                .enableDoubletap(true)
                                .spacing(2)
                                .scrollHandle(new DefaultScrollHandle(getApplicationContext()))
                                .load();
                        mAdView.loadAd(adRequest);

                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                        Toast.makeText(MainActivity3.this, "Error"+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setDayNightSwitch()
    {

        dayNightSwitch.setListener(new DayNightSwitchListener() {
            @Override
            public void onSwitch(boolean is_night) {

                if (is_night)
                {
                    pdfView.setNightMode(true);
                }
                else
                {
                    pdfView.setNightMode(false);
                }
            }
        });

    }

}