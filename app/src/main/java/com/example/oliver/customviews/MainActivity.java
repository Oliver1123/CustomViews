package com.example.oliver.customviews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.oliver.customviews.View.DyingLightProgress;
import com.example.oliver.customviews.View.PieMenuView;

public class MainActivity extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PieMenuView pie1 = (PieMenuView) this.findViewById(R.id.Pie1);
        pie1.setItemClickListener(new PieMenuView.OnItemCLickListener() {
            @Override
            public void OnItemCLick(PieMenuView source, int item) {
                Log.d("tag", "MainActivity pie1.onItemClick item: " + item);
            }
        });

        pie1.addItem(R.mipmap.ic_launcher);
        pie1.addItem(R.drawable.ic_phone_main);
        pie1.addItem(R.mipmap.ic_launcher);
        pie1.addItem(R.drawable.ic_map_main);
        pie1.addItem(R.mipmap.ic_launcher);
        pie1.addItem(R.drawable.ic_link_main);
        pie1.addItem(R.mipmap.ic_launcher);

        final PieMenuView pie2 = (PieMenuView) this.findViewById(R.id.Pie2);
        pie2.setItemClickListener(new PieMenuView.OnItemCLickListener() {
            @Override
            public void OnItemCLick(PieMenuView source, int item) {
                Log.d("tag", "MainActivity pie2.onItemClick item: " + item);
            }
        });

        pie2.addItem(R.mipmap.ic_launcher);
        pie2.addItem(R.drawable.ic_phone_main);
        pie2.addItem(R.mipmap.ic_launcher);
        pie2.addItem(R.drawable.ic_map_main);

        findViewById(R.id.btnProgressActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ProgressActivity.class));
            }
        });
    }


}
