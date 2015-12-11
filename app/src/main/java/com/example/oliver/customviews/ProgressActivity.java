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
        DyingLightProgress progress3 = (DyingLightProgress) findViewById(R.id.progress3);
        progress3.setIcon(getResources(), R.drawable.batman_icon);
        progress3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DyingLightProgress)v).setItemType(DyingLightProgress.ITEM_TYPE_CIRCLE);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("tag", "onBackPressed");
    }
}
