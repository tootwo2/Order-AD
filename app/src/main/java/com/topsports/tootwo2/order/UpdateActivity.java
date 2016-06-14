package com.topsports.tootwo2.order;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SaxAsyncHttpResponseHandler;
import com.topsports.tootwo2.app.OrderApplication;
import com.topsports.tootwo2.helper.RocTools;
import com.topsports.tootwo2.helper.StaticVar;
import com.topsports.tootwo2.helper.UpdateRestClient;
import com.topsports.tootwo2.model.ProConstType;
import com.topsports.tootwo2.model.ProductComments;
import com.topsports.tootwo2.model.ProductHisTrend;
import com.topsports.tootwo2.model.ProductInfo;
import com.topsports.tootwo2.model.ProductMinOrderNum;
import com.topsports.tootwo2.model.ProductOldCompare;
import com.topsports.tootwo2.model.ProductOrderInfo;
import com.topsports.tootwo2.model.ProductPlanInfo;
import com.topsports.tootwo2.widget.autolistview.widget.AutoListView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class UpdateActivity extends AppCompatActivity {

    /**
     * 商品信息
     */
    final static int PRO_INFO=0;
    private BootstrapProgressBar proInfoProgress;

    /**
     * 商品属性
     */
    final static int PRO_ATTR=1;
    private BootstrapProgressBar proAttrProgress;

    /**
     * 商品订量
     */
    final static int PRO_ORDER=2;
    private BootstrapProgressBar proOrderProgress;

    /**
     * 商品标签
     */
    final static int PRO_COMMENTS=3;
    private BootstrapProgressBar proCommentsProgress;

    /**
     * 历史数据
     */
    final static int PRO_BIDATA=4;
    private BootstrapProgressBar proBiDataProgress;

    /**
     * 商品对照
     */
    final static int PRO_COMPARE=5;
    private BootstrapProgressBar proCompareProgress;

    /**
     * 商品计划
     */
    final static int PRO_PLAN=6;
    private BootstrapProgressBar proPlanProgress;

    /**
     * 最小订量
     */
    final static int PRO_MIN=7;
    private BootstrapProgressBar proMinOrderProgress;



    /**
     * 更新进度条状态
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int progress=(int)msg.obj;
            switch (msg.what) {
                case PRO_INFO:
                    proInfoProgress.setProgress(progress);
                    break;
                case PRO_ATTR:
                    proAttrProgress.setProgress(progress);
                    break;
                case PRO_ORDER:
                    proOrderProgress.setProgress(progress);
                    break;
                case PRO_COMMENTS:
                    proCommentsProgress.setProgress(progress);
                    break;
                case PRO_BIDATA:
                    proBiDataProgress.setProgress(progress);
                    break;
                case PRO_COMPARE:
                    proCompareProgress.setProgress(progress);
                    break;
                case PRO_PLAN:
                    proPlanProgress.setProgress(progress);
                    break;
                case PRO_MIN:
                    proMinOrderProgress.setProgress(progress);
                    break;
                default:
                    break;
            }

        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        proInfoProgress=(BootstrapProgressBar)findViewById(R.id.progress_dialog_proinfo);
        proAttrProgress=(BootstrapProgressBar)findViewById(R.id.progress_dialog_proattr);
        proOrderProgress=(BootstrapProgressBar)findViewById(R.id.progress_dialog_proorder);
        proCommentsProgress=(BootstrapProgressBar)findViewById(R.id.progress_dialog_procomments);
        proBiDataProgress=(BootstrapProgressBar)findViewById(R.id.progress_dialog_probidata);
        proCompareProgress=(BootstrapProgressBar)findViewById(R.id.progress_dialog_procomparte);
        proPlanProgress=(BootstrapProgressBar)findViewById(R.id.progress_dialog_proplan);
        proMinOrderProgress=(BootstrapProgressBar)findViewById(R.id.progress_dialog_minorder);


        final CheckBox checkAll=(CheckBox)findViewById(R.id.cb_checkall);

        final CheckBox proInfoCb=(CheckBox)findViewById(R.id.cb_pro_info);
        final CheckBox proAttrCb=(CheckBox)findViewById(R.id.cb_pro_attr);
        final CheckBox proOrderCb=(CheckBox)findViewById(R.id.cb_pro_order);
        final CheckBox proCommentsCb=(CheckBox)findViewById(R.id.cb_pro_comments);
        final CheckBox proBiDataCb=(CheckBox)findViewById(R.id.cb_pro_bidata);
        final CheckBox proCompareCb=(CheckBox)findViewById(R.id.cb_pro_compare);
        final CheckBox proPlanCb=(CheckBox)findViewById(R.id.cb_pro_plan);
        final CheckBox proMinCb=(CheckBox)findViewById(R.id.cb_pro_minorder);

        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    proInfoCb.setChecked(true);
                    proAttrCb.setChecked(true);
                    proOrderCb.setChecked(true);
                    proCommentsCb.setChecked(true);
                    proBiDataCb.setChecked(true);
                    proCompareCb.setChecked(true);
                    proPlanCb.setChecked(true);
                    proMinCb.setChecked(true);
                }else {
                    proInfoCb.setChecked(false);
                    proAttrCb.setChecked(false);
                    proOrderCb.setChecked(false);
                    proCommentsCb.setChecked(false);
                    proBiDataCb.setChecked(false);
                    proCompareCb.setChecked(false);
                    proPlanCb.setChecked(false);
                    proMinCb.setChecked(false);
                }
            }
        });


        BootstrapButton bootstrapButton=(BootstrapButton)findViewById(R.id.update);
        bootstrapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(StaticVar.LOG_TAG,"click");
                OrderApplication application=(OrderApplication) getApplication();
                String orderMeetNo=application.getOrderMeetingNo();
                String userId=application.getUserId();

                SQLiteDatabase database=Connector.getDatabase();

                if(proInfoCb.isChecked()){
                    updateProInfo(orderMeetNo,userId,database);
                }
                if(proAttrCb.isChecked()){
                    updateProAttr(orderMeetNo,userId);
                }
                if(proOrderCb.isChecked()){
                    updateProOrder(orderMeetNo,userId,database);
                }
                if(proCommentsCb.isChecked()){
                    updateProComments(orderMeetNo,userId,database);
                }
                if(proBiDataCb.isChecked()){
                    updateBidata(orderMeetNo,userId,database);
                }
                if(proCompareCb.isChecked()){
                    updateProCompare(orderMeetNo,userId,database);
                }
                if(proPlanCb.isChecked()){
                    updateProPlan(orderMeetNo,userId,database);
                }
                if(proMinCb.isChecked()){
                    updateProMinNum(orderMeetNo,userId,database);
                }

            }
        });

        BootstrapButton finishBtn=(BootstrapButton)findViewById(R.id.finish);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UpdateActivity.this,SearchQtyActivity.class);
                startActivity(intent);
                UpdateActivity.this.finish();
            }
        });
    }

    /**
     * 同步商品信息
     * @param orderMeetNo
     * @param userId
     */
    private void updateProInfo(String orderMeetNo,String userId,final SQLiteDatabase database){
        handleMsg(PRO_INFO,0);
        RequestParams params = new RequestParams();
        params.put("orderMeetNo", orderMeetNo);
        params.put("userId", userId);
        UpdateRestClient.post("proinfo",params,new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                Log.d(StaticVar.LOG_TAG,"start");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                handleMsg(PRO_INFO,30);

                new Thread(){
                    public void run(){
                        System.out.println("Thread is running.");
                        try{
                            JSONArray jsonArray=response.getJSONArray("rows");
                            Log.d(StaticVar.LOG_TAG,String.valueOf(jsonArray.length()));
                            DataSupport.deleteAll(ProductInfo.class);

                            Log.d(StaticVar.LOG_TAG, "保存商品信息开始");

                            String sql = "insert into productinfo";
                            sql+="(ageLevel,asecondaryColor,brandStyle,brandStyleDesc,brdSeason,category,categoryDesc,categorySys,colorNo,composition,compositionDesc,custom1,custom10,custom2,custom3,custom4,custom5,custom6,custom7,custom8,custom9,directoryPage,division,eastLaunch,eastLaunchMonth,gender,genderDesc,goodsDescribe,goodsNo,havetoOrder,havetoOrderDesc,listingMonth,localRp,mainBrandCode,mainColor,marketSupport,marketSupportDesc,marketingDivision,marketingDivisionDesc,modelName,modelNo,modelNoSys,newGoodsId,orderPlanNo,orderPlanY,package_,pop,popDesc,pricePoint,pricePointDesc,remark,repeatListing,seasonStyle,sizeRange,status,suitableSituation,supportStyle,systemSize,technology,technologyDesc,divisionDesc)";
                            sql+="values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                            SQLiteStatement stat = database.compileStatement(sql);
                            database.beginTransaction();
                            try {
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject=jsonArray.getJSONObject(i);

                                    stat.bindString(1, RocTools.getJsonVal(jsonObject,"ageLevel"));
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

                                database.setTransactionSuccessful();
                                handleMsg(PRO_INFO,100);
                            } catch (Exception var6) {
                                Log.e(StaticVar.LOG_TAG, var6.getMessage());
                            } finally {
                                database.endTransaction();
                            }
                        }catch (Exception e){
                            Log.e(StaticVar.LOG_TAG,e.getMessage());
                        }
                        Log.d(StaticVar.LOG_TAG, "保存商品信息结束");

                    }
                }.start();

            }
        });

    }


    /**
     * 同步商品属性
     * @param orderMeetNo
     * @param userId
     */
    private void updateProAttr(String orderMeetNo,String userId){
        handleMsg(PRO_ATTR, 0);
        RequestParams params = new RequestParams();
        params.put("orderMeetNo", orderMeetNo);
        params.put("userId", userId);
        UpdateRestClient.post("proconst",params,new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        Log.d(StaticVar.LOG_TAG, "start");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                        handleMsg(PRO_ATTR, 30);
                        Log.d(StaticVar.LOG_TAG, "保存静态属性开始");
                        new Thread(){
                            @Override
                            public void run() {
                                try{
                                    JSONArray jsonArray=response.getJSONArray("rows");
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
                                    handleMsg(PRO_ATTR, 100);
                                    Log.d(StaticVar.LOG_TAG, "保存静态属性成功");
                                }catch (Exception e){
                                    Log.e(StaticVar.LOG_TAG,e.getMessage());
                                    Log.e(StaticVar.LOG_TAG, "解析静态属性失败");
                                }


                            }
                        }.start();
                    }
                });

    }

    /**
     * 同步商品订量
     * @param orderMeetNo
     * @param userId
     */
    private void updateProOrder(String orderMeetNo,final String userId,final SQLiteDatabase database){
        handleMsg(PRO_ORDER, 0);
        RequestParams params = new RequestParams();
        params.put("orderMeetNo", orderMeetNo);
        params.put("userId", userId);
        UpdateRestClient.post("proOrderInfo",params,new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.d(StaticVar.LOG_TAG, "start");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                handleMsg(PRO_ORDER, 30);
                Log.d(StaticVar.LOG_TAG, "解析订单信息开始");
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Log.d(StaticVar.LOG_TAG, "解析订单信息开始");
                            JSONArray jsonArray=response.getJSONArray("rows");

                            DataSupport.deleteAll(ProductOrderInfo.class,"userid='"+"Z001"+"'");

                            String sql = "insert into ProductOrderInfo";
                            sql+="(userId,newGoodsId,buyerUnitCode,BuyerUnitName,orderQty,minOrderQty)";
                            sql+="values(?,?,?,?,?,?)";

                            SQLiteStatement stat = database.compileStatement(sql);
                            database.beginTransaction();
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

                                database.setTransactionSuccessful();
                            } catch (Exception var6) {
                                throw new DataSupportException(var6.getMessage());
                            } finally {
                                database.endTransaction();
                            }
                        } catch (Exception e) {
                            Log.e(StaticVar.LOG_TAG,e.getMessage());
                            e.printStackTrace();
                        }
                        Log.d(StaticVar.LOG_TAG, "更新订单信息结束");

                        handleMsg(PRO_ORDER, 100);
                    }
                }.start();
            }
        });
    }


    /**
     * 同步商品标签
     * @param orderMeetNo
     * @param userId
     */
    private void updateProComments(String orderMeetNo,String userId,final SQLiteDatabase database){
        handleMsg(PRO_COMMENTS, 0);
        RequestParams params = new RequestParams();
        params.put("orderMeetNo", orderMeetNo);
        params.put("userId", userId);
        UpdateRestClient.post("commentsDownload",params,new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.d(StaticVar.LOG_TAG, "start");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                handleMsg(PRO_COMMENTS, 30);
                Log.d(StaticVar.LOG_TAG, "更新标签资料");
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            JSONArray jsonArray=response.getJSONArray("rows");

                            DataSupport.deleteAll(ProductComments.class);

                            String sql = "insert into ProductComments";
                            sql+="(newGoodsID,userid,commentsDetail,commentsTime,status)";
                            sql+="values(?,?,?,?,0)";

                            SQLiteStatement stat = database.compileStatement(sql);
                            database.beginTransaction();
                            try {
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject=jsonArray.getJSONObject(i);

                                    stat.bindString(1,RocTools.getJsonVal(jsonObject,"newGoodsID"));
                                    stat.bindString(2,RocTools.getJsonVal(jsonObject,"userId"));
                                    stat.bindString(3,RocTools.getJsonVal(jsonObject,"commentsDetail"));
                                    stat.bindString(4,RocTools.getJsonVal(jsonObject,"commentsTime"));

                                    stat.executeInsert();
                                }

                                database.setTransactionSuccessful();
                                Log.d(StaticVar.LOG_TAG, "更新标签资料成功");
                                handleMsg(PRO_COMMENTS, 100);
                            } catch (Exception var6) {
                                Log.e(StaticVar.LOG_TAG,var6.getMessage());
                                throw new DataSupportException(var6.getMessage());
                            } finally {
                                database.endTransaction();
                            }
                        } catch (Exception e) {
                            Log.e("TEST","标签:"+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    /**
     * 同步历史数据
     * @param orderMeetNo
     * @param userId
     */
    private void updateBidata(String orderMeetNo,String userId,final SQLiteDatabase database){
        handleMsg(PRO_BIDATA, 0);
        RequestParams params = new RequestParams();
        params.put("orderMeetNo", orderMeetNo);
        params.put("userId", userId);
        UpdateRestClient.post("proHisTrend",params,new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.d(StaticVar.LOG_TAG, "start");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                handleMsg(PRO_BIDATA, 30);
                Log.d(StaticVar.LOG_TAG, "更新历史数据");


                try {
                    JSONArray jsonArray=response.getJSONArray("rows");
                    DataSupport.deleteAll(ProductHisTrend.class);

                    String sql = "insert into ProductHisTrend";
                    sql+="(newGoodsId,goodsNo,modelName,brdSeason, ";
                    sql+="totalSalQty1,totalSalAmt1,totalSalNosPrmAmt1,totalSalPrmAmt1,invQty1,invAmt1, ";
                    sql+="totalSalQty2,totalSalAmt2,totalSalNosPrmAmt2,totalSalPrmAmt2,invQty2,invAmt2, ";
                    sql+="totalSalQty3,totalSalAmt3,totalSalNosPrmAmt3,totalSalPrmAmt3,invQty3,invAmt3, ";
                    sql+="distrOrgNum,salOrgNum,orderNum, ";
                    sql+="buyerUnitId,buyerUnitNm,regionId,regionNm,provId,provNm,mgmtCityId,mgmtCityNm )";
                    sql+="values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                    SQLiteStatement stat = database.compileStatement(sql);
                    database.beginTransaction();
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

                        database.setTransactionSuccessful();
                    } catch (Exception var6) {
                        throw new DataSupportException(var6.getMessage());
                    } finally {
                        database.endTransaction();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(StaticVar.LOG_TAG, "解析BI数据失败");
                }
                Log.d(StaticVar.LOG_TAG, "请求BI历史数据结束");
                handleMsg(PRO_BIDATA,100);
            }
        });
    }

    /**
     * 同步商品对照
     * @param orderMeetNo
     * @param userId
     */
    private void updateProCompare(String orderMeetNo,String userId,final SQLiteDatabase database){
        handleMsg(PRO_COMPARE, 0);
        RequestParams params = new RequestParams();
        params.put("orderMeetNo", orderMeetNo);
        params.put("userId", userId);
        UpdateRestClient.post("proOldCompare",params,new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.d(StaticVar.LOG_TAG, "start");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                handleMsg(PRO_COMPARE, 30);
                Log.d(StaticVar.LOG_TAG, "更新商品对应开始");

                new Thread(){
                    @Override
                    public void run() {
                        try {
                            JSONArray jsonArray=response.getJSONArray("rows");
                            DataSupport.deleteAll(ProductOldCompare.class);

                            String sql = "insert into ProductOldCompare";
                            sql+="(proId,brdSeason,proIdOld,proNmOld,priceBandNm,proStyleNm,proCateNm,proGenderNm,brdSeasonOld) ";
                            sql+="values(?,?,?,?,?,?,?,?,?)";

                            SQLiteStatement stat = database.compileStatement(sql);
                            database.beginTransaction();
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

                                database.setTransactionSuccessful();
                                handleMsg(PRO_COMPARE, 100);
                            } catch (Exception var6) {
                                throw new DataSupportException(var6.getMessage());
                            } finally {
                                database.endTransaction();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(StaticVar.LOG_TAG, "请求历史商品对应关系失败");
                        }
                        Log.d(StaticVar.LOG_TAG, "请求历史商品对应关系结束");
                    }
                }.start();
            }
        });
    }

    /**
     * 同步商品计划
     * @param orderMeetNo
     * @param userId
     */
    private void updateProPlan(String orderMeetNo,String userId,final SQLiteDatabase database){
        handleMsg(PRO_PLAN, 0);
        RequestParams params = new RequestParams();
        params.put("orderMeetNo", orderMeetNo);
        params.put("userId", userId);
        UpdateRestClient.post("proPlanInfo",params,new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.d(StaticVar.LOG_TAG, "start");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                handleMsg(PRO_PLAN, 30);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            JSONArray jsonArray=response.getJSONArray("rows");

                            DataSupport.deleteAll(ProductPlanInfo.class);

                            String sql = "insert into ProductPlanInfo";
                            sql+="(orderPlanNo,buyerUnitCode,buyerUnitName,proSeriesId,proSeriesNm,proCateId,proCateNm,proGenderId,proGenderNm,inSku,inOrdSal,inOrdQty)";
                            sql+="values(?,?,?,?,?,?,?,?,?,?,?,?)";

                            SQLiteStatement stat = database.compileStatement(sql);
                            database.beginTransaction();
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

                                database.setTransactionSuccessful();
                                handleMsg(PRO_PLAN, 100);
                            } catch (Exception var6) {
                                throw new DataSupportException(var6.getMessage());
                            } finally {
                                database.endTransaction();
                            }
                        } catch (Exception e) {
                            Log.d("TEST", "更新计划数据失败");
                            Log.e("TEST",e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    /**
     * 同步最小起订量
     * @param orderMeetNo
     * @param userId
     */
    private void updateProMinNum(String orderMeetNo,String userId,final SQLiteDatabase database){
        handleMsg(PRO_MIN, 0);
        RequestParams params = new RequestParams();
        params.put("orderMeetNo", orderMeetNo);
        params.put("userId", userId);
        UpdateRestClient.post("proMinOrderNum",params,new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.d(StaticVar.LOG_TAG, "start");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                handleMsg(PRO_MIN, 30);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            JSONArray jsonArray=response.getJSONArray("rows");

                            DataSupport.deleteAll(ProductMinOrderNum.class);

                            String sql = "insert into productMinOrderNum";
                            sql+="(orderPlanNo,goodsNo,buyerUnitCode,buyerUnitName,minNum)";
                            sql+="values(?,?,?,?,?)";

                            SQLiteStatement stat = database.compileStatement(sql);
                            database.beginTransaction();
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

                                database.setTransactionSuccessful();
                                handleMsg(PRO_MIN, 100);
                            } catch (Exception var6) {
                                throw new DataSupportException(var6.getMessage());
                            } finally {
                                database.endTransaction();
                            }

                        } catch (Exception e) {
                            Log.e(StaticVar.LOG_TAG, "解析最小起订量数据失败");
                            e.printStackTrace();
                        }
                        Log.d(StaticVar.LOG_TAG, "更新最小起订量数据结束");
                    }
                }.start();
            }
        });
    }


    public void handleMsg(int what,int percent){
        Message msg=new Message();
        msg.what=what;
        msg.obj=percent;
        handler.sendMessage(msg);
    }
}
