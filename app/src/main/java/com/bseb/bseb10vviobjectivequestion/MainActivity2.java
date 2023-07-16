package com.bseb.bseb10vviobjectivequestion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class MainActivity2 extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MaxAdListener {

    // WebView
    String url = "";
    String aurl = "";
    String burl = "";
    String title = "";
    WebView webView;
    SwipeRefreshLayout swipeRefreshLayout;
    Custom_load load;
    TextView textView;
    Intent intent;

    AdRequest adRequest;
    ImageView imageView;

    private MaxInterstitialAd interstitialAd;
    private int retryAttempt;
    InterstitialAd mInterstitialAd;

    CustomTabsIntent customTabsIntent;
    Uri uri;

    String Provider;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        webView = findViewById(R.id.webview1);
        textView = findViewById(R.id.settext1);
        imageView = findViewById(R.id.back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSupportNavigateUp();
            }
        });
        swipeRefreshLayout = findViewById(R.id.swipe1);
        loadWebView();
        load = new Custom_load(this);
        load.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        builder.setInstantAppsEnabled(true);
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.status));
        customTabsIntent = builder.build();
        customTabsIntent.intent.setPackage("com.android.chrome");

        AppLovinSdk.getInstance(this).setMediationProvider("max");
        AppLovinSdk.initializeSdk(this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                // AppLovin SDK is initialized, start loading ads
                loadAd();
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });



        DatabaseReference db = FirebaseDatabase.getInstance().getReference("AdsController");
        db.child("BSEB Class 10 VVI Objective").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Provider = snapshot.child("AdProvider").getValue().toString();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity2.this, error.toString(), Toast.LENGTH_SHORT).show();

            }
        });


    }


    private void loadWebView() {

        webView.loadUrl(getIntent().getStringExtra("links"));
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
               /* contentView.setVisibility(View.GONE);
                nocontent.setVisibility(View.VISIBLE);*/
                load.show();
                webView.setVisibility(View.GONE);
                super.onReceivedError(view, request, error);
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                load.dismiss();
                webView.setVisibility(View.VISIBLE);

            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {

                //    Toast.makeText(MainActivity.this, consoleMessage.message(), Toast.LENGTH_SHORT).show();

                uri = Uri.parse(consoleMessage.message());

                if (url.equals("true")) {
                    Intent intent = new Intent(MainActivity2.this, MainActivity2.class);
                    intent.putExtra("links", consoleMessage.message());
                    url = "false";
                    startActivity(intent);
                }
                if (consoleMessage.message().equals("url")) {
                    url = "true";
                }
                if (aurl.equals("true")) {
                    // for pdf link
                    intent = new Intent(MainActivity2.this, MainActivity3.class);
                    intent.putExtra("links", consoleMessage.message());
                    aurl = "false";
                }
                if (consoleMessage.message().equals("aurl")) {
                    aurl = "true";
                }

                if (title.equals("true")) {
                    intent.putExtra("title", consoleMessage.message());
                    title = "false";
                    load.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adRequest = new AdRequest.Builder().build();
                            setPdfAd();
                        }
                    }, 2000);

                    // for the pdf title
                }
                if (consoleMessage.message().equals("title")) {
                    title = "true";
                }
                if (burl.equals("true")) {
                    load.show();
                    // for custom tab intent
                    WhoWin(uri);
                    burl = "false";
                }
                if (consoleMessage.message().equals("burl")) {
                    burl = "true";
                }


                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress < 100) {
                    load.show();
                }
                if (newProgress == 100) {
                    load.dismiss();
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                textView.setText(title);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);

    }


    @Override
    public void onRefresh() {
        webView.reload();
    }

    private void loadAd() {

        interstitialAd = new MaxInterstitialAd("233271d1a50204e8", this);
        interstitialAd.setListener((MaxAdListener) this);

        // Load the first ad
        interstitialAd.loadAd();


    }


    @Override
    public void onAdLoaded(MaxAd ad) {

        // Toast.makeText(this, "loaded", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAdDisplayed(MaxAd ad) {

    }

    @Override
    public void onAdHidden(MaxAd ad) {

        //  Intent intent = new Intent(MainActivity2.this,MainActivity.class);
        // startActivity(intent);
        // Interstitial ad is hidden. Pre-load the next ad
        customTabsIntent.launchUrl(MainActivity2.this, uri);
        interstitialAd.loadAd();

    }

    @Override
    public void onAdClicked(MaxAd ad) {

    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {

        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                interstitialAd.loadAd();
            }
        }, delayMillis);
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        // Interstitial ad failed to display. AppLovin recommends that you load the next ad.
        interstitialAd.loadAd();
    }


    private void WhoWin(Uri uri) {

        if (Provider.equals("G")) {
          //  Toast.makeText(this, "Google", Toast.LENGTH_SHORT).show();
            adRequest = new AdRequest.Builder().build();
            setAds(uri);

        } else if (Provider.equals("A")) {
         ///   Toast.makeText(this, "Applovin", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    load.dismiss();
                    if (interstitialAd.isReady()) {
                        interstitialAd.showAd();
                    }
                }
            }, 2000);

        } else {
           // Toast.makeText(this, "none", Toast.LENGTH_SHORT).show();
            load.dismiss();
            customTabsIntent.launchUrl(MainActivity2.this,uri);
        }

    }

    private void setAds(Uri uri) {

        InterstitialAd.load(this,"ca-app-pub-6247363290195980/7472002505", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        //    Toast.makeText(MainActivity2.this, "Ad Loaded..", Toast.LENGTH_SHORT).show();
                        mInterstitialAd.show(MainActivity2.this);
                        load.dismiss();
                        //  Log.i(TAG, "onAdLoaded");

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                //  Log.d(TAG, "Ad was clicked.");
                                //  Toast.makeText(MainActivity2.this, "Ad Clicked..", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                //    Log.d(TAG, "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;
                                //   Toast.makeText(MainActivity2.this, "Ad Dismissed..", Toast.LENGTH_SHORT).show();
                                load.dismiss();
                                customTabsIntent.launchUrl(MainActivity2.this,uri);


                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                //   Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                                load.dismiss();
                                customTabsIntent.launchUrl(MainActivity2.this,uri);
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                //  Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                //   Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        //   Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                        load.dismiss();
                        customTabsIntent.launchUrl(MainActivity2.this,uri);

                    }
                });

    }


    private void setPdfAd() {

        InterstitialAd.load(this,"ca-app-pub-6247363290195980/7472002505", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        //    Toast.makeText(MainActivity2.this, "Ad Loaded..", Toast.LENGTH_SHORT).show();
                        mInterstitialAd.show(MainActivity2.this);
                        load.dismiss();
                        //  Log.i(TAG, "onAdLoaded");

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                //  Log.d(TAG, "Ad was clicked.");
                                //  Toast.makeText(MainActivity2.this, "Ad Clicked..", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                //    Log.d(TAG, "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;
                                //   Toast.makeText(MainActivity2.this, "Ad Dismissed..", Toast.LENGTH_SHORT).show();
                                load.dismiss();
                               startActivity(intent);


                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                //   Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                                load.dismiss();
                                startActivity(intent);
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                //  Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                //   Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        //   Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                        load.dismiss();
                        startActivity(intent);
                    }
                });

    }

}