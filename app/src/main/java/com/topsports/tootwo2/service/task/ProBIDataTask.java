package com.topsports.tootwo2.service.task;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.topsports.tootwo2.helper.RocTools;
import com.topsports.tootwo2.helper.StaticVar;
import com.topsports.tootwo2.model.ProductHisTrend;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;
import org.litepal.tablemanager.Connector;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品历史数据
 * Created by tootwo2 on 16/5/6.
 */
public class ProBIDataTask implements BaseTask {
    private String proBIUrl=StaticVar.URL_BASE+"proHisTrend";;
    @Override
    public String doTask(Map<String, String> params, Context context) {
        RocTools.sendBroadCast(context,"更新历史数据",StaticVar.BROADCAST_TYPE_SEARCH);

        String result="";

        Log.d(StaticVar.LOG_TAG, "请求BI历史数据开始");
        result= RocTools.getDataByHttpRequest(proBIUrl, (new JSONObject(params)).toString());


        try {
            JSONArray jsonArray=new JSONObject(result).getJSONArray("rows");
            DataSupport.deleteAll(ProductHisTrend.class);

            SQLiteDatabase db = Connector.getDatabase();
            String sql = "insert into ProductHisTrend";
            sql+="(newGoodsId,goodsNo,modelName,brdSeason, ";
            sql+="totalSalQty1,totalSalAmt1,totalSalNosPrmAmt1,totalSalPrmAmt1,invQty1,invAmt1, ";
            sql+="totalSalQty2,totalSalAmt2,totalSalNosPrmAmt2,totalSalPrmAmt2,invQty2,invAmt2, ";
            sql+="totalSalQty3,totalSalAmt3,totalSalNosPrmAmt3,totalSalPrmAmt3,invQty3,invAmt3, ";
            sql+="distrOrgNum,salOrgNum,orderNum, ";
            sql+="buyerUnitId,buyerUnitNm,regionId,regionNm,provId,provNm,mgmtCityId,mgmtCityNm )";
            sql+="values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            SQLiteStatement stat = db.compileStatement(sql);
            db.beginTransaction();
            try {
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);

                    stat.bindString(1,RocTools.getJsonVal(jsonObject,"pro_id"));
                    stat.bindString(2,RocTools.getJsonVal(jsonObject,"pro_id"));
                    stat.bindString(3,RocTools.getJsonVal(jsonObject,"pro_id"));
                    stat.bindString(4,RocTools.getJsonVal(jsonObject,"brd_season"));

                    stat.bindString(5,RocTools.getJsonVal(jsonObject,"total_sal_qty1"));
                    stat.bindString(6,RocTools.getJsonVal(jsonObject,"total_sal_amt1"));
                    stat.bindString(7,RocTools.getJsonVal(jsonObject,"total_sal_nos_prm_amt1"));
                    stat.bindString(8,RocTools.getJsonVal(jsonObject,"total_sal_prm_amt1"));
                    stat.bindString(9,RocTools.getJsonVal(jsonObject,"inv_qty1"));
                    stat.bindString(10,RocTools.getJsonVal(jsonObject,"inv_amt1"));

                    stat.bindString(11,RocTools.getJsonVal(jsonObject,"total_sal_qty2"));
                    stat.bindString(12,RocTools.getJsonVal(jsonObject,"total_sal_amt2"));
                    stat.bindString(13,RocTools.getJsonVal(jsonObject,"total_sal_nos_prm_amt2"));
                    stat.bindString(14,RocTools.getJsonVal(jsonObject,"total_sal_prm_amt2"));
                    stat.bindString(15,RocTools.getJsonVal(jsonObject,"inv_qty2"));
                    stat.bindString(16,RocTools.getJsonVal(jsonObject,"inv_amt2"));

                    stat.bindString(17,RocTools.getJsonVal(jsonObject,"total_sal_qty3"));
                    stat.bindString(18,RocTools.getJsonVal(jsonObject,"total_sal_amt3"));
                    stat.bindString(19,RocTools.getJsonVal(jsonObject,"total_sal_nos_prm_amt3"));
                    stat.bindString(20,RocTools.getJsonVal(jsonObject,"total_sal_prm_amt3"));
                    stat.bindString(21,RocTools.getJsonVal(jsonObject,"inv_qty3"));
                    stat.bindString(22,RocTools.getJsonVal(jsonObject,"inv_amt3"));

                    stat.bindString(23,RocTools.getJsonVal(jsonObject,"distr_org_num"));
                    stat.bindString(24,RocTools.getJsonVal(jsonObject,"sal_org_num"));
                    stat.bindString(25,RocTools.getJsonVal(jsonObject,"order_num"));

                    stat.bindString(26,RocTools.getJsonVal(jsonObject,"buyer_unit_id"));
                    stat.bindString(27,RocTools.getJsonVal(jsonObject,"buyer_unit_nm"));
                    stat.bindString(28,RocTools.getJsonVal(jsonObject,"region_id"));
                    stat.bindString(29,RocTools.getJsonVal(jsonObject,"region_nm"));
                    stat.bindString(30,RocTools.getJsonVal(jsonObject,"prov_id"));
                    stat.bindString(31,RocTools.getJsonVal(jsonObject,"prov_nm"));
                    stat.bindString(32,RocTools.getJsonVal(jsonObject,"mgmt_city_id"));
                    stat.bindString(33,RocTools.getJsonVal(jsonObject,"mgmt_city_nm"));

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
            e.printStackTrace();
            Log.e(StaticVar.LOG_TAG, "解析BI数据失败");
        }
        Log.d(StaticVar.LOG_TAG, String.valueOf(DataSupport.count(ProductHisTrend.class)));
        Log.d(StaticVar.LOG_TAG, "请求BI历史数据结束");

        return "";
    }
}
