package com.bluezhang.standerdcompuse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.bluezhang.standerdcompuse.service.MusicService;

public class MainActivity extends AppCompatActivity {
    private BroadcastReceiver progressRcciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String[] urls = intent.getStringArrayExtra("urls");
                if (urls.length == 0) {
                    if (BuildConfig.DEBUG) Log.d("MainActivity", "URLS长度为0");

                }else{
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < urls.length; i++) {
                        sb.append(urls[i]);
                        sb.append('\n');

                    }
                    txtInfo.setText(sb.toString());
                }
            }

        }
    };

    private TextView txtInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtInfo = (TextView)findViewById(R.id.progress_info);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_PROGRESS_UPDATE);
        registerReceiver(progressRcciver,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(progressRcciver);
    }

    public void btnAddRequest(View view) {
        Intent service = new Intent(Constants.ACTION_DARA_FETCH);
        service.putExtra("action",0);//0 代表添加请求
        service.putExtra("url","http://www.baidu.com/");
        service.setPackage(getPackageName());
        startService(service);
    }

    public void btnGetProgress(View view) {
        Intent service = new Intent(Constants.ACTION_DARA_FETCH);
        service.putExtra("action",1);//0 代表添加请求
        service.setPackage(getPackageName());
        startService(service);
    }

    public void removeProgress(View view) {
        Intent service = new Intent(Constants.ACTION_DARA_FETCH);
        service.putExtra("action", 1);//0 代表添加请求
        service.setPackage(getPackageName());
        startService(service);

    }

    /**
     * 启动服务播放音乐
     *
     * @param view
     */
    public void btnPlayClick(View view) {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("action",0);
        intent.putExtra("url","http://10.10.60.18:8080/nobody.mp3");


        startService(intent);

    }

    public void btnPauseMusic(View view) {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("action",1);
        intent.putExtra("url", "http://10.10.60.18:8080/nobody.mp3");




    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){

        }
        return super.onTouchEvent(event);
    }
}
