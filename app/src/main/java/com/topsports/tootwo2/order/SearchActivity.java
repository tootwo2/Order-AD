package com.topsports.tootwo2.order;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.topsports.tootwo2.AsyncTasks.uploadProOrderTask;
import com.topsports.tootwo2.app.OrderApplication;
import com.topsports.tootwo2.helper.RocTools;
import com.topsports.tootwo2.helper.VolleySingleton;
import com.topsports.tootwo2.model.ProConstType;
import com.topsports.tootwo2.model.ProductBIData;
import com.topsports.tootwo2.model.ProductInfo;
import com.topsports.tootwo2.model.ProductOrderInfo;
import com.topsports.tootwo2.model.ProductPic;
import com.topsports.tootwo2.widget.autolistview.adapter.OrderSearchAdapter;
import com.topsports.tootwo2.widget.autolistview.adapter.OrderSearchQtyAdapter;
import com.topsports.tootwo2.widget.autolistview.widget.AutoListView;
import com.topsports.tootwo2.widget.checkableexpandablelistview.Child;
import com.topsports.tootwo2.widget.checkableexpandablelistview.EListAdapter;
import com.topsports.tootwo2.widget.checkableexpandablelistview.Group;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.ClusterQuery;
import org.litepal.crud.DataSupport;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 订货查询界面
 */

public class SearchActivity extends AppCompatActivity implements AutoListView.OnRefreshListener,
        AutoListView.OnLoadListener{

    public final static String EXTRA_MESSAGE = "com.topsports.order.orderplan.SearchActivity.MESSAGE";

    private String urlBase="http://192.168.4.217/ordermeet/app/";
//    private String urlBase="http://192.168.9.212/ordermeet/app/app/";

    private String proConstUrl=urlBase+"proconst";
    private String proInfoUrl=urlBase+"proinfo";
    private String proPicUrl=urlBase+"downloadPic?fileName=";
    private String upLoadImg=urlBase+"upLoadImg";
    private String procommitUrl=urlBase+"procommit";
    private String commentscommitUrl=urlBase+"commentscommit";
    private String proBIUrl=urlBase+"proBI";
    private String checkUpdate = urlBase + "checkUpdate";
    private String updateAPP = urlBase + "updateAPP";


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
    //订货量
    private Spinner orderLevelSpinner;
    //SKU
    private TextView skuNumTextView;

    //下载等待
    private ProgressDialog progressDialog;

    //页码查询使用
    private EditText pageEditText;

    //page页数
    private int pageId=0;
    //page大小
    private int pageSize=8;

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
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderApplication=(OrderApplication)getApplication();

        setContentView(R.layout.activity_search);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        initActionBar();
        initView();

//        DataSupport.deleteAll(ProductOrderInfo.class);
//        for(int i=0;i<list.size();i++){
//            ProductOrderInfo productOrderInfo=new ProductOrderInfo();
//            productOrderInfo.setUserId("Z001");
//            productOrderInfo.setBuyerUnitCode("M001");
//            productOrderInfo.setBuyerUnitName("上海");
//            productOrderInfo.setNewGoodsId(list.get(i).getNewGoodsId());
//            productOrderInfo.save();
//
//            productOrderInfo=new ProductOrderInfo();
//            productOrderInfo.setUserId("Z001");
//            productOrderInfo.setBuyerUnitCode("M002");
//            productOrderInfo.setBuyerUnitName("苏州");
//            productOrderInfo.setNewGoodsId(list.get(i).getNewGoodsId());
//            productOrderInfo.save();
//        }

    }

    @Override
    protected void onStart(){
        super.onStart();
        if(list==null||list.size()==0){
            if(!progressDialog.isShowing()){
                new UpdateProinfoTask().execute();
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

        orderInfoTv.setText("AD16Q3 | AD | " + areaName + " | " + userName);

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
        searchFilterTv.setTypeface(iconfont);


        skuNumTextView=(TextView)findViewById(R.id.skuNum);
        //初始化筛选条件
        initGroups();

        //初始化搜索框
        mSearchView=(SearchView)findViewById(R.id.search_view);
        initSearchView();

        //初始化商品列表listview
        listView=(AutoListView)findViewById(R.id.search_list_View);
        initListview();

        //排序下拉框
        orderSpinner=(Spinner)findViewById(R.id.search_orderby);
        initOrderSpinner();

        //是否预订
        isOrderedSpinner=(Spinner)findViewById(R.id.search_isordered);
        initIsOrderedSpinner();

        //订货量查询
        orderLevelSpinner=(Spinner)findViewById(R.id.search_orderlevel);
        initOrderLevelSpinner();

        //测试助手
//        Spinner testHelper=(Spinner)findViewById(R.id.test_helper);
//        final ArrayList<String> testList=new ArrayList<>();
//        testList.add("");
//        testList.add("A");
//        testList.add("B");
//        testList.add("C");
//        testList.add("D");
//        ArrayAdapter<String> orderLevelAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,testList);
//        testHelper.setAdapter(orderLevelAdapter);
//        testHelper.setOnItemSelectedListener(
//                new AdapterView.OnItemSelectedListener() {
//                    public void onItemSelected(AdapterView<?> parent,
//                                               View view, int position, long id) {
//                        String orderStr=testList.get(position);
//                        if(orderStr!=null&&!orderStr.equals(""))
//                        testHelp(orderStr);
//                    }
//
//                    public void onNothingSelected(AdapterView<?> parent) {
//                    }
//                });
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
        adapter=new OrderSearchAdapter(this,list,R.layout.adapter_search,
                new String[]{"img","modelNo","goodsNo","modelName","radioGroup","supportStyle","eastLaunch"},
                new int[]{R.id.img,R.id.modelNo,R.id.goodsNo,R.id.modelName,R.id.supportStyle,R.id.eastLaunch}){
            public void customImgAction(View v, int position) {
                try{
//                    adapter.cancelAllTasks();
                    Intent intent=new Intent(SearchActivity.this,DetailActivity.class);

                    intent.putExtra(EXTRA_MESSAGE, list.get(position).getModelNo());
                    startActivity(intent);
                }catch (Exception e){
                    Log.e("TEST",e.getMessage());
                }
            }
            public void customRadioAction(String level, int position) {

                String proid=list.get(position).getGoodsNo();
                list.get(position).setOrderLevel(level);
                ProductInfo productInfo=DataSupport.where("goodsNo=?", proid).find(ProductInfo.class).get(0);
                ProductInfo productInfo2=new ProductInfo();
                productInfo2.setOrderLevel(level);
                productInfo2.update(productInfo.getId());

            }
        };
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(this);
        listView.setOnLoadListener(this);
    }

    /**
     * 初始化订货量排序下拉条件
     */
    private void initOrderSpinner(){
        ArrayList<String> orderbyList=new ArrayList<>();
        orderbyList.add("默认排序");
        orderbyList.add("按定量升序");
        orderbyList.add("按定量降序");

        ArrayAdapter<String> orderByAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,orderbyList);
        orderSpinner.setAdapter(orderByAdapter);
        //orderSpinner.setPrompt("默认排序");
        orderSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {
                        loadData(AutoListView.REFRESH);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        loadData(AutoListView.REFRESH);
                    }
                });

    }

    /**
     * 初始化是否预订下拉条件
     */
    private void initIsOrderedSpinner(){
        ArrayList<String> isOrderedList=new ArrayList<>();
        isOrderedList.add("全部");
        isOrderedList.add("已订");
        isOrderedList.add("未订");
        isOrderedList.add("不订");
        ArrayAdapter<String> isOrderedAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,isOrderedList);
        isOrderedSpinner.setAdapter(isOrderedAdapter);
        isOrderedSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {
                        loadData(AutoListView.REFRESH);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        loadData(AutoListView.REFRESH);
                    }
                });
    }

    /**
     * 初始化订货量下拉框
     */
    private void initOrderLevelSpinner(){
        ArrayList<String> orderLevel=new ArrayList<>();
        orderLevel.add("全部");
        orderLevel.add("A");
        orderLevel.add("B");
        orderLevel.add("C");
        orderLevel.add("D");
        ArrayAdapter<String> orderLevelAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,orderLevel);
        orderLevelSpinner.setAdapter(orderLevelAdapter);
        orderLevelSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {
                        loadData(AutoListView.REFRESH);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        loadData(AutoListView.REFRESH);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==R.id.action_download){
            new UpdateProinfoTask().execute();
            return true;
        }else if(id==R.id.action_downloadPic){
            new DownloadPicTask().execute();
            return true;
        }else if(id==R.id.action_submit){
            new uploadProOrderTask(progressDialog,orderApplication.getUserId()).execute();
            return true;
        }else if(id==R.id.action_submitPic){

            new UploadPicTask().execute();
        }else if(id==R.id.action_logout){

            AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
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
        pageId=0;
        loadData(AutoListView.REFRESH);

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
        String queryStr="userid='"+orderApplication.getUserId()+"' ";

        //货号查询
        String proid=mSearchView.getQuery().toString().toUpperCase();
        if((!proid.equals(""))&&(proid!=null)){
//            clusterQuery=clusterQuery.where("goodsNo like ?", "%" + proid + "%");
            queryStr+="and (goodsNo like '%" + proid + "%' or modelNo like '%"+proid+"%' or modelName like '%"+proid+"%') ";
        }

        //是否已订
        if(isOrderedSpinner!=null){
            int isOrdered=isOrderedSpinner.getSelectedItemPosition();
            switch (isOrdered){
                case 0:
                    break;
                case 1:
                    queryStr+="and orderLevel is not null and orderLevel!='' and orderLevel!='X' ";
                    break;
                case 2:
                    queryStr+="and (orderLevel is null or orderLevel='') ";
                    break;
                case 3:
                    queryStr+="and orderLevel='X' ";
                    break;
                default:
                    break;
            }
        }

        //订货量
        if(orderLevelSpinner!=null){
            int orderLevel=orderLevelSpinner.getSelectedItemPosition();
            switch (orderLevel){
                case 0:
                    break;
                case 1:
                    queryStr+="and orderLevel='A' ";
                    break;
                case 2:
                    queryStr+="and orderLevel='B' ";
                    break;
                case 3:
                    queryStr+="and orderLevel='C' ";
                    break;
                case 4:
                    queryStr+="and orderLevel='D' ";
                    break;
                default:
                    break;
            }
        }

        //页码
        if(pageEditText!=null){
            if(pageEditText.getText()!=null&&!pageEditText.getText().toString().equals("")){
                String text=pageEditText.getText().toString();
                queryStr+="and directoryPage='"+pageEditText.getText()+"' ";
            }

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
            String categorys=getStringFromArray(groups.get(1).getChildren());
            if(!categorys.equals("")){
                filterCount++;
                queryStr+="and category in ("+categorys+") ";
            }

            //系列
            String marketingDivisions=getStringFromArray(groups.get(2).getChildren());
            if(!marketingDivisions.equals("")){
                filterCount++;
                queryStr+="and marketingDivision in ("+marketingDivisions+") ";
            }

            //性别
            String genders=getStringFromArray(groups.get(3).getChildren());
            if(!genders.equals("")){
                filterCount++;
                queryStr+="and gender in ("+genders+") ";
            }

            //POP
            String popStr=getStringFromArray(groups.get(4).getChildren());
            if(!popStr.equals("")){
                filterCount++;
                queryStr+="and pop in ("+popStr+") ";
            }

            //上市月
            String eastMonthStr=getStringFromArray(groups.get(5).getChildren());
            if(!eastMonthStr.equals("")){
                filterCount++;
                queryStr+="and eastLaunchMonth in ("+eastMonthStr+") ";
            }

            //分配必定
            String mustStr=getStringFromArray(groups.get(6).getChildren());
            if(!mustStr.equals("")){
                filterCount++;
                queryStr+="and havetoOrder in ("+mustStr+") ";
            }

            //品牌季节
            String seasonStr=getStringFromArray(groups.get(7).getChildren());
            if(!seasonStr.equals("")){
                filterCount++;
                queryStr+="and brdSeason in ("+seasonStr+") ";
            }
            if(filterCount>0){
                searchFilterTv.setTextColor(Color.rgb(255, 0, 255));
            }else{
                searchFilterTv.setTextColor(-1979711488);
            }

        }


        //排序
        if(orderSpinner!=null){
            int orderPosition=orderSpinner.getSelectedItemPosition();

            //if(orderPosition!=0){queryStr+="and (orderLevel!='X' or orderLevel is null)  ";}
            switch (orderPosition){
                case 0:
                    break;
                case 1:
                    clusterQuery=clusterQuery.order("(case when orderLevel='X' then 'F' when orderLevel='' or orderLevel is null then 'E' else orderLevel end) desc");
                    break;
                case 2:
                    clusterQuery=clusterQuery.order("(case when orderLevel='X' then 'F' when orderLevel='' or orderLevel is null then 'E' else orderLevel end) asc");
                    break;
                default:
                    break;
            }
        }

        List<ProductInfo> productInfoList=clusterQuery.where(queryStr).limit(pageSize).offset(pageId*pageSize).find(ProductInfo.class);

        Cursor cursor= DataSupport.findBySQL("select count(*) ct from productinfo where " + queryStr);
        String count;
        if(cursor.moveToFirst()==true){
            count=cursor.getString(cursor.getColumnIndex("ct"));
            skuNumTextView.setText("SKU:" + count);
        }
//        skuNumTextView.setText("SKU:" + String.valueOf(productInfoList2.size()));
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


    //根据货号下载图片
    public void downLoadPic(String urlString, String goodsNo){
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        Bitmap bitmap=null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            bitmap= BitmapFactory.decodeStream(in);
            if(bitmap!=null){
                RocTools.saveBitmap2Local(bitmap, goodsNo,getApplicationContext());
            }


        } catch (final IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if(bitmap!=null){
                    bitmap.recycle();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 上传订货量(注销的时候)
     */
    public void layout(){
        progressDialog = ProgressDialog.show(this, "提交注销中", "请稍候……");
        List<ProductInfo> productInfoList=DataSupport.where("orderLevel is not null and orderLevel!='' and orderLevel!='X' and userid='"+orderApplication.getUserId()+"'").find(ProductInfo.class);
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
        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.POST,
                "http://192.168.4.217/ordermeet/app/app/procommit",
                jsonArray,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try{
                            if(jsonArray.getJSONObject(0).getString("resultType").equals("SUCCESS")){
                                clearAndGotoLogin();
                            }else{
                                Toast.makeText(getApplicationContext(),"注销失败0",Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            handlerProgressDialog.sendMessage(new Message());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TEST", error.getMessage(), error);
                        handlerProgressDialog.sendMessage(new Message());
                        Toast.makeText(getApplicationContext(),"注销失败",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        req.setRetryPolicy(new DefaultRetryPolicy(
                200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }



    /**
     * 显示筛选条件框(按钮触发)
     * @param view
     */
    public void showFilter(View view){
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
            }
        });

//        WindowManager.LayoutParams lp=getWindow().getAttributes();
//        lp.alpha = 0.4f;
//        getWindow().setAttributes(lp);



        mPopupWindow.showAtLocation(getCurrentFocus(), Gravity.RIGHT, 0, 0);

        ExpandableListView expandableListView=(ExpandableListView)popupView.findViewById(R.id.expand_activities_button);
        //setDetailData(popupView, getDetailData(postion));

        if(groups==null||groups.size()==0){
            initGroups();
        }

        adapter2 = new EListAdapter(this, groups);
        expandableListView.setAdapter(adapter2);
        expandableListView.setOnChildClickListener(adapter2);

        //页码
        pageEditText=(EditText)popupView.findViewById(R.id.pageNum);

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
        List<ProConstType> styleList= DataSupport.where("ClassType=?", "0000000043").find(ProConstType.class);
        Group styleGroup = new Group("0000000043","款式");
        for(int i=0;i<styleList.size();i++){
            Child child = new Child(styleList.get(i).getClassKey(),styleList.get(i).getClassValue(), styleList.get(i).getClassValue());
            styleGroup.addChildrenItem(child);
        }
        groups.add(styleGroup);

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
        Child child = new Child("16Q3","16Q3","");
        seasonGroup.addChildrenItem(child);
        child = new Child("16Q4","16Q4","");
        seasonGroup.addChildrenItem(child);
        groups.add(seasonGroup);
//        String classType="";
//        Group group = new Group();
//        for(int i=0;i<p.size();i++){
//            ProConstType proConstType=p.get(i);
//            if(!classType.equals(proConstType.getClassType())){
//                if(!classType.equals("")){groups.add(group);}
//                classType=proConstType.getClassType();
//                group = new Group(proConstType.getClassKey(), proConstType.getRemark());
//
//            }
//            Child child = new Child(p.get(i).getClassKey(), p.get(i).getClassValue(),
//                    p.get(i).getClassValue());
//            group.addChildrenItem(child);
//
//
//        }
//        groups.add(group);

    }


    /**
     * 测试助手
     */
    private void testHelp(String level){
        ContentValues values = new ContentValues();
        values.put("orderLevel", level);
        DataSupport.updateAll(ProductInfo.class,values,"newGoodsId>'1865' and newGoodsId<='2865' and userid=?",orderApplication.getUserId());
    }



    /**
     * 下载图片
     */
    class DownloadPicTask extends AsyncTask<String,Integer,Boolean>{

        public DownloadPicTask(){

        }

        protected void onPreExecute() {
            progressDialog.setMessage("下载中，请稍候……");
            progressDialog.setTitle("下载图片");
            progressDialog.show();
        }
        @Override
        protected Boolean doInBackground(String... params) {

            List<ProductPic> oldProductPicList=DataSupport.where("status<>?","0").find(ProductPic.class);
            for(int i=0;i<oldProductPicList.size();i++){
                ProductPic productPic=oldProductPicList.get(i);
                uploadPic(oldProductPicList.get(i));
                if(productPic.getStatus().equals("1")){
                    productPic.setStatus("0");
                }else{
                    productPic.delete();
                }
            }
            publishProgress(0);

            String result=downPicList("");

            List<ProductPic> productPicList=new ArrayList<ProductPic>();
            try{
                JSONArray jsonArray=new JSONArray(result);
                DataSupport.deleteAll(ProductPic.class,"1=1");

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    ProductPic productPic=new ProductPic();
                    productPic.setOrderPlanNo(jsonObject.getString("orderPlanNo"));
                    productPic.setGoodsNo(jsonObject.getString("goodsNo"));
                    productPic.setImgName(jsonObject.getString("imgName"));
                    productPic.setStatus(jsonObject.getString("status"));
//                    productPic.save();
                    productPicList.add(productPic);

                }
                Log.i("TEST",String.valueOf(jsonArray.length()));

            }catch (Exception e){
                e.printStackTrace();
            }
            publishProgress(1);

            DataSupport.saveAll(productPicList);
            for(int i=0;i<productPicList.size();i++){
                try {
                    String imgName=productPicList.get(i).getImgName().replace(" ","");
                    File file=new File(getFilesDir()+"/"+imgName);
                    if(!file.exists()){
                        downLoadPic(proPicUrl + imgName, imgName);
                        Log.i("TEST", String.valueOf(i) + "     successPic");
                    }
                    publishProgress(productPicList.size()-i);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("TEST", String.valueOf(i) + "     failedPic" );
                }
            }
            publishProgress(99999);


            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int value=values[0];
            String message="";
            if(value==0){
                message="开始下载图片列表";
            }else if(value==1){
                message="开始下载图片";
            }else if(value==99999){
                message="下载图片完成";
            }else{
                message="剩余下载图片"+String.valueOf(value);
            }
            progressDialog.setMessage(message);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
//            handlerProgressDialog.sendMessage(new Message());
        }

        //获取图片列表
        private String downPicList(String orderPlanNo){
            String result="";
            String str="http://192.168.4.217/ordermeet/app/getImgNameList?orderPlanNo=AD16Q2";
            try {
                URL url=new URL(str);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("content-type", "text/html");
                PrintWriter pw = new PrintWriter(connection.getOutputStream());
                pw.print("");
                pw.flush();
                pw.close();
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
//                    Log.i("TEST",result);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        //上传图片文件
        private String uploadPic(ProductPic productPic){

            String str=upLoadImg + "?orderPlanNo="+"AD16Q2"+"&fileName="+productPic.getImgName()+"&status="+productPic.getStatus();

            File file=new File(getFilesDir()+"/"+productPic.getImgName());
            try {
                URL url=new URL(str);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("content-type", "text/html");
                BufferedOutputStream  out=new BufferedOutputStream(connection.getOutputStream());

                FileInputStream fileInputStream;
                if(file.exists()){
                    //读取文件上传到服务器
                    fileInputStream=new FileInputStream(file);
                    byte[]bytes=new byte[1024];
                    int numReadByte=0;
                    while((numReadByte=fileInputStream.read(bytes,0,1024))>0)
                    {
                        out.write(bytes, 0, numReadByte);
                    }
                    out.flush();
                    fileInputStream.close();
                }else {
                    out.flush();
                }
                //读取URLConnection的响应
                InputStreamReader in=new InputStreamReader(connection.getInputStream());
                String result="";
                StringBuffer buffer=new StringBuffer();
                BufferedReader reader=null;
                try{
                    reader=new BufferedReader(in);
                    String line=null;

                    while ((line=reader.readLine())!=null){
                        buffer.append(line);
                    }
                    result=buffer.toString();
                    Log.i("TEST",result);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }
    }

    /**
     * 上传图片
     */
    class UploadPicTask extends AsyncTask<String,Integer,Boolean>{

        @Override
        protected void onPreExecute(){
//            progressDialog.setTitle( "提交图片");
//            progressDialog.setMessage("提交图片中，请稍候……");
//            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
//            File f=new File(getFilesDir()+"/takePic/");
//            File[] files=f.listFiles();
//            if(files!=null){
//                for(int i=0;i<files.length;i++){
//                    uploadPic(files[i]);
//                }
//            }
            List<ProductPic> oldProductPicList=DataSupport.where("status<>?","0").find(ProductPic.class);
            for(int i=0;i<oldProductPicList.size();i++){
                ProductPic productPic=oldProductPicList.get(i);
                uploadPic(productPic);
                if(productPic.getStatus().equals("1")){
                    productPic.setStatus("0");
                }else{
                    productPic.delete();
                }

                productPic.save();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            handlerProgressDialog.sendMessage(new Message());
        }


        //上传图片
        private String uploadPic(ProductPic productPic){
            String str=upLoadImg + "?orderPlanNo="+"AD16Q2"+"&fileName="+productPic.getImgName()+"&status="+productPic.getStatus();

            File file=new File(getFilesDir()+"/"+productPic.getImgName());
            try {
                URL url=new URL(str);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("content-type", "text/html");
                BufferedOutputStream  out=new BufferedOutputStream(connection.getOutputStream());
                FileInputStream fileInputStream;
                if(file.exists()){
                    //读取文件上传到服务器
                    fileInputStream=new FileInputStream(file);
                    byte[]bytes=new byte[1024];
                    int numReadByte=0;
                    while((numReadByte=fileInputStream.read(bytes,0,1024))>0)
                    {
                        out.write(bytes, 0, numReadByte);
                    }
                    out.flush();
                    fileInputStream.close();
                }else {
                    out.flush();
                }



                //读取URLConnection的响应
                InputStreamReader in=new InputStreamReader(connection.getInputStream());
                String result="";
                StringBuffer buffer=new StringBuffer();
                BufferedReader reader=null;
                try{
                    reader=new BufferedReader(in);
                    String line=null;

                    while ((line=reader.readLine())!=null){
                        buffer.append(line);
                    }
                    result=buffer.toString();
                    Log.i("TEST",result);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }
    }

    /**
     * 更新商品信息
     */
    class UpdateProinfoTask extends AsyncTask<String,Integer,Boolean>{

        @Override
        protected void onPreExecute(){
            progressDialog.setTitle("更新基础数据中");
            progressDialog.setMessage("提交订量信息");
            progressDialog.show();
        }
        @Override
        protected Boolean doInBackground(String... params) {
            String result="";
            //提交商品订单信息
            result= uploadProOrder();
            if(!result.equals("SUCCESS")){return false;}

            publishProgress(0);

            //更新静态属性
            updateProconst();

            publishProgress(1);

            //更新商品信息
            updateProinfo();

            publishProgress(2);

            updateProBIData();

            publishProgress(3);

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int value=values[0];
            String message="";
            if(value==0){
                message="更新静态属性";
            }else if(value==1){
                message="更新商品信息";
            }else if(value==2){
                message="更新历史数据";
            }else if(value==3){
                message="更新结束";
            }
            progressDialog.setMessage(message);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            initGroups();
            loadData(AutoListView.REFRESH);
        }

        /**
         * 提交商品订单信息
         * @return
         */
        private String uploadProOrder(){
            String str=procommitUrl;
            List<ProductInfo> productInfoList=DataSupport.where("orderLevel is not null and orderLevel!='' and orderLevel!='X' and userid='"+orderApplication.getUserId()+"'").find(ProductInfo.class);
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
                URL url=new URL(str);
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
                    Log.i("TEST",result);
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
                Log.i("TEST",e.toString());
            }
            return resultType;
        }

        /**
         * 更新商品静态属性
         * @return
         */
        private void updateProconst(){
            String urlStr=proConstUrl;
            String result="";

            Log.i("TEST", "请求静态属性开始");
            result=getDataByHttpRequest(urlStr,"");
            Log.i("TEST", "请求静态属性结束");


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
                    proConstType.setRemark(jsonObject.getString("remark"));
                    proConstType.setCustom_Resource(jsonObject.getString("custom_Resource"));
                    proConstTypeList.add(proConstType);
                }
                DataSupport.saveAll(proConstTypeList);
                Log.i("TEST", "保存静态属性成功");
            }catch (Exception e){
                Log.e("TEST", "解析静态属性失败");
            }

        }

        /**
         * 更新商品资料
         * @return
         */
        private void updateProinfo(){
            String result="";
            String urlStr=proInfoUrl;
            Map<String, String> params = new HashMap<String, String>();

            params.put("userid", orderApplication.getUserId());
            params.put("orderPlanNo", "AD16Q3"/*orderApplication.getOrderPlanNo()*/);

            Log.i("TEST", "请求商品信息开始");
            result=getDataByHttpRequest(urlStr,(new JSONObject(params)).toString());
            Log.i("TEST", "请求商品信息结束");

            try {
                Log.i("TEST","开始解析商品信息");
                JSONArray jsonArray=new JSONObject(result).getJSONArray("rows");

                DataSupport.deleteAll(ProductInfo.class,"userid='"+orderApplication.getUserId()+"'");
                List<ProductInfo> productInfoList=new ArrayList<>();
                Log.i("TEST","循环商品信息开始");
                for(int i=0;i<jsonArray.length();i++){
                    ProductInfo productInfo=new ProductInfo();
                    JSONObject jsonObject=jsonArray.getJSONObject(i);

                    Field[] fields=productInfo.getClass().getDeclaredFields();
                    for(int j=0;j<fields.length;j++){
                        Field field=fields[j];
                        //field.setAccessible(true);
                        String name=field.getName();
                        if(name.equals("userid")||name.equals("orderLevel")||name.equals("orderLevelPlus")||name.equals("productComments")){continue;}
                        if(jsonObject.has(name)){
                            String str=jsonObject.getString(name);
                            name=name.substring(0,1).toUpperCase()+name.substring(1);
                            Method m=productInfo.getClass().getMethod("set"+name,String.class);
                            m.invoke(productInfo,str);
                        }
                    }
                    productInfo.setUserId(orderApplication.getUserId());
                    productInfo.setId(Integer.valueOf(jsonObject.getString("newGoodsId")));

                    String depth=jsonObject.getString("depth");
                    if(depth!=null&&(!depth.equals(""))&&(!depth.equals("null"))){
                        productInfo.setOrderLevel(depth.substring(0,1));
                        productInfo.setOrderLevelPlus(depth.substring(1));
                    }

                    productInfoList.add(productInfo);
                }
                Log.i("TEST", "保存商品信息开始");
                DataSupport.saveAll(productInfoList);
                Log.i("TEST", "保存商品信息成功");
            } catch (Exception e) {
                Log.i("TEST","解析商品信息失败");
                e.printStackTrace();
            }
        }


        /**
         * 获取BI历史数据
         */
        private void updateProBIData(){
            String result="";

            String urlStr=proBIUrl;

            Map<String, String> params = new HashMap<String, String>();

            params.put("userid", orderApplication.getUserId());

            Log.i("TEST", "请求BI历史数据开始");
            result=getDataByHttpRequest(urlStr,(new JSONObject(params)).toString());
            Log.i("TEST", "请求BI历史数据结束");

            try {
                Log.i("TEST", "解析BI数据开始");
                JSONArray jsonArray=new JSONObject(result).getJSONArray("rows");
                DataSupport.deleteAll(ProductBIData.class);
                List<ProductBIData> productBIDataList=new ArrayList<>();

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    ProductBIData productBIData=new ProductBIData();

                    Field[] fields=productBIData.getClass().getDeclaredFields();
                    for(int j=0;j<fields.length;j++){
                        Field field=fields[j];
                        //field.setAccessible(true);
                        String name=field.getName();
                        if(jsonObject.has(name)){
                            String str=jsonObject.getString(name);

                            name=name.substring(0,1).toUpperCase()+name.substring(1);
                            Method m=productBIData.getClass().getMethod("set"+name,String.class);
                            m.invoke(productBIData,str);
                        }
                    }

                    productBIData.setUserid(orderApplication.getUserId());
                    productBIDataList.add(productBIData);
                }
                DataSupport.saveAll(productBIDataList);
                Log.i("TEST", "解析BI数据成功");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TEST", "解析BI数据失败");
            }
        }

        /**
         * 通过HTTP请求获取数据
         * @param urlStr
         * @param paramStr
         * @return
         */
        private String getDataByHttpRequest(String urlStr,String paramStr){
            String result="";

            try {
                URL url=new URL(urlStr);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setConnectTimeout(100000);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                DataOutputStream out = new DataOutputStream(connection
                        .getOutputStream());

                byte[] content = paramStr.getBytes("utf-8");

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
                }catch (Exception e){
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }
    }
}