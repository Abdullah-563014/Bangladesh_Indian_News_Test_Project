package com.easysoftbd.bangladeshindiannews.ui.activities.my_webview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.easysoftbd.bangladeshindiannews.database_connection.ApiInterface;
import com.easysoftbd.bangladeshindiannews.database_connection.RetrofitClient;
import com.facebook.ads.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.data.local.DatabaseClient;
import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.favourite_list.FavouriteList;
import com.easysoftbd.bangladeshindiannews.databinding.ActivityWebViewBinding;
import com.easysoftbd.bangladeshindiannews.ui.activities.home.HomeActivity;
import com.easysoftbd.bangladeshindiannews.ui.activities.main.MainActivity;
import com.easysoftbd.bangladeshindiannews.ui.activities.no_internet.NoInternetActivity;
import com.easysoftbd.bangladeshindiannews.utils.CommonMethods;
import com.easysoftbd.bangladeshindiannews.utils.Constants;
import com.facebook.ads.InterstitialAd;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonElement;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubView;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.IUnityBannerListener;
import com.unity3d.services.banners.UnityBanners;
import com.unity3d.services.banners.view.BannerPosition;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebViewActivity extends AppCompatActivity {


    private ActivityWebViewBinding binding;
    private String currentUrl, currentPageTitle, currentCountryName="";
    private BroadcastReceiver onDownloadComplete;
    private long downloadID;
    private String url, downloadUrl, userAgent, contentDisposition, mimeType;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private int INPUT_FILE_REQUEST_CODE = 121, timeDiff=0, nonClickInterstitialTimeDiff=0,firstAdsShownTimeDiff=0;
    private boolean willLoadFirstInterstitialAds=true, willLoadFirstBannerAds=true,willWaitForSecondAds=false,pageFinishedFlag=false;
    private AlertDialog progressAlertDialog,permissionAlertDialog;
    private InterstitialAd facebookInterstitialAd;
    private AdsThread adsThread;
    private AdView facebookBannerAdView;
    private MoPubInterstitial moPubInterstitial;
    private MoPubView moPubBannerAdsView;
    private View unityBannerView;
    private com.google.android.gms.ads.AdView admobBannerAdsView;
    public com.google.android.gms.ads.InterstitialAd admobInsterstitialAd;
    private DatabaseReference databaseReference;
    private ConnectivityManager connectivityManager;
    private NewsDatabase newsDatabase;
    private CompositeDisposable compositeDisposable;
    private MyBroadCastReceiver myBroadCastReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_web_view);



        initializeAll();

        registerMyBroadCastReceiver();

        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("Url", null);
            willLoadFirstInterstitialAds=bundle.getBoolean("WillShowInterstitialAds",true);
        }
        if (url != null) {
            openWebPage(url);
        } else {
            Toast.makeText(this, "Your web url is not valid", Toast.LENGTH_SHORT).show();
        }


        initializeSwipeRefreshLayout();

        initializeDownloadManager();

        binding.addToFavouriteListTextView.setOnClickListener(view -> bookmarkAlertDialog());

        loadIpCheckingStatusFromDatabase();

        loadAdsPlatformNameFromDatabase();


    }


    private void loadIpCheckingStatusFromDatabase() {
        databaseReference.child("options").child("willCheckIpAddress").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getValue()!=null) {
                    String status=dataSnapshot.getValue(String.class);
                    CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.ipCheckSwitchKey,status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadAdsPlatformNameFromDatabase() {
        databaseReference.child("Ads").child("adsPlatformName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getValue()!=null) {
                    String platform=dataSnapshot.getValue(String.class);
                    CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.platformNameKey,platform);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initFacebookInterstitialAds() {
        if (facebookInterstitialAd != null) {
            facebookInterstitialAd.destroy();
            facebookInterstitialAd=null;
        }
//        facebookInterstitialAd = new InterstitialAd(this, getResources().getString(R.string.web_view_activity_facebook_interstitial_ads_code));
        facebookInterstitialAd = new InterstitialAd(this, Constants.facebookInterstitialAdsCode);
        facebookInterstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                if (facebookInterstitialAd != null) {
                    facebookInterstitialAd.destroy();
                    facebookInterstitialAd=null;
                }
                if (adsThread != null) {
                    adsThread.interrupt();
                    adsThread=null;
                }
                if (willWaitForSecondAds) {
                    CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.lastInterstitialAdsTimeForNonClickKey,CommonMethods.getCurrentTime());
                    willWaitForSecondAds=false;
                } else {
                    willWaitForSecondAds=true;
                }
                CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.firstAdsShownTimeKey,CommonMethods.getCurrentTime());
                calculateTimeDiffForNonClickAds();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Constants.platformName="unity";
                initUnityInterstitialAds();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (willLoadFirstInterstitialAds && currentCountryName!=null && currentCountryName.equalsIgnoreCase("bangladesh")) {
                    if (adsThread != null) {
                        adsThread.interrupt();
                        adsThread = null;
                    }
                    adsThread = new AdsThread();
                    adsThread.start();
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.lastAdsLoadedTimeKey,CommonMethods.getCurrentTime());
                CommonMethods.setBooleanToSharedPreference(WebViewActivity.this,Constants.willLoadFirstBannerAdsKey,false);
                if (facebookBannerAdView!=null){
                    facebookBannerAdView.destroy();
                    facebookBannerAdView=null;
                }
                calculateTimeDiff();
                RelativeLayout bannerRootLayout=findViewById(R.id.webViewActivityBannerAdsContainerId);
                bannerRootLayout.removeAllViews();
                bannerRootLayout.setVisibility(View.GONE);
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        facebookInterstitialAd.loadAd();
    }

    private void initFacebookBannerAdView() {
        if (facebookBannerAdView!=null){
            facebookBannerAdView.destroy();
            facebookBannerAdView=null;
        }
        facebookBannerAdView = new AdView(this, Constants.facebookBannerAdsCode, AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.webViewActivityFacebookBannerAdsContainerId);
        adContainer.setVisibility(View.VISIBLE);
        adContainer.addView(facebookBannerAdView);
        AdListener adListener=new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {
                CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.lastAdsLoadedTimeKey,CommonMethods.getCurrentTime());
                CommonMethods.setBooleanToSharedPreference(WebViewActivity.this,Constants.willLoadFirstBannerAdsKey,false);
                if (facebookBannerAdView!=null){
                    facebookBannerAdView.destroy();
                    facebookBannerAdView=null;
                }
                calculateTimeDiff();
                RelativeLayout bannerRootLayout=findViewById(R.id.webViewActivityBannerAdsContainerId);
                bannerRootLayout.removeAllViews();
                bannerRootLayout.setVisibility(View.GONE);
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };
        facebookBannerAdView.loadAd(facebookBannerAdView.buildLoadAdConfig().withAdListener(adListener).build());
    }

    private void showFacebookInterstitialAds() {
        if (willLoadFirstInterstitialAds && Constants.interstitialAdsFlagForNonClick && currentCountryName!=null && currentCountryName.equalsIgnoreCase("bangladesh")) {
            if (facebookInterstitialAd == null || !facebookInterstitialAd.isAdLoaded()) {
                return;
            }
            if (facebookInterstitialAd.isAdInvalidated()) {
                return;
            }
            facebookInterstitialAd.show();
        }
    }

    private void initMoPubInterstitialAds() {
        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(Constants.moPubInterstitialAdsCode)
                .withLogLevel(MoPubLog.LogLevel.NONE)
                .build();
        MoPub.initializeSdk(this, sdkConfiguration, () -> {
            moPubInterstitial = new MoPubInterstitial(WebViewActivity.this, Constants.moPubInterstitialAdsCode);
            moPubInterstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
                @Override
                public void onInterstitialLoaded(MoPubInterstitial interstitial) {

                }

                @Override
                public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                    Constants.platformName="unity";
                    initUnityInterstitialAds();
                }

                @Override
                public void onInterstitialShown(MoPubInterstitial interstitial) {

                }

                @Override
                public void onInterstitialClicked(MoPubInterstitial interstitial) {
                    CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.lastAdsLoadedTimeKey,CommonMethods.getCurrentTime());
                    CommonMethods.setBooleanToSharedPreference(WebViewActivity.this,Constants.willLoadFirstBannerAdsKey,false);
                    if (moPubBannerAdsView != null) {
                        MoPub.onPause(WebViewActivity.this);
                        MoPub.onStop(WebViewActivity.this);
                        moPubBannerAdsView.destroy();
                        moPubBannerAdsView = null;
                    }
                    calculateTimeDiff();
                    RelativeLayout bannerRootLayout=findViewById(R.id.webViewActivityBannerAdsContainerId);
                    bannerRootLayout.removeAllViews();
                    bannerRootLayout.setVisibility(View.GONE);
                }

                @Override
                public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                    if (willWaitForSecondAds) {
                        CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.lastInterstitialAdsTimeForNonClickKey,CommonMethods.getCurrentTime());
                        willWaitForSecondAds=false;
                    } else {
                        willWaitForSecondAds=true;
                    }
                    CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.firstAdsShownTimeKey,CommonMethods.getCurrentTime());
                    calculateTimeDiffForNonClickAds();
                }
            });
            moPubInterstitial.load();
        });
    }

    private void initMoPubBannerAds() {
        moPubBannerAdsView = (MoPubView) findViewById(R.id.webViewActivityMoPubBannerAdsContainerId);
        moPubBannerAdsView.setVisibility(View.VISIBLE);
        moPubBannerAdsView.setAdUnitId(Constants.moPubBannerAdsCode);
        moPubBannerAdsView.setBannerAdListener(new MoPubView.BannerAdListener() {
            @Override
            public void onBannerLoaded(MoPubView banner) {

            }

            @Override
            public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {

            }

            @Override
            public void onBannerClicked(MoPubView banner) {
                CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.lastAdsLoadedTimeKey,CommonMethods.getCurrentTime());
                CommonMethods.setBooleanToSharedPreference(WebViewActivity.this,Constants.willLoadFirstBannerAdsKey,false);
                if (moPubBannerAdsView != null) {
                    MoPub.onPause(WebViewActivity.this);
                    MoPub.onStop(WebViewActivity.this);
                    moPubBannerAdsView.destroy();
                    moPubBannerAdsView = null;
                }
                calculateTimeDiff();
                RelativeLayout bannerRootLayout=findViewById(R.id.webViewActivityBannerAdsContainerId);
                bannerRootLayout.removeAllViews();
                bannerRootLayout.setVisibility(View.GONE);
            }

            @Override
            public void onBannerExpanded(MoPubView banner) {

            }

            @Override
            public void onBannerCollapsed(MoPubView banner) {

            }
        });
        moPubBannerAdsView.setAutorefreshEnabled(true);
        moPubBannerAdsView.loadAd();
    }

    private void initUnityInterstitialAds() {
        UnityAds.initialize(WebViewActivity.this, Constants.unityAdsGameId, Constants.unityAdTestMode);
        UnityAds.addListener(new IUnityAdsListener() {
            @Override
            public void onUnityAdsReady(String s) {
                if (unityBannerView == null && UnityAds.isInitialized() && willLoadFirstBannerAds) {
                    initUnityBannerAds();
                }
            }

            @Override
            public void onUnityAdsStart(String s) {

            }

            @Override
            public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
                if (s.equalsIgnoreCase(Constants.unityInterstitialAdsCode)) {
//                    Utils.saveStringToStorage(WebViewActivity.this,Constants.lastAdsLoadedTimeKey,Utils.getCurrentTime());
//                    calculateTimeDiff();
                    if (willWaitForSecondAds) {
                        CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.lastInterstitialAdsTimeForNonClickKey,CommonMethods.getCurrentTime());
                        willWaitForSecondAds=false;
                    } else {
                        willWaitForSecondAds=true;
                    }
                    CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.firstAdsShownTimeKey,CommonMethods.getCurrentTime());
                    calculateTimeDiffForNonClickAds();
                }
            }

            @Override
            public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

            }
        });
        if (willLoadFirstInterstitialAds) {
            UnityAds.load(Constants.unityInterstitialAdsCode);
        }
    }

    private void initUnityBannerAds() {
        UnityBanners.setBannerListener(new IUnityBannerListener() {
            @Override
            public void onUnityBannerLoaded(String s, View view) {
                unityBannerView = view;
                ViewGroup viewGroup = ((ViewGroup) findViewById(R.id.webViewActivityBannerAdsContainerId));
                viewGroup.removeAllViews();
                viewGroup.addView(view);
            }

            @Override
            public void onUnityBannerUnloaded(String s) {
                unityBannerView = null;
            }

            @Override
            public void onUnityBannerShow(String s) {

            }

            @Override
            public void onUnityBannerClick(String s) {
                CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.lastAdsLoadedTimeKey,CommonMethods.getCurrentTime());
                CommonMethods.setBooleanToSharedPreference(WebViewActivity.this,Constants.willLoadFirstBannerAdsKey,false);
                if (unityBannerView != null) {
                    UnityBanners.destroy();
                    unityBannerView = null;
                }
                calculateTimeDiff();
                RelativeLayout bannerRootLayout=findViewById(R.id.webViewActivityBannerAdsContainerId);
                bannerRootLayout.removeAllViews();
                bannerRootLayout.setVisibility(View.GONE);
            }

            @Override
            public void onUnityBannerHide(String s) {

            }

            @Override
            public void onUnityBannerError(String s) {
                unityBannerView = null;
            }
        });
        UnityBanners.destroy();
        if (unityBannerView == null) {
            UnityBanners.setBannerPosition(BannerPosition.BOTTOM_CENTER);
            UnityBanners.loadBanner(WebViewActivity.this, Constants.unityBannerAdsCode);
        }
    }

    private void initAdmobInterstitialAd() {
        admobInsterstitialAd = new com.google.android.gms.ads.InterstitialAd(this);
        admobInsterstitialAd.setAdUnitId(Constants.adMobInterstitialAdsCode);
        admobInsterstitialAd.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdClosed() {
                if (willWaitForSecondAds) {
                    CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.lastInterstitialAdsTimeForNonClickKey,CommonMethods.getCurrentTime());
                    willWaitForSecondAds=false;
                } else {
                    willWaitForSecondAds=true;
                }
                CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.firstAdsShownTimeKey,CommonMethods.getCurrentTime());
                calculateTimeDiffForNonClickAds();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Constants.platformName="unity";
                initUnityInterstitialAds();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.lastAdsLoadedTimeKey,CommonMethods.getCurrentTime());
                CommonMethods.setBooleanToSharedPreference(WebViewActivity.this,Constants.willLoadFirstBannerAdsKey,false);
                if (admobBannerAdsView != null) {
                    admobBannerAdsView.pause();
                    admobBannerAdsView.destroy();
                    admobBannerAdsView=null;
                }
                calculateTimeDiff();
                RelativeLayout bannerRootLayout=findViewById(R.id.webViewActivityBannerAdsContainerId);
                bannerRootLayout.removeAllViews();
                bannerRootLayout.setVisibility(View.GONE);
            }
        });
        admobInsterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void initAdMobBannerAds() {
        admobBannerAdsView = new com.google.android.gms.ads.AdView(WebViewActivity.this);
        admobBannerAdsView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        admobBannerAdsView.setAdUnitId(Constants.admobBannerAdsCode);
        RelativeLayout rootLayout = findViewById(R.id.webViewActivityBannerAdsContainerId);
        rootLayout.removeAllViews();
        rootLayout.addView(admobBannerAdsView);
        admobBannerAdsView.setAdListener(new com.google.android.gms.ads.AdListener(){

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                CommonMethods.setStringToSharedPreference(WebViewActivity.this,Constants.lastAdsLoadedTimeKey,CommonMethods.getCurrentTime());
                CommonMethods.setBooleanToSharedPreference(WebViewActivity.this,Constants.willLoadFirstBannerAdsKey,false);
                if (admobBannerAdsView != null) {
                    admobBannerAdsView.pause();
                    admobBannerAdsView.destroy();
                    admobBannerAdsView=null;
                }
                calculateTimeDiff();
                rootLayout.removeAllViews();
                rootLayout.setVisibility(View.GONE);
            }
        });
        admobBannerAdsView.loadAd(new AdRequest.Builder().build());
    }

    private void showInterstitialAds() {
        if (!Constants.willCheckIpAddress || Constants.platformName.equalsIgnoreCase("unity")) {
            currentCountryName="bangladesh";
        }
        if (willLoadFirstInterstitialAds && currentCountryName!=null && currentCountryName.equalsIgnoreCase("bangladesh") && Constants.interstitialAdsFlagForNonClick) {
            if (Constants.platformName.equalsIgnoreCase("mopub")) {
                if (moPubInterstitial!=null && moPubInterstitial.isReady()) {
                    moPubInterstitial.show();
                }
            } else if (Constants.platformName.equalsIgnoreCase("unity")) {
                if (UnityAds.isReady(Constants.unityInterstitialAdsCode)) {
                    UnityAds.show(WebViewActivity.this, Constants.unityInterstitialAdsCode);
                }
            } else if (Constants.platformName.equalsIgnoreCase("admob")) {
                if (admobInsterstitialAd!=null && admobInsterstitialAd.isLoaded()) {
                    admobInsterstitialAd.show();
                }
            }
        }
    }

    private void destroyAllAds() {
        RelativeLayout bannerRootLayout=findViewById(R.id.webViewActivityBannerAdsContainerId);
        bannerRootLayout.removeAllViews();
        bannerRootLayout.setVisibility(View.GONE);
        if (facebookInterstitialAd != null) {
            facebookInterstitialAd.destroy();
            facebookInterstitialAd=null;
        }
        if (adsThread != null) {
            adsThread.interrupt();
            adsThread=null;
        }
        if (facebookBannerAdView!=null){
            facebookBannerAdView.destroy();
            facebookBannerAdView=null;
        }
        MoPub.onPause(WebViewActivity.this);
        MoPub.onStop(WebViewActivity.this);
        if (moPubInterstitial != null) {
            moPubInterstitial.destroy();
            moPubInterstitial = null;
        }
        if (moPubBannerAdsView != null) {
            moPubBannerAdsView.destroy();
            moPubBannerAdsView = null;
        }
        if (unityBannerView != null) {
            UnityBanners.destroy();
            unityBannerView = null;
        }
        if (admobBannerAdsView != null) {
            admobBannerAdsView.pause();
            admobBannerAdsView.destroy();
            admobBannerAdsView=null;
        }
    }

    private void calculateTimeDiffForNonClickAds() {
        String firstAdsShownTime=CommonMethods.getStringFromSharedPreference(WebViewActivity.this,Constants.firstAdsShownTimeKey,"0");
        if (firstAdsShownTime!=null) {
            String diff=CommonMethods.getMinDifBetweenToTime(firstAdsShownTime,CommonMethods.getCurrentTime());
            if (diff!=null) {
                firstAdsShownTimeDiff=Integer.parseInt(diff);
            }
        } else {
            firstAdsShownTimeDiff=1;
        }


        String lastLoadedTime=CommonMethods.getStringFromSharedPreference(WebViewActivity.this,Constants.lastInterstitialAdsTimeForNonClickKey,"0");
        if (lastLoadedTime!=null) {
            String diff=CommonMethods.getMinDifBetweenToTime(lastLoadedTime,CommonMethods.getCurrentTime());
            if (diff!=null) {
                nonClickInterstitialTimeDiff=Integer.parseInt(diff);
            }
        } else {
            nonClickInterstitialTimeDiff=5;
        }
        Constants.interstitialAdsFlagForNonClick= nonClickInterstitialTimeDiff >= 5;
    }

    private void calculateTimeDiff() {
        willLoadFirstBannerAds=CommonMethods.getBooleanFromSharedPreference(WebViewActivity.this,Constants.willLoadFirstBannerAdsKey,true);
        String lastLoadedTime=CommonMethods.getStringFromSharedPreference(WebViewActivity.this,Constants.lastAdsLoadedTimeKey,"0");
        if (lastLoadedTime!=null) {
            String diff=CommonMethods.getTimeDifBetweenToTime(lastLoadedTime,CommonMethods.getCurrentTime());
            if (diff!=null) {
                timeDiff=Integer.parseInt(diff);
            }
        } else {
            timeDiff=1;
        }
        willLoadFirstInterstitialAds= timeDiff >= 1;
        if (willLoadFirstInterstitialAds) {
            CommonMethods.setBooleanToSharedPreference(WebViewActivity.this,Constants.willLoadFirstBannerAdsKey,true);
            willLoadFirstBannerAds=true;
        }
        if (willLoadFirstInterstitialAds || willLoadFirstBannerAds) {
            if (Constants.willCheckIpAddress) {
                getCurrentIpInfo();
            } else {
                loadSpecificAds();
            }
        }
    }

    private void getCurrentIpInfo() {
        ApiInterface apiInterface= RetrofitClient.getApiClient().create(ApiInterface.class);
        Call<JsonElement> call = apiInterface.getIpInfo();
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {
                            JSONObject rootObject=new JSONObject(response.body().toString());
                            currentCountryName=rootObject.getString("country");
                            if (currentCountryName.equalsIgnoreCase("bangladesh")) {
                                loadSpecificAds();
                            } else {
                                destroyAllAds();
                                if (Constants.platformName.equalsIgnoreCase("unity")) {
                                    initUnityInterstitialAds();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            destroyAllAds();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                destroyAllAds();
            }
        });
    }

    private void loadSpecificAds() {
        RelativeLayout bannerRootLayout=findViewById(R.id.webViewActivityBannerAdsContainerId);
        bannerRootLayout.setVisibility(View.VISIBLE);

        if (Constants.platformName.equalsIgnoreCase("facebook")) {
            if (willLoadFirstBannerAds) {
                initFacebookBannerAdView();
            }
        } else if (Constants.platformName.equalsIgnoreCase("mopub")) {
            if (willLoadFirstInterstitialAds) {
                initMoPubInterstitialAds();
            }
            if (willLoadFirstBannerAds) {
                initMoPubBannerAds();
            }
        } else if (Constants.platformName.equalsIgnoreCase("admob")) {
            if (willLoadFirstInterstitialAds) {
                initAdmobInterstitialAd();
            }
            if (willLoadFirstBannerAds) {
                initAdMobBannerAds();
            }
        } else if (Constants.platformName.equalsIgnoreCase("unity")) {
            initUnityInterstitialAds();
        }
    }

    private void initCustomProgressBar() {
        View view = getLayoutInflater().inflate(R.layout.custom_progress_bar, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(view);
        progressAlertDialog = builder.create();
    }

    private void showHideCustomProgressBar(boolean command) {
        if (command && !isFinishing()) {
            progressAlertDialog.show();
        } else {
            progressAlertDialog.dismiss();
        }
    }

    private class AdsThread extends Thread {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showHideCustomProgressBar(true);
                }
            });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showHideCustomProgressBar(false);
                    showFacebookInterstitialAds();
                }
            });
        }
    }

    private void initializeDownloadManager() {
        binding.myWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String currentUserAgent, String currentContentDisposition, String currentMimeType, long contentLength) {
                downloadUrl = url;
                userAgent = currentUserAgent;
                contentDisposition = currentContentDisposition;
                mimeType = currentMimeType;

                if (Build.VERSION.SDK_INT >= 23) {
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!hasPermission(WebViewActivity.this, permissions)) {
                        showPermissionAlertDialog();
                    } else {
                        startDownload();
                    }
                } else {
                    startDownload();
                }
            }
        });
    }

    private void showPermissionAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(WebViewActivity.this)
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.read_write_permission_details))
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        int PERMISSION_ALL = 1;
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        ActivityCompat.requestPermissions(WebViewActivity.this, permissions, PERMISSION_ALL);
                    }
                });
        permissionAlertDialog=builder.create();
        if (!isFinishing()){
            permissionAlertDialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownload();
            } else {
                Toast.makeText(this, "Permission denied, Please accept permission to download this file in your storage.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeSwipeRefreshLayout() {
        binding.webViewActivitySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!CommonMethods.haveInternet(connectivityManager)) {
                    Intent intent = new Intent(WebViewActivity.this, NoInternetActivity.class);
                    intent.putExtra("url", binding.myWebView.getUrl());
                    startActivity(intent);
                    finish();
                } else {
                    binding.myWebView.reload();
                }
                binding.webViewActivitySwipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void initializeAll() {
        databaseReference= FirebaseDatabase.getInstance().getReference();
        Sprite fadingCircle = new FadingCircle();
        binding.spinKit.setIndeterminateDrawable(fadingCircle);
        binding.spinKit.setVisibility(View.GONE);
        binding.webActivityProgressBar.setVisibility(View.GONE);
        connectivityManager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        newsDatabase= DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
        compositeDisposable=new CompositeDisposable();

        initCustomProgressBar();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void openWebPage(String myUrl) {
        binding.myWebView.getSettings().setJavaScriptEnabled(true);
        binding.myWebView.getSettings().setSupportMultipleWindows(false);
        binding.myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        binding.myWebView.getSettings().setDomStorageEnabled(true);
        binding.myWebView.getSettings().setAppCacheEnabled(true);
        binding.myWebView.getSettings().setDatabaseEnabled(true);
        binding.myWebView.getSettings().setUseWideViewPort(true);
        binding.myWebView.getSettings().setSupportZoom(true);
        binding.myWebView.getSettings().setLoadsImagesAutomatically(true);
        binding.myWebView.getSettings().setGeolocationEnabled(true);
        binding.myWebView.getSettings().setBuiltInZoomControls(true);
        binding.myWebView.getSettings().setDisplayZoomControls(false);
        binding.myWebView.getSettings().setLoadWithOverviewMode(false);
        binding.myWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
        binding.myWebView.setLongClickable(false);
        binding.myWebView.getSettings().setAllowFileAccess(true);
        binding.myWebView.getSettings().setAllowContentAccess(true);
        binding.myWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        binding.myWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        binding.myWebView.setWebChromeClient(new MyWebChromeClient());
        binding.myWebView.setWebViewClient(new MyWebViewClient());
        if (myUrl.contains("https://play.google.com/")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(myUrl));
            startActivity(intent);
            finish();
        } else {
            binding.myWebView.loadUrl(myUrl);
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        @Override
        public void onHideCustomView() {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = view;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = callback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        @Nullable
        @Override
        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result)
        {
            Log.e("alert triggered", message);
            return true;
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
//            WebView newWebView = new WebView(view.getContext());
//            newWebView.setWebViewClient(new MyWebViewClient());

//            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
//            transport.setWebView(webView);
//            resultMsg.sendToTarget();

            WebView.HitTestResult result = view.getHitTestResult();
            String data = result.getExtra();
            chooseBrowserToOpenUrl(data);

            return false;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            binding.webActivityProgressBar.setProgress(newProgress);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {

                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("*/*");

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

            return true;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    class MyWebViewClient extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (timeDiff>=1 && firstAdsShownTimeDiff>=1 && nonClickInterstitialTimeDiff>=5) {
                showInterstitialAds();
            }

            if (url.contains("play.google.com") || url.contains("youtube.com")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException e) {
                    return false;
                }
            } else if (url.contains("facebook.com")) {
                return openFacebookApp(url);
            } else {
                return false;
            }
        }

        @RequiresApi(24)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri url = request.getUrl();
            if (timeDiff>=1 && firstAdsShownTimeDiff>=1 && nonClickInterstitialTimeDiff>=5) {
                showInterstitialAds();
            }

            if (url.toString().contains("play.google.com") || url.toString().contains("youtube.com")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(url);
                    startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException e) {
                    return false;
                }
            } else if (url.toString().contains("facebook.com")) {
                return openFacebookApp(url.toString());
            } else {
                return false;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            binding.webActivityProgressBar.setVisibility(View.GONE);
            binding.spinKit.setVisibility(View.GONE);
            currentUrl = binding.myWebView.getUrl();
            currentPageTitle = view.getTitle();
            if (pageFinishedFlag) {
                if (willLoadFirstInterstitialAds && Constants.platformName.equalsIgnoreCase("facebook") && timeDiff>=1 && nonClickInterstitialTimeDiff>=5 && firstAdsShownTimeDiff>=1 && Constants.interstitialAdsFlagForNonClick && facebookInterstitialAd == null) {
                    initFacebookInterstitialAds();
                }
                calculateTimeDiffForNonClickAds();
                if (firstAdsShownTimeDiff>=1 && timeDiff>=1 && nonClickInterstitialTimeDiff>=5) {
                    calculateTimeDiff();
                }
                if (timeDiff>=1 && firstAdsShownTimeDiff>=1 && nonClickInterstitialTimeDiff>=5) {
                    showInterstitialAds();
                }
                pageFinishedFlag=false;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            binding.webActivityProgressBar.setVisibility(View.VISIBLE);
            binding.spinKit.setVisibility(View.VISIBLE);
            if (!CommonMethods.haveInternet(connectivityManager)) {
                try {
                    binding.myWebView.stopLoading();
                } catch (Exception e) {

                }
                Intent intent = new Intent(WebViewActivity.this, NoInternetActivity.class);
                startActivity(intent);
                if (binding.myWebView.canGoBack()) {
                    binding.myWebView.goBack();
                }
                pageFinishedFlag=true;
            }
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);

        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
//            if (error.getErrorCode() == ERROR_CONNECT || error.getErrorCode() == ERROR_TIMEOUT || error.getErrorCode() == ERROR_BAD_URL || error.getErrorCode() == ERROR_IO || error.getErrorCode() == ERROR_PROXY_AUTHENTICATION || error.getErrorCode() == ERROR_UNSAFE_RESOURCE || error.getErrorCode() == ERROR_UNSUPPORTED_AUTH_SCHEME || error.getErrorCode() == ERROR_UNSUPPORTED_SCHEME || error.getErrorCode() == SAFE_BROWSING_THREAT_UNKNOWN || error.getErrorCode() == ERROR_AUTHENTICATION) {
////                ERROR_HOST_LOOKUP removed temporary.
//                try {
//                    binding.myWebView.stopLoading();
//                } catch (Exception e) {
//
//                }
//                if (binding.myWebView.canGoBack()) {
//                    binding.myWebView.goBack();
//                }
//                Log.d(Constants.TAG,"error is:- "+error.getErrorCode());
//                Intent intent = new Intent(WebViewActivity.this, NoInternetActivity.class);
//                startActivity(intent);
//            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
//            if (errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT || errorCode == ERROR_BAD_URL || errorCode == ERROR_IO || errorCode == ERROR_PROXY_AUTHENTICATION || errorCode == ERROR_UNSAFE_RESOURCE || errorCode == ERROR_UNSUPPORTED_AUTH_SCHEME || errorCode == ERROR_UNSUPPORTED_SCHEME || errorCode == SAFE_BROWSING_THREAT_UNKNOWN || errorCode == ERROR_AUTHENTICATION) {
////                ERROR_HOST_LOOKUP removed temporary.
//                try {
//                    binding.myWebView.stopLoading();
//                } catch (Exception e) {
//
//                }
//                if (binding.myWebView.canGoBack()) {
//                    binding.myWebView.goBack();
//                }
//                Log.d(Constants.TAG,"error code is:- "+errorCode);
//                Intent intent = new Intent(WebViewActivity.this, NoInternetActivity.class);
//                startActivity(intent);
//            }
        }
    }

    private boolean openFacebookApp(String url) {
        try {
            String temporaryUrl;
            PackageManager pm = getPackageManager();
            PackageInfo info=pm.getPackageInfo("com.facebook.katana", PackageManager.GET_ACTIVITIES);
            int versionCode = info.versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                temporaryUrl= "fb://facewebmodal/f?href=" + url;
            } else {
                temporaryUrl=url;
            }
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
//            intent.setPackage("com.facebook.katana");
            intent.setData(Uri.parse(temporaryUrl));
            startActivity(Intent.createChooser(intent,"Please select a browser"));
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(Intent.createChooser(intent,"Please select a browser"));
                return true;
            } catch (ActivityNotFoundException exc) {
                return false;
            }
        }
    }

    private void bookmarkAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this)
                .setTitle("Add to favourite list")
                .setMessage("Do you want to add this page in your favourite list ?")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (currentPageTitle != null && currentUrl != null) {
                            addToFavouriteList();
                        } else {
                            Toast.makeText(WebViewActivity.this, "Current page is not loading successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    private void addToFavouriteList() {
        FavouriteList favouriteList = new FavouriteList();
        favouriteList.setTitle(currentPageTitle);
        favouriteList.setUrl(currentUrl);
        Completable.fromAction(()->{
            newsDatabase.favouriteListDao().insert(favouriteList);
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onComplete() {
                Toast.makeText(WebViewActivity.this, "Successfully added to favourite list.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }
        });
    }

    private void registerMyBroadCastReceiver() {
        if (myBroadCastReceiver==null) {
            myBroadCastReceiver=new MyBroadCastReceiver();
        }
        if (intentFilter==null) {
            intentFilter=new IntentFilter();
            intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            intentFilter.addAction(Constants.networkStateIntentFilter);
        }
        registerReceiver(myBroadCastReceiver,intentFilter);
    }

    private class MyBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction()!=null) {
                if (intent.getAction().equalsIgnoreCase(Constants.networkStateIntentFilter)) {
                    if (CommonMethods.haveInternet(connectivityManager)) {
                        calculateTimeDiff();
                        calculateTimeDiffForNonClickAds();
                    }
                } else if (intent.getAction().equalsIgnoreCase(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (downloadID == id) {
                        Toast.makeText(WebViewActivity.this, "Download Completed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void startDownload() {
        DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadUrl));
        if (Build.VERSION.SDK_INT < 29) {
            downloadRequest.allowScanningByMediaScanner();
        }
        downloadRequest.setMimeType(mimeType);
        String cookies = CookieManager.getInstance().getCookie(downloadUrl);
        downloadRequest.addRequestHeader("cookie", cookies);
        downloadRequest.addRequestHeader("User-Agent", userAgent);
//        downloadRequest.setTitle(URLUtil.guessFileName(downloadUrl, contentDisposition, mimeType));
        downloadRequest.setTitle(getResources().getString(R.string.app_name));
        String guessFileName = URLUtil.guessFileName(downloadUrl, contentDisposition, mimeType);
//        String fileName = guessFileName.replace("downloadfile", getResources().getString(R.string.app_name) + System.currentTimeMillis());
//        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, guessFileName);
        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadID = downloadManager.enqueue(downloadRequest);
            Toast.makeText(WebViewActivity.this, "Download Starting…", Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseBrowserToOpenUrl(String data) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
        startActivity(Intent.createChooser(browserIntent,"Please choose a browser."));
    }

    public static boolean hasPermission(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                // If there is not data, then we may have taken a photo
                if (mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
        return;
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            if (binding.myWebView.canGoBack()) {
                binding.myWebView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.isUserActive=true;
        MoPub.onResume(this);
        if (admobBannerAdsView != null) {
            admobBannerAdsView.resume();
        }
        if (!CommonMethods.haveInternet(connectivityManager)) {
            Intent intent = new Intent(WebViewActivity.this, NoInternetActivity.class);
            startActivity(intent);
        } else {
            calculateTimeDiff();
            calculateTimeDiffForNonClickAds();
        }
    }

    @Override
    protected void onPause() {
        if (admobBannerAdsView != null) {
            admobBannerAdsView.pause();
        }
        super.onPause();
        Constants.isUserActive=false;
        MoPub.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MoPub.onStop(this);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myBroadCastReceiver);
        if (adsThread != null) {
            adsThread.interrupt();
            adsThread = null;
        }
        if (facebookInterstitialAd != null) {
            facebookInterstitialAd.destroy();
            facebookInterstitialAd=null;
        }
        if (facebookBannerAdView!=null){
            facebookBannerAdView.destroy();
            facebookBannerAdView=null;
        }
        if (moPubInterstitial != null) {
            moPubInterstitial.destroy();
            moPubInterstitial = null;
        }
        if (moPubBannerAdsView != null) {
            moPubBannerAdsView.destroy();
            moPubBannerAdsView = null;
        }
        if (unityBannerView != null) {
            UnityBanners.destroy();
            unityBannerView = null;
        }
        if (admobBannerAdsView != null) {
            admobBannerAdsView.destroy();
            admobBannerAdsView=null;
        }
        compositeDisposable.dispose();
        super.onDestroy();
    }



}