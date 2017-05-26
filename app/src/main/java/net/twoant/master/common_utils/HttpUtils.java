package net.twoant.master.common_utils;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


import android.os.AsyncTask;
import android.util.Log;

public class HttpUtils {

	private static HttpParams httpParams;  
	private static DefaultHttpClient httpClient;  
	private static String JSESSIONID; //定义一个静态的字段，保存sessionID  

	/** 
	 * @Title:              getHttpClient  
	 * @author              郭凯
	 * @date                2014-3-18 下午1:11:18 
	 * @Description:        获取HttpClient 
	 * @return 
	 * @throws Exception  
	 * HttpClient               返回 
	 */  
	public static HttpClient getHttpClient() throws Exception {  
		// 创建 HttpParams 以用来设置 HTTP 参数（这一部分不是必需的）  
		httpParams = new BasicHttpParams();  
		// 设置连接超时和 Socket 超时，以及 Socket 缓存大小  
		HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 10000);  
		HttpConnectionParams.setSoTimeout(httpParams, 20 * 10000);  
		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);  
		// 设置重定向，缺省为 true  
		HttpClientParams.setRedirecting(httpParams, true);  
		// 设置 user agent  
		String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";  
		HttpProtocolParams.setUserAgent(httpParams, userAgent);  
		// 创建一个 HttpClient 实例  
		// 注意 HttpClient httpClient = new HttpClient(); 是Commons HttpClient  
		// 中的用法，在 Android 1.5 中我们需要使用 Apache 的缺省实现 DefaultHttpClient  
		httpClient = new DefaultHttpClient(httpParams);  
		return httpClient;  
	}  

	/** 
	 * @Title:              doGet  
	 * @author              郭凯 
	 * @date                2014-3-18 下午12:39:58 
	 * @Description:        doGet请求 
	 * @param url 
	 * @param params 
	 * @return 
	 * @throws Exception  
	 * String               返回 
	 */  
	@SuppressWarnings("rawtypes")  
	public static String doGet(String url, Map params,final NetCallBack conntection_callback){  
		if (httpClient==null) {
			try {
				getHttpClient();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String strResult = "doGetError";  

		try {
			/* 建立HTTPGet对象 */  
			String paramStr = "";  
			if (params != null) {  
				Iterator iter = params.entrySet().iterator();  
				while (iter.hasNext()) {  
					Map.Entry entry = (Map.Entry) iter.next();  
					Object key = entry.getKey();  
					Object val = entry.getValue();  
					paramStr += paramStr = "&" + key + "=" + val;  
				}  
			}  
			if (!paramStr.equals("")) {  
				paramStr = paramStr.replaceFirst("&", "?");  
				url += paramStr;  
			}  
			final HttpGet httpRequest = new HttpGet(url);  
			new AsyncTask<Void, Void, String>() {

				@Override
				protected String doInBackground(Void... params) {
					String Str="";
					try {
						/* 发送请求并等待响应 */  
						HttpResponse httpResponse = httpClient.execute(httpRequest);  
						/* 若状态码为200 ok */  
						if (httpResponse.getStatusLine().getStatusCode() == 200) {  
							/* 读返回数据 */  
							Str = EntityUtils.toString(httpResponse.getEntity());  
						} else {  
							Str = "Error Response: " + httpResponse.getStatusLine().toString();  
						} 
					} catch (Exception e) {
						Str=e.getMessage();
					}
					return Str;
				}
				@Override
				protected void onPostExecute(String result) {
					conntection_callback.SuccessFull(result);
				}
			}.execute(new Void[] {});

		} catch (Exception e) {
			strResult=e.getMessage();
			conntection_callback.Failure(e.getMessage());
		}

		return strResult;  
	}  

	/** 
	 * @Title:              doPost  
	 * @author              郭凯
	 * @date                2014-3-18 下午12:39:38 
	 * @Description:        doPost请求 
	 * @param url 
	 * @param params 
	 * @return 
	 * @throws Exception  
	 * String               返回 
	 */  
	public static String doPost(String url, List<NameValuePair> params) throws Exception {  
		/* 建立HTTPPost对象 */  
		if (httpClient==null) {
			try {
				getHttpClient();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		HttpPost httpRequest = new HttpPost(url);  
		//  httpRequest.setHeader("host", url);
		String strResult = "doPostError";  
		try {


			/* 添加请求参数到请求对象 */  
			if (params != null && params.size() > 0) {  
				httpRequest.setEntity((HttpEntity) new UrlEncodedFormEntity(params, HTTP.UTF_8));  
			}  
			if(null != JSESSIONID){  
				httpRequest.setHeader("Cookie", "JSESSIONID="+JSESSIONID);  
			}  
			/* 发送请求并等待响应 */  
			HttpResponse httpResponse = httpClient.execute(httpRequest);  
			/* 若状态码为200 ok */  
			if (httpResponse.getStatusLine().getStatusCode() == 200) {  
				/* 读返回数据 */  
				strResult = EntityUtils.toString(httpResponse.getEntity());  
				/* 获取cookieStore */  
				CookieStore cookieStore = httpClient.getCookieStore();  
				List<Cookie> cookies = cookieStore.getCookies();  
				for(int i=0;i<cookies.size();i++){  
					if("JSESSIONID".equals(cookies.get(i).getName())){  
						JSESSIONID = cookies.get(i).getValue();  
						break;  
					}  
				}  
			}else
			{
				strResult=httpResponse.getStatusLine()+"";
			}  
			Log.v("strResult", strResult);
		} catch (Exception e) {
			System.out.println("post_error==="+e.getMessage()+"-----"+url);
		}
		return strResult;  
	}  
	/** 
	 * @Title:              doPost  
	 * @author              郭凯
	 * @date                2014-3-18 下午12:39:38 
	 * @Description:        doPost请求 
	 * @param url 
	 * @param params 
	 * @return 
	 * @throws Exception  
	 * String               返回 
	 */  
	public static String doPost(String url, List<NameValuePair> params,final NetCallBack callback) throws Exception {  
		/* 建立HTTPPost对象 */  
		if (httpClient==null) {
			try {
				getHttpClient();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		final HttpPost httpRequest = new HttpPost(url);  

		try {
			/* 添加请求参数到请求对象 */  
			if (params != null && params.size() > 0) {  
				httpRequest.setEntity((HttpEntity) new UrlEncodedFormEntity(params, HTTP.UTF_8));  
			}  
			if(null != JSESSIONID){  
				httpRequest.setHeader("Cookie", "JSESSIONID="+JSESSIONID);  
			} 
			new AsyncTask<Void, Void, String>(){

				@Override
				protected String doInBackground(Void... params) {
					String strResult = "doPostError";  
					try {
						/* 发送请求并等待响应 */  
						HttpResponse httpResponse = httpClient.execute(httpRequest);  
						/* 若状态码为200 ok */  
						if (httpResponse.getStatusLine().getStatusCode() == 200) {  
							/* 读返回数据 */  
							strResult = EntityUtils.toString(httpResponse.getEntity());  
							/* 获取cookieStore */  
							CookieStore cookieStore = httpClient.getCookieStore();  
							List<Cookie> cookies = cookieStore.getCookies();  
							for(int i=0;i<cookies.size();i++){  
								if("JSESSIONID".equals(cookies.get(i).getName())){  
									JSESSIONID = cookies.get(i).getValue();  
									break;  
								}  
							}  
						}else
						{
							strResult=httpResponse.getStatusLine()+"";
						}
					} catch (Exception e) {
						callback.Failure(e.getMessage());
					}
					return strResult;
				}
				@Override
				protected void onPostExecute(String result) {
					callback.SuccessFull(result);
				}
			}.execute();
			Log.v("strResult", "");
		} catch (Exception e) {
			System.out.println("post_error==="+e.getMessage()+"-----"+url);
		}
		return "";  
	} 
}
