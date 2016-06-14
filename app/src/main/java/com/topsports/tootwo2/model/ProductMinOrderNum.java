package com.topsports.tootwo2.model;

import org.litepal.crud.DataSupport;

/**
 * Created by tootwo2 on 16/2/14.
 * 最小起订量
 */
public class ProductMinOrderNum extends DataSupport{
    private int id;

    /**
     * 订货计划编号
     */
    private String orderPlanNo;
    /**
     * 货号
     */
    private String goodsNo;
    /**
     * 买货单位编号
     */
    private String buyerUnitCode;
    /**
     * 买货单位名称
     */
    private String buyerUnitName;
    /**
     * 最小起订量
     */
    private int minNum;

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

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getBuyerUnitCode() {
        return buyerUnitCode;
    }

    public void setBuyerUnitCode(String buyerUnitCode) {
        this.buyerUnitCode = buyerUnitCode;
    }

    public String getBuyerUnitName() {
        return buyerUnitName;
    }

    public void setBuyerUnitName(String buyerUnitName) {
        this.buyerUnitName = buyerUnitName;
    }

    public int getMinNum() {
        return minNum;
    }

    public void setMinNum(int minNum) {
        this.minNum = minNum;
    }
}
