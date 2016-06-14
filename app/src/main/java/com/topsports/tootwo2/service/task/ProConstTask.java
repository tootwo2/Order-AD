package com.topsports.tootwo2.service.task;

import android.content.Context;
import android.util.Log;

import com.topsports.tootwo2.helper.RocTools;
import com.topsports.tootwo2.helper.StaticVar;
import com.topsports.tootwo2.model.ProConstType;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取商品静态属性
 * Created by tootwo2 on 16/5/6.
 */
public class ProConstTask implements BaseTask{
    private String proConstUrl=StaticVar.URL_BASE+"proconst";

    @Override
    public String doTask(Map<String, String> params, Context context) {
        RocTools.sendBroadCast(context,"更新静态属性",StaticVar.BROADCAST_TYPE_SEARCH);

        String result="";

        Log.d(StaticVar.LOG_TAG, "请求静态属性开始");
        result= RocTools.getDataByHttpRequest(proConstUrl, "");


        try{
            JSONArray jsonArray=new JSONObject(result).getJSONArray("rows");
            DataSupport.deleteAll(ProConstType.class);
            List<ProConstType> proConstTypeList=new ArrayList<>();

            for(int i=0;i<jsonArray.length();i++){
                ProConstType proConstType=new ProConstType();
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                proConstType.setClassKey(jsonObject.getString("classKey"));
                proConstType.setClassType(jsonObject.getString("classType"));
                proConstType.setClassValue(jsonObject.getString("classValue"));
                proConstType.setRemark(RocTools.getJsonVal(jsonObject, "remark"));
                proConstType.setCustom_Resource(RocTools.getJsonVal(jsonObject,"custom_Resource"));
                proConstTypeList.add(proConstType);
            }

            DataSupport.saveAll(proConstTypeList);

            Log.d(StaticVar.LOG_TAG, "保存静态属性成功");
        }catch (Exception e){
            Log.e(StaticVar.LOG_TAG,e.getMessage());
            Log.e(StaticVar.LOG_TAG, "解析静态属性失败");
        }

        return "";
    }
}
