package com.uwaterloo.portfoliorebalancing.ui;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Created by lucas on 18/12/16.
 */

public class AsyncTaskActivity extends AppCompatActivity {

    //Activities with an AsyncTask should override this method to cancel its Async Tasks whenever
    //the Activity is no longer in use.
    protected void stopAsyncTask() {
    }

    @Override
    public void onBackPressed() {
        stopAsyncTask();
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        stopAsyncTask();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // click on 'up' button in the action bar, handle it here
                stopAsyncTask();
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}