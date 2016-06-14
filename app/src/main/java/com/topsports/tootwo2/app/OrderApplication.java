package com.topsports.tootwo2.app;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.topsports.tootwo2.order.R;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.litepal.LitePalApplication;

/**
 * Created by tootwo2 on 15/10/29.
 */
public class OrderApplication extends LitePalApplication {

    private final String version = "20160314";
    private PushAgent mPushAgent;


    @Override
    public void onCreate(){
        super.onCreate();
        TypefaceProvider.registerDefaultIconSets();
        initUmengNotification();
    }

    private void initUmengNotification(){
        mPushAgent = PushAgent.getInstance(getApplicationContext());
        mPushAgent.enable();
        mPushAgent.setDebugMode(true);

        UmengMessageHandler messageHandler = new UmengMessageHandler(){
            /**
             * 参考集成文档的1.6.3
             * http://dev.umeng.com/push/android/integration#1_6_3
             * */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if(isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Log.d("TEST","dealWithCustomMessage");
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
//                        MyIntentService.startActionFoo(context,"","");


                    }
                });
            }

            /**
             * 参考集成文档的1.6.4
             * http://dev.umeng.com/push/android/integration#1_6_4
             * */
            @Override
            public Notification getNotification(Context context,
                                                UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                        builder.setContent(myNotificationView)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);

                        return builder.build();

                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 该Handler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * 参考集成文档的1.6.2
         * http://dev.umeng.com/push/android/integration#1_6_2
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                Log.d("TEST","dealWithCustomAction");
            }
        };
        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知
        //参考http://bbs.umeng.com/thread-11112-1-1.html
        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }


    @Override
    public String toString() {
        return "OrderApplication{" +
                "version='" + version + '\'' +
                ", userId='" + userId + '\'' +
                ", role='" + role + '\'' +
                ", areaName='" + areaName + '\'' +
                ", name='" + name + '\'' +
                ", orderMeetingNo='" + orderMeetingNo + '\'' +
                ", mainBrandCode='" + mainBrandCode + '\'' +
                '}';
    }


    private String userId;
    private String role;
    private String areaName;
    private String name;
    private String orderMeetingNo;
    private String mainBrandCode;
    private String orderMeetingType;
    private String buyerUnitsStr;


    public String getVersion() {
        return version;
    }


    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getMainBrandCode() {
        return mainBrandCode;
    }

    public void setMainBrandCode(String mainBrandCode) {
        this.mainBrandCode = mainBrandCode;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userid) {
        this.userId = userid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOrderMeetingNo() {
        return orderMeetingNo;
    }

    public void setOrderMeetingNo(String orderMeetingNo) {
        this.orderMeetingNo = orderMeetingNo;
    }

    public String getOrderMeetingType() {
        return orderMeetingType;
    }

    public void setOrderMeetingType(String orderMeetingType) {
        this.orderMeetingType = orderMeetingType;
    }

    public String getBuyerUnitsStr() {
        return buyerUnitsStr;
    }

    public void setBuyerUnitsStr(String buyerUnitsStr) {
        this.buyerUnitsStr = buyerUnitsStr;
    }
}
