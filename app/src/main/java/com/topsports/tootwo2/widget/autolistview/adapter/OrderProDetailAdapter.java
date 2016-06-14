package com.topsports.tootwo2.widget.autolistview.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.topsports.tootwo2.model.ProductComments;
import com.topsports.tootwo2.model.ProductInfo;
import com.topsports.tootwo2.model.ProductPic;
import com.topsports.tootwo2.order.R;
import com.topsports.tootwo2.widget.customRadio.CheckboxGroup;
import com.topsports.tootwo2.widget.customRadio.CustomRadioButton;
import com.topsports.tootwo2.widget.tagview.Tag;
import com.topsports.tootwo2.widget.tagview.TagListView;
import com.topsports.tootwo2.widget.tagview.TagView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by zhang.p on 2015/8/25.
 */

public class OrderProDetailAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private List<ProductInfo> list;
    private int layoutID;
    private Context context;

    public OrderProDetailAdapter(Context context, List<ProductInfo> list,
                                 int layoutID) {
        this.context=context;
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
        this.layoutID = layoutID;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(layoutID, null);
        ImageView iv = (ImageView) convertView.findViewById(R.id.img);
        List<ProductPic> productPics=DataSupport.where("goodsNo=?",list.get(position).getGoodsNo()).find(ProductPic.class);
        String imgName=(productPics!=null&&productPics.size()>0)?productPics.get(0).getImgName():"";
        Bitmap bitmap=getBitmap(imgName);
        if (bitmap == null) {
            iv.setImageResource(R.drawable.roc);
        }else{
            iv.setImageBitmap(bitmap);
        }

        ProductInfo productInfo=list.get(position);
        TextView goodsNoTv = (TextView) convertView.findViewById(R.id.goodsNo);
        goodsNoTv.setText(productInfo.getGoodsNo());

        TextView modelNameTv = (TextView) convertView.findViewById(R.id.modelName);
        modelNameTv.setText(productInfo.getModelName());

        TextView eastLaunchTv=(TextView)convertView.findViewById(R.id.eastLaunch);
        eastLaunchTv.setText(productInfo.getEastLaunch());

        TextView supportStyleTv=(TextView)convertView.findViewById(R.id.supportStyle);
        supportStyleTv.setText(productInfo.getSupportStyle());

        TextView custom4Tv=(TextView)convertView.findViewById(R.id.custome4);
        custom4Tv.setText(productInfo.getCustom4());

        TextView localRpTv=(TextView)convertView.findViewById(R.id.localRp);
        localRpTv.setText(productInfo.getLocalRp());

        TextView pageDirTv=(TextView)convertView.findViewById(R.id.directoryPage);
        pageDirTv.setText("P"+productInfo.getDirectoryPage());


        TagListView mTagListView = (TagListView) convertView.findViewById(R.id.tagView);
        List<Tag> mTags = new ArrayList<Tag>();
        List<ProductComments> productComments = list.get(position).getProductComments();
        if(productComments!=null){
            for (int i = 0; i < productComments.size(); i++) {
                Tag tag = new Tag();
                tag.setId(productComments.get(i).getId());
                tag.setChecked(true);
                tag.setTitle(productComments.get(i).getCommentsDetail());
                mTags.add(tag);
            }
        }
        mTagListView.setDeleteMode(true);
        mTagListView.setTags(mTags);
        final TagListView tagListView=mTagListView;
        mTagListView.setOnTagClickListener(new TagListView.OnTagClickListener() {
            @Override
            public void onTagClick(TagView tagView, final Tag tag) {
                final  Tag tag1=tag;
                new AlertDialog.Builder(context).setTitle("确认删除吗？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tagListView.removeTag(tag1);
                                DataSupport.delete(ProductComments.class,tag1.getId());
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();

            }
        });

        CheckboxGroup checkboxGroup=(CheckboxGroup)convertView.findViewById(R.id.checkboxGroup);
        CheckboxGroup checkboxGroup_add=(CheckboxGroup)convertView.findViewById(R.id.checkboxGroup_add);


        String level=list.get(position).getOrderLevel()==null?"":list.get(position).getOrderLevel();


        if(level!=null&&!level.equals("")){
            int checkboxId=context.getResources().getIdentifier("checkbox_"+level,"id",context.getPackageName());
            checkboxGroup.check(checkboxId);
            if(level.equals("X")){
                checkboxGroup_add.setEnabled(false);
            }else{
                checkboxGroup_add.setEnabled(true);
            }
        }else {
            checkboxGroup_add.setEnabled(false);
            if(checkboxGroup.getCheckedRadioButtonId()!=-1){
                checkboxGroup.clearCheck();
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
                checkboxGroup_add.check(checkboxId);
//                ((CheckBox)viewHolder.checkboxGroup_add.findViewById(checkboxId)).setChecked(true);
            }else{
                if(checkboxGroup_add.getCheckedRadioButtonId()!=-1){
                    checkboxGroup_add.clearCheck();
                }
            }

        }else {
//            viewHolder.checkboxGroup_add.setEnabled(false);
            if(checkboxGroup_add.getCheckedRadioButtonId()!=-1){
                checkboxGroup_add.clearCheck();
            }
        }

        addListener(convertView, position);
        return convertView;
    }

    /**
     * 设置监听事件的组件
     */
    public void addListener(View convertView, final int position) {
        ((ImageView) convertView.findViewById(R.id.img)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customImgAction(v, position);
                    }
                });
//        ((RadioGroup) convertView.findViewById(R.id.radioGroup)).setOnCheckedChangeListener(
//                new RadioGroup.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(RadioGroup arg0, int arg1) {
//                        int radioButtonId = arg0.getCheckedRadioButtonId();
//                        RadioButton rb = (RadioButton) arg0.findViewById(radioButtonId);
//
//                        customRadioAction(rb.getText().toString(), position);
//                    }
//                });
        ((Button)convertView.findViewById(R.id.addtag)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addTag(v, position);
                    }
                });

        final CheckboxGroup checkboxGroup=(CheckboxGroup)convertView.findViewById(R.id.checkboxGroup);
        final CheckboxGroup checkboxGroup_add=(CheckboxGroup)convertView.findViewById(R.id.checkboxGroup_add);

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

    //自定义图片事件
    public void customImgAction(View v, int position) {

    }
    //自定义按钮组事件
    public void customRadioAction(String level, int position) {

    }

    public void addTag(View v,int position){

    }

}