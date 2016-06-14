package com.topsports.tootwo2.order;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.topsports.tootwo2.app.OrderApplication;
import com.topsports.tootwo2.helper.RocTools;
import com.topsports.tootwo2.model.ProductComments;
import com.topsports.tootwo2.model.ProductHisTrend;
import com.topsports.tootwo2.model.ProductInfo;
import com.topsports.tootwo2.model.ProductOrderInfo;
import com.topsports.tootwo2.model.ProductPic;
import com.topsports.tootwo2.widget.autolistview.adapter.BIHistoryDataAdapter;
import com.topsports.tootwo2.widget.autolistview.adapter.OrderSearchQtyAdapter;
import com.topsports.tootwo2.widget.autolistview.widget.AutoListView;
import com.topsports.tootwo2.widget.slideView.SlideShowView;
import com.topsports.tootwo2.widget.tagcloud.TagCloudView;

import org.litepal.crud.ClusterQuery;
import org.litepal.crud.DataSupport;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class DetailQtyActivity extends AppCompatActivity  {
    OrderApplication orderApplication;

    String goodsNo;
    String newGoodsId;

    String modelNo;
    //商品列表
    private ListView listView;
    //数据
    private List<ProductInfo> list = new ArrayList<ProductInfo>();
    //适配器
    private OrderSearchQtyAdapter adapter;

    //图片轮播器
    private SlideShowView slideShowView;

    //图片文件名数组
    private List<String> fileNmList;

    //买手单位筛选框
    private Spinner buyerUnitSpinner;

    //商品属性框
    TableLayout tableLayout;

    //搜索框
    SearchView mSearchView;

    List<String> buyerUnitList;

    //page页数
    private int pageId = 0;
    //page大小
    private int pageSize = 8;


    private MyBroadCastReceiver broadCastReceiver;

    /**
     * 查询条件的文本值
     */
    private String searchViewStr="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_qty);

        //从intent中获取model号
        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // Handle the normal search query case
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i("TEST", "1");
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // Handle a suggestions click (because the suggestions all use ACTION_VIEW)
            Uri data = intent.getData();
            Log.i("TEST","2");
        }

        modelNo = intent.getStringExtra(SearchQtyActivity.EXTRA_MESSAGE);



        orderApplication=(OrderApplication)getApplication();


        //iconFont设置图标
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/iconfont.ttf");
        Button btn = (Button)findViewById(R.id.deletePic);
        btn.setTypeface(iconfont);
        Button camera=(Button)findViewById(R.id.takePhoto);
        camera.setTypeface(iconfont);

        //初始化工具栏
        initActionBar();

        list = getData();
        //获取当前的货号
        goodsNo=intent.getStringExtra(SearchQtyActivity.EXTRA_MESSAGE_PROID);
//        goodsNo=list.get(0).getGoodsNo();
        for(int i=0;i<list.size();i++){
            if(list.get(i).getGoodsNo().equals(goodsNo)){
                newGoodsId=list.get(i).getNewGoodsId();
                break;
            }
        }

        //设置listview
        initListView();;

        //图片轮播器
        initSlideShowView(goodsNo);

        initTags(newGoodsId);
        mSearchView=(SearchView)findViewById(R.id.search_view);
        initSearchView();
        //设置精英买手权限
        setElite();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initBuyerUnitSpinner();
        initExpandList("");
        changeHintTv();

        //初始化商品属性
        tableLayout=(TableLayout)findViewById(R.id.pro_attr_layout);
        initProAttrs(newGoodsId);

        //注册广播
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.topsports.order_qtychange");
        broadCastReceiver=new MyBroadCastReceiver();
        registerReceiver(broadCastReceiver, intentFilter);
    }

    public Cursor getCursor(String queryStr){

        Cursor cursor = DataSupport.findBySQL("select id as _id,goodsno from productinfo where  goodsNo like '%" + queryStr + "%' limit 5");
        return cursor;
    }

    /**
     * 初始化工具栏
     */
    private void initActionBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.title_toolbar);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/iconfont.ttf");
        TextView backTv=(TextView)findViewById(R.id.back);
        backTv.setTypeface(iconfont);
        backTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
//                intent.putExtra("result","hello");
                setResult(100,intent);
                DetailQtyActivity.this.finish();
            }
        });

        TextView orderInfoTv = (TextView) findViewById(R.id.orderInfo);

        OrderApplication orderApplication=(OrderApplication)getApplication();
        String areaName=orderApplication.getAreaName()==null?"":orderApplication.getAreaName();
        String userName=orderApplication.getName()==null?"":orderApplication.getName();

        orderInfoTv.setText(orderApplication.getOrderMeetingNo()+" | AD | "+areaName+" | "+userName);

        toolbar.setTitle("");

        setSupportActionBar(toolbar);

    }

    private void initSearchView(){
        final TextView toolbar_modelNo = (TextView)findViewById(R.id.toolbar_modelNo);

        toolbar_modelNo.setText(modelNo);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if ((!query.equals("")) && (query != null)) {
                    mSearchView.clearFocus();

                    List<ProductInfo> productInfoList = DataSupport.where("goodsNo like '%" + query + "%' ").find(ProductInfo.class);
                    if (productInfoList != null && productInfoList.size() > 0) {
                        modelNo = productInfoList.get(0).getModelNo();

                        List<ProductInfo> result = getData();
                        list.clear();
                        list.addAll(result);
                        goodsNo = list.get(0).getGoodsNo();
                        initSlideShowView(goodsNo);
                        adapter.notifyDataSetChanged();
                        toolbar_modelNo.setText(modelNo);
                        initExpandList("");
                        newGoodsId = list.get(0).getNewGoodsId();
                        initProAttrs(newGoodsId);
                        initTags(newGoodsId);
                    }

                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {

                    return true;
                } else {
                    try {
                        searchViewStr = newText;
                        Cursor cursor = getCursor(searchViewStr);//DataSupport.findBySQL("select id as _id,goodsno from productinfo where userid='" + orderApplication.getUserid() + "' and goodsNo like '%" + newText + "%' limit 5");
                        @SuppressWarnings("deprecation")
                        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(),
                                R.layout.mytextview, cursor, new String[]{"goodsno"},
                                new int[]{R.id.textview});

                        mSearchView.setSuggestionsAdapter(adapter);
                    } catch (Exception e) {
                        Log.i("TEST", e.getMessage());
                    }
                }
                return false;
            }
        });


        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {

                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = getCursor(searchViewStr);
                cursor.moveToPosition(position);
                String queryStr = cursor.getString(1);

                mSearchView.setQuery(queryStr, true);
                mSearchView.clearFocus();

                List<ProductInfo> productInfoList = DataSupport.where("goodsNo like '%" + queryStr + "%' and userid='" + orderApplication.getUserId() + "'").find(ProductInfo.class);
                modelNo = productInfoList.get(0).getModelNo();
                List<ProductInfo> result = getData();
                list.clear();
                list.addAll(result);
                goodsNo = list.get(0).getGoodsNo();
                initSlideShowView(goodsNo);
                adapter.notifyDataSetChanged();
                toolbar_modelNo.setText(modelNo);
                toolbar_modelNo.setText(modelNo);
                return true;
            }
        });
    }
    /**
     * 初始化图片轮播器
     * @param goodsNo 货号
     */
    private void initSlideShowView(String goodsNo){
        fileNmList=new ArrayList<>();
        slideShowView=(SlideShowView)findViewById(R.id.slideshowView);
        List<Bitmap> bitmapList=new ArrayList<Bitmap>();
//        bitmapList.add(getBitmap(goodsNo));

        List<ProductPic> productPicList=DataSupport.where("goodsNo=? and status<>'2'",goodsNo).find(ProductPic.class);

        for(int i=0;i<productPicList.size();i++){
            String imgName=productPicList.get(i).getImgName();
            fileNmList.add(imgName);
            Bitmap bitmap= BitmapFactory.decodeFile(getFilesDir() + "/" + imgName);
            bitmapList.add(bitmap);
        }

        final String goodsNoFinal=goodsNo;


        slideShowView.imageBitmaps=bitmapList;
        slideShowView.initData();

    }

    /**
     * 初始化商品列表
     */
    private void initListView(){
        listView = (ListView) findViewById(R.id.detail_list_View);

        adapter = new OrderSearchQtyAdapter(this, list, R.layout.adapter_search_qty,orderApplication.getUserId()) {

            public void customImgAction(View v, int position) {
                if(goodsNo.equals(list.get(position).getGoodsNo())){

                }else {
                    goodsNo=list.get(position).getGoodsNo();
                    newGoodsId=list.get(position).getNewGoodsId();
                    //initImage3DSwitchView(goodsNo);
                    initSlideShowView(goodsNo);
                    initTags(newGoodsId);
                    initProAttrs(newGoodsId);
                }
            }


        };

        listView.setAdapter(adapter);

        if(list.size()>0){
            listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,list.size()*170));
        }
    }

    /**
     * 初始化买手单位选择框
     */
    private void initBuyerUnitSpinner(){
        buyerUnitSpinner=(Spinner)findViewById(R.id.buyer_unit_spinner);

        buyerUnitList=Arrays.asList(orderApplication.getBuyerUnitsStr().split(","));


        ArrayAdapter<String> orderByAdapter=new ArrayAdapter<String>(this,R.layout.custom_spinner_search1,buyerUnitList);
        buyerUnitSpinner.setAdapter(orderByAdapter);
        buyerUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initExpandList("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 初始化BI展开栏
     */
    private void initExpandList(String sortStr){

        String proids="";
        String cateId="";
        for(int i=0;i<list.size();i++){
            proids+=list.get(i).getGoodsNo()+"','";
            cateId=list.get(i).getDivision();
        }
        List<ProductHisTrend> productHisTrends=new ArrayList<ProductHisTrend>();

        String buyUnitNm=buyerUnitList.get(buyerUnitSpinner.getSelectedItemPosition());

        String sql="select t2.goodsNo,t2.modelName,sum(t2.totalSalQty3) totalSalQty3,sum(totalSalAmt3) totalSalAmt3,sum(totalSalNosPrmAmt3) totalSalNosPrmAmt3,sum(totalSalPrmAmt3) totalSalPrmAmt3,sum(invQty3) invQty3,sum(invAmt3) invAmt3,sum(distrOrgNum) distrOrgNum,sum(salOrgNum) salOrgNum,sum(orderNum) orderNum, ";
            sql+="sum(t2.totalSalQty2) totalSalQty2,sum(totalSalAmt2) totalSalAmt2,sum(totalSalNosPrmAmt2) totalSalNosPrmAmt2,sum(invQty2) invQty2, ";
            sql+="sum(t2.totalSalQty1) totalSalQty1,sum(totalSalAmt1) totalSalAmt1,sum(totalSalNosPrmAmt1) totalSalNosPrmAmt1,sum(invQty1) invQty1 ";
            sql+="from (select distinct a.proIdOld from productOldCompare a where a.proId in('"+proids+"')) t1,productHisTrend t2 ";
            sql+="where t2.goodsNo=t1.proIdOld  and t2.buyerUnitNm='"+buyUnitNm+"'  group by t2.goodsNo,t2.modelName ";
            sql+=sortStr;

        Cursor cursor=DataSupport.findBySQL(sql);
//        cursor.moveToFirst();
        while(cursor.moveToNext())
        {
            ProductHisTrend productHisTrend=new ProductHisTrend();
            productHisTrend.setGoodsNo(cursor.getString(0));
            productHisTrend.setModelName(cursor.getString(1));
            productHisTrend.setTotalSalQty3(cursor.getInt(2));
            productHisTrend.setTotalSalAmt3(cursor.getInt(3));
            productHisTrend.setTotalSalNosPrmAmt3(cursor.getInt(4));
            productHisTrend.setTotalSalPrmAmt3(cursor.getInt(5));
            productHisTrend.setInvQty3(cursor.getInt(6));
            productHisTrend.setInvAmt3(cursor.getInt(7));
            productHisTrend.setDistrOrgNum(cursor.getInt(8));
            productHisTrend.setSalOrgNum(cursor.getInt(9));
            productHisTrend.setOrderNum(cursor.getInt(10));

            productHisTrend.setTotalSalAmt1(cursor.getInt(cursor.getColumnIndex("totalSalAmt1")));
            productHisTrend.setTotalSalNosPrmAmt1(cursor.getInt(cursor.getColumnIndex("totalSalNosPrmAmt1")));
            productHisTrend.setTotalSalQty1(cursor.getInt(cursor.getColumnIndex("totalSalQty1")));
            productHisTrend.setInvQty1(cursor.getInt(cursor.getColumnIndex("invQty1")));

            productHisTrend.setTotalSalAmt2(cursor.getInt(cursor.getColumnIndex("totalSalAmt2")));
            productHisTrend.setTotalSalNosPrmAmt2(cursor.getInt(cursor.getColumnIndex("totalSalNosPrmAmt2")));
            productHisTrend.setTotalSalQty2(cursor.getInt(cursor.getColumnIndex("totalSalQty2")));
            productHisTrend.setInvQty2(cursor.getInt(cursor.getColumnIndex("invQty2")));



            productHisTrends.add(productHisTrend);
        }
        BIHistoryDataAdapter biHistoryDataAdapter=new BIHistoryDataAdapter(this,productHisTrends,buyUnitNm,cateId);

        final ExpandableListView expandableListView=(ExpandableListView)findViewById(R.id.his_expand_list_view);
        expandableListView.setAdapter(biHistoryDataAdapter);

        expandableListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (productHisTrends.size() + 1) * 100+200 ));

        final BIHistoryDataAdapter biHistoryDataAdapter2=biHistoryDataAdapter;
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < biHistoryDataAdapter2.getGroupCount(); i++) {
                    if (groupPosition != i) {
                        expandableListView.collapseGroup(i);
                    }
                }
            }
        });
    }

    /**
     * 初始化便签栏
     */
    private void initTags(final String newGoodsId){
        //标签栏
        final List<String> tags = new ArrayList<>();

        List<ProductComments> productCommentses=DataSupport.where("newGoodsId='" + newGoodsId + "'").find(ProductComments.class);
        for(int i=0;i<productCommentses.size();i++){
            tags.add(productCommentses.get(i).getCommentsDetail());
        }

        TagCloudView tagCloudView1 = (TagCloudView) findViewById(R.id.tag_cloud_view);
        tagCloudView1.setTags(tags);
        if (orderApplication.getRole().equals("1")) {
            tagCloudView1.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
                @Override
                public void onTagClick(final int position) {
                    new AlertDialog.Builder(DetailQtyActivity.this)
                            .setTitle("确认删除‘" + tags.get(position) + "’标签？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    DataSupport.deleteAll(ProductComments.class, "newGoodsId='" + newGoodsId + "' and commentsDetail='" + tags.get(position) + "'");
                                    initTags(newGoodsId);
                                }
                            })
                            .setNegativeButton("取消", null).show();

                }
            });
        }
    }

    /**
     * 初始化商品属性栏
     * @param newGoodsId
     */
    private void initProAttrs(String newGoodsId){
        //商品属性栏
        ProductInfo productInfo=DataSupport.where("newGoodsId=?",newGoodsId).find(ProductInfo.class).get(0);
        TextView goodsNoTv=(TextView)tableLayout.findViewById(R.id.goodsNo);
        goodsNoTv.setText(productInfo.getGoodsNo());
        //名称
        TextView modelNameTv=(TextView)tableLayout.findViewById(R.id.modelName);
        modelNameTv.setText(productInfo.getModelName());
        //系列
        TextView marketingDivisionTv=(TextView)tableLayout.findViewById(R.id.marketingDivision);
        marketingDivisionTv.setText(productInfo.getMarketingDivisionDesc());
        //大类
        TextView divisionTv=(TextView)tableLayout.findViewById(R.id.division);
        divisionTv.setText(productInfo.getDivisionDesc());
        //POP
        TextView popTv=(TextView)tableLayout.findViewById(R.id.pop);
        popTv.setText(productInfo.getPopDesc());
        //款式
        TextView categoryTv=(TextView)tableLayout.findViewById(R.id.category);
        categoryTv.setText(productInfo.getCategoryDesc());
        //上市日期
        TextView eastLaunchTv=(TextView)tableLayout.findViewById(R.id.eastLaunch);
        eastLaunchTv.setText(productInfo.getEastLaunch());
        //支持类型
        TextView supportStyleTv=(TextView)tableLayout.findViewById(R.id.supportStyle);
        supportStyleTv.setText(productInfo.getSupportStyle());
        //性别
        TextView genderTv=(TextView)tableLayout.findViewById(R.id.gender);
        genderTv.setText(productInfo.getGenderDesc());
        //面料
        TextView compositionTv=(TextView)tableLayout.findViewById(R.id.composition);
        compositionTv.setText(productInfo.getCompositionDesc());
    }



    /**
     * 改变计划剩余提示框内容
     */
    private void changeHintTv(){
        TextView hintTv=(TextView)findViewById(R.id.header_hint_detail);
        String condStr="1=1 ";
        String condStr2=" ";

        //大类
        condStr += "and proCateId = '" + list.get(0).getDivision() + "' ";
        condStr2+="and division = '" + list.get(0).getDivision() + "' ";

        //系列
        condStr += "and proSeriesId = '" + list.get(0).getMarketingDivision() + "' ";
        condStr2 += "and marketingDivision = '" + list.get(0).getMarketingDivision() + "' ";

        //性别
        condStr += "and proGenderId = '" + list.get(0).getGender() + "' ";
        condStr2 += "and gender = '" + list.get(0).getGender() + "' ";

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


            int inOrdSal=cursor.getInt(3);
            int orderAmt=cursor.getInt(5);
            text+=(new BigDecimal(orderAmt)).toPlainString()+"/"+(new BigDecimal(inOrdSal)).toPlainString();
            text+="       ";

            SpannableString spanttt = new SpannableString(text);
            if(inOrdSal<orderAmt){
                spanttt.setSpan(new ForegroundColorSpan(Color.rgb(253, 78, 64)), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            hintTv.append(spanttt);
        }

    }


    /**
     * 设置精英买手权限
     */
    private void setElite(){
        BootstrapButton bootstrapButton=(BootstrapButton)findViewById(R.id.addTag);
        final BootstrapEditText bootstrapEditText = (BootstrapEditText) findViewById(R.id.tagEditText);

        if(orderApplication.getRole().equals("1")){
            bootstrapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TagCloudView tagCloudView1 = (TagCloudView) findViewById(R.id.tag_cloud_view);


                    String str = bootstrapEditText.getText() + "";
                    if (!str.equals("")) {
                        ProductComments productComment = new ProductComments();
                        productComment.setUserid(orderApplication.getUserId());
                        productComment.setCommentsDetail(str);
                        productComment.setNewGoodsID(newGoodsId);
                        productComment.setCommentsTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        productComment.save();

                        tagCloudView1.addTag(str);
                        bootstrapEditText.setText("");
                    }

                }
            });
        }else{
            bootstrapButton.setVisibility(View.GONE);
            bootstrapEditText.setVisibility(View.GONE);
            TextView takePhotoTv=(TextView)findViewById(R.id.takePhoto);
            takePhotoTv.setVisibility(View.GONE);
            TextView deletePicTv=(TextView)findViewById(R.id.deletePic);
            deletePicTv.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /*
    * 定义下拉刷新接口
    */
    public interface OnRefreshListener {
        public void onRefresh();
    }



    //加载数据
    private List<ProductInfo> getData() {

        if((modelNo!=null)&&(!modelNo.equals(""))){
            ClusterQuery clusterQuery = DataSupport.where("modelNo=? ", modelNo);


            List<ProductInfo> productInfoList = clusterQuery.find(ProductInfo.class);


            return productInfoList;
        }else{
            return null;
        }

    }


    public void save(Bitmap bitmap, String filenm) {
        FileOutputStream out = null;
        BufferedOutputStream bos = null;
        try {
            out = openFileOutput(filenm, Context.MODE_PRIVATE);
            bos = new BufferedOutputStream(out);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    final int TAKE_PICTURE = 1;
    final int CROP_PHOTO=2;

    private Uri imageUri;
    //打开照相程序
    public void showTakePic(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File picFile=new File(Environment.getExternalStorageDirectory(),"topsports_order_pic");
        try {
            if(picFile.exists()){picFile.delete();}
            picFile.createNewFile();
        }catch (Exception e){
            e.printStackTrace();
        }

        imageUri=Uri.fromFile(picFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode != RESULT_OK)
//            return;

        switch (requestCode) {
            case TAKE_PICTURE:
                int degree= RocTools.getBitmapDegree(Environment.getExternalStorageDirectory() + "/topsports_order_pic");

                Intent intent = new Intent("com.android.camera.action.CROP");
                try{
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    // 调用上面定义的方法计算inSampleSize值
                    options.inSampleSize = 4;
                    // 使用获取到的inSampleSize值再次解析图片
                    options.inJustDecodeBounds = false;
                    Bitmap bitmapOrign = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri),null,options);
                    Bitmap bitmap=RocTools.rotateBitmapByDegree(bitmapOrign,degree);
                    if (bitmap != null) {
                        String filenm=goodsNo+"_"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".jpg";
                        saveBitmap(goodsNo, filenm, bitmap);

                        ProductPic productPic=new ProductPic();
                        productPic.setOrderPlanNo(orderApplication.getOrderMeetingNo());
                        productPic.setGoodsNo(goodsNo);
                        productPic.setImgName(filenm);
                        productPic.setStatus("1");
                        productPic.save();

//                        ImageView imageView=new ImageView(this);
//                        imageView.setImageBitmap(bitmap);
//                        slideShowView.imageViewsList.add(imageView);
//                        slideShowView.slideViewPagerAdapter.notifyDataSetChanged();
                        initSlideShowView(goodsNo);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
//                intent.setDataAndType(imageUri,"image/*");
//                intent.putExtra("scale", true);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
//                startActivityForResult(intent, CROP_PHOTO);
//                break;
            case CROP_PHOTO:
//                try{
//                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//                    if (bitmap != null) {
//                        saveBitmap(goodsNo, bitmap);
//                        ImageView imageView=new ImageView(this);
//                        imageView.setImageBitmap(bitmap);
//                        slideShowView.imageViewsList.add(imageView);
//                        slideShowView.slideViewPagerAdapter.notifyDataSetChanged();
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
                break;
        }
    }

    public void deletePic(View view){
        if(fileNmList.size()==0){return;}
//        if(slideShowView.currentItem==-1||slideShowView.currentItem==0){
        if(slideShowView.currentItem==-1){
            new AlertDialog.Builder(this).setTitle("提示").setMessage("第一张图片不能删除").setPositiveButton("确定",null).show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailQtyActivity.this);
            builder.setTitle("你确定要删除吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    int currentItem=slideShowView.currentItem;
                    File file=new File(getFilesDir()+"/" +fileNmList.get(currentItem));
                    if(file.exists()) {
                        if(file.isFile()){
                            try {
                                file.delete();
//                        DataSupport.deleteAll(ProductPic.class,"imgName=?",fileNmList.get(currentItem));
                                List<ProductPic> productPicList=DataSupport.where("imgName=?", fileNmList.get(currentItem)).find(ProductPic.class);
                                if(productPicList!=null&&productPicList.size()>0){
                                    ProductPic productPic=productPicList.get(0);
                                    if(productPic.getStatus().equals("1")){
                                        productPic.delete();
                                    }else{
                                        productPic.setStatus("2");
                                        productPic.save();
                                    }
                                }
                                slideShowView.currentItem=0;
                                initSlideShowView(goodsNo);
                            }catch (Exception e){
                                e.printStackTrace();
                                Log.e("TEST",e.getMessage());
                            }

                        }
                    }



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


        return;

    }

    public void saveBitmap(String goodsNo,String filenm,Bitmap bitmap){
        String path=getFilesDir().toString();

        FileOutputStream out=null;
        BufferedOutputStream bos=null;
        try{

//            File temp=new File(getFilesDir()+"/takePic");
//            if(!temp.exists()){
//                temp.mkdirs();
//            }
//            File file=new File(getFilesDir()+"/takePic/"+filenm+".jpg");
            File file=new File(getFilesDir()+"/"+filenm);
//            if(!file.exists()){file.createNewFile();}
            out=new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();


        }catch(Exception e){
            Log.e("TEST",e.getMessage());
            e.printStackTrace();
        }finally {
            try{
                if(bos!=null){
                    bos.close();
                }
                if(out!=null){
                    out.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    /**
     * 历史数据排序
     * @param view
     */
    public void sortHisData(View view){
        TextView textViewSal=(TextView)findViewById(R.id.sort_sal);
        TextView textViewIn=(TextView)findViewById(R.id.sort_in);
        TextView textViewSalOut=(TextView)findViewById(R.id.sort_sal_out);

        String sortStr="";

        String sortType=view.getTag()==null?"":view.getTag().toString();

        TextView tv=(TextView)view;
        if(sortType.equals("")||sortType.equals("asc")){
            sortType="desc";
            view.setTag("desc");
            tv.setText(tv.getText().toString().substring(0,1)+"🔽");
        }else if(sortType.equals("desc")){
            sortType="asc";
            view.setTag("asc");
            tv.setText(tv.getText().toString().substring(0, 1) + "🔼");
        }


        switch (view.getId())
        {
            case R.id.sort_in:
                sortStr=" order by sum(totalSalPrmAmt3)+sum(invAmt3) ";
                textViewSal.setText("销↕️");
                textViewSal.setTag("");
                textViewSalOut.setText("罄↕️");
                textViewSalOut.setTag("");
                break;
            case R.id.sort_sal:
                sortStr=" order by sum(totalSalPrmAmt3) ";
                textViewIn.setText("进↕️");
                textViewIn.setTag("");
                textViewSalOut.setText("罄↕️");
                textViewSalOut.setTag("");
                break;
            case R.id.sort_sal_out:
                sortStr=" order by sum(totalSalPrmAmt3)*100/(sum(totalSalPrmAmt3)+sum(invAmt3)) ";
                textViewSal.setText("销↕️");
                textViewSal.setTag("");
                textViewIn.setText("进↕️");
                textViewIn.setTag("");
                break;
            default:
                break;
        }


        initExpandList(sortStr + sortType);
    }

    class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context,Intent intent){
            Bundle bundle=intent.getExtras();
            if(bundle!=null){

            }else{
                handlerHint.sendMessage(new Message());
            }

        }
    }
    private Handler handlerHint=new Handler(){
        public void handleMessage(Message msg){
            changeHintTv();
        }
    };

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadCastReceiver);
    }

}
