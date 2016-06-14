package com.topsports.tootwo2.service.task;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.topsports.tootwo2.helper.RocTools;
import com.topsports.tootwo2.helper.StaticVar;
import com.topsports.tootwo2.model.ProductMinOrderNum;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;
import org.litepal.tablemanager.Connector;

import java.util.Map;

/**
 * 商品最小起订量
 * Created by tootwo2 on 16/5/9.
 */
public class ProMinOrderNumTask implements BaseTask{
    private String proMinOrderNumUrl=StaticVar.URL_BASE+"proMinOrderNum";
    @Override
    public String doTask(Map<String, String> params, Context context) {
        RocTools.sendBroadCast(context,"更新最小起订量",StaticVar.BROADCAST_TYPE_SEARCH);
        Log.d(StaticVar.LOG_TAG, "更新最小起订量数据开始");
        String result="";

        result= RocTools.getDataByHttpRequest(proMinOrderNumUrl, (new JSONObject(params)).toString());


        try {
            JSONArray jsonArray=new JSONObject(result).getJSONArray("rows");

            DataSupport.deleteAll(ProductMinOrderNum.class);

            SQLiteDatabase db = Connector.getDatabase();
            String sql = "insert into productMinOrderNum";
            sql+="(orderPlanNo,goodsNo,buyerUnitCode,buyerUnitName,minNum)";
            sql+="values(?,?,?,?,?)";

            SQLiteStatement stat = db.compileStatement(sql);
            db.beginTransaction();
            try {
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);

                    stat.bindString(1,RocTools.getJsonVal(jsonObject,"orderPlanNo"));
                    stat.bindString(2,RocTools.getJsonVal(jsonObject,"goodsNo"));
                    stat.bindString(3,RocTools.getJsonVal(jsonObject,"buyerUnitCode"));
                    stat.bindString(4,RocTools.getJsonVal(jsonObject,"buyerUnitName"));
                    stat.bindString(5,RocTools.getJsonVal(jsonObject,"minNum"));

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
            Log.e(StaticVar.LOG_TAG, "解析最小起订量数据失败");
            e.printStackTrace();
        }
        Log.d(StaticVar.LOG_TAG, "更新最小起订量数据结束");
        return "";
    }
}
