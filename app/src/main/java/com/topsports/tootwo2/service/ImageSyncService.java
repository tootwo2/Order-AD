package com.topsports.tootwo2.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.topsports.tootwo2.helper.StaticVar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 同步图片Service
 * <p/>
 * helper methods.
 */
public class ImageSyncService extends IntentService {
    // ACTION名
    private static final String ACTION_DOWN = "com.topsports.tootwo2.service.action.PIC_DOWN";
    private static final String ACTION_UPLOAD = "com.topsports.tootwo2.service.action.PIC_UPLOAD";

    // 参数
    private static final String ORDER_MEET_NO = "com.topsports.tootwo2.service.extra.ORDER_MEET_NO";
    private static final String EXTRA_PARAM2 = "com.topsports.tootwo2.service.extra.PARAM2";

    public ImageSyncService() {
        super("ImageSyncService");
    }

    /**
     * 下载图片
     * @see IntentService
     */
    public static void startDownloadPics(Context context, String orderMeetNo) {
        Intent intent = new Intent(context, ImageSyncService.class);
        intent.setAction(ACTION_DOWN);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
//    public static void startActionBaz(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, ImageSyncService.class);
//        intent.setAction(ACTION_BAZ);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        context.startService(intent);
//    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWN.equals(action)) {
                final String orderMeetNo = intent.getStringExtra(ORDER_MEET_NO);
                handleActionFoo(orderMeetNo);
            } else if (ACTION_UPLOAD.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String orderMeetNo) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //获取图片列表
    private String downPicList(String orderPlanNo){
        String result="";
        String str= StaticVar.URL_BASE+"getImgNameList?orderPlanNo="+orderPlanNo;
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
}
