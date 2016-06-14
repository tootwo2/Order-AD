package com.topsports.tootwo2.widget.autolistview.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.topsports.tootwo2.helper.MathTools;
import com.topsports.tootwo2.model.ProductHisTrend;
import com.topsports.tootwo2.order.R;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tootwo2 on 16/2/19.
 */
public class BIHistoryDataAdapter extends BaseExpandableListAdapter {

    private Context context;

    private List<ProductHisTrend> groups;

    /**
     * 买货单位集合
     */
    private String buyerUnits;

    private String cateId;

    public BIHistoryDataAdapter(Context context,List groups,String buyerUnits,String cateId){
        this.context=context;
        this.groups=groups;
        this.buyerUnits=buyerUnits;
        this.cateId=cateId;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expand_his_group_layout, null);
            viewHolder=new ViewHolder();
            viewHolder.imageView=(ImageView)convertView.findViewById(R.id.his_img_view);
            //货号
            viewHolder.hisProIdTv=(TextView)convertView.findViewById(R.id.his_pro_id);
            //销
            viewHolder.hisProSalTv=(TextView)convertView.findViewById(R.id.his_pro_sal_qty);
            //进
            viewHolder.hisProInTv=(TextView)convertView.findViewById(R.id.his_pro_in_qty);
            //折
            viewHolder.hisProDisTv=(TextView)convertView.findViewById(R.id.his_pro_dis);
            //罄
            viewHolder.hisProSalOutTv=(TextView)convertView.findViewById(R.id.his_pro_sal_out);
            //售罄状态
            viewHolder.imageViewSalStatus=(ImageView)convertView.findViewById(R.id.his_pro_sal_status);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        ProductHisTrend productHisTrend=groups.get(groupPosition);
        Bitmap bitmap=getBitmap(productHisTrend.getGoodsNo()+".jpg");
        if(bitmap==null){
            bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.roc);
        }
        viewHolder.imageView.setImageBitmap(bitmap);
        viewHolder.hisProIdTv.setText(productHisTrend.getGoodsNo());
        viewHolder.hisProSalTv.setText("销："+String.valueOf(productHisTrend.getTotalSalQty3()));
        viewHolder.hisProInTv.setText("进："+String.valueOf(productHisTrend.getTotalSalQty3() + productHisTrend.getInvQty3()));
        viewHolder.hisProDisTv.setText("折："+String.valueOf(MathTools.safePerDivideP(productHisTrend.getTotalSalAmt3(), productHisTrend.getTotalSalNosPrmAmt3()))+"%");
        viewHolder.hisProSalOutTv.setText("罄："+String.valueOf(MathTools.safePerDivideP(productHisTrend.getTotalSalQty3(),(productHisTrend.getTotalSalQty3() + productHisTrend.getInvQty3()))) +"%");
        Double limit1=1.0;
        Double limit2=1.0;
        Double limit3=1.0;

        if(cateId.equals("DP0001")){
            limit1=0.35;
            limit2=0.6;
            limit3=0.8;
        }else {
            limit1=0.4;
            limit2=0.65;
            limit3=0.85;
        }
        int imgId=R.drawable.his_sal_ping;
        Double sal_out=MathTools.safePerDivide(productHisTrend.getTotalSalPrmAmt3(), (productHisTrend.getTotalSalPrmAmt3() + productHisTrend.getInvAmt3()));
        if(sal_out<limit1){
            imgId=R.drawable.his_sal_zhi;
        }else if(sal_out<limit2){
            imgId=R.drawable.his_sal_ping;
        }else if(sal_out<limit3){
            imgId=R.drawable.his_sal_chang;
        }else{
            imgId=R.drawable.his_sal_tuo;
        }
        viewHolder.imageViewSalStatus.setImageResource(imgId);

        return convertView;
    }

    private static class ViewHolder
    {
        ImageView imageView;
        TextView hisProIdTv;
        TextView hisProSalTv;
        TextView hisProInTv;
        TextView hisProDisTv;
        TextView hisProSalOutTv;
        ImageView imageViewSalStatus;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expand_his_child_layout, null);
        }

        TableLayout tableLayout=(TableLayout)convertView.findViewById(R.id.his_child_table);
        tableLayout.removeAllViews();

        initChildTitle(tableLayout);
        initChildTableData(tableLayout, groupPosition);

        BarChart barChart=(BarChart)convertView.findViewById(R.id.chart1);
        LineChart lineChart=(LineChart)convertView.findViewById(R.id.chart2);
        initChildChart1(barChart,groupPosition);
        initChildChart2(lineChart,groupPosition);

        return convertView;
    }

    private void initChildTitle(TableLayout tableLayout){
        TableRow tableRowT=new TableRow(context);

        //城市title
        TextView cityTitleTv=new TextView(context);
        cityTitleTv.setBackgroundResource(R.drawable.shapee);
        cityTitleTv.setGravity(Gravity.CENTER);
        cityTitleTv.setText("城市");
        tableRowT.addView(cityTitleTv);
        //铺货店数
        TextView distrOrgNumTitleTv=new TextView(context);
        distrOrgNumTitleTv.setBackgroundResource(R.drawable.shapee);
        distrOrgNumTitleTv.setGravity(Gravity.CENTER);
        distrOrgNumTitleTv.setText("铺店");
        tableRowT.addView(distrOrgNumTitleTv);
        //销售店数
        TextView salOrgNumTitleTv=new TextView(context);
        salOrgNumTitleTv.setBackgroundResource(R.drawable.shapee);
        salOrgNumTitleTv.setGravity(Gravity.CENTER);
        salOrgNumTitleTv.setText("销店");
        tableRowT.addView(salOrgNumTitleTv);
        //销售深度
        TextView salDepthTitleTv=new TextView(context);
        salDepthTitleTv.setBackgroundResource(R.drawable.shapee);
        salDepthTitleTv.setGravity(Gravity.CENTER);
        salDepthTitleTv.setText("销深");
        tableRowT.addView(salDepthTitleTv);
        //铺货深度
        TextView distrDepthTitleTv=new TextView(context);
        distrDepthTitleTv.setBackgroundResource(R.drawable.shapee);
        distrDepthTitleTv.setGravity(Gravity.CENTER);
        distrDepthTitleTv.setText("铺深");
        tableRowT.addView(distrDepthTitleTv);
        //售罄
        TextView salOutTitleTv=new TextView(context);
        salOutTitleTv.setBackgroundResource(R.drawable.shapee);
        salOutTitleTv.setGravity(Gravity.CENTER);
        salOutTitleTv.setText("售罄");
        tableRowT.addView(salOutTitleTv);
        //折扣
        TextView discountTitleTv=new TextView(context);
        discountTitleTv.setBackgroundResource(R.drawable.shapee);
        discountTitleTv.setGravity(Gravity.CENTER);
        discountTitleTv.setText("折扣");
        tableRowT.addView(discountTitleTv);
        //销量
        TextView salQtyTitleTv=new TextView(context);
        salQtyTitleTv.setBackgroundResource(R.drawable.shapee);
        salQtyTitleTv.setGravity(Gravity.CENTER);
        salQtyTitleTv.setText("销量");
        tableRowT.addView(salQtyTitleTv);
        //进量
        TextView inQtyTitleTv=new TextView(context);
        inQtyTitleTv.setBackgroundResource(R.drawable.shapee);
        inQtyTitleTv.setGravity(Gravity.CENTER);
        inQtyTitleTv.setText("进量");
        tableRowT.addView(inQtyTitleTv);
        //订量
//        TextView orderQtyTitleTv=new TextView(context);
//        orderQtyTitleTv.setBackgroundResource(R.drawable.shapee);
//        orderQtyTitleTv.setGravity(Gravity.CENTER);
//        orderQtyTitleTv.setText("订量");
//        tableRowT.addView(orderQtyTitleTv);


        tableLayout.addView(tableRowT);
    }

    private void initChildTableData(TableLayout tableLayout,int groupPosition){
        List<ProductHisTrend> productHisTrends;
        if(buyerUnits.contains("MC")){
            productHisTrends= DataSupport.where("goodsNo='"+groups.get(groupPosition).getGoodsNo()+"' )").find(ProductHisTrend.class);
        }else{
            productHisTrends= DataSupport.where("goodsNo='"+groups.get(groupPosition).getGoodsNo()+"' and buyerUnitNm in('"+buyerUnits.replace(",","','")+ "')").find(ProductHisTrend.class);
        }


        DecimalFormat df = new DecimalFormat("###.0");
        for(int i=0;i<productHisTrends.size();i++){
            ProductHisTrend productHisTrend=productHisTrends.get(i);
            TableRow tableRow=new TableRow(context);

            //城市
            TextView cityTv=new TextView(context);
            cityTv.setBackgroundResource(R.drawable.shapee);
            cityTv.setGravity(Gravity.CENTER);
            cityTv.setText(productHisTrend.getMgmtCityNm());
            tableRow.addView(cityTv);
            //铺货店数
            TextView distrOrgNumTv=new TextView(context);
            distrOrgNumTv.setBackgroundResource(R.drawable.shapee);
            distrOrgNumTv.setGravity(Gravity.CENTER);
            distrOrgNumTv.setText(String.valueOf(productHisTrend.getDistrOrgNum()));
            tableRow.addView(distrOrgNumTv);
            //销售店数
            TextView salOrgNumTv=new TextView(context);
            salOrgNumTv.setBackgroundResource(R.drawable.shapee);
            salOrgNumTv.setGravity(Gravity.CENTER);
            salOrgNumTv.setText(String.valueOf(productHisTrend.getSalOrgNum()));
            tableRow.addView(salOrgNumTv);
            //均店销售深度
            TextView salDepthTv=new TextView(context);
            salDepthTv.setBackgroundResource(R.drawable.shapee);
            salDepthTv.setGravity(Gravity.CENTER);
            salDepthTv.setText(df.format(MathTools.safePerDivide(productHisTrend.getTotalSalQty3(), productHisTrend.getSalOrgNum())));
            tableRow.addView(salDepthTv);
            //均店铺货深度
            TextView distrDepthTv=new TextView(context);
            distrDepthTv.setBackgroundResource(R.drawable.shapee);
            distrDepthTv.setGravity(Gravity.CENTER);
            distrDepthTv.setText(df.format(MathTools.safePerDivide(productHisTrend.getTotalSalQty3(), productHisTrend.getDistrOrgNum())));
            tableRow.addView(distrDepthTv);
            //售罄
            TextView salOutTv=new TextView(context);
            salOutTv.setBackgroundResource(R.drawable.shapee);
            salOutTv.setGravity(Gravity.CENTER);
            salOutTv.setText(String.valueOf(MathTools.safePerDivideP(productHisTrend.getTotalSalQty3(), productHisTrend.getTotalSalQty3()+productHisTrend.getInvQty3()))+"%");
            tableRow.addView(salOutTv);
            //折扣
            TextView discountTv=new TextView(context);
            discountTv.setBackgroundResource(R.drawable.shapee);
            discountTv.setGravity(Gravity.CENTER);
            discountTv.setText(String.valueOf(MathTools.safePerDivideP(productHisTrend.getTotalSalAmt3(), productHisTrend.getTotalSalNosPrmAmt3()))+"%");
            tableRow.addView(discountTv);
            //销量
            TextView salQtyTv=new TextView(context);
            salQtyTv.setBackgroundResource(R.drawable.shapee);
            salQtyTv.setGravity(Gravity.CENTER);
            salQtyTv.setText(String.valueOf(productHisTrend.getTotalSalQty3()));
            tableRow.addView(salQtyTv);
            //进量
            TextView inQtyTv=new TextView(context);
            inQtyTv.setBackgroundResource(R.drawable.shapee);
            inQtyTv.setGravity(Gravity.CENTER);
            inQtyTv.setText(String.valueOf(productHisTrend.getTotalSalQty3()+productHisTrend.getInvQty3()));
            tableRow.addView(inQtyTv);
            //订量
//            TextView orderQtyTv=new TextView(context);
//            orderQtyTv.setBackgroundResource(R.drawable.shapee);
//            orderQtyTv.setGravity(Gravity.CENTER);
//            orderQtyTv.setText(String.valueOf(productHisTrend.getOrderNum()));
//            tableRow.addView(orderQtyTv);


            tableLayout.addView(tableRow);
        }
    }

    private void initChildChart1(BarChart barChart,int groupPosition){
        ProductHisTrend productHisTrend=groups.get(groupPosition);

        barChart.setDescription("");
        barChart.setPinchZoom(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);

        Legend l=barChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setYOffset(0f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xl=barChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis=barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(30f);
        leftAxis.setAxisMinValue(0f);

        barChart.getAxisRight().setEnabled(false);

        ArrayList<String> xVals=new ArrayList<>();
        xVals.add("1月");
        xVals.add("2月");
        xVals.add("3月");

        ArrayList<BarEntry> yVals1=new ArrayList<>();
        yVals1.add(new BarEntry(productHisTrend.getTotalSalQty1(), 0));
        yVals1.add(new BarEntry(productHisTrend.getTotalSalQty2(), 1));
        yVals1.add(new BarEntry(productHisTrend.getTotalSalQty3(), 2));

        BarDataSet set1=new BarDataSet(yVals1,"销售量");
        set1.setColor(Color.rgb(104, 241, 175));

        ArrayList<BarEntry> yVals2=new ArrayList<>();
        yVals2.add(new BarEntry(productHisTrend.getTotalSalQty1()+productHisTrend.getInvQty1(), 0));
        yVals2.add(new BarEntry(productHisTrend.getTotalSalQty2() + productHisTrend.getInvQty2(), 1));
        yVals2.add(new BarEntry(productHisTrend.getTotalSalQty3()+productHisTrend.getInvQty3(), 2));

        BarDataSet set2=new BarDataSet(yVals2,"进货量");
        set2.setColor(Color.rgb(104, 100, 175));

        ArrayList<IBarDataSet> dataSets=new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);
        BarData data=new BarData(xVals,dataSets);
        data.setGroupSpace(80f);
        data.setValueTextSize(10f);
        barChart.setScaleEnabled(false);
        barChart.setData(data);
        barChart.invalidate();
    }

    private void initChildChart2(LineChart lineChart,int groupPosition){
        ProductHisTrend productHisTrend=groups.get(groupPosition);

        lineChart.setDescription("");
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);

        Legend l=lineChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setYOffset(0f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xl=lineChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis=lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(30f);
        leftAxis.setAxisMinValue(0f);

        lineChart.getAxisRight().setEnabled(false);

        ArrayList<String> xVals=new ArrayList<>();
        xVals.add("1月");
        xVals.add("2月");
        xVals.add("3月");

        ArrayList<Entry> yVals1=new ArrayList<>();
        yVals1.add(new Entry(MathTools.safePerDivideP(productHisTrend.getTotalSalAmt1(),productHisTrend.getTotalSalNosPrmAmt1()), 0));
        yVals1.add(new Entry(MathTools.safePerDivideP(productHisTrend.getTotalSalAmt2(),productHisTrend.getTotalSalNosPrmAmt2()), 1));
        yVals1.add(new Entry(MathTools.safePerDivideP(productHisTrend.getTotalSalAmt3(), productHisTrend.getTotalSalNosPrmAmt3()), 2));

        LineDataSet set1=new LineDataSet(yVals1,"折扣");
        set1.setColor(Color.rgb(104, 241, 175));
        set1.setValueTextSize(10f);

        ArrayList<Entry> yVals2=new ArrayList<>();
        yVals2.add(new Entry(MathTools.safePerDivideP(productHisTrend.getTotalSalQty1(),productHisTrend.getTotalSalQty1()+productHisTrend.getInvQty1()), 0));
        yVals2.add(new Entry(MathTools.safePerDivideP(productHisTrend.getTotalSalQty2(), productHisTrend.getTotalSalQty2() + productHisTrend.getInvQty2()), 1));
        yVals2.add(new Entry(MathTools.safePerDivideP(productHisTrend.getTotalSalQty3(),productHisTrend.getTotalSalQty3()+productHisTrend.getInvQty3()), 2));

        LineDataSet set2=new LineDataSet(yVals2,"售罄");
        set2.setColor(Color.rgb(104, 100, 175));
        set2.setValueTextSize(10f);

        ArrayList<ILineDataSet> dataSets=new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);
        LineData data=new LineData(xVals,dataSets);

        lineChart.setScaleEnabled(false);
        lineChart.setData(data);
        lineChart.invalidate();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
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
}
