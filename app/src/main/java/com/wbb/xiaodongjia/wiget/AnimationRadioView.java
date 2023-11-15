package com.wbb.xiaodongjia.wiget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;

import com.airbnb.lottie.LottieAnimationView;

public class AnimationRadioView extends LottieAnimationView implements Checkable {
    private boolean checked;
    private int img;

    public AnimationRadioView(Context context) {
        this(context, null);
    }

    public AnimationRadioView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationRadioView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean isChecked() {
        return this.checked;
    }

    @Override
    public void setChecked(boolean checked) {

        try {
//            if (this.checked != checked) {
                this.checked = checked;
                if (isAnimating()) {
                    cancelAnimation();
                }
                if (checked) {
                    if (getSpeed() < 0.0F) {
                        setSpeed(1);
                    }
                    playAnimation();
                } else {
                    if (getSpeed() > 0.0F) {
                        setSpeed(-100);
                    }
                    playAnimation();
                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toggle() {
        setChecked(!this.checked);
    }

}
