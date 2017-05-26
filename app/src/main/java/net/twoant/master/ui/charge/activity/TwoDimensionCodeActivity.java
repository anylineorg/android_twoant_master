package net.twoant.master.ui.charge.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import net.twoant.master.common_utils.SPUtils;
import net.twoant.master.common_utils.ToastUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by DZY on 2016/12/3.
 */

public class TwoDimensionCodeActivity extends Activity {
    ImageView code_img;  Bitmap qrCodeBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(net.twoant.master.R.layout.zy_twodimensioncode_activity);
        initUI();
        setImage();
    }
    private void setImage() {

        String shop_id=  getIntent().getStringExtra("shop_id");
        if (shop_id==null) {
            shop_id = SPUtils.get(TwoDimensionCodeActivity.this, "shop_id", "") + "";
        }
        String contentString = "type=2,id="+shop_id;//AiSouAppInfoModel.uid.toString();
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
    String BaseFilePath="";
    public void saveFile(Bitmap bm) {
        BaseFilePath= Environment.getExternalStorageDirectory().getAbsolutePath();

        String name="shop.jpg";
        try {
            File dirFile = new File(BaseFilePath);
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }
            BaseFilePath += "/";
            File myCaptureFile = new File(BaseFilePath + name);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
            ToastUtil.showLong("保存文件成功,位置");
        }catch (Exception e){
            ToastUtil.showLong("错误:"+e.getMessage());
        }
    }


    void  savepic(){
        FileOutputStream out=null;
        if (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED)) // 判断是否可以对SDcard进行操作
        {    // 获取SDCard指定目录下
            String  sdCardDir = Environment.getExternalStorageDirectory()+ "/CoolImage/";
            File dirFile  = new File(sdCardDir);  //目录转化成文件夹
            if (!dirFile .exists()) {              //如果不存在，那就建立这个文件夹
                dirFile .mkdirs();
            }                          //文件夹有啦，就可以保存图片啦
            File file = new File(sdCardDir, System.currentTimeMillis()+".jpg");// 在SDcard的目录下创建图片文,以当前时间为其命名

            try {
                out = new FileOutputStream(file);
                qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                System.out.println("_________保存到____sd______指定目录文件夹下____________________");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(TwoDimensionCodeActivity.this,"保存已经至"+Environment.getExternalStorageDirectory()+"/CoolImage/"+"目录文件夹下", Toast.LENGTH_SHORT).show();
        }
    }

    void cartPic(){
        BaseFilePath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
        String name="shop.jpg";
        File imageFile = new File(BaseFilePath+name);
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(imageFile);
            Bitmap image = code_img.getDrawingCache();
            image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();

            ToastUtil.showLong("保存文件成功,位置"+name);
            //   Toast.makeText(UIUtils.getContext(), UIUtils.getString(R.string.save_picture_success), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            //  onError(e);
        }
    }
    void initUI(){
        code_img=(ImageView) findViewById(net.twoant.master.R.id.shop_qrcode);
        findViewById(net.twoant.master.R.id.down_loadqr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savepic();
                // saveFile(qrCodeBitmap);
                // cartPic();
            }
        });
        TextView qr= (TextView) findViewById(net.twoant.master.R.id.tv_Title);
        qr.setText("收款码");
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoDimensionCodeActivity.this.finish();
            }
        });
    }

    static class EncodingHandler {
        private static final int BLACK = 0xff000000;

        public static Bitmap createQRCode(String str,int widthAndHeight) throws WriterException {
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
