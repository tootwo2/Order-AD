package com.topsports.tootwo2.AsyncTasks;

/**

 */

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.topsports.tootwo2.helper.RocTools;
import com.topsports.tootwo2.model.ProductComments;
import com.topsports.tootwo2.model.ProductInfo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tootwo2 on 16/1/11.
 * <br/>
 * 上传订货信息和标签
 */
public class uploadProOrderTask extends AsyncTask<String,Integer,Boolean> {
    private ProgressDialog progressDialog;
    private String userId;

    public uploadProOrderTask(ProgressDialog progressDialog,String userId){
        this.progressDialog=progressDialog;
        this.userId=userId;
    }

    @Override
    protected void onPreExecute(){
        progressDialog.setTitle("提交订货数据中");
        progressDialog.setMessage("提交中，请稍候……");
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String result=uploadProOrder();

        if(!result.equals("SUCCESS")){return false;}

        result=uploadComments();

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        progressDialog.dismiss();
    }

    /**
     * 提交商品订单信息
     * @return
     */
    private String uploadProOrder(){
        String strUrl="http://192.168.4.217/ordermeet/app/procommit";

        List<ProductInfo> productInfoList= DataSupport.where("orderLevel is not null and orderLevel!='' and orderLevel!='X' and userid='" + userId + "'").find(ProductInfo.class);
        JSONArray jsonArray=new JSONArray();
        for(int i=0;i<productInfoList.size();i++){
            ProductInfo productInfo=productInfoList.get(i);
            Map<String, String> params = new HashMap<String, String>();
            params.put("userid", productInfo.getUserId());
            params.put("newGoodsID", productInfo.getNewGoodsId());
            params.put("orderPlanNo",productInfo.getOrderPlanNo());
            params.put("depth",productInfo.getOrderLevel()+productInfo.getOrderLevelPlus());
            jsonArray.put(new JSONObject(params));
        }


        String result="";

        try {
            URL url=new URL(strUrl);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            DataOutputStream out = new DataOutputStream(connection
                    .getOutputStream());

            byte[] content = jsonArray.toString().getBytes("utf-8");

            out.write(content, 0, content.length);
            out.flush();
            out.close(); // flush and close

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
                Log.i("TEST", result);
            }catch (Exception e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String resultType="";
        try{
            resultType=new JSONArray(result).getJSONObject(0).getString("resultType");
        }catch (Exception e){
            Log.i("TEST", e.toString());
        }
        return resultType;
    }

    /**
     * 上传标签信息
     */
    public String uploadComments() {
        String result="";
        String urlStr = "http://192.168.4.217/ordermeet/app/commentscommit";

        List<ProductComments> productCommentsList = DataSupport.where("userid=?", userId).find(ProductComments.class);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < productCommentsList.size(); i++) {
            ProductComments productComments = productCommentsList.get(i);
            Map<String, String> params = new HashMap<String, String>();
            params.put("userid", userId);
            params.put("newGoodsID", productComments.getNewGoodsID());
            params.put("commentsDetail", productComments.getCommentsDetail());
            params.put("commentsTime", productComments.getCommentsTime());
            jsonArray.put(new JSONObject(params));
        }

        result= RocTools.getDataByHttpRequest(urlStr, jsonArray.toString());
        return result;
    }
}
