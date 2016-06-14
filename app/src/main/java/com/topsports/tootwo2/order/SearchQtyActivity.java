package com.topsports.tootwo2.order;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.topsports.tootwo2.AsyncTasks.ImgSyncTask;
import com.topsports.tootwo2.AsyncTasks.uploadProOrderTask;
import com.topsports.tootwo2.app.OrderApplication;
import com.topsports.tootwo2.helper.RocTools;
import com.topsports.tootwo2.helper.StaticVar;
import com.topsports.tootwo2.helper.StatusCheckTools;
import com.topsports.tootwo2.helper.VolleySingleton;
import com.topsports.tootwo2.model.ProConstType;
import com.topsports.tootwo2.model.ProductBIData;
import com.topsports.tootwo2.model.ProductInfo;
import com.topsports.tootwo2.model.ProductMinOrderNum;
import com.topsports.tootwo2.model.ProductOrderInfo;
import com.topsports.tootwo2.model.ProductPic;
import com.topsports.tootwo2.model.ProductPlanInfo;
import com.topsports.tootwo2.service.UpdateIntentService;
import com.topsports.tootwo2.widget.autolistview.adapter.OrderSearchQtyAdapter;
import com.topsports.tootwo2.widget.autolistview.widget.AutoListView;
import com.topsports.tootwo2.widget.checkableexpandablelistview.Child;
import com.topsports.tootwo2.widget.checkableexpandablelistview.EListAdapter;
import com.topsports.tootwo2.widget.checkableexpandablelistview.Group;
import com.umeng.message.UmengRegistrar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.ClusterQuery;
import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;
import org.litepal.tablemanager.Connector;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * 下量主页面
 */
public class SearchQtyActivity extends AppCompatActivity implements AutoListView.OnRefreshListener,
        AutoListView.OnLoadListener {
    public final static String EXTRA_MESSAGE = "com.topsports.order.orderplan.SearchActivity.MESSAGE";
    public final static String EXTRA_MESSAGE_PROID="com.topsports.order.orderplan.SearchActivity.MESSAGE.PROID";

    private String urlBase= StaticVar.URL_BASE;;
//    private String urlBase="http://192.168.9.174/ordermeet/app/";

    private String proConstUrl=urlBase+"proconst";
    private String proInfoUrl=urlBase+"proinfo";
    private String proPicUrl=StaticVar.URL_PIC;
    private String upLoadImg=urlBase+"upLoadImg";


    //筛选按钮
    private TextView searchFilterTv;
    //商品列表
    private AutoListView listView;
    //数据
    private List<ProductInfo> list = new ArrayList<ProductInfo>();
    //适配器
    private BaseAdapter adapter;

    OrderApplication orderApplication;

    //筛选条件弹出框
    private PopupWindow mPopupWindow;
    //货号查询的文本框
    private SearchView mSearchView;
    //排序下拉框
    private Spinner orderSpinner;
    //是否预订下拉框
    private Spinner isOrderedSpinner;

    //SKU
    private TextView skuNumTextView;

    //下载等待
    private ProgressDialog progressDialog;

    //页码查询使用
    private EditText pageEditText;

    //提示框
    private TextView hintTv;

    //page页数
    private int pageId=0;
    //page大小
    private int pageSize=10;

    private int progressDialogCount;

    // handler接收到消息后就会执行此方法
    Handler handlerProgressDialog = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressDialogCount--;
            if(progressDialogCount<=0){
                progressDialog.dismiss();// 关闭ProgressDialog
                loadData(AutoListView.REFRESH);
                progressDialogCount=0;
            }
        }
    };

    //刷新（追加）数据
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            List<ProductInfo> result = (List<ProductInfo>) msg.obj;
            switch (msg.what) {
                case AutoListView.REFRESH:
                    pageId=0;
                    listView.onRefreshComplete();
                    list.clear();
                    list.addAll(result);

                    break;
                case AutoListView.LOAD:
                    listView.onLoadComplete();

                    list.addAll(result);
                    break;
            }
            listView.setResultSize(result.size());
            adapter.notifyDataSetChanged();
            if(msg.what==AutoListView.REFRESH){
                listView.setSelection(0);
            }
        };
    };

    private Handler handlerHint=new Handler(){
        public void handleMessage(Message msg){
            changeHintTv();
        }
    };


    private MyBroadCastReceiver broadCastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderApplication=(OrderApplication)getApplication();

        setContentView(R.layout.activity_search_qty);

        //注册广播
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.topsports.order_qtychange");
        broadCastReceiver=new MyBroadCastReceiver();
        registerReceiver(broadCastReceiver,intentFilter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        initActionBar();
        initView();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        List<ProductInfo> productInfos=DataSupport.where("1=1").find(ProductInfo.class);
//        Log.d(StaticVar.LOG_TAG,String.valueOf(productInfos.size()));
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(list==null||list.size()==0){
            if(!progressDialog.isShowing()){
//                new UpdateProinfoTask().execute();
            }
        }
    }

    /**
     * 初始化工具栏
     */
    private void initActionBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.title_toolbar);//mainBrandCode
        TextView orderInfoTv = (TextView) findViewById(R.id.orderInfo);

        String areaName=orderApplication.getAreaName()==null?"":orderApplication.getAreaName();
        String userName=orderApplication.getName()==null?"":orderApplication.getName();
        String orderPlanNo=orderApplication.getOrderMeetingNo()==null?"":orderApplication.getOrderMeetingNo();

        orderInfoTv.setText(orderPlanNo+" | AD | " + areaName + " | " + userName);

        toolbar.setTitle("");

        setSupportActionBar(toolbar);

    }

    /**
     * 初始化视图
     */
    public void initView(){
        //设置iconfont
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/iconfont.ttf");
        searchFilterTv = (TextView)findViewById(R.id.search_filter);


        skuNumTextView=(TextView)findViewById(R.id.skuNum);
        //初始化筛选条件
        initGroups();

        //初始化搜索框
        mSearchView=(SearchView)findViewById(R.id.search_view);
        initSearchView();


        hintTv=(TextView)findViewById(R.id.header_text);
        changeHintTv();

        //初始化商品列表listview
        listView=(AutoListView)findViewById(R.id.search_list_View);
        initListview();

        //排序下拉框
        orderSpinner=(Spinner)findViewById(R.id.search_orderby);
        initOrderSpinner();

        //是否预订
        isOrderedSpinner=(Spinner)findViewById(R.id.search_isordered);
        initIsOrderedSpinner();


    }

    /**
     * 初始化搜索框
     */
    private void initSearchView(){



        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                pageId = 0;
                Message msg = handler.obtainMessage();
                msg.what = AutoListView.REFRESH;
                msg.obj = getData();

                handler.sendMessage(msg);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    pageId = 0;
                    Message msg = handler.obtainMessage();
                    msg.what = AutoListView.REFRESH;
                    msg.obj = getData();

                    handler.sendMessage(msg);
                    return true;
                }
                return false;
            }
        });

    }

    /**
     * 初始化商品列表
     */
    private void initListview(){
        list=getData();
        adapter=new OrderSearchQtyAdapter(this,list,R.layout.adapter_search_qty,orderApplication.getUserId()){
            public void customImgAction(View v, int position) {
                try{
//                    adapter.cancelAllTasks();
                    Intent intent=new Intent(SearchQtyActivity.this,DetailQtyActivity.class);
                    intent.putExtra(EXTRA_MESSAGE_PROID,list.get(position).getGoodsNo());
                    intent.putExtra(EXTRA_MESSAGE, list.get(position).getModelNo());
                    startActivityForResult(intent,100);
                }catch (Exception e){
                    Log.e("TEST", e.getMessage());
                }
            }
            public void customRadioAction(String level, int position) {

                String proid=list.get(position).getGoodsNo();
                list.get(position).setOrderLevel(level);
                ProductInfo productInfo= DataSupport.where("goodsNo=?", proid).find(ProductInfo.class).get(0);
                ProductInfo productInfo2=new ProductInfo();
                productInfo2.setOrderLevel(level);
                productInfo2.update(productInfo.getId());

            }
        };
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(this);
        listView.setOnLoadListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==100 && resultCode==100){
            adapter.notifyDataSetChanged();
        }else if(requestCode==200&&resultCode==200){
            pageId = 0;
            loadData(AutoListView.REFRESH);
        }
    }

    /**
     * 初始化订货量排序下拉条件
     */
    private void initOrderSpinner(){
        ArrayList<String> orderbyList=new ArrayList<>();
        orderbyList.add("默认排序");
        orderbyList.add("按定量升序");
        orderbyList.add("按定量降序");

        ArrayAdapter<String> orderByAdapter=new ArrayAdapter<String>(this,R.layout.custom_spinner,orderbyList);
        orderSpinner.setAdapter(orderByAdapter);
        orderSpinner.setSelection(0,true);
        //orderSpinner.setPrompt("默认排序");
        orderSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {
                        pageId = 0;
                        loadData(AutoListView.REFRESH);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        pageId = 0;
                        loadData(AutoListView.REFRESH);
                    }
                });

    }


    /**
     * 改变计划剩余提示框内容
     */
    private void changeHintTv(){
        String condStr="1=1 ";
        String condStr2=" ";
        if(groups!=null&&groups.size()>1) {

            //大类
            String cates = getStringFromArray(groups.get(0).getChildren());
            if (!cates.equals("")) {
                condStr += "and proCateId in (" + cates + ") ";
                condStr2+="and division in (" + cates + ") ";
            }

            //系列
            String marketingDivisions = getStringFromArray(groups.get(1).getChildren());
            if (!marketingDivisions.equals("")) {
                condStr += "and proSeriesId in (" + marketingDivisions + ") ";
                condStr2 += "and marketingDivision in (" + marketingDivisions + ") ";
            }

            //性别
            String genders = getStringFromArray(groups.get(2).getChildren());
            if (!genders.equals("")) {
                condStr += "and proGenderId in (" + genders + ") ";
                condStr2 += "and gender in (" + genders + ") ";
            }
        }

        String queryStr="select t1.buyerUnitCode,t1.buyerUnitName,t1.inOrdQty,t1.inOrdSal,t2.orderQty,t2.orderAmt ";
        queryStr+="from(select buyerUnitCode,buyerUnitName,sum(inOrdQty) inOrdQty,sum(inOrdSal) inOrdSal from productPlanInfo where "+condStr+" group by buyerUnitCode,buyerUnitName) t1,";
        queryStr+="(select ProductOrderInfo.buyerUnitCode,ProductOrderInfo.buyerUnitName,sum(orderQty) orderQty,sum(orderQty*localRp) orderAmt from ProductOrderInfo,ProductInfo where ProductOrderInfo.newGoodsId=ProductInfo.newGoodsId "+condStr2+" and ProductOrderInfo.userid='"+orderApplication.getUserId()+"' group by ProductOrderInfo.buyerUnitCode,ProductOrderInfo.buyerUnitName) t2";
        queryStr+=" where t1.buyerUnitCode=t2.buyerUnitCode";

        Cursor cursor= DataSupport.findBySQL(queryStr);


        hintTv.setText("");

        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        {
            String text="";
            text+=cursor.getString(1)+"  ";


            Double inOrdSal=cursor.getDouble(3);
            Double orderAmt=cursor.getDouble(5);
            text+=(new BigDecimal(orderAmt)).toPlainString()+"/"+new BigDecimal(Math.round(inOrdSal)).toPlainString();
            text+="       ";

            SpannableString spanttt = new SpannableString(text);
            if(inOrdSal<orderAmt){
                spanttt.setSpan(new ForegroundColorSpan(Color.rgb(253,78,64)), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            hintTv.append(spanttt);
        }

    }

    /**
     * 初始化是否预订下拉条件
     */
    private void initIsOrderedSpinner(){
        ArrayList<String> isOrderedList=new ArrayList<>();
        isOrderedList.add("全部");
        isOrderedList.add("已订");
        isOrderedList.add("未订");

        ArrayAdapter<String> isOrderedAdapter=new ArrayAdapter<String>(this,R.layout.custom_spinner,isOrderedList);
        isOrderedSpinner.setAdapter(isOrderedAdapter);
        isOrderedSpinner.setSelection(0,true);
        isOrderedSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {
                        pageId = 0;
                        loadData(AutoListView.REFRESH);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        pageId = 0;
                        loadData(AutoListView.REFRESH);
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_qty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Context context=getApplicationContext();
        String userId=orderApplication.getUserId();
        if(!StatusCheckTools.isNetworkConnected(this)){
            Toast.makeText(this,"无网络可用网络连接",Toast.LENGTH_LONG).show();
            return true;
        }
        if(id==R.id.action_download){
//            new UpdateProinfoTask().execute();
//            progressDialog.setTitle("更新基础数据中");
//            progressDialog.setMessage("提交订量信息");
//            progressDialog.show();
//            UpdateIntentService.startActionUpdateBase(context, userId, orderApplication.getOrderMeetingNo());
            Intent intent=new Intent(SearchQtyActivity.this,UpdateActivity.class);
            startActivity(intent);
            this.finish();

            return true;
        }else if(id==R.id.action_downloadPic){
            new ImgSyncTask(progressDialog,orderApplication.getOrderMeetingNo(),getApplicationContext()).execute();
            return true;
        }else if(id==R.id.action_submit){
            progressDialog.setTitle("提交数据中");
            progressDialog.setMessage("提交订量信息");
            progressDialog.show();
            UpdateIntentService.startActionUploadOrderQty(context, userId, orderApplication.getOrderMeetingNo());
            return true;
        }else if(id==R.id.action_logout){

            AlertDialog.Builder builder = new AlertDialog.Builder(SearchQtyActivity.this);
            builder.setTitle("你确定要注销吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    layout();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //这里添加点击确定后的逻辑
                    //showDialog("你选择了取消");
                }
            });
            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onRestart(){
        super.onRestart();

    }



    /**
     * 清空SharedPreferences和Application，跳转到登陆页面
     */
    private void clearAndGotoLogin(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor e = pref.edit();
        e.putString("userid", "");
        e.putString("areaName", "");
        e.putString("isElite", "");
        e.putString("name", "");
        e.putString("buyerUnitsStr", "");
        e.putString("orderMeetingNo", "");
        e.commit();

        orderApplication.setUserId("");
        orderApplication.setName("");
        orderApplication.setRole("");
        orderApplication.setAreaName("");

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }


    /*
  * 定义下拉刷新接口
  */
    public interface OnRefreshListener {
        public void onRefresh();
    }

    /*
     * 定义加载更多接口
     */
    public interface OnLoadListener {
        public void onLoad();
    }



    @Override
    public void onLoad() {
        pageId++;
        loadData(AutoListView.LOAD);
    }
    public void onRefresh() {
        pageId=0;
        loadData(AutoListView.REFRESH);
    }

    //加载数据
    private void loadData(final int what) {
        Message msg = handler.obtainMessage();
        msg.what = what;
        msg.obj = getData();

        handler.sendMessage(msg);
    }


    public void query(View view){
        loadData(AutoListView.REFRESH);
        mPopupWindow.dismiss();
    }




    //加载数据
    private List<ProductInfo> getData(){

        ClusterQuery clusterQuery=DataSupport.where("1=1");
        String queryStr="1=1 ";

        //货号查询
        String proid=mSearchView.getQuery().toString().toUpperCase();
        if((!proid.equals(""))&&(proid!=null)){
//            clusterQuery=clusterQuery.where("goodsNo like ?", "%" + proid + "%");
            queryStr+="and (goodsNo like '%" + proid + "%' or modelNo like '%"+proid+"%' or modelName like '%"+proid+"%') ";
        }

        String ordered="";
        //是否已订
        if(isOrderedSpinner!=null){
            int isOrdered=isOrderedSpinner.getSelectedItemPosition();
            switch (isOrdered){
                case 0:
                    break;
                case 1:
                    ordered+=" having sum(orderQty)>0 ";
                    break;
                case 2:
                    ordered+=" having sum(orderQty)=0 ";
                    break;
                default:
                    break;
            }
        }

        //页码
        if(pageEditText!=null){
            if(pageEditText.getText()!=null&&!pageEditText.getText().toString().equals("")){
                queryPageNum=Integer.valueOf(pageEditText.getText().toString());
                queryStr+="and directoryPage='"+pageEditText.getText()+"' ";
            }
        }else{
            queryPageNum=0;
        }

        if(groups!=null&&groups.size()>1){
            //筛选条件计数标识
            int filterCount=0;
            //大类
            String cates=getStringFromArray(groups.get(0).getChildren());
            if(!cates.equals("")){
                filterCount++;
                queryStr+="and Division in ("+cates+") ";
            }

            //款式
//            String categorys=getStringFromArray(groups.get(1).getChildren());
//            if(!categorys.equals("")){
//                filterCount++;
//                queryStr+="and category in ("+categorys+") ";
//            }

            //系列
            String marketingDivisions=getStringFromArray(groups.get(1).getChildren());
            if(!marketingDivisions.equals("")){
                filterCount++;
                queryStr+="and marketingDivision in ("+marketingDivisions+") ";
            }

            //性别
            String genders=getStringFromArray(groups.get(2).getChildren());
            if(!genders.equals("")){
                filterCount++;
                queryStr+="and gender in ("+genders+") ";
            }

            //POP
            String popStr=getStringFromArray(groups.get(3).getChildren());
            if(!popStr.equals("")){
                filterCount++;
                queryStr+="and pop in ("+popStr+") ";
            }

            //上市月
            String eastMonthStr=getStringFromArray(groups.get(4).getChildren());
            if(!eastMonthStr.equals("")){
                filterCount++;
                queryStr+="and eastLaunchMonth in ("+eastMonthStr+") ";
            }

            //分配必定
            String mustStr=getStringFromArray(groups.get(5).getChildren());
            if(!mustStr.equals("")){
                filterCount++;
                queryStr+="and havetoOrder in ("+mustStr+") ";
            }

            //品牌季节
            String seasonStr=getStringFromArray(groups.get(6).getChildren());
            if(!seasonStr.equals("")){
                filterCount++;
                queryStr+="and brdSeason in ("+seasonStr+") ";
            }
            if(filterCount>0||queryPageNum!=0){
                searchFilterTv.setTextColor(Color.rgb(255, 155, 89));
            }else{
                searchFilterTv.setTextColor(Color.rgb(255,255,255));
            }

            //价格段
            String priceBrandStr=getStringFromArray(groups.get(7).getChildren());
            if(!priceBrandStr.equals("")){
                filterCount++;
                queryStr+="and pricePoint in ("+priceBrandStr+")";
            }

        }


        //排序
        String orderStr="";
        if(orderSpinner!=null){
            int orderPosition=orderSpinner.getSelectedItemPosition();

            //if(orderPosition!=0){queryStr+="and (orderLevel!='X' or orderLevel is null)  ";}
            switch (orderPosition){
                case 0:
                    break;
                case 1:
                    orderStr=" order by sum(orderQty) asc ";
                    break;
                case 2:
                    orderStr=" order by sum(orderQty) desc ";
                    break;
                default:
                    break;
            }
        }
        String sql="select  id,goodsNo,newgoodsid,modelNo,modelName,supportStyle,eastLaunch,custom4,localRp,directoryPage   ";
            sql+="from (select t1.id,goodsNo,t1.newGoodsId,modelNo,modelName,supportStyle,eastLaunch,custom4,localRp,directoryPage ";
            sql+="from ProductInfo t1,ProductOrderInfo t2 ";
            sql+="where t1.newGoodsID=t2.newGoodsID and t2.userId='"+orderApplication.getUserId()+"'  and "+queryStr;
            sql+=" group by t1.id,goodsNo,t1.newGoodsId,modelNo,modelName,supportStyle,eastLaunch,custom4,localRp,directoryPage ";
            sql+=ordered;
            sql+=orderStr;
            sql+=") limit "+String.valueOf(pageId*pageSize)+","+String.valueOf(pageSize);
        Cursor proCursor=DataSupport.findBySQL(sql);
        List<ProductInfo> productInfoList=new ArrayList<>();
        while (proCursor.moveToNext()){
            ProductInfo productInfo=new ProductInfo();
            productInfo.setId(proCursor.getInt(0));
            productInfo.setGoodsNo(proCursor.getString(proCursor.getColumnIndex("goodsNo")));
            productInfo.setNewGoodsId(proCursor.getString(proCursor.getColumnIndex("newgoodsid")));
            productInfo.setModelNo(proCursor.getString(proCursor.getColumnIndex("modelNo")));
            productInfo.setModelName(proCursor.getString(proCursor.getColumnIndex("modelName")));
            productInfo.setSupportStyle(proCursor.getString(proCursor.getColumnIndex("supportStyle")));
            productInfo.setEastLaunch(proCursor.getString(proCursor.getColumnIndex("eastLaunch")));
            productInfo.setCustom4(proCursor.getString(proCursor.getColumnIndex("custom4")));
            productInfo.setLocalRp(proCursor.getString(proCursor.getColumnIndex("localRp")));
            productInfo.setDirectoryPage(proCursor.getString(proCursor.getColumnIndex("directoryPage")));

            productInfoList.add(productInfo);
        }

//        List<ProductInfo> productInfoList=clusterQuery.where(queryStr).limit(pageSize).offset(pageId*pageSize).find(ProductInfo.class);

        String countSql="select  count(*) ct   ";
        countSql+="from (select t1.id,goodsNo,t1.newGoodsId,modelNo,modelName,supportStyle,eastLaunch,custom4,localRp,directoryPage ";
        countSql+="from ProductInfo t1,ProductOrderInfo t2 ";
        countSql+="where t1.newGoodsID=t2.newGoodsID and t2.userId='"+orderApplication.getUserId()+"'  and "+queryStr;
        countSql+=" group by t1.id,goodsNo,t1.newGoodsId,modelNo,modelName,supportStyle,eastLaunch,custom4,localRp,directoryPage ";
        countSql+=ordered;
        countSql+=orderStr;
        countSql+=") ";
        Cursor cursor= DataSupport.findBySQL(countSql);
        String count;
        if(cursor.moveToFirst()==true){
            count=cursor.getString(cursor.getColumnIndex("ct"));
            skuNumTextView.setText("SKU:" + count);
        }

        changeHintTv();
        return productInfoList;
    }

    /**
     * 获取筛选条件字符串参数
     * @param children
     * @return
     */
    private String getStringFromArray(ArrayList<Child> children){
        String str="";
        for(int i=0;i<children.size();i++){
            Child child=children.get(i);
            if(child.getChecked()==true){
                str+="'"+child.getUserid()+"',";
            }
        }
        if(str.length()==0){return "";}
        return str.substring(0,str.length()-1);
    }




    /**
     * 上传订货量(注销的时候)
     */
    public void layout(){
        clearAndGotoLogin();

    }


    /**
     * 筛选页面 页码
     */
    private int queryPageNum=0;
    /**
     * 显示筛选条件框(按钮触发)
     * @param view
     */
    public void showFilter(View view){
//        String device_token = UmengRegistrar.getRegistrationId(getApplicationContext());
//        Log.d("TEST","device_token:"+device_token);
        View popupView = getLayoutInflater().inflate(R.layout.pop_selection, null);

        mPopupWindow = new PopupWindow(popupView, 500, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.setAnimationStyle(R.style.anim_menu_rightsidebar);

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
                loadData(AutoListView.REFRESH);
            }
        });


        mPopupWindow.showAtLocation(getCurrentFocus(), Gravity.RIGHT, 0, 0);

        final ExpandableListView expandableListView=(ExpandableListView)popupView.findViewById(R.id.expand_activities_button);
        //setDetailData(popupView, getDetailData(postion));

        if(groups==null||groups.size()==0){
            initGroups();
        }

        adapter2 = new EListAdapter(this, groups);
        expandableListView.setAdapter(adapter2);
        expandableListView.setOnChildClickListener(adapter2);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < adapter2.getGroupCount(); i++) {
                    if (groupPosition != i) {
                        expandableListView.collapseGroup(i);
                    }
                }
            }
        });
        //页码
        pageEditText=(EditText)popupView.findViewById(R.id.pageNum);
        pageEditText.setText(queryPageNum==0?"":String.valueOf(queryPageNum));
    }

    /**
     * 清空筛选添加(按钮触发)
     * @param view
     */
    public void clearSelection(View view){
        for(int i=0;i<groups.size();i++){
            for(int j=0;j<groups.get(i).getChildrenCount();j++){
                Child child=groups.get(i).getChildItem(j);
                child.setChecked(false);
            }
        }
        adapter2.notifyDataSetChanged();

        pageEditText.setText("");
        queryPageNum=0;
    }

    public void rollBack(View view){
        listView.setSelection(0);
    }

    ArrayList<Group> groups=new ArrayList<Group>();
    EListAdapter adapter2;

    /**
     * 获取筛选条件组
     */
    private void initGroups() {
        groups.clear();
        //大类
        List<ProConstType> cateTypeList= DataSupport.where("ClassType=?", "0000000041").find(ProConstType.class);
        Group cateGroup = new Group("0000000041","大类");
        for(int i=0;i<cateTypeList.size();i++){
            Child child = new Child(cateTypeList.get(i).getClassKey(),cateTypeList.get(i).getClassValue(), cateTypeList.get(i).getCustom_Resource());
            cateGroup.addChildrenItem(child);
        }
        groups.add(cateGroup);
        //款式
//        List<ProConstType> styleList= DataSupport.where("ClassType=?", "0000000043").find(ProConstType.class);
//        Group styleGroup = new Group("0000000043","款式");
//        for(int i=0;i<styleList.size();i++){
//            Child child = new Child(styleList.get(i).getClassKey(),styleList.get(i).getClassValue(), styleList.get(i).getClassValue());
//            styleGroup.addChildrenItem(child);
//        }
//        groups.add(styleGroup);

        //系列
        List<ProConstType> seriesList= DataSupport.where("ClassType=?", "0000000044").find(ProConstType.class);
        Group seriesGroup = new Group("0000000044","系列");
        for(int i=0;i<seriesList.size();i++){
            Child child = new Child(seriesList.get(i).getClassKey(),seriesList.get(i).getClassValue(), seriesList.get(i).getCustom_Resource());
            seriesGroup.addChildrenItem(child);
        }
        groups.add(seriesGroup);

        //性别
        List<ProConstType> genderList= DataSupport.where("ClassType=?", "0000000042").find(ProConstType.class);
        Group genderGroup = new Group("0000000042","性别");
        for(int i=0;i<genderList.size();i++){
            Child child = new Child(genderList.get(i).getClassKey(),genderList.get(i).getClassValue(), genderList.get(i).getCustom_Resource());
            genderGroup.addChildrenItem(child);
        }
        groups.add(genderGroup);

        //POP
        List<ProConstType> popList= DataSupport.where("ClassType=?", "0000000054").find(ProConstType.class);
        Group popGroup = new Group("0000000054","POP");
        for(int i=0;i<popList.size();i++){
            Child child = new Child(popList.get(i).getClassKey(),popList.get(i).getClassValue(), popList.get(i).getCustom_Resource());
            popGroup.addChildrenItem(child);
        }
        groups.add(popGroup);

        //上市月
        List<ProConstType> monthList= DataSupport.where("ClassType=?", "0000000099").find(ProConstType.class);
        Group monthGroup = new Group("0000000099","上市月");
        for(int i=0;i<monthList.size();i++){
            Child child = new Child(monthList.get(i).getClassKey(),monthList.get(i).getClassValue(), monthList.get(i).getCustom_Resource());
            monthGroup.addChildrenItem(child);
        }
        groups.add(monthGroup);

        //分配必定
        List<ProConstType> mustList= DataSupport.where("ClassType=?", "0000000050").find(ProConstType.class);
        Group mustGroup = new Group("0000000050","分配必定");
        for(int i=0;i<mustList.size();i++){
            Child child = new Child(mustList.get(i).getClassKey(),mustList.get(i).getClassValue(), mustList.get(i).getCustom_Resource());
            mustGroup.addChildrenItem(child);
        }
        groups.add(mustGroup);

        //品牌季节
        Group seasonGroup = new Group("0000000999","品牌季节");
        Child child2 = new Child("16Q3","16Q3","");
        seasonGroup.addChildrenItem(child2);
        child2 = new Child("16Q4","16Q4","");
        seasonGroup.addChildrenItem(child2);
        groups.add(seasonGroup);

        //价格段
        List<ProConstType> priceBrands= DataSupport.where("ClassType=?", "0000000052").find(ProConstType.class);
        Group priceGroup = new Group("0000000052","价格段");
        for(int i=0;i<priceBrands.size();i++){
            Child child = new Child(priceBrands.get(i).getClassKey(),priceBrands.get(i).getClassValue(), priceBrands.get(i).getCustom_Resource());
            priceGroup.addChildrenItem(child);
        }
        groups.add(priceGroup);

    }





    class MyBroadCastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent){
            Bundle bundle=intent.getExtras();
            if(bundle!=null){
                String message=bundle.getString("message");
                if(message.equals("finished")){
                    initGroups();
                    loadData(AutoListView.REFRESH);
                    progressDialog.dismiss();
                }else if(message.equals("failed")){
                    progressDialog.dismiss();

                }else {
                    progressDialog.setMessage(message);
                }

            }else{
                handlerHint.sendMessage(new Message());
            }

        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadCastReceiver);
    }



}
