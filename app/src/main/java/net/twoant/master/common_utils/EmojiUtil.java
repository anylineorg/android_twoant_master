package net.twoant.master.common_utils;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;

import net.twoant.master.R;
import net.twoant.master.widget.emoji.Emoji;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiUtil {
    private static ArrayList<Emoji> emojiList;
    private static String newStr2;
    private static String find;

    public static ArrayList<Emoji> getEmojiList() {
        if (emojiList == null) {
            emojiList = generateEmojis();
        }
        return emojiList;
    }

    private static ArrayList<Emoji> generateEmojis() {
        ArrayList<Emoji> list = new ArrayList<>();
        for (int i = 0; i < EmojiResArray.length; i++) {
            Emoji emoji = new Emoji();
            emoji.setImageUri(EmojiResArray[i]);
            emoji.setContent(EmojiTextArray[i]);
            list.add(emoji);
        }
        return list;
    }


    public static final int[] EmojiResArray = {
            R.drawable.d_aini,
//            R.drawable.d_aoteman,
            R.drawable.d_baibai,
            R.drawable.d_beishang,
            R.drawable.d_bishi,
            R.drawable.d_bizui,
            R.drawable.d_chanzui,
            R.drawable.d_chijing,
            R.drawable.d_dahaqi,
            R.drawable.d_dalian,
            R.drawable.d_ding,
            R.drawable.d_doge,
            R.drawable.d_feizao,
            R.drawable.d_ganmao,
            R.drawable.d_guzhang,
            R.drawable.d_haha,
            R.drawable.d_haixiu,
            R.drawable.d_han,
            R.drawable.d_hehe,
            R.drawable.d_heixian,
            R.drawable.d_heng,
            R.drawable.d_huaxin,
            R.drawable.d_jiyan,
            R.drawable.d_keai,
            R.drawable.d_kelian,
            R.drawable.d_ku,
            R.drawable.d_kun,
            R.drawable.d_landelini,
            R.drawable.d_lei,
            R.drawable.d_miao,
            R.drawable.d_nanhaier,
            R.drawable.d_nu,
            R.drawable.d_numa,
            R.drawable.d_numa,
            R.drawable.d_qian,
            R.drawable.d_qinqin,
            R.drawable.d_shayan,
            R.drawable.d_shengbing,
            R.drawable.d_shenshou,
            R.drawable.d_shiwang,
            R.drawable.d_shuai,
            R.drawable.d_shuijiao,
            R.drawable.d_sikao,
            R.drawable.d_taikaixin,
            R.drawable.d_touxiao,
            R.drawable.d_tu,
            R.drawable.d_tuzi,
            R.drawable.d_wabishi,
            R.drawable.d_weiqu,
            R.drawable.d_xiaoku,
            R.drawable.d_xiongmao,
            R.drawable.d_xixi,
            R.drawable.d_xu,
            R.drawable.d_yinxian,
            R.drawable.d_yiwen,
            R.drawable.d_youhengheng,
            R.drawable.d_yun,
            R.drawable.d_zhuakuang,
            R.drawable.d_zhutou,
            R.drawable.d_zuiyou,
            R.drawable.d_zuohengheng,
            R.drawable.f_geili,
            R.drawable.f_hufen,
            R.drawable.f_jiong,
            R.drawable.f_meng,
            R.drawable.f_shenma,
            R.drawable.f_v5,
            R.drawable.f_xi,
            R.drawable.f_zhi,
            R.drawable.h_buyao,
            R.drawable.h_good,
            R.drawable.h_haha,
            R.drawable.h_lai,
            R.drawable.h_ok,
            R.drawable.h_quantou,
            R.drawable.h_ruo,
            R.drawable.h_woshou,
            R.drawable.h_ye,
            R.drawable.h_zan,
            R.drawable.h_zuoyi,
            R.drawable.l_shangxin,
            R.drawable.l_xin,
            R.drawable.o_dangao,
            R.drawable.o_feiji,
            R.drawable.o_ganbei,
            R.drawable.o_huatong,
            R.drawable.o_lazhu,
            R.drawable.o_liwu,
            R.drawable.o_lvsidai,
            R.drawable.o_weibo,
            R.drawable.o_weiguan,
            R.drawable.o_yinyue,
            R.drawable.o_zhaoxiangji,
            R.drawable.o_zhong,
            R.drawable.w_fuyun,
            R.drawable.w_shachenbao,
            R.drawable.w_taiyang,
            R.drawable.w_weifeng,
            R.drawable.w_xianhua,
            R.drawable.w_xiayu,
            R.drawable.w_yueliang,
    };

    public static final String[] EmojiTextArray = {
            "[爱你]",
//            "[奥特曼]",
            "[拜拜]",
            "[悲伤]",
            "[鄙视]",
            "[闭嘴]",
            "[馋嘴]",
            "[吃惊]",
            "[哈欠]",
            "[打脸]",
            "[我顶]",
            "[狗狗]",
            "[肥皂]",
            "[感冒]",
            "[鼓掌]",
            "[哈哈]",
            "[害羞]",
            "[流汗]",
            "[微笑]",
            "[黑线]",
            "[哼哼]",
            "[色脸]",
            "[挤眼]",
            "[可爱]",
            "[可怜]",
            "[酷脸]",
            "[犯困]",
            "[白眼]",
            "[流泪]",
            "[喵喵]",
            "[男孩]",
            "[发怒]",
            "[怒骂]",
            "[女孩]",
            "[钱钱]",
            "[亲亲]",
            "[傻眼]",
            "[生病]",
            "[泥马]",
            "[失望]",
            "[衰衰]",
            "[睡觉]",
            "[思考]",
            "[开心]",
            "[偷笑]",
            "[呕吐]",
            "[兔子]",
            "[挖鼻]",
            "[委屈]",
            "[笑哭]",
            "[熊猫]",
            "[嘻嘻]",
            "[唏嘘]",
            "[阴险]",
            "[疑问]",
            "[右哼]",
            "[晕了]",
            "[抓狂]",
            "[猪头]",
            "[最右]",
            "[左哼]",
            "[给力]",
            "[互粉]",
            "[囧囧]",
            "[萌萌]",
            "[神马]",
            "[威武]",
            "[喜字]",
            "[织线]",
            "[哦不]",
            "[很棒]",
            "[爱你]",
            "[勾引]",
            "[好的]",
            "[拳头]",
            "[弱爆]",
            "[握手]",
            "[耶耶]",
            "[点赞]",
            "[作揖]",
            "[伤心]",
            "[爱心]",
            "[蛋糕]",
            "[飞机]",
            "[干杯]",
            "[话筒]",
            "[蜡烛]",
            "[礼物]",
            "[绿丝]",
            "[围脖]",
            "[围观]",
            "[音乐]",
            "[相机]",
            "[闹钟]",
            "[浮云]",
            "[沙尘]",
            "[太阳]",
            "[微风]",
            "[鲜花]",
            "[下雨]",
            "[月亮]",
    };

    static {
        emojiList = generateEmojis();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static void handlerEmojiText(TextView comment, String content, Context context,int reqWidth,int reqHeight) throws IOException {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "\\[(\\S+?)\\]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        Iterator<Emoji> iterator;
        Emoji emoji = null;
        while (m.find()) {
            iterator = emojiList.iterator();
            String tempText = m.group();
            while (iterator.hasNext()) {
                emoji = iterator.next();
                if (tempText.equals(emoji.getContent())) {
                    //转换为Span并设置Span的大小
                    sb.setSpan(new ImageSpan(context, decodeSampledBitmapFromResource(context.getResources(), emoji.getImageUri()
                                    ,reqWidth,reqHeight)),
                            m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                }
            }
        }
        comment.setText(sb);
    }
    //输入法表情展示
    public static String getEmjoiStrig(String str) {
        // Log.i(TAG, "getEmjoiStrig: ----------------str-------" + str);
        // SpannableString spannStr = new SpannableString(str);
        newStr2 = str;
        Pattern p = Pattern.compile("&#[0-9]{4,6}");
        Matcher m = p.matcher(str);
        int index = 0; // 找到字符串所在的起始位置

        String finds = "";

        while (m.find()) {
            find = m.group();
            // Log.i(TAG, "getEmjoiStrig: -------------------find----" + find);
            Pattern p1 = Pattern.compile(find);
            Matcher m1 = p1.matcher(finds);
            int number = 0;
            while (m1.find()) {
                number++;

            }
            //   Log.i(TAG, "getEmjoiStrig: -------------------number----" + number);
            int count = 0;
            for (int i = 0; i < number; i++) {
                count = str.indexOf(find, count) + find.length();
            }
            index = str.indexOf(find, count);
            finds = finds + find;
            // 如果能找到对应的表情
            // Log.i(TAG, "getEmjoiStrig: -------------------finds----" + finds);
            String substring = find.substring(2, find.length());
            //  Log.i("TAG", "getEmjoiStrig: -------------------substring----" + substring);
            Integer integer = Integer.valueOf(substring);
//            Log.i("TAG", "getEmjoiStrig: -------------------integer----" + integer);
//            Log.i("TAG", "getEmjoiStrig: -------------Character.toChars(integer)----------" + Character.toChars(integer));

            String s1 = new String(Character.toChars(integer));
            //Log.i("TAG", "getEmjoiStrig: -------------------s1----" + s1);
            // Log.i(TAG, "getEmjoiStrig: -------------------find.length()----" + find.length());
            //  Log.i(TAG, "getEmjoiStrig: -------------------index----" + index);
            newStr2 = newStr2.replaceAll(find, s1);
        }
        Log.d("TAG", "getEmjoiStrig: "+newStr2);
        //return spannStr;
        return newStr2;
    }
    //输入法表情上传（转化为十六进制数）
    public static String getEmojiStrings(String s) {
        int a = 0;
        String aa = "";
        String sss = "";
        int b = 0;
        int cccc = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            String string = Integer.toHexString(c);
            int i2 = Integer.parseInt(string, 16);
            if (123 <= i2 && i2 <= 12953) {
                aa = "&#" + i2 ;
                a = 0;
                b = 0;
                cccc = 0;
                sss = sss + aa;
            } else if (isEmojiCharacter(c)) {
                b++;
                Log.i("TAG", "onClick: ---------------b-------------" + b);

                a = a + i2;
                if (b % 2 == 0) {
                    //  Log.d(TAG, "     " + Integer.toHexString(a + 16419));
                    Log.i("TAG", "onClick: ----------11-----a-------------" + (a));
                    // aa = "&#" + (a + 16419) + ";";
                    aa = "&#" + (a + cccc);
                    a = 0;
                    b = 0;
                    cccc = 0;
                    sss = sss + aa;
                } else {
                    Log.i("TAG", "onClick: ---------------i2-------------" + i2);

                    if (i2 == 55356) {
                        cccc = 15300;
                    } else if (i2 == 55357) {
                        cccc = 16323;
                    }
                }
                Log.i("TAG", "onClick: ---------------Integer.toHexString(codePoint)-------------" + Integer.toHexString(c));
                Log.i("TAG", "onClick: ---------------i2-------------" + i2);
                Log.i("TAG", "onClick: ---------------a-------------" + a);
            } else {
                sss = sss + new String(Character.toString(c));
            }
        }
        return sss;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }
}
