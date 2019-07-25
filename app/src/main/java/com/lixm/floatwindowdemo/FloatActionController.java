package com.lixm.floatwindowdemo;

import android.content.Context;
import android.content.Intent;

/**
 * Describe:与悬浮框交互的控制类
 * <p>
 * Author: Lixm
 * Date: 2019/7/24
 */
public class FloatActionController {

    private FloatActionController(){}

    public static FloatActionController getInstance(){
        return LittleMonkProviderHolder.sInstance;
    }

    private static class LittleMonkProviderHolder{
        private static final FloatActionController sInstance=new FloatActionController();
    }

    private FloatCallBack mCallLittleMonk;

    /**
     * 开启服务悬浮框
     * @param context
     */
    public void startMonkServer(Context context,String url,float time){
        Intent intent=new Intent(context,FloatMonkService.class);
        intent.putExtra("Url",url);
        intent.putExtra("Time",time);
        context.startService(intent);
    }

    /**
     * 关闭悬浮框
     * @param context
     */
    public void stopMonkServer(Context context){
        Intent intent=new Intent(context,FloatMonkService.class);
        context.stopService(intent);
    }

    public void registerCallLittleMonk(FloatCallBack callLittleMonk){
        mCallLittleMonk=callLittleMonk;
    }

    /**
     * 显示
     */
    public void show(){
        if (mCallLittleMonk==null) return;
        mCallLittleMonk.show();
    }

    /**
     * 隐藏
     */
    public void hide(){
        if (mCallLittleMonk==null) return;
        mCallLittleMonk.hide();
    }
}
