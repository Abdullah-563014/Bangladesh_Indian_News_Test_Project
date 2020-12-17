package com.easysoftbd.bangladeshindiannews.ui.activities.main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.databinding.ActivityMainBinding;
import com.easysoftbd.bangladeshindiannews.services.MyForgroundService;
import com.easysoftbd.bangladeshindiannews.services.NewsLoaderService;
import com.easysoftbd.bangladeshindiannews.ui.activities.favourite_list.FavouriteListActivity;
import com.easysoftbd.bangladeshindiannews.ui.activities.home.HomeActivity;
import com.easysoftbd.bangladeshindiannews.utils.CommonMethods;
import com.easysoftbd.bangladeshindiannews.utils.Constants;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private ActivityMainBinding binding;
    private AlertDialog alertDialog;
    private String[] countryList=new String[2];
    private String[] languageList=new String[3];
    private String selectedCountry,selectedLanguage;
    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);


        initAll();

        startForgroundService();


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

    private void startForgroundService() {
        Intent intent = new Intent(MainActivity.this, MyForgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
//            startService(intent);
            startBackgroundService();
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
        connectivityManager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        binding.nextButton.setOnClickListener(this);
        binding.setCountryButton.setOnClickListener(this);
        binding.favouriteListButton.setOnClickListener(this);
    }

    private void goToHomeActivity() {
        CommonMethods.setStringToSharedPreference(getApplicationContext(), Constants.selectedCountryTag,selectedCountry);
        CommonMethods.setStringToSharedPreference(getApplicationContext(), Constants.selectedLanguageTag,selectedLanguage);
        Intent intent=new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
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
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUiForCountryAndLanguage();
    }

    @Override
    protected void onDestroy() {
        binding.unbind();
        super.onDestroy();
    }


}