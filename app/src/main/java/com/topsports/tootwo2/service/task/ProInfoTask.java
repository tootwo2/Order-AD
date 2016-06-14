package com.topsports.tootwo2.service.task;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.topsports.tootwo2.helper.RocTools;
import com.topsports.tootwo2.helper.StaticVar;
import com.topsports.tootwo2.model.ProductInfo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.Map;

/**
 * 获取商品信息
 * Created by tootwo2 on 16/5/6.
 */
public class ProInfoTask implements BaseTask{
    private String proInfoUrl=StaticVar.URL_BASE+"proinfo";
    @Override
    public String doTask(Map<String,String> params, Context context) {
        RocTools.sendBroadCast(context,"更新商品资料",StaticVar.BROADCAST_TYPE_SEARCH);
        String result="";


        Log.d(StaticVar.LOG_TAG, "请求商品信息开始");
        result=RocTools.getDataByHttpRequest(proInfoUrl, (new JSONObject(params)).toString());
        Log.d(StaticVar.LOG_TAG, "请求商品信息结束");

        try {
            JSONArray jsonArray=new JSONObject(result).getJSONArray("rows");

            DataSupport.deleteAll(ProductInfo.class);

            Log.d(StaticVar.LOG_TAG, "保存商品信息开始");

            SQLiteDatabase db = Connector.getDatabase();
            String sql = "insert into productinfo";
            sql+="(ageLevel,asecondaryColor,brandStyle,brandStyleDesc,brdSeason,category,categoryDesc,categorySys,colorNo,composition,compositionDesc,custom1,custom10,custom2,custom3,custom4,custom5,custom6,custom7,custom8,custom9,directoryPage,division,eastLaunch,eastLaunchMonth,gender,genderDesc,goodsDescribe,goodsNo,havetoOrder,havetoOrderDesc,listingMonth,localRp,mainBrandCode,mainColor,marketSupport,marketSupportDesc,marketingDivision,marketingDivisionDesc,modelName,modelNo,modelNoSys,newGoodsId,orderPlanNo,orderPlanY,package_,pop,popDesc,pricePoint,pricePointDesc,remark,repeatListing,seasonStyle,sizeRange,status,suitableSituation,supportStyle,systemSize,technology,technologyDesc,divisionDesc)";
            sql+="values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            SQLiteStatement stat = db.compileStatement(sql);
            db.beginTransaction();
            try {
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);

                    stat.bindString(1,RocTools.getJsonVal(jsonObject,"ageLevel"));
                    stat.bindString(2,RocTools.getJsonVal(jsonObject,"asecondaryColor"));
                    stat.bindString(3,RocTools.getJsonVal(jsonObject,"brandStyle"));
                    stat.bindString(4,RocTools.getJsonVal(jsonObject,"brandStyleDesc"));
                    stat.bindString(5,RocTools.getJsonVal(jsonObject,"brdSeason"));
                    stat.bindString(6,RocTools.getJsonVal(jsonObject,"category"));
                    stat.bindString(7,RocTools.getJsonVal(jsonObject,"categoryDesc"));
                    stat.bindString(8,RocTools.getJsonVal(jsonObject,"categorySys"));
                    stat.bindString(9,RocTools.getJsonVal(jsonObject,"colorNo"));
                    stat.bindString(10,RocTools.getJsonVal(jsonObject,"composition"));
                    stat.bindString(11,RocTools.getJsonVal(jsonObject,"compositionDesc"));
                    stat.bindString(12,RocTools.getJsonVal(jsonObject,"custom1"));
                    stat.bindString(13,RocTools.getJsonVal(jsonObject,"custom10"));
                    stat.bindString(14,RocTools.getJsonVal(jsonObject,"custom2"));
                    stat.bindString(15,RocTools.getJsonVal(jsonObject,"custom3"));
                    stat.bindString(16,RocTools.getJsonVal(jsonObject,"custom4"));
                    stat.bindString(17,RocTools.getJsonVal(jsonObject,"custom5"));
                    stat.bindString(18,RocTools.getJsonVal(jsonObject,"custom6"));
                    stat.bindString(19,RocTools.getJsonVal(jsonObject,"custom7"));
                    stat.bindString(20,RocTools.getJsonVal(jsonObject,"custom8"));
                    stat.bindString(21,RocTools.getJsonVal(jsonObject,"custom9"));
                    stat.bindString(22,RocTools.getJsonVal(jsonObject,"directoryPage"));
                    stat.bindString(23,RocTools.getJsonVal(jsonObject,"division"));
                    stat.bindString(24,RocTools.getJsonVal(jsonObject,"eastLaunch"));
                    stat.bindString(25,RocTools.getJsonVal(jsonObject,"eastLaunchMonth"));
                    stat.bindString(26,RocTools.getJsonVal(jsonObject,"gender"));
                    stat.bindString(27,RocTools.getJsonVal(jsonObject,"genderDesc"));
                    stat.bindString(28,RocTools.getJsonVal(jsonObject,"goodsDescribe"));
                    stat.bindString(29,RocTools.getJsonVal(jsonObject,"goodsNo"));
                    stat.bindString(30,RocTools.getJsonVal(jsonObject,"havetoOrder"));
                    stat.bindString(31,RocTools.getJsonVal(jsonObject,"havetoOrderDesc"));
                    stat.bindString(32,RocTools.getJsonVal(jsonObject,"listingMonth"));
                    stat.bindString(33,RocTools.getJsonVal(jsonObject,"localRp"));
                    stat.bindString(34,RocTools.getJsonVal(jsonObject,"mainBrandCode"));
                    stat.bindString(35,RocTools.getJsonVal(jsonObject,"mainColor"));
                    stat.bindString(36,RocTools.getJsonVal(jsonObject,"marketSupport"));
                    stat.bindString(37,RocTools.getJsonVal(jsonObject,"marketSupportDesc"));
                    stat.bindString(38,RocTools.getJsonVal(jsonObject,"marketingDivision"));
                    stat.bindString(39,RocTools.getJsonVal(jsonObject,"marketingDivisionDesc"));
                    stat.bindString(40,RocTools.getJsonVal(jsonObject,"modelName"));
                    stat.bindString(41,RocTools.getJsonVal(jsonObject,"modelNo"));
                    stat.bindString(42,RocTools.getJsonVal(jsonObject,"modelNoSys"));
                    stat.bindString(43,RocTools.getJsonVal(jsonObject,"newGoodsId"));
                    stat.bindString(44,RocTools.getJsonVal(jsonObject,"orderPlanNo"));
                    stat.bindString(45,RocTools.getJsonVal(jsonObject,"orderPlanY"));
                    stat.bindString(46,RocTools.getJsonVal(jsonObject,"package_"));
                    stat.bindString(47,RocTools.getJsonVal(jsonObject,"pop"));
                    stat.bindString(48,RocTools.getJsonVal(jsonObject,"popDesc"));
                    stat.bindString(49,RocTools.getJsonVal(jsonObject,"pricePoint"));
                    stat.bindString(50,RocTools.getJsonVal(jsonObject,"pricePointDesc"));
                    stat.bindString(51,RocTools.getJsonVal(jsonObject,"remark"));
                    stat.bindString(52,RocTools.getJsonVal(jsonObject,"repeatListing"));
                    stat.bindString(53,RocTools.getJsonVal(jsonObject,"seasonStyle"));
                    stat.bindString(54,RocTools.getJsonVal(jsonObject,"sizeRange"));
                    stat.bindString(55,RocTools.getJsonVal(jsonObject,"status"));
                    stat.bindString(56,RocTools.getJsonVal(jsonObject,"suitableSituation"));
                    stat.bindString(57,RocTools.getJsonVal(jsonObject,"supportStyle"));
                    stat.bindString(58,RocTools.getJsonVal(jsonObject,"systemSize"));
                    stat.bindString(59,RocTools.getJsonVal(jsonObject,"technology"));
                    stat.bindString(60,RocTools.getJsonVal(jsonObject,"technologyDesc"));
                    stat.bindString(61,RocTools.getJsonVal(jsonObject,"divisionDesc"));
                    stat.executeInsert();
                }

                db.setTransactionSuccessful();
            } catch (Exception var6) {
                Log.e(StaticVar.LOG_TAG, var6.getMessage());
            } finally {
                db.endTransaction();
                db.close();
            }

            Log.d(StaticVar.LOG_TAG, "保存商品信息成功");
        } catch (Exception e) {
            Log.e(StaticVar.LOG_TAG,e.getMessage());
            Log.e(StaticVar.LOG_TAG,"解析商品信息失败");

            e.printStackTrace();
        }
        return "";
    }
}
