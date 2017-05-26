package net.twoant.master.widget.emoji;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiUtil {
    private static ArrayList<Emoji> emojiList;

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
            net.twoant.master.R.drawable.d_aini,
//            R.drawable.d_aoteman,
            net.twoant.master.R.drawable.d_baibai,
            net.twoant.master.R.drawable.d_beishang,
            net.twoant.master.R.drawable.d_bishi,
            net.twoant.master.R.drawable.d_bizui,
            net.twoant.master.R.drawable.d_chanzui,
            net.twoant.master.R.drawable.d_chijing,
            net.twoant.master.R.drawable.d_dahaqi,
            net.twoant.master.R.drawable.d_dalian,
            net.twoant.master.R.drawable.d_ding,
            net.twoant.master.R.drawable.d_doge,
            net.twoant.master.R.drawable.d_feizao,
            net.twoant.master.R.drawable.d_ganmao,
            net.twoant.master.R.drawable.d_guzhang,
            net.twoant.master.R.drawable.d_haha,
            net.twoant.master.R.drawable.d_haixiu,
            net.twoant.master.R.drawable.d_han,
            net.twoant.master.R.drawable.d_hehe,
            net.twoant.master.R.drawable.d_heixian,
            net.twoant.master.R.drawable.d_heng,
            net.twoant.master.R.drawable.d_huaxin,
            net.twoant.master.R.drawable.d_jiyan,
            net.twoant.master.R.drawable.d_keai,
            net.twoant.master.R.drawable.d_kelian,
            net.twoant.master.R.drawable.d_ku,
            net.twoant.master.R.drawable.d_kun,
            net.twoant.master.R.drawable.d_landelini,
            net.twoant.master.R.drawable.d_lei,
            net.twoant.master.R.drawable.d_miao,
            net.twoant.master.R.drawable.d_nanhaier,
            net.twoant.master.R.drawable.d_nu,
            net.twoant.master.R.drawable.d_numa,
            net.twoant.master.R.drawable.d_numa,
            net.twoant.master.R.drawable.d_qian,
            net.twoant.master.R.drawable.d_qinqin,
            net.twoant.master.R.drawable.d_shayan,
            net.twoant.master.R.drawable.d_shengbing,
            net.twoant.master.R.drawable.d_shenshou,
            net.twoant.master.R.drawable.d_shiwang,
            net.twoant.master.R.drawable.d_shuai,
            net.twoant.master.R.drawable.d_shuijiao,
            net.twoant.master.R.drawable.d_sikao,
            net.twoant.master.R.drawable.d_taikaixin,
            net.twoant.master.R.drawable.d_touxiao,
            net.twoant.master.R.drawable.d_tu,
            net.twoant.master.R.drawable.d_tuzi,
            net.twoant.master.R.drawable.d_wabishi,
            net.twoant.master.R.drawable.d_weiqu,
            net.twoant.master.R.drawable.d_xiaoku,
            net.twoant.master.R.drawable.d_xiongmao,
            net.twoant.master.R.drawable.d_xixi,
            net.twoant.master.R.drawable.d_xu,
            net.twoant.master.R.drawable.d_yinxian,
            net.twoant.master.R.drawable.d_yiwen,
            net.twoant.master.R.drawable.d_youhengheng,
            net.twoant.master.R.drawable.d_yun,
            net.twoant.master.R.drawable.d_zhuakuang,
            net.twoant.master.R.drawable.d_zhutou,
            net.twoant.master.R.drawable.d_zuiyou,
            net.twoant.master.R.drawable.d_zuohengheng,
            net.twoant.master.R.drawable.f_geili,
            net.twoant.master.R.drawable.f_hufen,
            net.twoant.master.R.drawable.f_jiong,
            net.twoant.master.R.drawable.f_meng,
            net.twoant.master.R.drawable.f_shenma,
            net.twoant.master.R.drawable.f_v5,
            net.twoant.master.R.drawable.f_xi,
            net.twoant.master.R.drawable.f_zhi,
            net.twoant.master.R.drawable.h_buyao,
            net.twoant.master.R.drawable.h_good,
            net.twoant.master.R.drawable.h_haha,
            net.twoant.master.R.drawable.h_lai,
            net.twoant.master.R.drawable.h_ok,
            net.twoant.master.R.drawable.h_quantou,
            net.twoant.master.R.drawable.h_ruo,
            net.twoant.master.R.drawable.h_woshou,
            net.twoant.master.R.drawable.h_ye,
            net.twoant.master.R.drawable.h_zan,
            net.twoant.master.R.drawable.h_zuoyi,
            net.twoant.master.R.drawable.l_shangxin,
            net.twoant.master.R.drawable.l_xin,
            net.twoant.master.R.drawable.o_dangao,
            net.twoant.master.R.drawable.o_feiji,
            net.twoant.master.R.drawable.o_ganbei,
            net.twoant.master.R.drawable.o_huatong,
            net.twoant.master.R.drawable.o_lazhu,
            net.twoant.master.R.drawable.o_liwu,
            net.twoant.master.R.drawable.o_lvsidai,
            net.twoant.master.R.drawable.o_weibo,
            net.twoant.master.R.drawable.o_weiguan,
            net.twoant.master.R.drawable.o_yinyue,
            net.twoant.master.R.drawable.o_zhaoxiangji,
            net.twoant.master.R.drawable.o_zhong,
            net.twoant.master.R.drawable.w_fuyun,
            net.twoant.master.R.drawable.w_shachenbao,
            net.twoant.master.R.drawable.w_taiyang,
            net.twoant.master.R.drawable.w_weifeng,
            net.twoant.master.R.drawable.w_xianhua,
            net.twoant.master.R.drawable.w_xiayu,
            net.twoant.master.R.drawable.w_yueliang,
    };

    public static final String[] EmojiTextArray = {
            "[吐爱]",
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

    public static void handlerEmojiText(TextView comment, String content, Context context) throws IOException {
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
                                    , dip2px(context, 21), dip2px(context, 21))),
                            m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                }
            }
        }
        comment.setText(sb);
    }
}
