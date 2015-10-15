package com.example.oliver.customviews;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.oliver.customviews.View.PieMenuView;

public class MainActivity extends AppCompatActivity implements PieMenuView.OnItemCLickListener {
    private int[] colors = {Color.BLACK, Color.GRAY, Color.GREEN, Color.CYAN, Color.RED, Color.YELLOW};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources res = getResources();

        setContentView(R.layout.activity_main);
        final PieMenuView pie = (PieMenuView) this.findViewById(R.id.Pie);
        pie.setItemClickListener(this);
//        pie.setLinesColor(Color.RED);
//
//        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, res.getDisplayMetrics());
//        pie.setLinesWidth(px);

        pie.addItem(R.mipmap.ic_launcher);
        pie.addItem(R.drawable.ic_phone_main);
        pie.addItem(R.mipmap.ic_launcher);
        pie.addItem(R.drawable.ic_map_main);
        pie.addItem(R.mipmap.ic_launcher);
        pie.addItem(R.drawable.ic_link_main);
        pie.addItem(R.mipmap.ic_launcher);
//        pie.addItem(R.drawable.ic_search);
//        pie.addItem(R.mipmap.ic_launcher);
//        pie.addItem(R.drawable.ic_search);
//        pie.addItem(R.mipmap.ic_launcher);

        ((Button) findViewById(R.id.Reset)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                Random r = new Random(53);
//                pie.setSegmentsColor(colors[r.nextInt(colors.length - 1)]);
                pie.setSelectedItem(1);
            }
        });
    }

    @Override
    public void OnItemCLick(PieMenuView source, int currentItem) {
//        Log.d("tag", "MainActivity PieChart.OnItemClick on item " + currentItem);
    }
}
