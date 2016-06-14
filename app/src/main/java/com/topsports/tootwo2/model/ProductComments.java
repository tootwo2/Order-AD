package com.topsports.tootwo2.model;

import org.litepal.crud.DataSupport;

/**
 * Created by tootwo2 on 15/9/10.
 * 商品评价
 */
public class ProductComments extends DataSupport{
    private int id;

    private String newGoodsID;

    private String userid;

    private String commentsDetail;

    private String commentsTime;

    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNewGoodsID() {
        return newGoodsID;
    }

    public void setNewGoodsID(String newGoodsID) {
        this.newGoodsID = newGoodsID;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCommentsDetail() {
        return commentsDetail;
    }

    public void setCommentsDetail(String commentsDetail) {
        this.commentsDetail = commentsDetail;
    }

    public String getCommentsTime() {
        return commentsTime;
    }

    public void setCommentsTime(String commentsTime) {
        this.commentsTime = commentsTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
