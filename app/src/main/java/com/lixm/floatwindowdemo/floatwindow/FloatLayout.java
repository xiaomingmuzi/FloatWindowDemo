package com.lixm.floatwindowdemo.floatwindow;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lixm.floatwindowdemo.R;
import com.lixm.floatwindowdemo.TimeUtil;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.lang.reflect.Field;

/**
 * Describe:小窗View
 * <p>
 * Author: Lixm
 * Date: 2019/7/24
 */
public class FloatLayout extends LinearLayout implements ITXVodPlayListener {

    private Context mContext;
    private TXCloudVideoView video_view;
    private ImageView iv_close;
    private TextView time_played, time;
    private SeekBar fixureSeekBar;

    private float xInView;//记录手指按下时在小悬浮窗的view上的横坐标
    private float yInView;//...............................纵坐标
    private static int statusBarHeight;//状态栏高度
    private float xInScreen;//记录当前手指在屏幕上的横坐标位置
    private float yInScreen;//记录当前手指位置在屏幕上的纵坐标位置
    private float xDownInScreen;//记录手指按下时在屏幕上的横坐标值
    private float yDownInScreen;//记录手指按下时在屏幕上的纵坐标值

    private UpdateController controller;

    private TXVodPlayer mVodPlayer;
    private int mCurrentRenderMode;

    public FloatLayout(Context context) {
        super(context);
        init(context);
    }

    public FloatLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.float_layout, this);
        mContext = context;
        video_view = findViewById(R.id.video_view);
        iv_close = findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller != null)
                    controller.onClose();
            }
        });

        mCurrentRenderMode = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
        mVodPlayer = new TXVodPlayer(mContext);
        mVodPlayer.setRenderMode(mCurrentRenderMode);
        mVodPlayer.setPlayerView(video_view);

        time_played = findViewById(R.id.time_played);
        time = findViewById(R.id.time);
        fixureSeekBar = findViewById(R.id.media_controller_progress);
        fixureSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                time_played.setText(TimeUtil.secondToString(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mVodPlayer.seek(seekBar.getProgress());
            }
        });
    }

    public void setController(UpdateController controller) {
        this.controller = controller;
    }


    public void startPlay(String url, float time) {
        mVodPlayer.setVodListener(this);
        if (mVodPlayer.isPlaying()) {
            mVodPlayer.stopPlay(false);
        }
        mVodPlayer.enableHardwareDecode(false);
        mVodPlayer.setLoop(true);
        mVodPlayer.setStartTime(time);
        mVodPlayer.startPlay(url);
    }

    public void pausePlay() {
        if (mVodPlayer.isPlaying())
            mVodPlayer.pause();
    }

    public void onDestory() {
        mVodPlayer.setVodListener(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xInView = event.getX();
                yInView = event.getY();

                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();

                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;

            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                //手指移动的时候，更新小悬浮框的位置
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
                    if (controller != null) {
                        controller.onClick();
                    }
                }
                break;
        }
        return true;
    }

    private void updateViewPosition() {
        int x = (int) (xInScreen - xInView);
        int y = (int) (yInScreen - yInView);
        if (controller != null) {
            controller.onFloatUpdate(x, y);
        }

    }

    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (int) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    public int dp2Px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    @Override
    public void onPlayEvent(TXVodPlayer txVodPlayer, int event, Bundle param) {
        Log.w("FloatLayout", "当前播放码：" + event);

        if (TXLiveConstants.PLAY_EVT_PLAY_PROGRESS == event) {       // 2005 忽略process事件
            int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS); //进度（秒数）
            int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION); //时间（秒数）
            // UI进度进行相应的调整
            fixureSeekBar.setProgress(progress);
            time_played.setText(TimeUtil.secondToString(progress));
            time.setText(TimeUtil.secondToString(duration));
            fixureSeekBar.setMax(duration);
        }
    }

    @Override
    public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {

    }
}
