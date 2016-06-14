package com.topsports.tootwo2.widget.autolistview.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.topsports.tootwo2.helper.DiskLruCache;
import com.topsports.tootwo2.model.ProductInfo;
import com.topsports.tootwo2.model.ProductOrderInfo;
import com.topsports.tootwo2.model.ProductPic;
import com.topsports.tootwo2.order.R;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tootwo2 on 16/2/15.
 */
public class OrderProDetailQtyAdapter   extends BaseAdapter {

    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     */
    private LruCache<String, Bitmap> mMemoryCache;

    /**
     * 图片硬盘缓存核心类。
     */
    private DiskLruCache mDiskLruCache;


    private LayoutInflater mInflater;
    private List<ProductInfo> list;
    private int layoutID;
    private Context context;

    String userId;

    //用户确定editText的光标位置
    private int touchedPosition=-1;
    private String editTextTag="";

    public OrderProDetailQtyAdapter(Context context, List<ProductInfo> list,
                                 int layoutID, String userId) {
        this.context=context;
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
        this.layoutID = layoutID;
        this.userId=userId;


        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        // 设置图片缓存大小为程序最大可用内存的1/5
        int cacheSize = maxMemory / 4;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
        try {
            // 获取图片缓存路径
            File cacheDir = getDiskCacheDir(context, "pic");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            // 创建DiskLruCache实例，初始化缓存数据
            mDiskLruCache = DiskLruCache
                    .open(cacheDir, 1, 1, 400 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return 0;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (null == convertView)
        {
            viewHolder = new ViewHolder();

            convertView = mInflater.inflate(layoutID, null);

            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.img);

            viewHolder.modelNoTv = (TextView) convertView.findViewById(R.id.modelNo);
            viewHolder.modelNmTv = (TextView) convertView.findViewById(R.id.modelName);
            viewHolder.goodsNoTv = (TextView) convertView.findViewById(R.id.goodsNo);
            viewHolder.supportStyleTv = (TextView) convertView.findViewById(R.id.supportStyle);
            viewHolder.eastLaunchTv = (TextView) convertView.findViewById(R.id.eastLaunch);
//            viewHolder.itemNoneBtn=(CustomRadioButton)convertView.findViewById(R.id.iv_itemNone);
            viewHolder.custome4=(TextView)convertView.findViewById(R.id.custome4);
            viewHolder.localRp=(TextView)convertView.findViewById(R.id.localRp);
            viewHolder.directoryPage=(TextView)convertView.findViewById(R.id.directoryPage);


            viewHolder.flowLayout=(LinearLayout)convertView.findViewById(R.id.flowLayout_detail);
            convertView.setTag(viewHolder);

        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ProductInfo productInfo=list.get(position);


        //图片
        viewHolder.imageView.setTag(productInfo.getGoodsNo());

        viewHolder.imageView.setImageResource(R.drawable.roc);
        loadBitmaps(viewHolder.imageView, productInfo.getGoodsNo(), productInfo.getPop());

        //款号
        viewHolder.modelNoTv.setText(productInfo.getModelNo());
        //款名
        viewHolder.modelNmTv.setText(productInfo.getModelName());
        //产品号
        viewHolder.goodsNoTv.setText(productInfo.getGoodsNo());
        //支持类型
        viewHolder.supportStyleTv.setText(productInfo.getSupportStyle());
        //上市日
        viewHolder.eastLaunchTv.setText(productInfo.getEastLaunch());
        //精英买手建议
        viewHolder.custome4.setText(productInfo.getCustom4().equals("null")?"":productInfo.getCustom4());
        //牌价
        viewHolder.localRp.setText(productInfo.getLocalRp());
        //页码
        viewHolder.directoryPage.setText("P"+productInfo.getDirectoryPage());
        //增加控件监听
        addListener(viewHolder, position);

        final List<ProductOrderInfo> productOrderInfoList= productInfo.getProductOrderInfoList(userId);

        if(productOrderInfoList.size()>0 &&productOrderInfoList.size()<6){
            for(int i=0;i<productOrderInfoList.size();i++){
                ProductOrderInfo productOrderInfo=productOrderInfoList.get(i);
                LinearLayout linearLayout= (LinearLayout)viewHolder.flowLayout.getChildAt(i);
                linearLayout.setVisibility(View.VISIBLE);
                TextView textView=(TextView)linearLayout.getChildAt(0);
                textView.setText(productOrderInfo.getBuyerUnitName());

                EditText editText=(EditText)linearLayout.getChildAt(1);
                int qty=productOrderInfo.getOrderQty();
                if(qty==0){
                    editText.setText("");
                }else {
                    editText.setText(String.valueOf(qty));
                }
                editText.setTag(productOrderInfo.getBuyerUnitCode());
                editText.setCursorVisible(true);
                editText.clearFocus();
                editText.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction()==MotionEvent.ACTION_DOWN){
                            touchedPosition = position;
                            editTextTag = v.getTag().toString();
                        }

                        return false;
                    }
                });
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            EditText editText1 = (EditText) v;
                            for (int i = 0; i < productOrderInfoList.size(); i++) {
                                ProductOrderInfo productOrderInfo = productOrderInfoList.get(i);
                                if (productOrderInfo.getBuyerUnitCode().equals(editText1.getTag())) {
                                    if (!editText1.getText().equals(String.valueOf(productOrderInfo.getOrderQty()))){
                                        if (!editText1.getText().toString().equals("")) {
                                            productOrderInfo.setOrderQty(Integer.valueOf(editText1.getText().toString()));
                                            productOrderInfo.save();
                                        }else{
                                            productOrderInfo.setOrderQty(0);
                                            productOrderInfo.save();
                                        }
                                    }

                                }
                            }
                        }else{
                            ((EditText)v).selectAll();
                        }
                    }
                });
            }
        }else{
            viewHolder.flowLayout.removeAllViews();

        }


        if (touchedPosition == position) {
            // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
            if(viewHolder.flowLayout.getChildCount()>0){
                if(!editTextTag.equals("")){
                    EditText editText=(EditText)viewHolder.flowLayout.findViewWithTag(editTextTag);
                    editText.requestFocus();
                    editText.selectAll();

                }
            }
        }


        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //关闭软键盘
                InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(v.getWindowToken(), 0);
                touchedPosition=-1;
                editTextTag="";
                return false;
            }
        });
        return convertView;
    }

    private static class ViewHolder
    {
        ImageView imageView;
        TextView modelNoTv;
        TextView modelNmTv;
        TextView goodsNoTv;
        TextView supportStyleTv;
        TextView eastLaunchTv;
        TextView custome4;
        TextView localRp;
        TextView directoryPage;

        LinearLayout flowLayout;
    }

    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @param bitmap
     *            LruCache的键，这里传入从网络上下载的Bitmap对象。
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
     * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
     */
    public void loadBitmaps(ImageView imageView, String goodsNo,String pop) {
        try {
            List<ProductPic> productPics=DataSupport.where("goodsNo=?",goodsNo).find(ProductPic.class);
            if(productPics!=null&&productPics.size()>0){
                String imgName= productPics.get(0).getImgName();

                Bitmap bitmap = getBitmapFromMemoryCache(imgName);
//            if(bitmap==null){bitmap=getBitmapFromDiskCache(goodsNo);}
                if(bitmap==null){
                    bitmap=getBitmap(imgName);
                    if (bitmap == null) {
                        bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.roc);
                    } else {
                        //添加POP水印
                        if(pop!=null&&pop.equals("1")){
                            Bitmap mark = BitmapFactory.decodeResource(context.getResources(),R.drawable.have_to_order);
                            bitmap = createBitmap(bitmap,mark);
                        }

                        //写入缓存
                        addBitmapToMemoryCache(imgName,bitmap);
                    }
                }

                if (imageView != null && bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmap(String filenm){
        String path=context.getFilesDir().toString();

        Bitmap bitmap=null;
        try{
//            FileInputStream in=openFileInput(filenm);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // 调用上面定义的方法计算inSampleSize值
            options.inSampleSize = 4;
            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;


            bitmap=BitmapFactory.decodeFile(path+"/"+filenm);

        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;

    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 从DiskLruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    public Bitmap getBitmapFromDiskCache(String key) {
        Bitmap bitmap=null;
        try {
            DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
            if (snapShot != null) {
                InputStream is = snapShot.getInputStream(0);
                bitmap=BitmapFactory.decodeStream(is);
                addBitmapToMemoryCache(key, bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        return bitmap;
    }


    /**
     * 根据传入的uniqueName获取硬盘缓存的路径地址。
     */
    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        return new File(context.getCacheDir() + File.separator + uniqueName);
    }

    /**
     * 设置监听事件的组件
     */
    public void addListener(ViewHolder viewHolder, final int position) {
        viewHolder.imageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customImgAction(v, position);
                    }
                });


    }

    //自定义图片事件
    public void customImgAction(View v, int position) {

    }
    //自定义按钮组事件
    public void customRadioAction(String level, int position) {

    }



    /**
     * 给src图片添加 watermark水印 并返回图片
     * @param src 原图
     * @param watermark 水印
     * @return
     */
    private Bitmap createBitmap( Bitmap src, Bitmap watermark ) {
        String tag = "createBitmap";
        // Log.d( tag, "create a new bitmap" );
        if( src == null ) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        int ww = watermark.getWidth();
        int wh = watermark.getHeight();
        //create the new blank bitmap
        Bitmap newb = Bitmap.createBitmap( w, h, Bitmap.Config.ARGB_8888 );
        //创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas( newb );
        //draw src into
        cv.drawBitmap( src, 0, 0, null );//在 0，0坐标开始画入src
        //draw watermark into
        cv.drawBitmap( watermark, 0, 0, null );//在src的右下角画入水印
        //save all clip
        cv.save( Canvas.ALL_SAVE_FLAG );//保存
        //store
        cv.restore();//存储
        return newb;
    }
}
