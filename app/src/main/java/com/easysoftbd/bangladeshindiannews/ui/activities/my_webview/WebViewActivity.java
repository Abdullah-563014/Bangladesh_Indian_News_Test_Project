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
import android.widget.Toast;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.data.local.DatabaseClient;
import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.favourite_list.FavouriteList;
import com.easysoftbd.bangladeshindiannews.databinding.ActivityWebViewBinding;
import com.easysoftbd.bangladeshindiannews.ui.activities.home.HomeActivity;
import com.easysoftbd.bangladeshindiannews.ui.activities.no_internet.NoInternetActivity;
import com.easysoftbd.bangladeshindiannews.utils.CommonMethods;
import com.easysoftbd.bangladeshindiannews.utils.Constants;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;

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

public class WebViewActivity extends AppCompatActivity {


    private ActivityWebViewBinding binding;
    private String currentUrl, currentPageTitle;
    private BroadcastReceiver onDownloadComplete;
    private long downloadID;
    private String url, downloadUrl, userAgent, contentDisposition, mimeType;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private int INPUT_FILE_REQUEST_CODE = 121;
    private boolean exitStatus = false, showFirstFacebookInterstitialAds = true, initFacebookInterstitialAds = true, willLoadFacebookInterstitial = true, willLoadAdmobInterstitial = true;
    private AlertDialog progressAlertDialog,permissionAlertDialog;
    private ConnectivityManager connectivityManager;
    private NewsDatabase newsDatabase;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_web_view);



        initializeAll();

        initBroadCastReceiver();

        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString(Constants.UrlTag, null);
        }
        if (url != null) {
            openWebPage(url);
        } else {
            Toast.makeText(this, "Your web url is not valid", Toast.LENGTH_SHORT).show();
        }


        initializeSwipeRefreshLayout();

        initializeDownloadManager();

        binding.addToFavouriteListTextView.setOnClickListener(view -> bookmarkAlertDialog());


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
        Sprite fadingCircle = new FadingCircle();
        binding.spinKit.setIndeterminateDrawable(fadingCircle);
        binding.spinKit.setVisibility(View.GONE);
        binding.webActivityProgressBar.setVisibility(View.GONE);
        connectivityManager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        newsDatabase= DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
        compositeDisposable=new CompositeDisposable();
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
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(binding.myWebView);
            resultMsg.sendToTarget();

            return true;
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
            if (url != null && url.contains("play.google.com")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException e) {
                    return false;
                }
            }else {
                return false;
            }
        }

        @RequiresApi(24)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri url = request.getUrl();
            if (url != null && url.toString().contains("play.google.com")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(url);
                    startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException e) {
                    return false;
                }
            }else {
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
            exitStatus = true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            binding.webActivityProgressBar.setVisibility(View.VISIBLE);
            binding.spinKit.setVisibility(View.VISIBLE);
            exitStatus = false;
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
            if (error.getErrorCode() == ERROR_CONNECT || error.getErrorCode() == ERROR_TIMEOUT || error.getErrorCode() == ERROR_BAD_URL || error.getErrorCode() == ERROR_IO || error.getErrorCode() == ERROR_PROXY_AUTHENTICATION || error.getErrorCode() == ERROR_UNSAFE_RESOURCE || error.getErrorCode() == ERROR_UNSUPPORTED_AUTH_SCHEME || error.getErrorCode() == ERROR_UNSUPPORTED_SCHEME || error.getErrorCode() == SAFE_BROWSING_THREAT_UNKNOWN || error.getErrorCode() == ERROR_AUTHENTICATION) {
//                ERROR_HOST_LOOKUP removed temporary.
                try {
                    binding.myWebView.stopLoading();
                } catch (Exception e) {

                }
                if (binding.myWebView.canGoBack()) {
                    binding.myWebView.goBack();
                }
                Log.d(Constants.TAG,"error is:- "+error.getErrorCode());
                exitStatus = true;
                Intent intent = new Intent(WebViewActivity.this, NoInternetActivity.class);
                startActivity(intent);
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT || errorCode == ERROR_BAD_URL || errorCode == ERROR_IO || errorCode == ERROR_PROXY_AUTHENTICATION || errorCode == ERROR_UNSAFE_RESOURCE || errorCode == ERROR_UNSUPPORTED_AUTH_SCHEME || errorCode == ERROR_UNSUPPORTED_SCHEME || errorCode == SAFE_BROWSING_THREAT_UNKNOWN || errorCode == ERROR_AUTHENTICATION) {
//                ERROR_HOST_LOOKUP removed temporary.
                try {
                    binding.myWebView.stopLoading();
                } catch (Exception e) {

                }
                if (binding.myWebView.canGoBack()) {
                    binding.myWebView.goBack();
                }
                Log.d(Constants.TAG,"error code is:- "+errorCode);
                exitStatus = true;
                Intent intent = new Intent(WebViewActivity.this, NoInternetActivity.class);
                startActivity(intent);
            }
        }
    }

    private void bookmarkAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this)
                .setTitle("ফেভারিট লিস্ট এ অ্যাড করুন")
                .setMessage("আপনি কি এই পেজটি আপনার ফেভারিট লিস্ট এ যোগ করতে চান ?")
                .setCancelable(false)
                .setNegativeButton("না", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("হ্যাঁ", new DialogInterface.OnClickListener() {
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

    private void initBroadCastReceiver() {
        onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadID == id) {
                    Toast.makeText(WebViewActivity.this, "ডাউনলোড শেষ হয়েছে", Toast.LENGTH_SHORT).show();
                }
            }
        };
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
        String guessFileName = URLUtil.guessFileName(null, getResources().getString(R.string.app_name), mimeType);
        String fileName = guessFileName.replace("downloadfile", getResources().getString(R.string.app_name) + System.currentTimeMillis());
//        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadID = downloadManager.enqueue(downloadRequest);
            Toast.makeText(WebViewActivity.this, "ডাউনলোড হচ্ছে…", Toast.LENGTH_SHORT).show();
        }
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
        if (exitStatus) {
            if (isTaskRoot()) {
                Intent intent = new Intent(WebViewActivity.this, HomeActivity.class);
                startActivity(intent);
            }
            if (binding.myWebView.canGoBack()) {
                binding.myWebView.goBack();
            } else {
                super.onBackPressed();
            }
        } else {
            Toast.makeText(this, "অনুগ্রহ করে পেজ লোড শেষ হওয়া পর্যন্ত অপেক্ষা করুন|", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(onDownloadComplete);
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!CommonMethods.haveInternet(connectivityManager)) {
            Intent intent = new Intent(WebViewActivity.this, NoInternetActivity.class);
            startActivity(intent);
        }
    }


}