package com.kachidoki.myselfview2.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.kachidoki.myselfview2.PolyToPolyTestView;
import com.kachidoki.myselfview2.R;

/**
 * Created by mayiwei on 16/11/11.
 */
public class PolytoPolyActivity extends AppCompatActivity {

    private PolyToPolyTestView polyToPolyTestView;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polytopoly);
        polyToPolyTestView = (PolyToPolyTestView) findViewById(R.id.poly);
        radioGroup = (RadioGroup) findViewById(R.id.group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case R.id.point0: polyToPolyTestView.setTestPoint(0); break;
                    case R.id.point1: polyToPolyTestView.setTestPoint(1); break;
                    case R.id.point2: polyToPolyTestView.setTestPoint(2); break;
                    case R.id.point3: polyToPolyTestView.setTestPoint(3); break;
                    case R.id.point4: polyToPolyTestView.setTestPoint(4); break;
                }
            }
        });
    }
}
