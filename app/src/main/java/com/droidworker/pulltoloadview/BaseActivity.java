package com.droidworker.pulltoloadview;

import com.droidworker.lib.PullToLoadBaseView;
import com.droidworker.lib.PullToLoadListener;
import com.droidworker.lib.constant.LoadMode;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public abstract class BaseActivity extends AppCompatActivity implements PullToLoadListener {

    protected abstract PullToLoadBaseView getPullToLoadView();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh: {
                getPullToLoadView().setLoading();
            }
            break;
            case R.id.pull_from_start: {
                getPullToLoadView().setMode(LoadMode.PULL_FROM_START);
            }
            break;
            case R.id.pull_from_start_auto_load_more: {
                getPullToLoadView().setMode(LoadMode.PULL_FROM_START_AUTO_LOAD_MORE);
            }
            break;
            case R.id.pull_from_end: {
                getPullToLoadView().setMode(LoadMode.PULL_FROM_END);
            }
            break;
            case R.id.both: {
                getPullToLoadView().setMode(LoadMode.BOTH);
            }
            break;
            case R.id.disabled: {
                getPullToLoadView().setMode(LoadMode.DISABLED);
            }
            break;
            case R.id.manual_only: {
                getPullToLoadView().setMode(LoadMode.MANUAL_ONLY);
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
