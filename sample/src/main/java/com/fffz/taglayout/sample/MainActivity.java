package com.fffz.taglayout.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.fffz.taglayout.TagLayout;
import com.fffz.taglayout.TagView;
import com.sigma.taglayout.example.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TagLayout tagLayout = findViewById(R.id.tag_layout);
        tagLayout.setText("aaa", "bbbbbbbbb", "cc", "ddd", "eee");
        tagLayout.setOnItemClickListener(new TagLayout.OnItemClickListener() {
            Toast mToast;

            @Override
            public void onItemClick(TagLayout parent, TagView view, int position) {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(parent.getContext(), "" + position, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }
}
