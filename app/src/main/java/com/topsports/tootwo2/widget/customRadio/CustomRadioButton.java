package com.topsports.tootwo2.widget.customRadio;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.topsports.tootwo2.order.R;

/**
 * Created by tootwo2 on 15/9/13.
 */
public class CustomRadioButton extends TextView {
    private Context context;

    private int index = 0;
    private boolean checked = false;// 判断是否选中

    private int state[] = { R.drawable.radio_hk,
            R.drawable.radio_yd };

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        if(checked==true){
            this.setBackgroundResource(R.drawable.radio_yd);
        }else{
            this.setBackgroundResource(R.drawable.radio_hk);
        }
        this.checked = checked;
    }

    public CustomRadioButton(Context context) {
        super(context);

    }

    public CustomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRadioButton(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
    }

}
