package com.easysoftbd.bangladeshindiannews.ui.activities.no_internet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.databinding.ActivityNoInternetBinding;
import com.easysoftbd.bangladeshindiannews.ui.activities.home.HomeActivity;
import com.easysoftbd.bangladeshindiannews.utils.CommonMethods;

public class NoInternetActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityNoInternetBinding binding;
    private ClipboardManager clipboardManager;
    private ConnectivityManager connectivityManager;
    private String url;
    private String activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_no_internet);
        binding.setLifecycleOwner(this);


        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            url=bundle.getString("url",null);
            activity=bundle.getString("activity");
        }

        initAll();

        initializeSwipeRefreshLayout();
    }

    private void initAll() {
        binding.noInternetActivityCopyEmailButton.setOnClickListener(this);
        binding.noInternetActivityCopyNumberButton.setOnClickListener(this);
        binding.noInternetActivityRefreshButton.setOnClickListener(this);
        binding.noInternetActivityCloseButton.setOnClickListener(this);
        binding.noInternetActivityBackButton.setOnClickListener(this);

        clipboardManager= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        connectivityManager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.noInternetActivityCopyEmailButton:
                copyEmail();
                break;

            case R.id.noInternetActivityCopyNumberButton:
                copyNumber();
                break;

            case R.id.noInternetActivityRefreshButton:
                checkInternet();
                break;

            case R.id.noInternetActivityCloseButton:
                NoInternetActivity.this.finishAffinity();
                break;

            case R.id.noInternetActivityBackButton:
                Intent intent=new Intent(NoInternetActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void copyEmail(){
        ClipData clipData=ClipData.newPlainText("AdminEmail","easysoft247@gmail.com");
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(NoInternetActivity.this, "Copied Successfully", Toast.LENGTH_SHORT).show();
    }

    private void copyNumber(){
        ClipData clipData=ClipData.newPlainText("AdminNumber","01303628419");
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(NoInternetActivity.this, "Copied Successfully", Toast.LENGTH_SHORT).show();
    }

    private void checkInternet(){
        if (CommonMethods.haveInternet(connectivityManager)){
            if (isTaskRoot()){
                startActivity(new Intent(NoInternetActivity.this,HomeActivity.class));
            }
            finish();
        }else {
            Toast.makeText(this, "Yet, You have no internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeSwipeRefreshLayout() {
        binding.noInternetActivitySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkInternet();
                binding.noInternetActivitySwipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}