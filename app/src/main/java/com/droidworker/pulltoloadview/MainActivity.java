package com.droidworker.pulltoloadview;

import com.droidworker.pulltoloadview.abslistview.ListViewActivity;
import com.droidworker.pulltoloadview.recyclerview.RecyclerViewActivity;
import com.droidworker.pulltoloadview.recyclerview.RecyclerViewHorizontalActivity;
import com.droidworker.pulltoloadview.scrollview.ScrollViewActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * 效果测试页面
 * @author https://github.com/DroidWorkerLYF
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolbar;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.demo1:
            startActivity(new Intent(MainActivity.this, RecyclerViewActivity.class));
            break;
        case R.id.demo2:
            startActivity(new Intent(MainActivity.this, ListViewActivity.class));
            break;
        case R.id.demo3:
            startActivity(new Intent(MainActivity.this, RecyclerViewHorizontalActivity.class));
            break;
        case R.id.demo4:
            startActivity(new Intent(MainActivity.this, WebViewActivity.class));
            break;
        case R.id.demo5:
            startActivity(new Intent(MainActivity.this, ScrollViewActivity.class));
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
    }
}
