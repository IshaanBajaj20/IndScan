package com.turno.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IntroActivity extends AppCompatActivity {

    private TextView tvNext;
    private ViewPager viewPager;
    private LinearLayout layoutDots;
    private IntroPref introPref;
    private int[] layouts;
    private TextView[] dots;
    private MyViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        SharedPreferences preferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String FirstTime = preferences.getString("FirstTimeInstall", "");

        if (FirstTime.equals("Yes")){
            Intent intent = new Intent(getApplicationContext(),Permissions.class);
            startActivity(intent);
        }
        else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("FirstTimeInstall", "Yes");
            editor.apply();
        }


        introPref = new IntroPref(this);
        if (introPref.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        tvNext = findViewById(R.id.tvNext);
        viewPager = findViewById(R.id.viewPager);
        layoutDots = findViewById(R.id.layoutDots);


        layouts = new int[]{
                R.layout.intro_one,
                R.layout.intro_two,
                R.layout.intro_three
        };

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(+1);
                if (current < layouts.length) {
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }

            }
        });

        viewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        addBottomDots(0);
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if (position == layouts.length - 1) {
                tvNext.setText("START");
            } else {
                tvNext.setText("NEXT");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        int[] activeColors = getResources().getIntArray(R.array.active);
        int[] inActiveColors = getResources().getIntArray(R.array.inactive);
        layoutDots.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8266"));
            dots[i].setTextSize(50);
            dots[i].setTextColor(inActiveColors[currentPage]);
            layoutDots.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[currentPage].setTextColor(activeColors[currentPage]);

        }
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {

        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + 1;
    }

    private void launchHomeScreen() {
        introPref.setIsFirstTimeLaunch(false);
        startActivity(new Intent(IntroActivity.this, Permissions.class));
        finish();
    }
}

