package com.wbb.base.util;

import android.os.CountDownTimer;

/**
 * Created by hy on 2016/7/25.
 */
public class CountDownUtil extends CountDownTimer {
//    private WeakReference<TextView> view;

    private TimeCallBack timeCallBack;

    public CountDownUtil(long millisInFuture, long countDownInterval, TimeCallBack timeCallBack) {
        super(millisInFuture, countDownInterval);
//        view = new WeakReference<>(textView);
        this.timeCallBack = timeCallBack;
    }

    @Override
    public void onTick(long millisUntilFinished) {
//        view.get().setText((millisUntilFinished / 1000) + "s");
        timeCallBack.onTimeTick(millisUntilFinished);
    }

    @Override
    public void onFinish() {
        cancel();//停止线程
        timeCallBack.onTimeOut();
//        view.get().setBackground(ContextCompat.getDrawable(BaseApplication.getInstance(), R.drawable.shape_getverify_code_green_bg));
    }


    public interface TimeCallBack {
        void onTimeOut();

        void onTimeTick(long millisUntilFinished);
    }
}
