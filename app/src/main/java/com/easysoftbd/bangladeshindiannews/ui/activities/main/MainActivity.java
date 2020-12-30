package com.easysoftbd.bangladeshindiannews.ui.activities.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.data.model.ImageNotice;
import com.easysoftbd.bangladeshindiannews.databinding.ActivityMainBinding;
import com.easysoftbd.bangladeshindiannews.services.MyForgroundService;
import com.easysoftbd.bangladeshindiannews.services.NewsLoaderService;
import com.easysoftbd.bangladeshindiannews.ui.activities.contact_us.ContactUsActivity;
import com.easysoftbd.bangladeshindiannews.ui.activities.favourite_list.FavouriteListActivity;
import com.easysoftbd.bangladeshindiannews.ui.activities.home.HomeActivity;
import com.easysoftbd.bangladeshindiannews.ui.activities.my_webview.WebViewActivity;
import com.easysoftbd.bangladeshindiannews.utils.CommonMethods;
import com.easysoftbd.bangladeshindiannews.utils.Constants;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private ActivityMainBinding binding;
    private AlertDialog alertDialog;
    private String[] countryList=new String[2];
    private String[] languageList=new String[3];
    private String selectedCountry,selectedLanguage;
    private ConnectivityManager connectivityManager;
    private DatabaseReference databaseReference;
    private ImageNotice imageNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);


        initAll();

        startForgroundService();

        checkUpdate();

        loadAdminImageNotice();

        showAdminNoticeImage();


    }


    public void checkUpdate() {
        DatabaseReference versionRef = FirebaseDatabase.getInstance().getReference().child("VersionCode");
        versionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String version = null;
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    version = dataSnapshot.getValue(String.class);
                }

                PackageManager manager = getApplicationContext().getPackageManager();
                PackageInfo info = null;
                try {
                    info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                int code = 0;
                if (info != null) {
                    code = info.versionCode;
                }

                if (version != null && code < Integer.parseInt(version)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Found New Update");
                    builder.setMessage("Your app is not updated, Do you want to update now?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName()));
                            startActivity(browserIntent);
                        }
                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    if (!isFinishing()) {
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to check update.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkMandatoryUpdate() {
        DatabaseReference versionRef = FirebaseDatabase.getInstance().getReference().child("VersionName");
        versionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                double version = 1.0;
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    version = Double.parseDouble(dataSnapshot.getValue(String.class));
                }

                PackageManager manager = getApplicationContext().getPackageManager();
                PackageInfo info = null;
                try {
                    info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                double versionName = 1.0;
                if (info != null) {
                    versionName = Double.parseDouble(info.versionName);
                }

                if (versionName < version) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Found New Update");
                    builder.setMessage("Your app is not updated, Please update now.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName()));
                            startActivity(browserIntent);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    if (!isFinishing()) {
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to check update.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAdminImageNotice() {
        databaseReference.child("adminMessage").child("image_notice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ImageNotice> imageNoticeList=new ArrayList<>();
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        imageNoticeList.add(snapshot.getValue(ImageNotice.class));
                    }
                }
                if (imageNoticeList.size()>0){
                    saveImageUrlToStorage(imageNoticeList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveImageUrlToStorage(List<ImageNotice> list) {
        CommonMethods.setStringToSharedPreference(MainActivity.this,Constants.adminNoticeImageOneUrlKey,list.get(0).getImageUrl());
        CommonMethods.setStringToSharedPreference(MainActivity.this,Constants.adminNoticeTargetOneUrlKey,list.get(0).getTargetUrl());
        CommonMethods.setStringToSharedPreference(MainActivity.this,Constants.adminNoticeImageTwoUrlKey,list.get(1).getImageUrl());
        CommonMethods.setStringToSharedPreference(MainActivity.this,Constants.adminNoticeTargetTwoUrlKey,list.get(1).getTargetUrl());
        CommonMethods.setStringToSharedPreference(MainActivity.this,Constants.adminNoticeImageThreeUrlKey,list.get(2).getImageUrl());
        CommonMethods.setStringToSharedPreference(MainActivity.this,Constants.adminNoticeTargetThreeUrlKey,list.get(2).getTargetUrl());
        CommonMethods.setStringToSharedPreference(MainActivity.this,Constants.adminNoticeImageFourUrlKey,list.get(3).getImageUrl());
        CommonMethods.setStringToSharedPreference(MainActivity.this,Constants.adminNoticeTargetFourUrlKey,list.get(3).getTargetUrl());
        CommonMethods.setStringToSharedPreference(MainActivity.this,Constants.adminNoticeImageFiveUrlKey,list.get(4).getImageUrl());
        CommonMethods.setStringToSharedPreference(MainActivity.this,Constants.adminNoticeTargetFiveUrlKey,list.get(4).getTargetUrl());
    }

    private void showAdminNoticeImage() {
        imageNotice =new ImageNotice();
        Random random=new Random();
        int value=random.nextInt(5);
        if (value==0){
            imageNotice.setImageUrl(CommonMethods.getStringFromSharedPreference(MainActivity.this,Constants.adminNoticeImageOneUrlKey,null));
            imageNotice.setTargetUrl(CommonMethods.getStringFromSharedPreference(MainActivity.this,Constants.adminNoticeTargetOneUrlKey,null));
        }else if (value==1){
            imageNotice.setImageUrl(CommonMethods.getStringFromSharedPreference(MainActivity.this,Constants.adminNoticeImageTwoUrlKey,null));
            imageNotice.setTargetUrl(CommonMethods.getStringFromSharedPreference(MainActivity.this,Constants.adminNoticeTargetTwoUrlKey,null));
        }else if (value==2){
            imageNotice.setImageUrl(CommonMethods.getStringFromSharedPreference(MainActivity.this,Constants.adminNoticeImageThreeUrlKey,null));
            imageNotice.setTargetUrl(CommonMethods.getStringFromSharedPreference(MainActivity.this,Constants.adminNoticeTargetThreeUrlKey,null));
        }else if (value==3){
            imageNotice.setImageUrl(CommonMethods.getStringFromSharedPreference(MainActivity.this,Constants.adminNoticeImageFourUrlKey,null));
            imageNotice.setTargetUrl(CommonMethods.getStringFromSharedPreference(MainActivity.this,Constants.adminNoticeTargetFourUrlKey,null));
        }else if (value==4){
            imageNotice.setImageUrl(CommonMethods.getStringFromSharedPreference(MainActivity.this,Constants.adminNoticeImageFiveUrlKey,null));
            imageNotice.setTargetUrl(CommonMethods.getStringFromSharedPreference(MainActivity.this,Constants.adminNoticeTargetFiveUrlKey,null));
        }

        if (imageNotice.getImageUrl()!=null && !imageNotice.getImageUrl().isEmpty() && !imageNotice.getImageUrl().equalsIgnoreCase(" ") && !imageNotice.getImageUrl().contains(" ")){
            binding.mainActivityAdminNoticeImageView.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(imageNotice.getImageUrl()).transform(new CommonMethods.PicassoTransform(getDisplayWidthInPixel())).into(binding.mainActivityAdminNoticeImageView);
            } catch (Exception e) {
                binding.mainActivityAdminNoticeImageView.setVisibility(View.GONE);
            }
        }else {
            binding.mainActivityAdminNoticeImageView.setVisibility(View.GONE);
        }
    }

    private int getDisplayWidthInPixel() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private void startBackgroundService() {
        String workerTag="Abdullah";
        Constraints constraints=new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest request=new PeriodicWorkRequest.Builder(NewsLoaderService.class,15, TimeUnit.MINUTES)
                .addTag(workerTag)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(getApplicationContext())
        .enqueueUniquePeriodicWork(workerTag, ExistingPeriodicWorkPolicy.REPLACE, request);
    }

    private boolean isWorkScheduled(String tag) {
        WorkManager instance = WorkManager.getInstance();
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(tag);
        try {
            boolean running = false;
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                running = state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED;
            }
            return running;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void startForgroundService() {
        if (!isWorkScheduled(Constants.TAG)) {
            Intent intent = new Intent(MainActivity.this, MyForgroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
//            startService(intent);
                startBackgroundService();
            }
        }
    }

    private void initAll() {
        countryList[0]="Bangladesh";
        countryList[1]="India";
        languageList[0]="Hindi";
        languageList[1]="Bangla";
        languageList[2]="English";
        selectedCountry="Bangladesh";
        selectedLanguage="Bangla";
        databaseReference=FirebaseDatabase.getInstance().getReference();
        connectivityManager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        binding.nextButton.setOnClickListener(this);
        binding.setCountryButton.setOnClickListener(this);
        binding.favouriteListButton.setOnClickListener(this);
        binding.contactUsButton.setOnClickListener(this);
        binding.giveFiveStarButton.setOnClickListener(this);
        binding.moreAppsButton.setOnClickListener(this);
        binding.shareAppsButton.setOnClickListener(this);
        binding.mainActivityAdminNoticeImageView.setOnClickListener(this);
    }

    private void goToHomeActivity() {
        CommonMethods.setStringToSharedPreference(getApplicationContext(), Constants.selectedCountryTag,selectedCountry);
        CommonMethods.setStringToSharedPreference(getApplicationContext(), Constants.selectedLanguageTag,selectedLanguage);
        Intent intent=new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void showCountryAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("Please select your country")
                .setSingleChoiceItems(countryList, 0, (dialog, which) -> {
                    dialog.dismiss();
                    selectedCountry=countryList[which];
                    CommonMethods.setStringToSharedPreference(getApplicationContext(),Constants.countryNameKey,selectedCountry);
                    if (selectedCountry.equalsIgnoreCase("India")) {
                        showLanguageAlertDialog();
                    } else {
                        selectedLanguage=Constants.bangla;
                        CommonMethods.setStringToSharedPreference(getApplicationContext(),Constants.languageNameKey,selectedLanguage);
                    }
                    updateUiForCountryAndLanguage();
                });

        alertDialog=builder.create();
        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    private void showLanguageAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("Please select your favourite language")
                .setSingleChoiceItems(languageList, 0, (dialog, which) -> {
                    dialog.dismiss();
                    selectedLanguage=languageList[which];
                    CommonMethods.setStringToSharedPreference(getApplicationContext(),Constants.languageNameKey,selectedLanguage);
                    updateUiForCountryAndLanguage();
                });

        alertDialog=builder.create();
        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    private void noInternetAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Network Connection Status")
                .setMessage("No valid internet connection detected. Please connect to internet and try again.")
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });
        alertDialog=builder.create();
        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    private void updateUiForCountryAndLanguage() {
        selectedCountry=CommonMethods.getStringFromSharedPreference(getApplicationContext(),Constants.countryNameKey,Constants.bangladesh);
        selectedLanguage=CommonMethods.getStringFromSharedPreference(getApplicationContext(),Constants.languageNameKey,Constants.bangla);
        binding.selectedCountryTextView.setText("Country:- "+selectedCountry);
        binding.selectedLanguageTextView.setText("Language:- "+selectedLanguage);
    }

    private void shareApp() {
        String url="https://play.google.com/store/apps/details?id="+getPackageName();
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String shareMessage= "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + url +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }

    private void rateApp() {
        String url="https://play.google.com/store/apps/details?id="+getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    private void moreApps() {
        String url="https://play.google.com/store/apps/developer?id=Easy+Soft+BD&hl=en";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.nextButton:
                if (CommonMethods.haveInternet(connectivityManager)) {
                    goToHomeActivity();
                } else {
                    noInternetAlertDialog();
                }
                break;

            case R.id.setCountryButton:
                showCountryAlertDialog();
                break;

            case R.id.favouriteListButton:
                intent=new Intent(MainActivity.this, FavouriteListActivity.class);
                startActivity(intent);
                break;

            case R.id.contactUsButton:
                intent=new Intent(MainActivity.this, ContactUsActivity.class);
                startActivity(intent);
                break;

            case R.id.giveFiveStarButton:
                rateApp();
                break;

            case R.id.moreAppsButton:
                moreApps();
                break;

            case R.id.shareAppsButton:
                shareApp();
                break;

            case R.id.mainActivityAdminNoticeImageView:
                if (imageNotice!=null && imageNotice.getTargetUrl()!=null) {
                    intent=new Intent(MainActivity.this, WebViewActivity.class);
                    intent.putExtra(Constants.UrlTag,imageNotice.getTargetUrl());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Sorry, Target url is not detected.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.isUserActive=true;
        updateUiForCountryAndLanguage();
        checkMandatoryUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Constants.isUserActive=false;
    }

    @Override
    protected void onDestroy() {
        binding.unbind();
        super.onDestroy();
    }


}