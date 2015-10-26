package com.bluezhang.standerdcompuse;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;


/**
 * videoView 怎么扩充全屏 视频的尺寸影响了Vise哦VIew的尺寸的计算；
 * VideoView中的onMeasure()方法，根据视频的尺寸进行了计算 ；
 * 3）MeasureSpec 一个参数包含两个信息 ，size 、mode
 * <p/>
 * 4）size 就是容器的最大的剩余空间VideoView就应该充满：
 */
public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, Runnable, SeekBar.OnSeekBarChangeListener {
    private VideoView videoView;
    private SeekBar seekBar;
    private Thread thread;
    private boolean running;
    private RelativeLayout relativeLayout;
    private LinearLayout linearLayout;
    private boolean isShowing = true;


    private android.os.Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 998) {
                seekBar.setMax(msg.arg1);
                seekBar.setProgress(msg.arg2);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        relativeLayout = (RelativeLayout) findViewById(R.id.player_title_container);
        linearLayout = (LinearLayout) findViewById(R.id.player_down_container);
        button = (Button) findViewById(R.id.btn_stop);
        GoneThread goneThread = new GoneThread();
        goneThread.start();

        videoView = (VideoView) findViewById(R.id.vidio_view);
        seekBar = (SeekBar) findViewById(R.id.mc_seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        //本地 手机  网络
//        videoView.setVideoURI(Uri.parse("http://10.10.60.18:8080/video_test.3gp"));
        videoView.setVideoURI(Uri.parse("http://10.10.60.18:8080/video_test.3gp"));
        videoView.setVideoPath("mnt/sdcard/Download/a.3gp");
        videoView.setOnPreparedListener(this);
        thread = new Thread(this);
        thread.start();
        //加上前进后退的控制器
        //videoView.setMediaController(new MediaController(this));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
        running = false;
        thread.interrupt();

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        videoView.start();
    }

    @Override
    public void run() {
        running = true;
        try {
            while (running) {
                if (videoView != null && videoView.isPlaying()) {

                    int currentPosition = videoView.getCurrentPosition();
                    int duration = videoView.getDuration();
                    Message message = handler.obtainMessage(998);
                    message.arg1 = duration;
                    message.arg2 = currentPosition;
                    handler.sendMessage(message);

                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //使用seekBar进行拖拽处理
        if (fromUser) {
            //用户处理才操作
            videoView.seekTo(progress);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isShowing) {
            } else {

                relativeLayout.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);

                isShowing = !isShowing;
            }

        }

        return super.onTouchEvent(event);
    }


    private Handler myHandle = new Handler();
    private Button button;

    public void btnStopMove(View view) {
        button.setBackgroundResource(android.R.drawable.ic_media_pause);
        try {
            if (videoView.isPlaying()) {

                videoView.pause();

            } else {
                videoView.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class GoneThread extends Thread {
        @Override
        public void run() {
            try {

                    while (true) {
                        Thread.sleep(3000);
                        if(isShowing) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                relativeLayout.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.GONE);
                                isShowing = !isShowing;


                            }
                        });
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
