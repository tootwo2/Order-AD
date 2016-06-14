package com.topsports.tootwo2.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * roc的工具类
 * Created by tootwo2 on 15/11/5.
 */
public class RocTools {

    /**
     * 保存位图至本地
     * @param bitmap  位图文件
     * @param filenm  文件名
     * @param context 上下文
     */
    public static void saveBitmap2Local(Bitmap bitmap,String filenm,Context context){
        FileOutputStream out=null;
        BufferedOutputStream bos=null;
        try{
            out=context.openFileOutput(filenm, Context.MODE_PRIVATE);
            bos=new BufferedOutputStream(out);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(bos!=null){
                    bos.close();
                }
                if(out!=null){
                    out.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过HTTP请求获取数据
     * @param urlStr
     * @param paramStr
     * @return
     */
    public static String getDataByHttpRequest(String urlStr,String paramStr){
        String result="";

        try {
            URL url=new URL(urlStr);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setConnectTimeout(100000);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            DataOutputStream out = new DataOutputStream(connection
                    .getOutputStream());

            byte[] content = paramStr.getBytes("utf-8");

            out.write(content, 0, content.length);
            out.flush();
            out.close(); // flush and close

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
            }catch (Exception e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 读取图片的旋转的角度
     *
     * @param path
     *            图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm
     *            需要旋转的图片
     * @param degree
     *            旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    /**
     * 从json对象中取值，如果没有返回空字符串
     * @param jsonObject
     * @param name
     * @return
     */
    public static String getJsonVal(JSONObject jsonObject, String name){
        if(jsonObject.has(name)){
            try{
                return jsonObject.getString(name);
            }catch (Exception e){
                return "";
            }

        }else{
            return "";
        }
    }

    /**
     * 推送广播消息
     * @param context 上下文
     * @param message 广播消息内容
     * @param broadType 广播类型
     */
    public static void sendBroadCast(Context context,String message,String broadType){
        Intent intent=new Intent(broadType);
        Bundle bundle=new Bundle();
        bundle.putString("message", message);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }
}
