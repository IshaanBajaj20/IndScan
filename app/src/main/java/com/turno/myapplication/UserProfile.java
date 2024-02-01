package com.turno.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    CircleImageView photo;
    ImageView backBtn;
    TextView name;
    TextView mail,userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        photo = findViewById(R.id.photo);
        name = findViewById(R.id.name);
        mail = findViewById(R.id.mail);
        userId = findViewById(R.id.id);
        backBtn = findViewById(R.id.back_icon);
        if (Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfile.super.onBackPressed();
            }
        });



        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount != null){

            Uri personPhoto = signInAccount.getPhotoUrl();

            Glide.with(this).load(String.valueOf(personPhoto)).into(photo);
            name.setText(signInAccount.getDisplayName());
            mail.setText(signInAccount.getEmail());
            userId.setText(signInAccount.getId());


        }
    }
}