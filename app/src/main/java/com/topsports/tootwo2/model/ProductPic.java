package com.topsports.tootwo2.model;

import org.litepal.crud.DataSupport;

/**
 * Created by zhang.p on 2015/8/27.
 */
public class ProductPic extends DataSupport {
    private int id;

    /**
     * 订货计划
     */
    private String orderPlanNo;
    /**
     * 图片名
     */
    private String imgName;
    /**
     * 货号
     */
    private String goodsNo;
    /**
     * 状态码 0正常，1新增，2删除
     */
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderPlanNo() {
        return orderPlanNo;
    }

    public void setOrderPlanNo(String orderPlanNo) {
        this.orderPlanNo = orderPlanNo;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
