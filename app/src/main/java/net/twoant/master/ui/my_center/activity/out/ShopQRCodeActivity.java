package net.twoant.master.ui.my_center.activity.out;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import net.twoant.master.R;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.SPUtils;
import net.twoant.master.ui.other.activity.ImageScaleAdapter;

import java.util.Hashtable;

/**
 * Created by DZY on 2016/12/31.
 * 佛祖保佑   永无BUG
 */

public class ShopQRCodeActivity extends LongBaseActivity {

    ImageView code_img;  Bitmap qrCodeBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.gk_shop_qrcode;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initUI();
        setImage();
    }

    private void setImage() {

        String shop_id = getIntent().getStringExtra("shop_id");
        if (shop_id==null) {
            shop_id = SPUtils.get(ShopQRCodeActivity.this, "shop_id", "") + "";
        }
        String contentString = shop_id;//AiSouAppInfoModel.uid.toString();
        if (!contentString.equals("")) {
            //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（400*400）
            try {
                qrCodeBitmap = EncodingHandler.createQRCode(contentString, 400);
                code_img.setImageBitmap(qrCodeBitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }

        }else {
            Toast.makeText(this, "请登录账号", Toast.LENGTH_SHORT).show();
        }
    }

    void initUI(){
        code_img=(ImageView) findViewById(net.twoant.master.R.id.shop_qrcode);
        findViewById(R.id.down_loadqr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageScaleAdapter.saveImageToGallery(ShopQRCodeActivity.this,qrCodeBitmap);
            }
        });
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopQRCodeActivity.this.finish();
            }
        });
    }

    public static class EncodingHandler {
        private static final int BLACK = 0xff000000;
        public static Bitmap createQRCode(String str, int widthAndHeight) throws WriterException {
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix matrix = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = BLACK;
                    }else {
                        pixels[y * width + x] = Color.WHITE;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        }
    }
}
