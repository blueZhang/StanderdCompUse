package com.bluezhang.standerdcompuse.service;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.bluezhang.standerdcompuse.BuildConfig;
import com.bluezhang.standerdcompuse.Constants;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 创建之后会自动在清单配置文件中创建Service的配置
 * 配置隐式意图，intent_filter 隐式起动 Service
 * 数据加载服务
 * 如果是启动服务和进程的生命周期是相同的
 * 1、第一个生命周期是onCreat（）（进行资源的初始化数据）
 * 所有的on开头的方法都是在主线程执行的
 * 2、onStartCommend（intent,int flag,int id）：
 * 第一个参数用于传递参数，
 * 这个方法在主线程执行，同时Intent需要多传递一些参数，不要只传递一个extra
 * Intent可以传递一些int类型的参数用于区分service内部的操作
 */
public class DataFetchService extends android.app.Service implements Runnable {
    private Queue<String> urls;

    public DataFetchService() {
        if (BuildConfig.DEBUG) Log.d("DataFetchService", "构造");
    }

    private Thread downloadThread;
    //控制县城时候继续运行
    private boolean running;

    /**
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        if (BuildConfig.DEBUG) Log.d("DataFetchService", "onBind");
        return null;

    }

    /**
     * 在对象的存活期间只会执行一次，
     * 用于初始化数据、对象
     * 通常都是启动线程；
     */
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) Log.d("DataFetchService", "onCreat()");
        urls = new LinkedList<>();

        //启动线程
        downloadThread = new Thread(this);
        downloadThread.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (BuildConfig.DEBUG) Log.d("DataFetchService", "onDestory");


        running = false;
        downloadThread.interrupt();

        //urls 关闭
        urls.clear();
        urls = null;

    }

    /**
     * @param intent
     * @param flags
     * @param startId
     * @return int 返回int类型代表的是意外终止的情况下，服务应该怎么恢复
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //如果调用的是super，那么启动的服务就是粘性的，进程终止，服务重新启动
        //但是 intent 为null

        //如果intent 不是空
        if (BuildConfig.DEBUG) Log.d("DataFetchService", "onStartCommend   " + flags);
        if (intent != null) {
            int action = intent.getIntExtra("action", -1);

            if (BuildConfig.DEBUG) Log.d("DataFetchService", "onStartCommend action = " + action);

            switch (action) {
                case 0: // 0 代表添加网址开始下载
                    processDownload(intent);
                    break;
                case 1: //1 代表获取下载进度
                    getProgress(intent);
                    break;
                case 2: //2 删除现在任务
                    removeDownloadRequest(intent);
                    break;
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }

    //---------------------------------------------

    /**
     * 处理地址下载的操作
     *
     * @param intent
     */
    private void processDownload(Intent intent) {
        if (intent != null) {
            String url = intent.getStringExtra("url");

            if (urls != null) {
                urls.offer(url);
            }
        }
    }
    //--------------------------------------------------

    /**
     * 获取下载的进度 ，利用广播获取
     *
     * @param intent
     */
    private void getProgress(Intent intent) {
        //因为要获取进度，服务在没有绑定的情况下，要使用广播完成进度的更新
        sendUpdateBroadcast();
    }

    private void sendUpdateBroadcast() {
        Intent boardcast = new Intent(Constants.ACTION_PROGRESS_UPDATE);

        String[] strs = new String[urls.size()];
        urls.toArray(strs);
        boardcast.putExtra("urls", strs);
        sendBroadcast(boardcast);
    }

    //--------------------------------

    /**
     * 删除下载的任务
     *
     * @param intent
     */
    private void removeDownloadRequest(Intent intent) {
        if (!urls.isEmpty()) {
            //删除第一个
            urls.poll();


        }

    }


    @Override
    public void run() {
        running = true;
        try {
            while (running) {

                String url = urls.poll();
                if (url != null) {
                    //TODO 模拟网络下载
                    Thread.sleep(3000);
                    sendUpdateBroadcast();

                }
                Thread.sleep(500);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
