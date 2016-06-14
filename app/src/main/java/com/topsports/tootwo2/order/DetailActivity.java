package com.topsports.tootwo2.order;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.topsports.tootwo2.app.OrderApplication;
import com.topsports.tootwo2.helper.RocTools;
import com.topsports.tootwo2.model.ProductBIData;
import com.topsports.tootwo2.model.ProductComments;
import com.topsports.tootwo2.model.ProductInfo;
import com.topsports.tootwo2.model.ProductPic;
import com.topsports.tootwo2.widget.autolistview.adapter.OrderProDetailAdapter;
import com.topsports.tootwo2.widget.autolistview.widget.AutoListView;
import com.topsports.tootwo2.widget.slideView.SlideShowView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.ClusterQuery;
import org.litepal.crud.DataSupport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.os.StrictMode.setThreadPolicy;


public class DetailActivity extends AppCompatActivity implements AutoListView.OnRefreshListener,
        AutoListView.OnLoadListener {
    OrderApplication orderApplication;

    private String proPicUrl = "http://192.168.4.217/test/pic/";

    String goodsNo;

    String modelNo;
    //商品列表
    private AutoListView listView;
    //数据
    private List<ProductInfo> list = new ArrayList<ProductInfo>();
    //适配器
    private OrderProDetailAdapter adapter;

    //图片轮播器
    private SlideShowView slideShowView;

    //图片文件名数组
    private List<String> fileNmList;


    //page页数
    private int pageId = 0;
    //page大小
    private int pageSize = 8;
    //刷新（追加）数据
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            List<ProductInfo> result = (List<ProductInfo>) msg.obj;
            switch (msg.what) {
                case AutoListView.REFRESH:
                    listView.onRefreshComplete();
                    list.clear();
                    list.addAll(result);
                    break;
                case AutoListView.LOAD:
                    listView.onLoadComplete();

                    list.addAll(result);
                    break;
            }

            goodsNo=list.get(0).getGoodsNo();
            initSlideShowView(goodsNo);
            listView.setResultSize(result.size());
            adapter.notifyDataSetChanged();
        }

    };


    /**
     * 查询条件的文本值
     */
    private String searchViewStr="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //从intent中获取model号
        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // Handle the normal search query case
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i("TEST","1");
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // Handle a suggestions click (because the suggestions all use ACTION_VIEW)
            Uri data = intent.getData();
            Log.i("TEST","2");
        }

        modelNo = intent.getStringExtra(SearchActivity.EXTRA_MESSAGE);

        final TextView toolbar_modelNo = (TextView)findViewById(R.id.toolbar_modelNo);

        toolbar_modelNo.setText(modelNo);

        orderApplication=(OrderApplication)getApplication();

        //设置可以在主线程调用网络通讯
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        setThreadPolicy(policy);

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
        goodsNo=list.get(0).getGoodsNo();
        //设置listview
        initListView();;

        //图片轮播器
        initSlideShowView(goodsNo);

        //搜索框
        final SearchView mSearchView=(SearchView)findViewById(R.id.search_view);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if ((!query.equals("")) && (query != null)) {
                    mSearchView.clearFocus();

                    List<ProductInfo> productInfoList = DataSupport.where("goodsNo like '%" + query + "%' and userid='" + orderApplication.getUserId() + "'").find(ProductInfo.class);
                    if(productInfoList!=null&&productInfoList.size()>0){
                        modelNo=productInfoList.get(0).getModelNo();
                        loadData(AutoListView.REFRESH);
                        toolbar_modelNo.setText(modelNo);

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
                String queryStr=cursor.getString(1);

                mSearchView.setQuery(queryStr,true);
                mSearchView.clearFocus();

                List<ProductInfo> productInfoList = DataSupport.where("goodsNo like '%" + queryStr + "%' and userid='" + orderApplication.getUserId() + "'").find(ProductInfo.class);
                modelNo=productInfoList.get(0).getModelNo();
                loadData(AutoListView.REFRESH);
                toolbar_modelNo.setText(modelNo);
                return true;
            }
        });
    }

    public Cursor getCursor(String queryStr){

        Cursor cursor = DataSupport.findBySQL("select id as _id,goodsno from productinfo where userid='" + orderApplication.getUserId() + "' and goodsNo like '%" + queryStr + "%' limit 5");
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
                DetailActivity.this.finish();
            }
        });

        TextView orderInfoTv = (TextView) findViewById(R.id.orderInfo);

        OrderApplication orderApplication=(OrderApplication)getApplication();
        String areaName=orderApplication.getAreaName()==null?"":orderApplication.getAreaName();
        String userName=orderApplication.getName()==null?"":orderApplication.getName();

        orderInfoTv.setText("AD16Q2 | AD | "+areaName+" | "+userName);

        toolbar.setTitle("");

        setSupportActionBar(toolbar);

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
            Bitmap bitmap=BitmapFactory.decodeFile(getFilesDir()+"/"+imgName);
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
        listView = (AutoListView) findViewById(R.id.detail_list_View);

        adapter = new OrderProDetailAdapter(this, list, R.layout.adapter_detail) {

            public void customImgAction(View v, int position) {
                if(goodsNo.equals(list.get(position).getGoodsNo())){
                    showPopView(position);
                }else {
                    goodsNo=list.get(position).getGoodsNo();
                    //initImage3DSwitchView(goodsNo);
                    initSlideShowView(goodsNo);
                }

            }

            public void customRadioAction(String level, int position) {
                String proid = list.get(position).getGoodsNo();
                ProductInfo productInfo = DataSupport.where("goodsNo=?", proid).find(ProductInfo.class).get(0);
                ProductInfo productInfo2 = new ProductInfo();
                productInfo2.setOrderLevel(level);
                productInfo2.update(productInfo.getId());

            }

            public void addTag(final View v, final int position){

                final EditText editText=new EditText(DetailActivity.this);

                new AlertDialog.Builder(DetailActivity.this)
                        .setTitle("请输入标签")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                String str = editText.getText().toString();
                                if (!str.equals("")) {
                                    ProductInfo productInfo=list.get(position);
                                    ProductComments productComment= new ProductComments();
                                    productComment.setUserid(orderApplication.getUserId());
                                    productComment.setCommentsDetail(str);
                                    productComment.setNewGoodsID(productInfo.getNewGoodsId());
                                    productComment.setCommentsTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    productComment.save();

                                    adapter.notifyDataSetChanged();
                                }
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        };

        listView.setAdapter(adapter);
        listView.setOnRefreshListener(this);
        listView.setOnLoadListener(this);
        if (list.size() < 8) {
            listView.setResultSize(list.size());
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
        pageId = 0;
        loadData(AutoListView.REFRESH);
    }

    //加载数据
    private void loadData(final int what) {
        Message msg = handler.obtainMessage();
        msg.what = what;
        msg.obj = getData();

        handler.sendMessage(msg);
    }

    //加载数据
    private List<ProductInfo> getData() {

        if((modelNo!=null)&&(!modelNo.equals(""))){
            ClusterQuery clusterQuery = DataSupport.where("modelNo=? and userid='"+orderApplication.getUserId()+"'", modelNo);


            List<ProductInfo> productInfoList = clusterQuery.limit(pageSize).offset(pageId * pageSize).find(ProductInfo.class);


            return productInfoList;
        }else{
            return null;
        }

    }

    //获取bitmap
    public Bitmap getBitmap(String filenm) {
        String path = getFilesDir().toString();

        Bitmap bitmap = null;
        try {
//            FileInputStream in=openFileInput(filenm);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // 调用上面定义的方法计算inSampleSize值
            options.inSampleSize = 4;
            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;


            bitmap = BitmapFactory.decodeFile(path + "/" + filenm);

            if (bitmap == null) {
                bitmap = getBitFromWeb(filenm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    //从网络获取bitmap
    private Bitmap getBitFromWeb(String filenm) {
        HttpURLConnection urlConnection = null;
        Bitmap bit = null;
        try {
            String str = proPicUrl + filenm;
            URL url = new URL(str);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            bit = BitmapFactory.decodeStream(in);
            save(bit, filenm);
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("TEST", e.getMessage().toString());
        } finally {
            urlConnection.disconnect();
        }
        return bit;
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
                int degree=RocTools.getBitmapDegree(Environment.getExternalStorageDirectory()+"/topsports_order_pic");

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
        if(slideShowView.currentItem==-1){
            new AlertDialog.Builder(this).setTitle("提示").setMessage("第一张图片不能删除").setPositiveButton("确定",null).show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
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

    Boolean popViewIsShowed=false;
    /**
     * 打开属性框
     * @param position
     */
    public void showPopView(int position){
        if(popViewIsShowed==false){
            View popupView=getLayoutInflater().inflate(R.layout.pop_attr, null);

            PopupWindow mPopupWindow=new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
            mPopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp=getWindow().getAttributes();
                    lp.alpha=1f;
                    getWindow().setAttributes(lp);
                    popViewIsShowed=false;
                }
            });
            //设置背景色
//        WindowManager.LayoutParams lp=getWindow().getAttributes();
//        lp.alpha=0.4f;
//        getWindow().setAttributes(lp);

            mPopupWindow.showAtLocation(getCurrentFocus(), Gravity.BOTTOM,0,0);
            popViewIsShowed=true;
            setPopAttrData(popupView,position);
        }else{
            popViewIsShowed=false;
        }


    }



    private void setPopAttrData(View view,int position){
        ProductInfo productInfo=list.get(position);

        TextView goodsNo=(TextView)view.findViewById(R.id.goodsNo);
        goodsNo.setText(productInfo.getGoodsNo());

        TextView modelName=(TextView)view.findViewById(R.id.modelName);
        modelName.setText(productInfo.getModelName());

        TextView marketingDivision=(TextView)view.findViewById(R.id.marketingDivision);
        marketingDivision.setText(productInfo.getMarketingDivisionDesc());

        TextView division=(TextView)view.findViewById(R.id.division);
        String divisionId=productInfo.getDivision();
        String divisionStr="";
        if(divisionId.equals("APP")){
            divisionStr="服";
        }else if(divisionId.equals("FTW")){
            divisionStr="鞋";
        }else if(divisionId.equals("ACC")){
            divisionStr="配";
        }
        division.setText(divisionStr);

        TextView pop=(TextView)view.findViewById(R.id.pop);
        pop.setText(productInfo.getPopDesc());

        TextView category=(TextView)view.findViewById(R.id.category);
        category.setText(productInfo.getCategoryDesc());

        TextView eastLaunch=(TextView)view.findViewById(R.id.eastLaunch);
        eastLaunch.setText(productInfo.getEastLaunch());

        TextView supportStyle=(TextView)view.findViewById(R.id.supportStyle);
        supportStyle.setText(productInfo.getSupportStyle());

        TextView gender=(TextView)view.findViewById(R.id.gender);
        String genderString = productInfo.getGenderDesc();
        gender.setText(genderString);

        //gender.setText(productInfo.getGender());

        TextView composition=(TextView)view.findViewById(R.id.composition);
        composition.setText(productInfo.getCompositionDesc());

        TextView localRp=(TextView)view.findViewById(R.id.localRp);
        localRp.setText("￥ " + productInfo.getLocalRp());

        TextView directoryPage=(TextView)view.findViewById(R.id.directoryPage);
        directoryPage.setText(productInfo.getDirectoryPage());

        TextView goodsDesc=(TextView)view.findViewById(R.id.goodsDesc);
        goodsDesc.setText(productInfo.getGoodsDescribe());

        TextView technology=(TextView)view.findViewById(R.id.technology);
        technology.setText(productInfo.getTechnologyDesc());

        TextView brdSeason=(TextView)view.findViewById(R.id.brdSeason);
        brdSeason.setText(productInfo.getBrdSeason());

        initChartWebView(view);
    }


    private void initChartWebView(View view){
        WebView charts = (WebView) view.findViewById(R.id.charts);
        charts.setWebChromeClient(new WebChromeClient());
        charts.getSettings().setJavaScriptEnabled(true);

        charts.addJavascriptInterface(new JsObject(), "myjs");
        charts.loadUrl("file:///android_asset/html/charts.html");
    }

    public class JsObject {
        //Html调用此方法传递图表数据
        @JavascriptInterface
        public String getChartsData(String newgoodsId) {
            List<ProductInfo> productInfoList=DataSupport.where("goodsNo='"+goodsNo+"'").find(ProductInfo.class);
            ProductInfo productInfo=productInfoList.get(0);

            List<ProductBIData> productBIDataList=new ArrayList<>();
            String queryStr="";
            queryStr+="groupCode='"+productInfo.getDivision()+ "' ";
            queryStr+="and gender='"+productInfo.getGender()+ "' ";
            queryStr+="and brdSeason='"+productInfo.getBrdSeason()+"' ";
            queryStr+="and priceArea='"+productInfo.getPricePoint()+"' ";
            queryStr+="and series='"+productInfo.getMarketingDivision()+ "' ";
            queryStr+="and style='"+productInfo.getCategory()+ "' ";


            productBIDataList=DataSupport.where(queryStr).order("cast( totalSaleQty3 as INTEGER) desc").limit(7).find(ProductBIData.class);
            JSONArray jsonArray=new JSONArray();
            for(int i=0;i<productBIDataList.size();i++){
                JSONObject jsonObject=new JSONObject();
                ProductBIData productBIData=productBIDataList.get(i);
                Field[] fields=productBIData.getClass().getDeclaredFields();
                for(int j=0;j<fields.length;j++){
                    try{
                        String name=fields[j].getName();
                        name=name.substring(0,1).toUpperCase()+name.substring(1);
                        Method m=productBIData.getClass().getMethod("get"+name);
                        String a=(String)m.invoke(productBIData);
                        jsonObject.put(fields[j].getName(),a);
                    }catch (Exception e){
                        Log.e("TEST",e.getMessage());
                    }
                }


                jsonArray.put(jsonObject);
            }

            String json = jsonArray.toString();
            // 调用JS中的方法
            return json;
        }

        //Html调用此方法传递图片数据
        @JavascriptInterface
        public String getImgByGoodsNo(String goodsNo){

            return "";
        }

        //确定newGoodsId
        @JavascriptInterface
        public String getNewGoodsId(){
            return "";
        }

    }
}
