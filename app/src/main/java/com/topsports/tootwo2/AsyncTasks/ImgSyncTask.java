package com.topsports.tootwo2.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.topsports.tootwo2.helper.RocTools;
import com.topsports.tootwo2.helper.StaticVar;
import com.topsports.tootwo2.model.ProductPic;
import com.topsports.tootwo2.widget.autolistview.widget.AutoListView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tootwo2 on 16/5/9.
 */
public class ImgSyncTask extends AsyncTask<String,Integer,Boolean> {
    private Context context;
    private ProgressDialog progressDialog;
    private String orderMeetNo;

    public ImgSyncTask(ProgressDialog progressDialog,String orderMeetNo,Context context){
        this.progressDialog=progressDialog;
        this.orderMeetNo=orderMeetNo;
        this.context=context;
    }

    protected void onPreExecute() {
        progressDialog.setMessage("下载中，请稍候……");
        progressDialog.setTitle("下载图片");
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        List<ProductPic> oldProductPicList= DataSupport.where("status<>?","0").find(ProductPic.class);
        for(int i=0;i<oldProductPicList.size();i++){
            ProductPic productPic=oldProductPicList.get(i);
            uploadPic(oldProductPicList.get(i));
            if(productPic.getStatus().equals("1")){
                productPic.setStatus("0");
            }else{
                productPic.delete();
            }
        }
        publishProgress(0);

        String result=downPicList(orderMeetNo);

        List<ProductPic> productPicList=new ArrayList<ProductPic>();
        try{
            JSONArray jsonArray=new JSONArray(result);
            DataSupport.deleteAll(ProductPic.class,"1=1");

            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                ProductPic productPic=new ProductPic();
                productPic.setOrderPlanNo(orderMeetNo);
                productPic.setGoodsNo(jsonObject.getString("goodsNo"));
                productPic.setImgName(jsonObject.getString("imgName"));
                productPic.setStatus(jsonObject.getString("status"));
//                    productPic.save();
                productPicList.add(productPic);

            }
            Log.i("TEST",String.valueOf(jsonArray.length()));

        }catch (Exception e){
            e.printStackTrace();
        }
        publishProgress(1);

        DataSupport.saveAll(productPicList);
        for(int i=0;i<productPicList.size();i++){
            try {
                String imgName=productPicList.get(i).getImgName().replace(" ","");
                File file=new File(context.getFilesDir()+"/"+imgName);
                if(!file.exists()){
                    downLoadPic(StaticVar.URL_PIC + imgName, imgName);
                    Log.i("TEST", String.valueOf(i) + "     successPic");
                }
                publishProgress(productPicList.size()-i);
            }catch (Exception e){
                e.printStackTrace();
                Log.i("TEST", String.valueOf(i) + "     failedPic" );
            }
        }
        publishProgress(99999);


        return true;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        int value=values[0];
        String message="";
        if(value==0){
            message="开始下载图片列表";
        }else if(value==1){
            message="开始下载图片";
        }else if(value==99999){
            message="下载图片完成";
        }else{
            message="剩余下载图片"+String.valueOf(value);
        }
        progressDialog.setMessage(message);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        progressDialog.dismiss();
//        loadData(AutoListView.REFRESH);
//
//        File[] files=new File(getFilesDir()+"").listFiles();
//        Log.d("TEST",String.valueOf(files.length));


//            handlerProgressDialog.sendMessage(new Message());
    }


    //获取图片列表
    private String downPicList(String orderPlanNo){
        String result="";
        String str=StaticVar.URL_BASE+"getImgNameList?orderPlanNo="+orderPlanNo;
        try {
            URL url=new URL(str);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type", "text/html");
            PrintWriter pw = new PrintWriter(connection.getOutputStream());
            pw.print("");
            pw.flush();
            pw.close();
            //读取URLConnection的响应
            InputStreamReader in=new InputStreamReader(connection.getInputStream());

            StringBuffer buffer=new StringBuffer();
            BufferedReader reader=null;
            try{
                reader=new BufferedReader(in);
                String line=null;

                while ((line=reader.readLine())!=null){
                    buffer.append(line);
                }
                result=buffer.toString();
//                    Log.i("TEST",result);
            }catch (Exception e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    //上传图片文件
    private String uploadPic(ProductPic productPic){

        String str=StaticVar.URL_BASE + "upLoadImg?orderPlanNo="+"AD16Q2"+"&fileName="+productPic.getImgName()+"&status="+productPic.getStatus();

        File file=new File(context.getFilesDir()+"/"+productPic.getImgName());
        try {
            URL url=new URL(str);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type", "text/html");
            BufferedOutputStream out=new BufferedOutputStream(connection.getOutputStream());

            FileInputStream fileInputStream;
            if(file.exists()){
                //读取文件上传到服务器
                fileInputStream=new FileInputStream(file);
                byte[]bytes=new byte[1024];
                int numReadByte=0;
                while((numReadByte=fileInputStream.read(bytes,0,1024))>0)
                {
                    out.write(bytes, 0, numReadByte);
                }
                out.flush();
                fileInputStream.close();
            }else {
                out.flush();
            }
            //读取URLConnection的响应
            InputStreamReader in=new InputStreamReader(connection.getInputStream());
            String result="";
            StringBuffer buffer=new StringBuffer();
            BufferedReader reader=null;
            try{
                reader=new BufferedReader(in);
                String line=null;

                while ((line=reader.readLine())!=null){
                    buffer.append(line);
                }
                result=buffer.toString();
                Log.i("TEST",result);
            }catch (Exception e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    //根据货号下载图片
    public void downLoadPic(String urlString, String goodsNo){
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        Bitmap bitmap=null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            bitmap= BitmapFactory.decodeStream(in);
            if(bitmap!=null){
                RocTools.saveBitmap2Local(bitmap, goodsNo, context);
                Log.d("TEST",goodsNo+"success");
            }


        } catch (final IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if(bitmap!=null){
                    bitmap.recycle();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
