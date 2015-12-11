package com.example.oliver.customviews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.oliver.customviews.View.DyingLightProgress;

public class ProgressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        final DyingLightProgress progress3 = (DyingLightProgress) findViewById(R.id.progress3);
        progress3.setIcon(getResources(), R.drawable.batman_icon);
        progress3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((DyingLightProgress)v).setItemType(DyingLightProgress.ITEM_TYPE_CIRCLE);
                int animPartDuration = progress3.getAnimPartDuration();
                progress3.setAnimPartDuration(100);
//                ((DyingLightProgress)v).setAnimPartDuration(250);

//               if (progress3.isAnimationRunning()) {
//                   progress3.stopAnimation();
//               } else {
//                   progress3.startAnimation();
//               }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("tag", "onBackPressed");
    }
}
