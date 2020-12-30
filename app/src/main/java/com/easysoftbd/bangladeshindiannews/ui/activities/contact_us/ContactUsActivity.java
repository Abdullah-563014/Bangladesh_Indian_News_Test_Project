package com.easysoftbd.bangladeshindiannews.ui.activities.contact_us;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.databinding.ActivityContactUsBinding;
import com.easysoftbd.bangladeshindiannews.ui.activities.main.MainActivity;

public class ContactUsActivity extends AppCompatActivity implements View.OnClickListener {


    private ActivityContactUsBinding binding;
    private Button copyEmailButton,copyNumberButton,closeButton,backButton;
    private ClipboardManager clipboardManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_contact_us);
        binding.setLifecycleOwner(this);


        initAll();


    }


    private void initAll() {
        binding.contactActivityCopyEmailButtonId.setOnClickListener(this);
        binding.contactActivityCopyNumberButtonId.setOnClickListener(this);
        binding.contactActivityCloseButtonId.setOnClickListener(this);
        binding.contactActivityBackButtonId.setOnClickListener(this);

        clipboardManager= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.contactActivityCopyEmailButtonId:
                copyEmail();
                break;

            case R.id.contactActivityCopyNumberButtonId:
                copyNumber();
                break;

            case R.id.contactActivityCloseButtonId:
                finishAffinity();
                break;

            case R.id.contactActivityBackButtonId:
                finish();
                break;
        }
    }

    private void copyEmail(){
        ClipData clipData=ClipData.newPlainText("AdminEmail","easysoft247@gmail.com");
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(ContactUsActivity.this, "Copied Successfully", Toast.LENGTH_SHORT).show();
    }

    private void copyNumber(){
        ClipData clipData=ClipData.newPlainText("AdminNumber","+8801303628419");
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(ContactUsActivity.this, "Copied Successfully", Toast.LENGTH_SHORT).show();
    }


}