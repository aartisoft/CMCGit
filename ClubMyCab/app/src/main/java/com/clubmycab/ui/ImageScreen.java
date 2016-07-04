package com.clubmycab.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.clubmycab.R;

public class ImageScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_screen);
       if(getIntent() != null){
           if(getIntent().getExtras() != null){
               String url = getIntent().getExtras().getString("url");
               Glide.with(ImageScreen.this).load(url).animate(android.R.anim.fade_in).error(R.drawable.avatar_rides_list).into((ImageView)findViewById(R.id.ivFullImage));

           }
       }
    }
}
