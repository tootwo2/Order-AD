package com.topsports.tootwo2.service.task;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.topsports.tootwo2.helper.RocTools;
import com.topsports.tootwo2.helper.StaticVar;
import com.topsports.tootwo2.model.ProductOldCompare;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;
import org.litepal.tablemanager.Connector;

import java.util.Map;

/**
 * 历史商品关联
 * Created by tootwo2 on 16/5/6.
 */
public class ProOldCompareTask implements BaseTask{
    private String proOldCompareUrl=StaticVar.URL_BASE+"proOldCompare";
    @Override
    public String doTask(Map<String, String> params, Context context) {
        String result="";

        String urlStr=proOldCompareUrl;


        Log.d(StaticVar.LOG_TAG, "请求历史商品对应关系开始");
        result= RocTools.getDataByHttpRequest(urlStr, (new JSONObject(params)).toString());


        try {
            JSONArray jsonArray=new JSONObject(result).getJSONArray("rows");
            DataSupport.deleteAll(ProductOldCompare.class);

            SQLiteDatabase db = Connector.getDatabase();
            String sql = "insert into ProductOldCompare";
            sql+="(proId,brdSeason,proIdOld,proNmOld,priceBandNm,proStyleNm,proCateNm,proGenderNm,brdSeasonOld) ";
            sql+="values(?,?,?,?,?,?,?,?,?)";

            SQLiteStatement stat = db.compileStatement(sql);
            db.beginTransaction();
            try {
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);

                    stat.bindString(1,RocTools.getJsonVal(jsonObject,"pro_id"));
                    stat.bindString(2,RocTools.getJsonVal(jsonObject,"brd_season"));
                    stat.bindString(3,RocTools.getJsonVal(jsonObject,"pro_id_old"));
                    stat.bindString(4,RocTools.getJsonVal(jsonObject,"pro_nm_old"));
                    stat.bindString(5,RocTools.getJsonVal(jsonObject,"price_band_nm"));
                    stat.bindString(6,RocTools.getJsonVal(jsonObject,"pro_style_nm"));
                    stat.bindString(7,RocTools.getJsonVal(jsonObject,"pro_cate_nm"));
                    stat.bindString(8,RocTools.getJsonVal(jsonObject,"pro_gender_nm"));
                    stat.bindString(9,RocTools.getJsonVal(jsonObject,"brd_season_old"));

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
            Log.e(StaticVar.LOG_TAG, "请求历史商品对应关系失败");
        }
        Log.d(StaticVar.LOG_TAG,String.valueOf(DataSupport.count(ProductOldCompare.class)));
        Log.d(StaticVar.LOG_TAG, "请求历史商品对应关系结束");

        return null;
    }
}
