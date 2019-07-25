package com.lixm.floatwindowdemo;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements ITXVodPlayListener {

    private Context mContext;
    private TXCloudVideoView video_view;
    private TXVodPlayer mVodPlayer;
    private int mCurrentRenderMode;

    private ImageView iv_back;
    private TextView time_played, time;
    private SeekBar fixureSeekBar;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        video_view = findViewById(R.id.video_view);
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

        mVodPlayer = new TXVodPlayer(mContext);
        mCurrentRenderMode = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
        mVodPlayer.setRenderMode(mCurrentRenderMode);
        mVodPlayer.setLoop(true);

        mVodPlayer.setPlayerView(video_view);
        mVodPlayer.setVodListener(this);
        mVodPlayer.enableHardwareDecode(true);
        url = "http://200024424.vod.myqcloud.com/200024424_709ae516bdf811e6ad39991f76a4df69.f20.mp4";
        mVodPlayer.startPlay(url);

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 6.0动态申请悬浮窗权限
                    if (!Settings.canDrawOverlays(mContext)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                        mContext.startActivity(intent);
                        return;
                    }
                } else {
                    if (!checkOp(mContext, OP_SYSTEM_ALERT_WINDOW)) {
                        Toast.makeText(mContext, "进入设置页面失败,请手动开启悬浮窗权限", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
//
//                mWindowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//                mWindowParams = new WindowManager.LayoutParams();
//                mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//                mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//                mWindowParams.format = PixelFormat.TRANSLUCENT;
//                mWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
//
////                SuperPlayerGlobalConfig.TXRect rect = prefs.floatViewRect;
//                int []screenSize=getScreenSize((Activity) mContext);
//                mWindowParams.x = screenSize[0]/2-dp2Px(240)/2;
//                mWindowParams.y = screenSize[1]/2-dp2Px(120)/2;
//                mWindowParams.width = dp2Px(240);
//                mWindowParams.height = dp2Px(120);
//
//                final View view1 = LayoutInflater.from(mContext).inflate(R.layout.float_layout, null);
//                view1.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mWindowManager.removeViewImmediate(view1);
//                    }
//                });
//                mWindowManager.addView(view1, mWindowParams);

                //开启悬浮框
                float time=fixureSeekBar.getProgress();
                Log.w("当前时间","time : "+time);
                FloatActionController.getInstance().startMonkServer(mContext, url, time);
                if (mVodPlayer.isPlaying())
                    mVodPlayer.pause();

            }
        });
    }

    private final int OP_SYSTEM_ALERT_WINDOW = 24;

    /**
     * API <18，默认有悬浮窗权限，不需要处理。无法接收无法接收触摸和按键事件，不需要权限和无法接受触摸事件的源码分析
     * API >= 19 ，可以接收触摸和按键事件
     * API >=23，需要在manifest中申请权限，并在每次需要用到权限的时候检查是否已有该权限，因为用户随时可以取消掉。
     * API >25，TYPE_TOAST 已经被谷歌制裁了，会出现自动消失的情况
     */
    private boolean checkOp(Context context, int op) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Method method = AppOpsManager.class.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                Log.e(getClass().getName(), Log.getStackTraceString(e));
            }
        }
        return true;
    }


    @Override
    public void onPlayEvent(TXVodPlayer txVodPlayer, int event, Bundle param) {
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

    public int dp2Px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public int[] getScreenSize(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FloatActionController.getInstance().stopMonkServer(this);
    }
}
