package com.bseb.bseb10vviobjectivequestion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener {

    // WebView
    String url = "";
    String tab = "";
    WebView webView;
    SwipeRefreshLayout swipeRefreshLayout;
    Custom_load load;

    // for drawer menu
    // Drawer Menu
    ImageView imageBtn;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    static final float END_SCALE = 0.5f;
    LinearLayout contentView;
    public static int UPDATE_CODE = 22;
    // In app review code
    ReviewManager manager;
    ReviewInfo reviewInfo;
    private Date FutureDate, future;


    // Initialize Firebase Firestore
    FirebaseFirestore db;

    // Get the current app version code
    int currentVersionCode = BuildConfig.VERSION_CODE;

    private Boolean hideButton;

    LinearLayout ContentView, nocontent;
    Button button;

    ImageView shareIcon;

   CustomTabsIntent customTabsIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);
        webView.setVerticalScrollBarEnabled(false);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation);
        imageBtn = findViewById(R.id.menu_btn);
        ContentView = findViewById(R.id.internet);
        nocontent = findViewById(R.id.no_internet);
        load = new Custom_load(this);
        contentView = findViewById(R.id.contentV);
        load.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        swipeRefreshLayout = findViewById(R.id.swipe);
        button = findViewById(R.id.reload);

        shareIcon = findViewById(R.id.shareIcon);
        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("text/plain");
                intent1.putExtra(Intent.EXTRA_TEXT,"Download this app using this link..\n\n https://play.google.com/store/apps/details?id="+ getPackageName());
                startActivity(intent1);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefresh();
                ContentView.setVisibility(View.VISIBLE);
                nocontent.setVisibility(View.GONE);
            }
        });
        loadWebView();
        navigationDrawer();
        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();

        DocumentReference versionDocRef = db.collection("Apps").document("com_bseb_bseb10vviobjectivequestion");

        versionDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    long latestVersionCode = documentSnapshot.getLong("versionCode");
                    hideButton = documentSnapshot.getBoolean("is_force");
                    if (latestVersionCode > currentVersionCode) {
                        // Show the update dialog
                        showUpdateDialog();
                    }
                }
            }
        });


        OneSignal.setNotificationOpenedHandler(new OneSignal.OSNotificationOpenedHandler() {
            @Override
            public void notificationOpened(OSNotificationOpenedResult osNotificationOpenedResult) {
                OSNotification notification = osNotificationOpenedResult.getNotification();
                JSONObject object = notification.getAdditionalData();
                if (object != null)
                {
                    String data = object.optString("url","none");
                    Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                    intent.putExtra("links",data);
                    startActivity(intent);
                }
                else {



                }
            }
        });


        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        builder.setInstantAppsEnabled(true);
        builder.setToolbarColor(ContextCompat.getColor(this,R.color.tra));
        customTabsIntent = builder.build();
        customTabsIntent.intent.setPackage("com.android.chrome");


    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Required");
        builder.setMessage("A new version of the app is available. Please update to the latest version.");


        if (hideButton.equals(true)){

            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Redirect the user to the Play Store for the update
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" +getPackageName()));
                    startActivity(intent);
                }
            });

        }
        else {

            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Redirect the user to the Play Store for the update
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" +getPackageName()));
                    startActivity(intent);
                }
            });

            builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

        }


        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
    }


    private void loadWebView() {

        webView.loadUrl("https://bsebtarget.com//BSEB/Class%2010th/new-home.php");
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient(){


            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                ContentView.setVisibility(View.GONE);
                nocontent.setVisibility(View.VISIBLE);
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
                webView.setVisibility(View.VISIBLE);
            }
        });

        webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {

                //    Toast.makeText(MainActivity.this, consoleMessage.message(), Toast.LENGTH_SHORT).show();

                Uri uri = Uri.parse(consoleMessage.message());

                if (url.equals("true"))
                {
                    Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                    intent.putExtra("links",consoleMessage.message());
                    url = "false";
                    startActivity(intent);
                }

                if (consoleMessage.message().equals("url"))
                {
                    url = "true";
                }

                if (tab.equals("true")){
                    // custom tab
                    customTabsIntent.launchUrl(MainActivity.this,uri);
                    tab = "false";
                }
                if (consoleMessage.message().equals("tab")){
                    tab = "true";
                }

                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                if (newProgress<100)
                {
                    load.show();
                }
                if (newProgress==100)
                {
                    load.dismiss();
                }

            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);

    }


    @Override
    public void onRefresh() {
        webView.reload();
    }


    private void navigationDrawer() {

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        //  navigationView.setCheckedItem(R.id.share);

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else drawerLayout.openDrawer(GravityCompat.START);
            }
        });



    }


    // Navigation Drawer item click
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {

            case R.id.more:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=8534749914765371345"));
                startActivity(intent);
                break;

            case R.id.share:
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("text/plain");
                intent1.putExtra(Intent.EXTRA_TEXT,"Download this app using this link..\n\n https://play.google.com/store/apps/details?id="+ getPackageName());
                startActivity(intent1);
                break;

            case R.id.rate:

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));

                }catch (ActivityNotFoundException e)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
                }

                break;

            case R.id.privacy:
                Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://howtodoanythinka.blogspot.com/2022/09/bseb-12-science-objective-2023.html"));
                startActivity(intent2);
                break;

            case R.id.exit:
                finishAffinity();
                break;
        }
        return true;
    }



    // It is for close navigation drawer
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else
            showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you want to exit the app ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Finish the current activity and exit the app
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog and continue with the app
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}