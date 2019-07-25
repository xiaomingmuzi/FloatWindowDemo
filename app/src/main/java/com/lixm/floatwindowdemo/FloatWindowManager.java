package com.lixm.floatwindowdemo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

/**
 * Describe:悬浮窗统一管理，与悬浮窗交互的真正实现
 * <p>
 * Author: Lixm
 * Date: 2019/7/24
 */
public class FloatWindowManager {

    private static WindowManager.LayoutParams params;
    private static WindowManager manager;
    private static FloatLayout floatLayout;
    private static boolean mHasShown;

    /**
     * @param context 必须为应用程序的Context
     */
    public static void createFloatWindow(Context context,String url,float time) {
        params = new WindowManager.LayoutParams();
        final WindowManager manager = getWindowManager(context);
        floatLayout = new FloatLayout(context);
        floatLayout.setController(new UpdateController() {
            @Override
            public void onFloatUpdate(int x, int y) {
                params.x = x;
                params.y = y;
                manager.updateViewLayout(floatLayout, params);
            }

            @Override
            public void onClick() {

            }

            @Override
            public void onClose() {
                hide();
            }
        });
        floatLayout.startPlay(url,time);
        if (Build.VERSION.SDK_INT >= 24) {//android7.0 不能用TYPE_TOAST
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {//以下代码块是的android6.0 之后的用户不必再去手动开启悬浮框权限
            String packName = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission(
                    "android.permission.SYSTEM_ALERT_WINDOW", packName));
            if (permission) {
                params.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                params.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        }

        //设置图片格式，效果为背景透明
        params.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦(实现操作除浮动窗口外的其他可见窗口的操作)
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为居中
        params.gravity = Gravity.START | Gravity.TOP;

        DisplayMetrics dm = new DisplayMetrics();
        //取得手机屏幕属性
        manager.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        //以屏幕左上角为原点，设置x，y初始值，相对于gravity
        params.x = (screenWidth - floatLayout.dp2Px(240)) / 2;
        params.y = (screenHeight - floatLayout.dp2Px(120)) / 2;

        //设置悬浮框的长宽
        params.width = floatLayout.dp2Px(240);
        params.height = floatLayout.dp2Px(120);

        manager.addView(floatLayout, params);
        mHasShown = true;

    }

    private static WindowManager getWindowManager(Context context) {
        if (manager == null) {
            manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return manager;
    }

    /**
     * 移除悬浮窗
     */
    public static void removeFloatWindowManager() {
        //移除悬浮窗口
        boolean isAttach = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isAttach = floatLayout.isAttachedToWindow();
        }
        if (mHasShown && isAttach && manager != null) {
            manager.removeView(floatLayout);
        }
    }

    public static void hide() {
        if (mHasShown)
            manager.removeViewImmediate(floatLayout);
        mHasShown = false;
        floatLayout.pausePlay();
    }

    public static void show() {
        if (!mHasShown)
            manager.addView(floatLayout, params);
        mHasShown = true;
    }

}
