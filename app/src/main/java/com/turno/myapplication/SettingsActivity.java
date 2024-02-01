package com.turno.myapplication;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.view.Window;

import com.turno.myapplication.AboutFragment;
import com.turno.myapplication.R;
import com.turno.myapplication.SettingsFragment;
import com.turno.myapplication.Utils;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private SettingsFragment sf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        FragmentManager fm=getFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();

        if (Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        sf=new SettingsFragment();
        ft.replace(android.R.id.content, sf);
        ft.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Preference aboutPreference = sf.findPreference("about_preference");
        aboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                androidx.fragment.app.FragmentManager fm = getSupportFragmentManager();
                AboutFragment aboutDialog = new AboutFragment();
                aboutDialog.show(fm, "about_view");
                return true;
            }
        });


    }
}