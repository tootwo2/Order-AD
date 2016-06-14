package com.topsports.tootwo2.service.task;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.topsports.tootwo2.helper.RocTools;
import com.topsports.tootwo2.helper.StaticVar;
import com.topsports.tootwo2.model.ProductOrderInfo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;
import org.litepal.tablemanager.Connector;

import java.util.Map;

/**
 * 更新商品订量信息
 * Created by tootwo2 on 16/5/6.
 */
public class ProOrderInfoTask implements BaseTask {
    private String proOrderInfoUrl=StaticVar.URL_BASE+"proOrderInfo";
    @Override
    public String doTask(Map<String,String> params, Context context) {
        RocTools.sendBroadCast(context,"更新商品订量",StaticVar.BROADCAST_TYPE_SEARCH);

        Log.d(StaticVar.LOG_TAG, "更新订单信息开始");

        String result="";
        String userId=params.get("userId");

//        params.put("userId", userId);
//        params.put("orderMeetNo", orderMeetNo);

        result= RocTools.getDataByHttpRequest(proOrderInfoUrl, (new JSONObject(params)).toString());

        try {
            Log.d(StaticVar.LOG_TAG, "解析订单信息开始");
            JSONArray jsonArray=new JSONObject(result).getJSONArray("rows");

            DataSupport.deleteAll(ProductOrderInfo.class,"userid='"+userId+"'");

            SQLiteDatabase db = Connector.getDatabase();
            String sql = "insert into ProductOrderInfo";
            sql+="(userId,newGoodsId,buyerUnitCode,BuyerUnitName,orderQty,minOrderQty)";
            sql+="values(?,?,?,?,?,?)";

            SQLiteStatement stat = db.compileStatement(sql);
            db.beginTransaction();
            try {
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);

                    stat.bindString(1,userId);
                    stat.bindString(2,RocTools.getJsonVal(jsonObject,"newGoodsID"));
                    stat.bindString(3,RocTools.getJsonVal(jsonObject,"buyerUnitCode"));
                    stat.bindString(4,RocTools.getJsonVal(jsonObject,"buyerUnitName"));
                    stat.bindString(5,RocTools.getJsonVal(jsonObject,"depthQty"));
                    stat.bindString(6,RocTools.getJsonVal(jsonObject,"minNum"));

                    stat.executeInsert();
                }

                db.setTransactionSuccessful();
            } catch (Exception var6) {
                throw new DataSupportException(var6.getMessage());
            } finally {
                db.endTransaction();
                db.close();
            }
        } catch (Exception e) {
            Log.e(StaticVar.LOG_TAG,e.getMessage());
            e.printStackTrace();
        }
        Log.d(StaticVar.LOG_TAG, "更新订单信息结束");

        return "";
    }
}
