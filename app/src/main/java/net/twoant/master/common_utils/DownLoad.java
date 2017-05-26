package net.twoant.master.common_utils;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class DownLoad {

	private Context context;
	private app_update update;
	public static String ver="";
	private static final int DOWNLOAD = 1;
	private static final int DOWNLOAD_FINISH = 2;
	HashMap<String, String> mHashMap;
	private String mSavePath;
	private int progress;
	private boolean cancelUpdate = false;
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	private String down_url="";
	/***
	 * update apk
	 * @param ct context
	 */
	public DownLoad(Context ct){
		context=ct;
	}
	public static void cleanDatabases(Context context) {
		deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/databases"));
	}
	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}

	private Handler show=new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				cleanDatabases(context);

				showNoticeDialog();
				//showDownloadDialog();
				break;
			case 1:
				cleanDatabases(context);
        		showDownloadDialog();
				break;
			default:
				break;
			}
		}

	};
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case DOWNLOAD:
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				installApk();
				break;
			default:
				break;
			}
		};
	};
	private boolean isUpdate()
	{
		int versionCode = getVersionCode(context);
		return false;
	}
	public void LongHttp(Map<String,String> map, StringCallback callback){
		OkHttpUtils.post().url(ApiConstants.APP_UPDATE).params(map).build().execute(callback);
	}
	public Runnable run=new Runnable() {

		@Override
		public void run() {

			HashMap<String,String> map=new HashMap<>();
           map.put("p","android");
			try {
				LongHttp(map, new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {

					}

					@Override
					public void onResponse(String response, int id) {

						DataRow row=DataRow.parseJson(response).getRow("data");
						update=new app_update();
						update.setFILE_URL(row.getString("FILE_URL"));
						update.setMAX_VERSION(row.getInt("MAX_VERSION"));
						update.setMIN_VERSION(row.getInt("MIN_VERSION"));
						update.setVERSION_DESCRIPTION(row.getString("VERSION_DESCRIPTION"));
						update.setVERSION_TITLE(row.getString("VERSION_TITLE"));
						down_url=update.getFILE_URL();
				        if (getVersionCode(context)<update.getMIN_VERSION()){
							show.sendEmptyMessage(1);
						}else if(getVersionCode(context)<update.getMAX_VERSION()){
							show.sendEmptyMessage(0);
						}
							//show.sendEmptyMessage(update.getIsforce());
					}

				});
			} catch (Exception e) {
			}


		}
	};
	/**
	 * ��ȡ����汾��
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context)
	{
		int versionCode = 0;
		try
		{
			versionCode = context.getPackageManager().getPackageInfo("net.twoant.model", 0).versionCode;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return versionCode;
	}


	private void showNoticeDialog()
	{
		Builder builder = new Builder(context);
		builder.setTitle(update.getVERSION_TITLE());
		builder.setMessage(update.getVERSION_DESCRIPTION());
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	private void showDownloadDialog()
	{
		Builder builder = new Builder(context);
		builder.setTitle("更新提醒");
		builder.setCancelable(false);
		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(net.twoant.master.R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(net.twoant.master.R.id.update_progress);
		builder.setView(v);

		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		downloadApk();
	}

	private void downloadApk()
	{
		new downloadApkThread().start();
	}

	private class downloadApkThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{

					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					URL url = new URL(down_url);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					int length = conn.getContentLength();
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					if (!file.exists())
					{
						file.mkdir();
					}
					DataOutputStream fos = new DataOutputStream(
							new FileOutputStream(mSavePath+ "//market.apk"));
					int count = 0;
					byte buf[] = new byte[1024];
					do
					{
						int numread = is.read(buf);
						count += numread;
						progress = (int) (((float) count / length) * 100);
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// ���ȡ����ֹͣ����.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			mDownloadDialog.dismiss();
		}
	};

	private void installApk()
	{
		File apkfile = new File(mSavePath, "//market.apk");
		if (!apkfile.exists())
		{
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		context.startActivity(i);
		System.exit(0);
	}
	class app_update{
		private int isforce;
		private String version;
		private String content;
		private int MIN_VERSION;
		private int MAX_VERSION;

		public String getVERSION_DESCRIPTION() {
			return VERSION_DESCRIPTION;
		}

		public void setVERSION_DESCRIPTION(String VERSION_DESCRIPTION) {
			this.VERSION_DESCRIPTION = VERSION_DESCRIPTION;
		}

		public String getFILE_URL() {
			return FILE_URL;
		}

		public void setFILE_URL(String FILE_URL) {
			this.FILE_URL = FILE_URL;
		}

		private String VERSION_DESCRIPTION;

		public String getVERSION_TITLE() {
			return VERSION_TITLE;
		}

		public void setVERSION_TITLE(String VERSION_TITLE) {
			this.VERSION_TITLE = VERSION_TITLE;
		}

		private String VERSION_TITLE;
        private String FILE_URL;

		public int getMAX_VERSION() {
			return MAX_VERSION;
		}

		public void setMAX_VERSION(int MAX_VERSION) {
			this.MAX_VERSION = MAX_VERSION;
		}

		public int getMIN_VERSION() {
			return MIN_VERSION;
		}

		public void setMIN_VERSION(int MIN_VERSION) {
			this.MIN_VERSION = MIN_VERSION;
		}

		private int os;
		public int getIsforce() {
			return isforce;
		}
		public void setIsforce(int isforce) {
			this.isforce = isforce;
		}
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public int getOs() {
			return os;
		}
		public void setOs(int os) {
			this.os = os;
		}
	}
	/**************************/
}
