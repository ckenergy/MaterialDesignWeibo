package cn.net.cc.weibo.view.emojitextview;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import cn.net.cc.weibo.R;

/**
 * Created by wenmingvs on 16/4/15.
 */
public class WeiBoContentClickableSpan extends ClickableSpan {

    private Context mContext;

    public WeiBoContentClickableSpan(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(View widget) {

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(mContext.getResources().getColor(R.color.click_txt));
        ds.setUnderlineText(false);
    }



}
