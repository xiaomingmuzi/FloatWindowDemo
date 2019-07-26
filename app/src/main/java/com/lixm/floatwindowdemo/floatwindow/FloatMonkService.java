package com.lixm.floatwindowdemo.floatwindow;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * Describe:悬浮框在服务中创建，通过暴漏接口FloatCallBack与Activity进行交互
 * <p>
 * Author: Lixm
 * Date: 2019/7/24
 */
public class FloatMonkService extends Service implements FloatCallBack {

    /**
     * home键监听
     */
    private HomeWatchReceiver homeWatchReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        FloatActionController.getInstance().registerCallLittleMonk(this);
        //注册广播接受者
        homeWatchReceiver = new HomeWatchReceiver();
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(homeWatchReceiver, homeFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url=intent.getStringExtra("Url");
        float time=intent.getFloatExtra("Time",0);
        //初始化悬浮框UI
        initWindowData(url,time);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initWindowData(String url,float time) {
        FloatWindowManager.createFloatWindow(this,url,time);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void show() {
        FloatWindowManager.show();
    }

    @Override
    public void hide() {
        FloatWindowManager.hide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //移除悬浮窗
        FloatWindowManager.removeFloatWindowManager();
        //注销广播接收者
        if (null != homeWatchReceiver) {
            unregisterReceiver(homeWatchReceiver);
        }
    }
}
