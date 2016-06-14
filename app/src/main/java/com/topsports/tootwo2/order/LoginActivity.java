package com.topsports.tootwo2.order;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.topsports.tootwo2.app.OrderApplication;
import com.topsports.tootwo2.helper.StaticVar;
import com.topsports.tootwo2.helper.VolleySingleton;
import com.topsports.tootwo2.model.ProductInfo;
import com.topsports.tootwo2.model.ProductOrderInfo;
import com.topsports.tootwo2.widget.autolistview.widget.AutoListView;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登陆界面
 */
public class LoginActivity extends Activity implements View.OnClickListener,TextView.OnEditorActionListener {

    final private static String TAG = "TAG";
    private String urlBase= StaticVar.URL_BASE;;
//    private String urlBase = "http://192.168.9.174/ordermeet/app/";
    private String proLoginUrl = urlBase + "prologin";
    private String checkUpdate = urlBase + "checkUpdate";
    private String updateAPP = urlBase + "updateAPP";
    OrderApplication orderApplication;

    Button btnLoginIn;
    TextView txtUserid;
    TextView txtPassword;
    String userid;
    String password;
    Spinner orderMeetSelectSpinner;
    ArrayList<String> orderMeetList;

    //下载等待
    private ProgressDialog progressDialog;

    Handler handlerProgressDialog = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            progressDialog.dismiss();// 关闭ProgressDialog
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        //去除title
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //去掉Activity上面的状态栏
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        orderApplication=(OrderApplication)getApplication();
        readFromSharedPreferences();

        btnLoginIn  = (Button)findViewById(R.id.btnLoginIn);
        txtUserid = (TextView)findViewById(R.id.useridEdittext);
        txtPassword = (TextView)findViewById(R.id.passwordEdittext);
        btnLoginIn.setOnClickListener(this);
        txtUserid.setOnEditorActionListener(this);
        txtPassword.setOnEditorActionListener(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        initOrderSpinner();
    }

    private void initOrderSpinner(){
        orderMeetSelectSpinner=(Spinner)findViewById(R.id.orderMeetSelect);
        orderMeetSelectSpinner.setBackgroundColor(Color.WHITE);
        orderMeetList=new ArrayList<>();
        orderMeetList.add("AD16Q4");
        orderMeetList.add("AD16Q3");
        orderMeetList.add("AD16Q2");
        orderMeetList.add("AD16Q1");
        ArrayAdapter<String> isOrderedAdapter=new ArrayAdapter<String>(this,R.layout.custom_spinner_login,orderMeetList);
        orderMeetSelectSpinner.setAdapter(isOrderedAdapter);
    }

    public void clearhelper(View view){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("你确定要删除本地订货信息吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Log.i(TAG, "clear data begin");

                DataSupport.deleteAll(ProductInfo.class,"1=1");
                DataSupport.deleteAll(ProductOrderInfo.class,"1=1");

                Log.i(TAG,"data:"+String.valueOf(DataSupport.count(ProductInfo.class)));

            }
        });
        builder.create().show();
    }

    public void updateAPP(View view){
        new UpdateAPP().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoginIn:
                userid = txtUserid.getText().toString();
                password = txtPassword.getText().toString();
                progressDialog = ProgressDialog.show(this, "登录", "登录中，请稍候……");
                login();

                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
        switch (v.getId()){
            case R.id.useridEdittext:

                if (actionId == EditorInfo.IME_ACTION_NEXT ||(event!=null&&event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                {
                    //do something;
                    txtPassword.setFocusable(true);
                    return false;
                }
                break;
        }
        return false;
    }


    private boolean login(){
        if(txtUserid.getText().equals("") || txtUserid.getText().equals("")){
            alert("用户名密码不能为空!");
            return false;
        }

        Map<String, String> params = new HashMap<String, String>();
        userid = txtUserid.getText().toString();
        password = txtPassword.getText().toString();
        params.put("userid", userid);
        params.put("password", password);

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                proLoginUrl,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonobj) {
                        Log.d(TAG, jsonobj.toString());
                        try{
                            boolean isSuccessful = jsonobj.getJSONObject("returnObject").getBoolean("status");
                            if(isSuccessful){
                                String areaName = jsonobj.getJSONObject("returnObject").getString("areaName");
                                String role = jsonobj.getJSONObject("returnObject").getString("role");
                                String name = jsonobj.getJSONObject("returnObject").getString("name");
                                String buyerUnitsStr=jsonobj.getJSONObject("returnObject").getString("buyerUnitsStr");

                                int orderMeetPos=orderMeetSelectSpinner.getSelectedItemPosition();
                                String orderPlanNo =orderMeetList.get(orderMeetPos);
                                //页面跳转到SearchActivity
                                orderApplication.setUserId(userid);
                                orderApplication.setAreaName(areaName);
                                orderApplication.setRole(role);
                                orderApplication.setName(name);
                                orderApplication.setOrderMeetingNo(orderPlanNo);
                                orderApplication.setBuyerUnitsStr(buyerUnitsStr);

                                writeToSharedPreferences();
                                loginToSearch();
                            }else {
                                alert("账号或密码错误!");
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e(TAG,e.getMessage());
                        }finally {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.getMessage(), error);
                        progressDialog.dismiss();
                        alert("登陆失败,请检查网络连接");
                    }
                }
        );

        req.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(req);


        return false;
    }



    private void alert(String message){
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定", null)
                .show();
    }

    /**
     * 登陆成功跳转
     */
    public void loginToSearch(){
        Intent intent = new Intent(this, SearchQtyActivity.class);
        startActivity(intent);
        this.finish();
    }


    /**
     * 写入SharedPreferences
     */
    private void writeToSharedPreferences(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor e = pref.edit();
        e.putString("userid", orderApplication.getUserId());
        e.putString("areaName", orderApplication.getAreaName());
        e.putString("role", orderApplication.getRole());
        e.putString("name", orderApplication.getName());
        e.putString("buyerUnitsStr", orderApplication.getBuyerUnitsStr());
        e.putString("orderMeetingNo",orderApplication.getOrderMeetingNo());

        e.commit();
    }


    /**
     * 读取SharedPreferences,如果用户信息已存在 直接登录
     */
    private void readFromSharedPreferences(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(pref.getString("userid", "").equals("")){
            return;
        }
        orderApplication.setUserId(pref.getString("userid", ""));
        orderApplication.setAreaName(pref.getString("areaName", ""));
        orderApplication.setRole(pref.getString("role", ""));
        orderApplication.setName(pref.getString("name", ""));
        orderApplication.setBuyerUnitsStr(pref.getString("buyerUnitsStr", ""));
        orderApplication.setOrderMeetingNo(pref.getString("orderMeetingNo",""));
        loginToSearch();
    }


    //升级APP
    class UpdateAPP extends AsyncTask<String,Integer,Boolean> {
        @Override
        protected void onPreExecute(){
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String apk = "";
            JSONObject obj = null;
            try {
                URL url = new URL(checkUpdate + "?version=" + orderApplication.getVersion());
                URLConnection con = url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String lines="";
                String ss="";
                while((lines = in.readLine()) != null){
                    ss+=lines;
                }

                JSONTokener jsonTokener = new JSONTokener(ss);

                obj = (JSONObject) jsonTokener.nextValue();

            } catch (Exception e){
                e.printStackTrace();
            }
            try {
                Log.i("TEST",obj.toString());
                if (obj.getString("msg").equals("0")) {

                    Log.i("TEST","已经是最新版本");
                    publishProgress(0);

                } else if(obj.getString("msg").equals("1")){
                    publishProgress(1);
                    String filePath = "/data/data/com.topsports.tootwo2.order/files/";
                    String filePathAndName = filePath + "updateAPP";
                    File path = new File(filePath);
                    if (!path.exists()) {
                        path.mkdirs();
                    }
                    File file = new File(filePathAndName);
                    //如果目标文件已经存在，则删除。产生覆盖旧文件的效果
                    if(file.exists())
                    {
                        file.delete();
                    }
                    // 构造URL
                    URL url = new URL(updateAPP + "?apk=" + obj.getString("returnObject"));
                    // 打开连接
                    URLConnection con = url.openConnection();
                    //获得文件的长度
                    //int contentLength = con.getContentLength();
                    //System.out.println("长度 :"+contentLength);
                    // 输入流
                    InputStream is = con.getInputStream();
                    // 1K的数据缓冲
                    byte[] bs = new byte[64];
                    // 读取到的数据长度
                    int len;
                    // 输出的文件流
                    OutputStream os = new FileOutputStream(filePathAndName);
                    // 开始读取
                    while ((len = is.read(bs)) != -1) {
                        os.write(bs, 0, len);
                    }
                    // 完毕，关闭所有链接
                    os.close();
                    is.close();
                    File apkFile = new File(filePathAndName);
                    String command = "chmod 775 " + filePath;
                    Log.i("TEST", command);
                    Runtime runtime = Runtime.getRuntime();
                    runtime.exec(command);

                    command = "chmod 775 " + apkFile;
                    Log.i("TEST", command);
                    runtime = Runtime.getRuntime();
                    runtime.exec(command);

                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(apkFile),
                            "application/vnd.android.package-archive");

                    startActivity(intent);

//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    File apkFile = new File(filePathAndName);
//                    i.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    getApplicationContext().startActivity(i);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
            /////////////////////////////////////////////

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int value=values[0];
            if(value == 0){
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("提示")
                        .setMessage("现在已经是最新版本")
                        .setPositiveButton("确定", null)
                        .show();
            } else if(value == 1){
                progressDialog.setTitle("更新APP");
                progressDialog.setMessage("下载apk中，请稍候……");
                progressDialog.show();

            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
        }
    }

}
