package com.topsports.tootwo2.widget.autolistview.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.text.InputType;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.topsports.tootwo2.helper.DiskLruCache;
import com.topsports.tootwo2.model.ProductInfo;
import com.topsports.tootwo2.model.ProductOrderInfo;
import com.topsports.tootwo2.model.ProductPic;
import com.topsports.tootwo2.order.R;
import com.topsports.tootwo2.widget.customRadio.CheckboxGroup;
import com.topsports.tootwo2.widget.tagview.FlowLayout;

import org.litepal.crud.DataSupport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhang.p on 2015/8/21.
 */
public class OrderSearchAdapter extends BaseAdapter {

    private final String proPicUrl="http://192.168.3.242/test/16Q2/";

    /**
     * 记录所有正在下载或等待下载的任务。
     */
    private Set<BitmapWorkerTask> taskCollection;
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
    private String flag[];
    private int ItemIDs[];
    private String ItemIDs2[];
    private Context context;

    //用户确定editText的光标位置
    private int touchedPosition=-1;
    private String editTextTag="";

    public OrderSearchAdapter(Context context, List<ProductInfo> list,
                              int layoutID, String flag[], int ItemIDs[]) {
        this.context=context;
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
        this.layoutID = layoutID;
        this.flag = flag;
        this.ItemIDs = ItemIDs;


        taskCollection = new HashSet<BitmapWorkerTask>();
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


            viewHolder.checkboxGroup=(CheckboxGroup)convertView.findViewById(R.id.checkboxGroup);
            viewHolder.checkboxGroup_add=(CheckboxGroup)convertView.findViewById(R.id.checkboxGroup_add);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ProductInfo productInfo=list.get(position);


        //图片
        viewHolder.imageView.setTag(productInfo.getGoodsNo());
        if(productInfo.getPop().equals("1")){
            //viewHolder.imageView.setBackgroundResource(R.drawable.bg_border1);

        }else{
            //viewHolder.imageView.setBackground(null);

        }
        viewHolder.imageView.setImageResource(R.drawable.roc);
        loadBitmaps(viewHolder.imageView, productInfo.getGoodsNo(),productInfo.getPop());

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

//        viewHolder.checkboxGroup.clearCheck();
//        viewHolder.checkboxGroup_add.clearCheck();
        //选量
        String level=productInfo.getOrderLevel()==null?"":productInfo.getOrderLevel();
        viewHolder.checkboxGroup.setOnCheckedChangeListener(null);
        viewHolder.checkboxGroup_add.setOnCheckedChangeListener(null);

        if(level!=null&&!level.equals("")){
            int checkboxId=context.getResources().getIdentifier("checkbox_"+level,"id",context.getPackageName());
            viewHolder.checkboxGroup.check(checkboxId);
            if(level.equals("X")){
                viewHolder.checkboxGroup_add.setEnabled(false);
            }else{
                viewHolder.checkboxGroup_add.setEnabled(true);
            }
        }else {
            viewHolder.checkboxGroup_add.setEnabled(false);
            if(viewHolder.checkboxGroup.getCheckedRadioButtonId()!=-1){
                viewHolder.checkboxGroup.clearCheck();
            }
        }


        //量+-
        String plusOrMinus="";
        if(!level.equals("")){
            plusOrMinus=productInfo.getOrderLevelPlus()==null?"":productInfo.getOrderLevelPlus();
            if(!plusOrMinus.equals("")){
                String plusOrMinusStr="";
                if(plusOrMinus.equals("+")){
                    plusOrMinusStr="checkbox_plus";
                }else{
                    plusOrMinusStr="checkbox_minus";
                }
                int checkboxId=context.getResources().getIdentifier(plusOrMinusStr,"id",context.getPackageName());
                viewHolder.checkboxGroup_add.check(checkboxId);
//                ((CheckBox)viewHolder.checkboxGroup_add.findViewById(checkboxId)).setChecked(true);
            }else{
                if(viewHolder.checkboxGroup_add.getCheckedRadioButtonId()!=-1){
                    viewHolder.checkboxGroup_add.clearCheck();
                }
            }

        }else {
//            viewHolder.checkboxGroup_add.setEnabled(false);
            if(viewHolder.checkboxGroup_add.getCheckedRadioButtonId()!=-1){
                viewHolder.checkboxGroup_add.clearCheck();
            }
        }




        //增加控件监听
        addListener(viewHolder, position);


       
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
//        CustomRadioButton itemNoneBtn;
//        LinearLayout linearLayout;
//        LinearLayout linearLayout2;

        CheckboxGroup checkboxGroup;
        CheckboxGroup checkboxGroup_add;
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
                        if(pop.equals("1")){
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
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
//                // 调用上面定义的方法计算inSampleSize值
//                options.inSampleSize = 4;
//                // 使用获取到的inSampleSize值再次解析图片
//                options.inJustDecodeBounds = false;
//                bitmap = BitmapFactory.decodeStream(is,new Rect(),options);
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
     * 取消所有正在下载或等待下载的任务。
     */
    public void cancelAllTasks() {
        if (taskCollection != null) {
            for (BitmapWorkerTask task : taskCollection) {
                task.cancel(false);
            }
        }
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

        //选量A,B,C,D
        final CheckboxGroup checkboxGroup=viewHolder.checkboxGroup;
        //选+-
        final CheckboxGroup checkboxGroup_add=viewHolder.checkboxGroup_add;


        checkboxGroup.setOnCheckedChangeListener(new CheckboxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CheckboxGroup group, int checkedId) {
                checkboxGroup_add.setEnabled(true);
                ProductInfo productInfo = list.get(position);

                if (checkedId == -1) {
                    productInfo.setOrderLevel("");
                    productInfo.setOrderLevelPlus("");
                    checkboxGroup_add.clearCheck();
                    checkboxGroup_add.setEnabled(false);
                } else {
                    CheckBox checkBox = (CheckBox) group.findViewById(checkedId);
                    String level = checkBox.getText().toString();
                    productInfo.setOrderLevel(level);

                    if (level.equals("X")) {
                        checkboxGroup_add.clearCheck();
                        checkboxGroup_add.setEnabled(false);
                    }
                }

                productInfo.save();

            }


        });

        checkboxGroup_add.setOnCheckedChangeListener(new CheckboxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CheckboxGroup group, int checkedId) {
                ProductInfo productInfo = list.get(position);
                if(checkedId==-1){
                    productInfo.setOrderLevelPlus("");
                }else{
                    CheckBox checkBox = (CheckBox) group.findViewById(checkedId);
                    String levelPlus=checkBox.getText().toString();

                    productInfo.setOrderLevelPlus(levelPlus);
                }
                productInfo.save();
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
     * 异步下载图片的任务。
     *
     * @author guolin
     */
    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        /**
         * 图片的URL地址
         */
        private String imageUrl;
        /*
         *货号
         */
        private String goodsNo;

        @Override
        protected Bitmap doInBackground(String... params) {
            goodsNo = params[0];
            imageUrl = proPicUrl+goodsNo+".jpg";
            FileDescriptor fileDescriptor = null;
            FileInputStream fileInputStream = null;
            DiskLruCache.Snapshot snapShot = null;
            try {
                // 生成图片对应的key
                final String key = goodsNo;
                // 查找key对应的缓存
                snapShot = mDiskLruCache.get(key);
                if (snapShot == null) {
                    // 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
                    DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                    if (editor != null) {
                        OutputStream outputStream = editor.newOutputStream(0);
                        if (downloadUrlToStream(imageUrl, outputStream)) {
                            editor.commit();
                        } else {
                            editor.abort();
                        }
                    }
                    // 缓存被写入后，再次查找key对应的缓存
                    snapShot = mDiskLruCache.get(key);
                }
                if (snapShot != null) {
                    fileInputStream = (FileInputStream) snapShot.getInputStream(0);
                    fileDescriptor = fileInputStream.getFD();
                }
                // 将缓存数据解析成Bitmap对象
                Bitmap bitmap = null;
                if (fileDescriptor != null) {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    // 调用上面定义的方法计算inSampleSize值
                    options.inSampleSize = 4;
                    // 使用获取到的inSampleSize值再次解析图片
                    options.inJustDecodeBounds = false;

//                    bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor,new Rect(),options);
                    bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                }
                if (bitmap != null) {
                    // 将Bitmap对象添加到内存缓存当中
                    addBitmapToMemoryCache(params[0], bitmap);
                }
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileDescriptor == null && fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            View view=mInflater.inflate(layoutID, null);
            // 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。
            ImageView imageView = (ImageView) view.findViewWithTag(goodsNo);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            taskCollection.remove(this);
        }

        /**
         * 建立HTTP请求，并获取Bitmap对象。
         *
         * @param urlString
         *        图片的URL地址
         * @return 解析后的Bitmap对象
         */
        private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
            HttpURLConnection urlConnection = null;
            BufferedOutputStream out = null;
            BufferedInputStream in = null;
            Bitmap bitmap=null;
            try {
                final URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
                out=new BufferedOutputStream(outputStream);
                bitmap=BitmapFactory.decodeStream(in);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                return true;
            } catch (final IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
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
            return false;
        }

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
