package cn.net.cc.weibo.ui.view.emojitextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.ui.user.UserActivity;
import cn.net.cc.weibo.util.DensityUtil;

//TODO callback
public class WeiBoContentTextUtil {

   public static class Section {
       public static final int AT = 1;
       public static final int TOPIC = 2;// ##话题
       public static final int URL = 3;// url
       public static final int EMOJI = 4;//emoji 表情

       private int start;
       private int end;
       private int type;
       private String name;


       Section(int start, int end, int type, String name) {
           this.start = start;
           this.end = end;
           this.type = type;
           this.name = name;
        }

       @Override
       public String toString() {
           return "start:"+start+",end:"+end;
       }
   }
    private static final String TAG = "WeiBoContentTextUtil";

    private static final String AT = "@[\\w\\p{InCJKUnifiedIdeographs}-]{1,26}";// @人
    private static final String TOPIC = "#[\\p{Print}\\p{InCJKUnifiedIdeographs}&&[^#]]+#";// ##话题
    private static final String URL = "http://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]";// url
    private static final String EMOJI = "\\[(\\S+?)\\]";//emoji 表情

    private static final String ALL = "(" + AT + ")" + "|" + "(" + TOPIC + ")" + "|" + "(" + URL + ")" + "|" + "(" + EMOJI + ")";

    public static SpannableStringBuilder getWeiBoContent(final String source, final Context context, final TextView textView,
                                                         final OnClickListener onClickListener) {

        final ArrayList<Section> sections = new ArrayList<>();
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(source);
        //设置正则
        Pattern pattern = Pattern.compile(ALL);
        Matcher matcher = pattern.matcher(spannableStringBuilder);

        if (matcher.find()) {
            if (!(textView instanceof EditText)) {
                textView.setMovementMethod(ClickableMovementMethod.getInstance());
                textView.setFocusable(false);
                textView.setClickable(false);
                textView.setLongClickable(false);
            }
            matcher.reset();
        }

        while (matcher.find()) {
            final String at = matcher.group(1);
            final String topic = matcher.group(2);
            final String url = matcher.group(3);
            final String emoji = matcher.group(4);

            //处理@用户
            if (at != null) {
                int start = matcher.start(1);
                int end = start + at.length();
                Section section = new Section(start,end,Section.AT,at);
                sections.add(section);
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.navigation_green));
                spannableStringBuilder.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
            //处理##话题
            if (topic != null) {
                int start = matcher.start(2);
                int end = start + topic.length();
                Section section = new Section(start,end,Section.TOPIC,topic);
                sections.add(section);
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.navigation_green));
                spannableStringBuilder.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }

            // 处理url地址
            if (url != null) {
                int start = matcher.start(3);
                int end = start + url.length();
                Section section = new Section(start,end,Section.URL,url);
                sections.add(section);
                Drawable websiteDrawable = context.getResources().getDrawable(R.drawable.button_web);
                websiteDrawable.setBounds(0, 0, websiteDrawable.getIntrinsicWidth(), websiteDrawable.getIntrinsicHeight());
                ImageSpan imageSpan = new ImageSpan(websiteDrawable, ImageSpan.ALIGN_BOTTOM) {

                    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
                        Drawable b = getDrawable();
                        canvas.save();
                        int transY = bottom - b.getBounds().bottom;
                        transY -= paint.getFontMetricsInt().descent / 2;
                        canvas.translate(x, transY);
                        b.draw(canvas);
                        canvas.restore();
                    }

                };

                spannableStringBuilder.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
            //emoji
            if (emoji != null) {
                int start = matcher.start(4);
                int end = start + emoji.length();
                String imgName = Emoticons.getImgName(emoji);
                if (!TextUtils.isEmpty(imgName)) {
                    int resId = context.getResources().getIdentifier(imgName, "drawable", context.getPackageName());
                    if (resId != 0){
                        Drawable emojiDrawable = context.getResources().getDrawable(resId);
                        if (emojiDrawable != null) {
                            emojiDrawable.setBounds(0, 0, DensityUtil.sp2px(context, 17), DensityUtil.sp2px(context, 17));
                            ImageSpan imageSpan = new ImageSpan(emojiDrawable, ImageSpan.ALIGN_BOTTOM) {
                                public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
                                    Drawable b = getDrawable();
                                    canvas.save();
                                    int transY = bottom - b.getBounds().bottom;
                                    transY -= paint.getFontMetricsInt().descent / 2;
                                    canvas.translate(x, transY);
                                    b.draw(canvas);
                                    canvas.restore();
                                }
                            };
                            spannableStringBuilder.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        }
                    }
                }
            }

        }
        final BackgroundColorSpan span = new BackgroundColorSpan(context.getResources().getColor(R.color.light_blue));
        final int slop = ViewConfiguration.get(context).getScaledTouchSlop();
        textView.setOnTouchListener(new View.OnTouchListener() {

            int downX,downY;
            Section downSection = null;
            int id;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                Layout layout = textView.getLayout();
                if (layout == null) {
                    Log.d(TAG,"layout is null");
                    return false;
                }
                int line = 0;
                int index = 0;

                switch(action) {
                    case MotionEvent.ACTION_DOWN://TODO 最后一行点击问题 网址链接
                        int actionIndex = event.getActionIndex();
                        id = event.getPointerId(actionIndex);
                        downX = (int) event.getX(actionIndex);
                        downY = (int) event.getY(actionIndex);
                        Log.d(TAG, "ACTION_down,x:"+event.getX()+",y:"+event.getY());
                        line = layout.getLineForVertical(textView.getScrollY() + (int)event.getY());
                        index = layout.getOffsetForHorizontal(line, (int)event.getX());
                        int lastRight = (int) layout.getLineRight(line);
                        Log.d(TAG,"lastRight:"+lastRight);
                        if (lastRight < event.getX()) {  //文字最后为话题时，如果点击在最后一行话题之后，也会造成话题被选中效果
                            return false;
                        }
                        Log.d(TAG," index:"+ index+",sections:"+sections.size());
                        for (Section section : sections) {
                            if (index >= section.start &&  index <= section.end) {
                                spannableStringBuilder.setSpan(span,section.start,section.end,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                downSection = section;
                                textView.setText(spannableStringBuilder);
                                textView.getParent().requestDisallowInterceptTouchEvent(true);//不允许父view拦截
                                Log.d(TAG,"downSection"+downSection.toString());
                                return true;
                            }
                        }
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        int indexMove = event.findPointerIndex(id);
                        int currentX = (int) event.getX(indexMove);
                        int currentY = (int) event.getY(indexMove);
                        Log.d(TAG, "ACTION_MOVE,x:"+currentX+",y:"+currentY);
                        if (Math.abs(currentX-downX) < slop && Math.abs(currentY-downY) < slop) {
                            if (downSection == null) {
                                return false;
                            }
                            break;
                        }
                        downSection = null;
                        textView.getParent().requestDisallowInterceptTouchEvent(false);//允许父view拦截

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        int indexUp = event.findPointerIndex(id);
                        spannableStringBuilder.removeSpan(span);
                        textView.setText(spannableStringBuilder);
                        int upX = (int) event.getX(indexUp);
                        int upY = (int) event.getY(indexUp);
                        Log.d(TAG, "ACTION_UP,x:"+upX+",y:"+upY);
                        if (Math.abs(upX-downX) < slop && Math.abs(upY-downY) < slop) {
                            //TODO startActivity or whatever
                            if (downSection != null) {
                                String name = downSection.name;
                                if (onClickListener != null) {
                                    onClickListener.onClick(downSection.type, name);
                                }

                                Toast.makeText(context,name,Toast.LENGTH_SHORT).show();
                                downSection = null;
                            }else {
                                return false;
                            }
                        }else {
                            downSection = null;
                            return false;
                        }
                        break;
                }
                Log.d(TAG,"true");
                return true;
            }
        });
        return spannableStringBuilder;
    }

    public static class SimpleClickListener implements OnClickListener {

        Context context;

        public SimpleClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(int type, String name) {
            switch (type) {
                case Section.AT:
                    if (name.startsWith("@")) {
                        name = name.substring(1);
                    }
                    UserActivity.startThis(context, name);
                    break;
            }
        }
    }

    public interface OnClickListener {
        public void onClick(int type, String name);
    }
}
