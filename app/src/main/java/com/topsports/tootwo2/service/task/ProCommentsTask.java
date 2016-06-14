package com.topsports.tootwo2.service.task;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.topsports.tootwo2.helper.RocTools;
import com.topsports.tootwo2.helper.StaticVar;
import com.topsports.tootwo2.model.ProductComments;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;
import org.litepal.tablemanager.Connector;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tootwo2 on 16/5/6.
 */
public class ProCommentsTask implements BaseTask{
    private String commentsDownloadUrl= StaticVar.URL_BASE+"commentsDownload";
    @Override
    public String doTask(Map<String, String> params, Context context) {
        RocTools.sendBroadCast(context,"更新标签资料",StaticVar.BROADCAST_TYPE_SEARCH);

        Log.d("TEST", "更新标签数据开始");
        String result="";

        result= RocTools.getDataByHttpRequest(commentsDownloadUrl, (new JSONObject(params)).toString());

        try {
            JSONArray jsonArray=new JSONObject(result).getJSONArray("rows");

            DataSupport.deleteAll(ProductComments.class);

            SQLiteDatabase db = Connector.getDatabase();
            String sql = "insert into ProductComments";
            sql+="(newGoodsID,userid,commentsDetail,commentsTime,status)";
            sql+="values(?,?,?,?,0)";

            SQLiteStatement stat = db.compileStatement(sql);
            db.beginTransaction();
            try {
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);

                    stat.bindString(1,RocTools.getJsonVal(jsonObject,"newGoodsID"));
                    stat.bindString(2,RocTools.getJsonVal(jsonObject,"userId"));
                    stat.bindString(3,RocTools.getJsonVal(jsonObject,"commentsDetail"));
                    stat.bindString(4,RocTools.getJsonVal(jsonObject,"commentsTime"));

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
            Log.e("TEST",e.getMessage());
            e.printStackTrace();
        }
        Log.d("TEST", "更新标签数据结束");
        return "";
    }
}
