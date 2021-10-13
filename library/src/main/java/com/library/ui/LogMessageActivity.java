package com.library.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;


import com.library.AppUtils;
import com.library.BuildConfig;
import com.library.CrashReporter;
import com.library.FileUtils;
import com.library.R;

import java.io.File;

public class LogMessageActivity extends AppCompatActivity {

    private TextView appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_message);
        appInfo = (TextView) findViewById(R.id.appInfo);

        Intent intent = getIntent();
        if (intent != null) {
            String dirPath = intent.getStringExtra("LogMessage");
            File file = new File(dirPath);
            String crashLog = com.library.FileUtils.readFromFile(file);
            TextView textView = (TextView) findViewById(R.id.logMessage);
            textView.setText(crashLog);
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(getString(R.string.crash_reporter));
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getAppInfo();
    }

    private void getAppInfo() {
        appInfo.setText(AppUtils.getDeviceDetails(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_crash_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = getIntent();
        String filePath = null;
        if (intent != null) {
            filePath = intent.getStringExtra("LogMessage");
        }

        if (item.getItemId() == R.id.delete_log) {
            if (FileUtils.delete(filePath)) {
                finish();
            }
            return true;
        } else if (item.getItemId() == R.id.share_text) {
            shareCrashReportAsText(filePath);
            return true;
        } else if (item.getItemId() == R.id.share_file) {
            shareCrashReport(filePath);
            return true;
        }  else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void shareCrashReportAsText(String filePath) {
        Log.d("LogMessage", "filePath "+ filePath);
        File file = new File(filePath);
        String txt = FileUtils.readFromFile(file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TEXT, txt);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void shareCrashReport(String filePath) {
        File file = new File(filePath);

        String authority = String.format("%s.provider",CrashReporter.getPackName());
        Uri fileUri = FileProvider.getUriForFile(this, authority, file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share via"));
    }
}
