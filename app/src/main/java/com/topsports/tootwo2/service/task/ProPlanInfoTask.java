package com.topsports.tootwo2.service.task;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.topsports.tootwo2.helper.RocTools;
import com.topsports.tootwo2.helper.StaticVar;
import com.topsports.tootwo2.model.ProductPlanInfo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;
import org.litepal.tablemanager.Connector;

import java.util.Map;

/**
 * 商品订量计划
 * Created by tootwo2 on 16/5/9.
 */
public class ProPlanInfoTask implements BaseTask{
    private String proPlanInfoUrl= StaticVar.URL_BASE+"proPlanInfo";
    @Override
    public String doTask(Map<String, String> params, Context context) {
        RocTools.sendBroadCast(context,"更新订货计划",StaticVar.BROADCAST_TYPE_SEARCH);
        Log.d("TEST", "更新计划数据开始");
        String result="";

        result= RocTools.getDataByHttpRequest(proPlanInfoUrl, (new JSONObject(params)).toString());

        try {
            JSONArray jsonArray=new JSONObject(result).getJSONArray("rows");

            DataSupport.deleteAll(ProductPlanInfo.class);

            SQLiteDatabase db = Connector.getDatabase();
            String sql = "insert into ProductPlanInfo";
            sql+="(orderPlanNo,buyerUnitCode,buyerUnitName,proSeriesId,proSeriesNm,proCateId,proCateNm,proGenderId,proGenderNm,inSku,inOrdSal,inOrdQty)";
            sql+="values(?,?,?,?,?,?,?,?,?,?,?,?)";

            SQLiteStatement stat = db.compileStatement(sql);
            db.beginTransaction();
            try {
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);

                    stat.bindString(1,RocTools.getJsonVal(jsonObject,"orderPlanNo"));
                    stat.bindString(2,RocTools.getJsonVal(jsonObject,"buyer_unit_code"));
                    stat.bindString(3,RocTools.getJsonVal(jsonObject,"buyer_unit_name"));
                    stat.bindString(4,RocTools.getJsonVal(jsonObject,"pro_series_id"));
                    stat.bindString(5,RocTools.getJsonVal(jsonObject,"pro_series_nm"));
                    stat.bindString(6,RocTools.getJsonVal(jsonObject,"pro_cate_id"));
                    stat.bindString(7,RocTools.getJsonVal(jsonObject,"pro_cate_nm"));
                    stat.bindString(8,RocTools.getJsonVal(jsonObject,"pro_gender_id"));
                    stat.bindString(9,RocTools.getJsonVal(jsonObject,"pro_gender_nm"));
                    stat.bindString(10,RocTools.getJsonVal(jsonObject,"in_sku"));
                    stat.bindString(11,RocTools.getJsonVal(jsonObject,"in_ord_sal"));
                    stat.bindString(12,RocTools.getJsonVal(jsonObject,"in_ord_qty"));

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
            Log.d("TEST", "更新计划数据失败");
            Log.e("TEST",e.getMessage());
            e.printStackTrace();
        }
        Log.d("TEST", "更新计划数据结束");
        return "";
    }
}
