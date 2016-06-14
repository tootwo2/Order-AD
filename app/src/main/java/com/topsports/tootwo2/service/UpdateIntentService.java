package com.topsports.tootwo2.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.topsports.tootwo2.helper.RocTools;
import com.topsports.tootwo2.helper.StaticVar;
import com.topsports.tootwo2.model.ProConstType;
import com.topsports.tootwo2.model.ProductBIData;
import com.topsports.tootwo2.model.ProductComments;
import com.topsports.tootwo2.model.ProductHisTrend;
import com.topsports.tootwo2.model.ProductInfo;
import com.topsports.tootwo2.model.ProductMinOrderNum;
import com.topsports.tootwo2.model.ProductOldCompare;
import com.topsports.tootwo2.model.ProductOrderInfo;
import com.topsports.tootwo2.model.ProductPlanInfo;
import com.topsports.tootwo2.order.SearchQtyActivity;
import com.topsports.tootwo2.service.task.BaseTask;
import com.topsports.tootwo2.service.task.ProBIDataTask;
import com.topsports.tootwo2.service.task.ProCommentsTask;
import com.topsports.tootwo2.service.task.ProConstTask;
import com.topsports.tootwo2.service.task.ProInfoTask;
import com.topsports.tootwo2.service.task.ProMinOrderNumTask;
import com.topsports.tootwo2.service.task.ProOldCompareTask;
import com.topsports.tootwo2.service.task.ProOrderInfoTask;
import com.topsports.tootwo2.service.task.ProPlanInfoTask;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;
import org.litepal.tablemanager.Connector;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 更新基础数据
 * <p/>
 *
 *
 */
public class UpdateIntentService extends IntentService {

    Handler msgHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(UpdateIntentService.this, msg.getData().getString("Text"), Toast.LENGTH_SHORT).show();
            super.handleMessage(msg);
        }
    };


    private String urlBase= StaticVar.URL_BASE;

    private String procommitUrl=urlBase+"procommit";
    private String commentscommitUrl=urlBase+"commentscommit";

    /**
     *基础数据Intent
     */
    private static final String ACTION_UPDATE_BASE = "com.topsports.tootwo2.service.action.update";
    private static final String ACTION_UPLOAD_ORDERQTY = "com.topsports.tootwo2.service.action.upload_orderqty";

    // 参数
    private static final String EXTRA_PARAM1 = "com.topsports.tootwo2.service.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.topsports.tootwo2.service.extra.PARAM2";

    public UpdateIntentService() {
        super("UpdateIntentService");
    }

    /**
     * 更新基础数据
     *
     * @see IntentService
     */
    public static void startActionUpdateBase(Context context, String userId,String orderPlanNo) {
        Intent intent = new Intent(context, UpdateIntentService.class);
        intent.setAction(ACTION_UPDATE_BASE);
        intent.putExtra(EXTRA_PARAM1, userId);
        intent.putExtra(EXTRA_PARAM2, orderPlanNo);
        context.startService(intent);
    }

    /**
     * 提交订货量
     *
     * @see IntentService
     */
    public static void startActionUploadOrderQty(Context context, String userId,String orderPlanNo) {
        Intent intent = new Intent(context, UpdateIntentService.class);
        intent.setAction(ACTION_UPLOAD_ORDERQTY);
        intent.putExtra(EXTRA_PARAM1, userId);
        intent.putExtra(EXTRA_PARAM2, orderPlanNo);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            //更新数据
            if (ACTION_UPDATE_BASE.equals(action)) {
                final String userId = intent.getStringExtra(EXTRA_PARAM1);
                final String orderPlanNo = intent.getStringExtra(EXTRA_PARAM2);
                handleActionUpdateBase(userId,orderPlanNo);
            }
            //提交数据
            else if (ACTION_UPLOAD_ORDERQTY.equals(action)) {
                final String userId = intent.getStringExtra(EXTRA_PARAM1);
                final String orderPlanNo=intent.getStringExtra(EXTRA_PARAM2);
                handleActionUploadOrderQty(userId,orderPlanNo);
            }
        }
    }

    /**
     * 处理更新基础资料
     * parameters.
     */
    private void handleActionUpdateBase(String userId,String orderMeetNo) {
        Context context=getApplicationContext();
        Map<String, String> params = new HashMap<String, String>();

        params.put("userId", userId);
        params.put("orderMeetNo", orderMeetNo);

        String resultType=uploadProOrder(userId,orderMeetNo);
        String result2=uploadComments(userId,orderMeetNo);
        if(resultType.equals("SUCCESS")&&result2.equals("SUCCESS")){
            List<BaseTask> tasks=new ArrayList<>();
            tasks.add(new ProConstTask());
            tasks.add(new ProInfoTask());
            tasks.add(new ProOrderInfoTask());
            tasks.add(new ProCommentsTask());
            tasks.add(new ProBIDataTask());
            tasks.add(new ProOldCompareTask());
            tasks.add(new ProPlanInfoTask());
            tasks.add(new ProMinOrderNumTask());

            for(BaseTask task:tasks){
                task.doTask(params,context);
            }

            sendBroadCast("finished");
        }else {
            sendBroadCast("failed");
            Bundle data = new Bundle();
            data.putString("Text", "订单提交失败");
            Message msg = new Message();
            msg.setData(data);
            msgHandler.sendMessage(msg);

        }

    }

    private void sendBroadCast(String message){
        Intent intent=new Intent("com.topsports.order_qtychange");
        Bundle bundle=new Bundle();
        bundle.putString("message", message);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    /**
     * 处理提交订单信息和标注信息
     * parameters.
     */
    private void handleActionUploadOrderQty(String userId,String orderPlanNo) {
        String result=uploadProOrder(userId,orderPlanNo);
        sendBroadCast("提交标签信息");

        uploadComments(userId, orderPlanNo);
        sendBroadCast("finished");
    }

    /**
     * 从json对象中取值，如果没有返回空字符串
     * @param jsonObject
     * @param name
     * @return
     */
    private String getJsonVal(JSONObject jsonObject,String name){
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
     * 提交商品订单信息
     * @return
     */
    private String uploadProOrder(String userId,String orderPlanNo){
        String strUrl=procommitUrl;
        Log.d("TEST","开始提交订单");
        List<ProductOrderInfo> productOrderInfoList= DataSupport.where("orderQty>0 and userid='" + userId + "'").find(ProductOrderInfo.class);
        JSONArray jsonArray=new JSONArray();
        for(int i=0;i<productOrderInfoList.size();i++){
            ProductOrderInfo productOrderInfo=productOrderInfoList.get(i);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userId", productOrderInfo.getUserId());
            params.put("orderPlanNo", orderPlanNo);
            params.put("newGoodsId", productOrderInfo.getNewGoodsId());
            params.put("buyerUnitCode",productOrderInfo.getBuyerUnitCode());
            params.put("orderQty",productOrderInfo.getOrderQty());
            jsonArray.put(new JSONObject(params));
        }


        String result="";
        Log.d("TEST","开始网络请求");
        result= RocTools.getDataByHttpRequest(strUrl, jsonArray.toString());

        String resultType="";
        try{
            JSONObject jsonObject=new JSONObject(result);
            resultType=jsonObject.getString("resultType");
            if(resultType.equals("ERROR")){
                Bundle data = new Bundle();
                data.putString("Text", "订单提交失败");
                Message msg = new Message();
                msg.setData(data);
                msgHandler.sendMessage(msg);
            }else{
                if(jsonObject.getString("msg").equals("INPUTMODE_CLOSED")){
                    Bundle data = new Bundle();
                    data.putString("Text", "订单提交已关闭");
                    Message msg = new Message();
                    msg.setData(data);
                    msgHandler.sendMessage(msg);
                }
            }

        }catch (Exception e){
            Log.e("TEST", e.toString());
        }
        Log.d("TEST","提交订单结束");
        return resultType;
    }

    /**
     * 上传标签信息
     */
    public String uploadComments(String userId,String orderPlanNo) {
        String result="";
        String urlStr = commentscommitUrl;

        List<ProductComments> productCommentsList = DataSupport.where("userid=?", userId).find(ProductComments.class);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < productCommentsList.size(); i++) {
            ProductComments productComments = productCommentsList.get(i);
            Map<String, String> params = new HashMap<String, String>();
            params.put("userid", userId);
            params.put("orderPlanNo",orderPlanNo);
            params.put("newGoodsID", productComments.getNewGoodsID());
            params.put("commentsDetail", productComments.getCommentsDetail());
            params.put("commentsTime", productComments.getCommentsTime());
            jsonArray.put(new JSONObject(params));
        }

        result= RocTools.getDataByHttpRequest(urlStr, jsonArray.toString());
        String resultType="";
        try{
            resultType=new JSONObject(result).getString("resultType");
        }catch (Exception e){
            Log.e("TEST", e.toString());
        }
        return resultType;
    }
}
