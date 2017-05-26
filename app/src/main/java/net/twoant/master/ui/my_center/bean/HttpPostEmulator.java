package net.twoant.master.ui.my_center.bean;

import android.content.Context;
import android.os.AsyncTask;

import net.twoant.master.common_utils.SPUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kaiguokai on 2016/10/26.
 */

public class HttpPostEmulator {



    // 每个post参数之间的分隔。随意设定，只要不会和其他的字符串重复即可。
    private static final String BOUNDARY = "----------HV2ymHFg03ehbqgZCaKO6jyH";



    public interface upload_finsh{

        void complte(String result);
    }

    /**
     * @param
     *            :请求接口
     * @param httpArg
     *            :参数
     * @return 返回结果
     */
    public static String request(String httpUrl, String httpArg, upload_finsh callback) {
         httpUrl = "http://apis.baidu.com/datatiny/cardinfo_vip/cardinfo_vip";
         httpArg = "cardnum="+httpArg;
       // String jsonResult = request(httpUrl, httpArg);
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey",  "fe8510e8271042ff4e04bec92806c1a4");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
            callback.complte(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void upFile(Context ct, List<File> files, List<String> path_name, String type){

        path_name=new ArrayList<>();
        path_name.add("/storage/emulated/0/20161102155836.jpg");
        path_name.add("/storage/emulated/0/20161102160156.jpg");

        MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        multipartBody.addFormDataPart("uid" , SPUtils.get(ct,"uid","")+"");
        multipartBody  .addFormDataPart("shop_id" ,  SPUtils.get(ct,"shop_id","")+"");
        multipartBody  .addFormDataPart("goods_name" , "测试商品");
        multipartBody   .addFormDataPart("goods_sort" , type);
        multipartBody  .addFormDataPart("goods_price" , "34");
        multipartBody   .addFormDataPart("goods_number" , "11");
        multipartBody    .addFormDataPart("goods_shiny" , "你大爷666");
        multipartBody    .addFormDataPart("goods_description" , "你大爷666");
        multipartBody    .addFormDataPart("goods_carriage" , "0");
        for(int i= 0;i<path_name.size();i++){
            multipartBody.addFormDataPart("image","image"+i+".jpg",
                    RequestBody.create(MediaType.parse("application/octet-stream"),new File(path_name.get(i))));
        }


        File file1 = new File("/storage/emulated/0/20161102155836.jpg");
        RequestBody fileBody1 = RequestBody.create(MediaType.parse("application/octet-stream") , file1);
        String file1Name = "testFile1.jpg";

        multipartBody .addFormDataPart("goods_cover" , file1Name , fileBody1);


//        File file2 = new File("/storage/emulated/0/20161102160156.jpg");
//        RequestBody fileBody2 = RequestBody.create(MediaType.parse("application/octet-stream") , file2);
//        String file2Name = "testFile2.jpg";
//
//        File file3 = new File("/storage/emulated/0/20161102155836.jpg");
//        RequestBody fileBody3 = RequestBody.create(MediaType.parse("application/octet-stream") , file3);
//        String file3Name = "testFile3.jpg";
//
//    /* form的分割线,自己定义 */
//        String boundary = "xx--------------------------------------------------------------xx";
//
//        MultipartBody mBody = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM)
//            /* 上传一个普通的String参数 , key 叫 "p" */
//                .addFormDataPart("uid" , SPUtils.get(ct,"uid","")+"")
//                .addFormDataPart("shop_id" ,  SPUtils.get(ct,"shop_id","")+"")
//                .addFormDataPart("goods_name" , "测试商品")
//                .addFormDataPart("goods_sort" , type)
//                .addFormDataPart("goods_price" , "34")
//                .addFormDataPart("goods_number" , "11")
//                .addFormDataPart("goods_shiny" , "你大爷666")
//                .addFormDataPart("goods_description" , "你大爷666")
//                .addFormDataPart("goods_carriage" , "0")
////                .addFormDataPart("p" , "你大爷666")
////                .addFormDataPart("p" , "你大爷666")
////                .addFormDataPart("p" , "你大爷666")
//
//
//
////        ffkvp.add(new FormFieldKeyValuePair("uid", SPUtils.get(getApplicationContext(),"uid","")+""));//其他参数
////        ffkvp.add(new FormFieldKeyValuePair("shop_id", SPUtils.get(getApplicationContext(),"shop_id","")+""));//其他参数
////        ffkvp.add(new FormFieldKeyValuePair("goods_name", edit_product_name.getText()+""));//其他参数
////        ffkvp.add(new FormFieldKeyValuePair("goods_sort", fenlei.getData().get(1).getSort_id()));//其他参数
////        ffkvp.add(new FormFieldKeyValuePair("goods_price", edit_product_price.getText()+""));//其他参数
////        ffkvp.add(new FormFieldKeyValuePair("goods_number", edit_product_count.getText()+""));//其他参数
////        ffkvp.add(new FormFieldKeyValuePair("goods_shiny", edit_product_remark.getText()+""));//其他参数
////        ffkvp.add(new FormFieldKeyValuePair("goods_description", edit_product_disc.getText()+""));//其他参数
//            /* 底下是上传了两个文件 */
//                .addFormDataPart("image" , file1Name , fileBody1)
//                .addFormDataPart("image1" , file2Name , fileBody2)
//
//                .addFormDataPart("goods_cover" , file2Name , fileBody2)
//
//                .build();

    /* 下边的就和post一样了 */

        OkHttpClient okHttpClient = new OkHttpClient();
        //构建请求体
        RequestBody requestBody = multipartBody.build();
        //构建请求
        Request request = new Request.Builder()
                .url("http://aisou.zgdsw.cn/webroot/index.php?r=ztrader/shopPublicGoods")//地址
                .post(requestBody)//添加请求体
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response);


            }
        });
//        Request request = new Request.Builder().url("http://aisou.zgdsw.cn/webroot/index.php?r=ztrader/shopPublicGoods").post(mBody).build();
//        OkHttpClient client=new OkHttpClient();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                System.out.println(response);
//            }
//        });
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//            }
//        }
//    });
    }



    public void sendHttpPostRequest(final String serverUrl,
                                    final ArrayList<FormFieldKeyValuePair> generalFormFields,
                                    final ArrayList<UploadFileItem> filesToBeUploaded, final upload_finsh async)
    {
        AsyncTask<Integer,String,String> task=new AsyncTask<Integer, String, String>() {

            @Override
            protected String doInBackground(Integer... params) {
                String resul="";
                try {
                    resul= sendHttpPostRequest(serverUrl, generalFormFields, filesToBeUploaded);
                }catch (Exception e){

                    System.out.println(e.getMessage());
                }
                return resul;
            }

            @Override
            protected void onPostExecute(String s) {
                async.complte(s);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
        }.execute(1);
    }

    public static String sendHttpPostRequest(String serverUrl,
                                             ArrayList<FormFieldKeyValuePair> generalFormFields,
                                             ArrayList<UploadFileItem> filesToBeUploaded) throws Exception {


        // 向服务器发送post请求

        URL url = new URL(serverUrl/* "http://127.0.0.1:8080/test/upload" */);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 发送POST请求必须设置如下两行

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Charset", "UTF-8");
        connection.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + BOUNDARY);

        // 头

        String boundary = BOUNDARY;

        // 传输内容

        StringBuffer contentBody = new StringBuffer("--" + BOUNDARY);

        // 尾

        String endBoundary = "\r\n--" + boundary + "--\r\n";

        OutputStream out = connection.getOutputStream();

        // 1. 处理文字形式的POST请求

        for (FormFieldKeyValuePair ffkvp : generalFormFields)

        {

            contentBody.append("\r\n")

                    .append("Content-Disposition: form-data; name=\"")

                    .append(ffkvp.getKey() + "\"")

                    .append("\r\n")

                    .append("\r\n")

                    .append(ffkvp.getValue())

                    .append("\r\n")

                    .append("--")

                    .append(boundary);

        }

        String boundaryMessage1 = contentBody.toString();

        out.write(boundaryMessage1.getBytes("utf-8"));

        // 2. 处理文件上传

        for (UploadFileItem ufi : filesToBeUploaded)

        {
            System.out.println("文件名:"+ufi.getFileName()+"--->文件路径:"+ufi.getFormFieldName());
            contentBody = new StringBuffer();

            contentBody.append("\r\n")

                    .append("Content-Disposition:form-data; name=\"")

                    .append(ufi.getFormFieldName() + "\"; ") // form中field的名称

                    .append("filename=\"")

                    .append(ufi.getFileName() + "\"") // 上传文件的文件名，包括目录

                    .append("\r\n")

                    .append("Content-Type:application/x-jpg")

                    .append("\r\n\r\n");
            System.out.println("**--->"+contentBody);

            String boundaryMessage2 = contentBody.toString();

            out.write(boundaryMessage2.getBytes("utf-8"));

            // 开始真正向服务器写文件

            File file = new File(ufi.getFileName());

            DataInputStream dis = new DataInputStream(new FileInputStream(file));

            int bytes = 0;

            byte[] bufferOut = new byte[(int) file.length()];

            bytes = dis.read(bufferOut);

            out.write(bufferOut, 0, bytes);

            dis.close();

            contentBody.append("------------HV2ymHFg03ehbqgZCaKO6jyH");

            String boundaryMessage = contentBody.toString();

            out.write(boundaryMessage.getBytes("utf-8"));

            // System.out.println(boundaryMessage);

        }

        out.write("------------HV2ymHFg03ehbqgZCaKO6jyH--\r\n"
                .getBytes("UTF-8"));

        // 3. 写结尾

        out.write(endBoundary.getBytes("utf-8"));

        out.flush();

        out.close();

        // 4. 从服务器获得回答的内容

        String strLine = "";

        String strResponse = "";
        int code=connection.getResponseCode();
        InputStream in=null;
        if (code!= HttpURLConnection.HTTP_OK)
        {
            in=connection.getErrorStream();
        }else {
            in = connection.getInputStream();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        while ((strLine = reader.readLine()) != null)

        {

            strResponse += strLine + "\n";

        }

        // System.out.print(strResponse);

        return strResponse;

    }

}
