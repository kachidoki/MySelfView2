package com.kachidoki.myselfview2.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kachidoki.myselfview2.R;

public class MainActivity extends AppCompatActivity {

    private Button paintTest;
    private Button searchView;
    private Button myImageView;
    private Button poiytopoly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paintTest = (Button) findViewById(R.id.paintTest);
        searchView = (Button) findViewById(R.id.SearchView);
        myImageView = (Button) findViewById(R.id.MyImageView);
        poiytopoly = (Button) findViewById(R.id.PolytoPoly);
        paintTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PaintTestActivity.class));
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));

            }
        });
        myImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MyImageViewActivity.class));

            }
        });
        poiytopoly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PolytoPolyActivity.class));

            }
        });
    }


}
