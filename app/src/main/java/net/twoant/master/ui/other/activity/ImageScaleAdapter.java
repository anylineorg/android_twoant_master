package net.twoant.master.ui.other.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.base_app.BasePagerAdapter;
import net.twoant.master.common_utils.HintDialogBlackUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.widget.SavePhotoDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ImageScaleAdapter extends BasePagerAdapter<String> {
	private ImageScaleActivity activity;
	private HintDialogBlackUtil hintDialogUtil;
	public boolean flag;

	public ImageScaleAdapter(ArrayList<String> list, ImageScaleActivity imageScaleActivity) {
		super(list);
		activity = imageScaleActivity;
		hintDialogUtil = new HintDialogBlackUtil(activity);
	}
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		final PhotoView imageView = new PhotoView(activity);

		imageView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				final SavePhotoDialog savePhotoDialog = new SavePhotoDialog(activity, Gravity.CENTER,false);
				savePhotoDialog.setOnClickListener(new SavePhotoDialog.IOnClickListener() {
					@Override
					public void onClickListener(View v) {
						Drawable drawable = imageView.getDrawable();
						int width = drawable.getIntrinsicWidth();// 取drawable的长宽
						int height = drawable.getIntrinsicHeight();
						Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ?Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;// 取drawable的颜色格式
						Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应bitmap
						Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布
						drawable.setBounds(0, 0, width, height);
						drawable.draw(canvas);// 把drawable内容画到画布中
//						Bitmap bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
						saveImageToGallery(activity,bitmap);
						savePhotoDialog.dismiss();
					}
				});
				savePhotoDialog.showDialog(true,true);
				return false;
			}
		});
		ImageLoader.getImageFromNetwork(imageView,list.get(position));
		flag = true;
		hintDialogUtil.showLoading();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (flag){
					try {
						Thread.sleep(500);
						Drawable drawable = imageView.getDrawable();
						if (null != drawable) {
							flag = false;
							hintDialogUtil.dismissDialog();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				activity.finish();
			}
		});

		//将ImageView加入到ViewPager中
		container.addView(imageView);
		return imageView;
	}

	public static void saveImageToGallery(Context context, Bitmap bmp) {
		if (bmp == null){
			ToastUtil.showLong("保存出错了...");
			return;
		}
		// 首先保存图片
		File appDir = new File(Environment.getExternalStorageDirectory(), "两只蚂蚁");
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		String fileName = System.currentTimeMillis() + ".jpg";
		File file = new File(Environment.getExternalStorageDirectory()+"/两只蚂蚁", fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);

			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			ToastUtil.showLong("文件未发现");
			e.printStackTrace();
		} catch (IOException e) {
			ToastUtil.showLong("保存出错了...");
			e.printStackTrace();
		}catch (Exception e){
			ToastUtil.showLong("保存出错了...");
			e.printStackTrace();
		}
		//解决会保存两张图片
		/*	try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri uri = Uri.fromFile(file);
		intent.setData(uri);
		context.sendBroadcast(intent);
		ToastUtil.showLong("保存到了"+file.getAbsolutePath());

	}

}
