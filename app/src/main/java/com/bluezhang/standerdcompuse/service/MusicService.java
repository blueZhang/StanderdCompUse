package com.bluezhang.standerdcompuse.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;
import java.util.Vector;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {


    //确保服务唯一性
    private  MediaPlayer player;
    //创建状态
    public static final int STATE_CREATED = 0;
    //destoy 的状态
    public static final int STATE_DESTORY = 1;
    //准备中的状态
    private static final int STATE_PREPARING = 2;
    //播放中
    private static final int STATE_START = 3;
    //暂停的状态
    private static final int STATE_POUSE = 4;
    private static final int STATE_PLAYER_STOP =5 ;

    /**
     * 
       代表播放器的状态
     */
    private int playerState;
    /**
     * 是否继续下一首
     * 
     */
    private boolean configAutoNext;
    /**
     *  播放列表
     */
   
    private Vector<String> playList;
    private int currentPosition = 0;
    

    public MusicService() {}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playList = new Vector<>();
        player = new MediaPlayer();
        //当准备完成的时候就会回掉
        player.setOnPreparedListener(this);
        //播放结束的时候做的接口
        player.setOnCompletionListener(this);
        
        playerState = STATE_CREATED;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            playerState = STATE_DESTORY;
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();

        }catch (Exception e){
            //因为player的操作可能出现状态的错误
        }
        player = null;
        playList .clear();
        playList = null;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent != null) {
            int action = intent.getIntExtra("action",-1);
            //http://10.10.60.18:8080/nobody.mp3

            switch(action){
                case 0://播放指定网址的声音
                    playMusic(intent);
                    break;
                case 1:
                    pauseMusic(intent);
                case 2:
                    continueMusic(intent);
                    break;
                case 3://重新设置播放列表0 设置播放列表，可以添加自动播放功能
                    break;
            }
        }



        return super.onStartCommand(intent, flags, startId);
    }

    private void pauseMusic(Intent intent) {
        try {
            if (playerState == STATE_START && player.isPlaying()) {
                player.pause();
                playerState = STATE_POUSE;
                
            }
        }catch (Exception e){
            e.printStackTrace();

        }


    }


    private void continueMusic(Intent intent){
        if (player.isPlaying() && playerState == STATE_POUSE){
            player.start();
            playerState = STATE_START;
        }else{
            
        }

    }

    /**
     * 播放intent中的网址
     * @param intent
     */
    private void playMusic(Intent intent) {
        if (intent != null) {

            String url = intent.getStringExtra("url");
            playMusic(url);
        }

    }

    private void playMusic(String url) {
        if (url != null) {

            resetPlayer();

            try {

                player.setDataSource(url);

                player.prepareAsync();

                playerState = STATE_PREPARING;


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 如果正在播放，那么停止，并且重置mediaplayer
     * 准备下次播放
     */
    private void resetPlayer(){
        try {

                player.stop();

        }catch(Exception e){

        }
        player.reset();

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
        playerState = STATE_START;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playerState = STATE_PLAYER_STOP;
        if(playList != null && configAutoNext){
            int size = playList.size();
            if(currentPosition +1 >= size){
                //TODO 不再播放
            }else{
                //下一首
                String url = playList.get(currentPosition + 1);
                playMusic(url);
                currentPosition++;

            }
        }
        
    }
}
