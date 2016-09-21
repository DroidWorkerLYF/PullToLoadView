package com.droidworker.example;

import com.droidworker.pulltoloadview.PullToLoadBaseView;
import com.droidworker.pulltoloadview.PullToLoadListener;
import com.droidworker.pulltoloadview.constant.LoadMode;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
        case R.id.refresh:
            getPullToLoadView().setLoading();
            break;
        case R.id.pull_from_start:
            getPullToLoadView().setMode(LoadMode.PULL_FROM_START);
            break;
        case R.id.pull_from_start_auto_load_more:
            getPullToLoadView().setMode(LoadMode.PULL_FROM_START_AUTO_LOAD_MORE);
            break;
        case R.id.pull_from_start_auto_load_more_with_footer:
            getPullToLoadView().setMode(LoadMode.PULL_FROM_START_AUTO_LOAD_MORE_WITH_FOOTER);
            break;
        case R.id.pull_from_end:
            getPullToLoadView().setMode(LoadMode.PULL_FROM_END);
            break;
        case R.id.both:
            getPullToLoadView().setMode(LoadMode.BOTH);
            break;
        case R.id.disabled:
            getPullToLoadView().setMode(LoadMode.DISABLED);
            break;
        case R.id.manual_only:
            getPullToLoadView().setMode(LoadMode.MANUAL_ONLY);
            break;
        case R.id.show_empty:
            getPullToLoadView().showConditionView(ConditionType.EMPTY);
            break;
        case R.id.show_error:
            getPullToLoadView().showConditionView(ConditionType.ERROR);
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void addEmptyView(View view){
        getPullToLoadView().addConditionView(view, ConditionType.EMPTY);
    }

    protected void addErrorView(View view){
        getPullToLoadView().addConditionView(view, ConditionType.ERROR);
    }
}
