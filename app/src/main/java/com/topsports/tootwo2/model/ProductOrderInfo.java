package com.topsports.tootwo2.model;

import android.database.Cursor;

import org.litepal.crud.DataSupport;

/**
 * Created by tootwo2 on 15/11/17.
 */
public class ProductOrderInfo extends DataSupport{

//    private ProductInfo productInfo;

    private int id;

    private String userId;

    private String newGoodsId;

    //买手单位代号
    private String buyerUnitCode;
    //买手单位名称
    private String BuyerUnitName;
    //下量
    private Integer orderQty;
    //最小起订量
    private Integer minOrderQty;

    public Integer getMinOrderQty() {
        return minOrderQty;
    }

    public void setMinOrderQty(Integer minOrderQty) {
        this.minOrderQty = minOrderQty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNewGoodsId() {
        return newGoodsId;
    }

    public void setNewGoodsId(String newGoodsId) {
        this.newGoodsId = newGoodsId;
    }

//    public ProductInfo getProductInfo() {
//        return productInfo;
//    }
//
//    public void setProductInfo(ProductInfo productInfo) {
//        this.productInfo = productInfo;
//    }

    public String getBuyerUnitCode() {
        return buyerUnitCode;
    }

    public void setBuyerUnitCode(String buyerUnitCode) {
        this.buyerUnitCode = buyerUnitCode;
    }

    public String getBuyerUnitName() {
        return BuyerUnitName;
    }

    public void setBuyerUnitName(String buyerUnitName) {
        BuyerUnitName = buyerUnitName;
    }

    public Integer getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(Integer orderQty) {
        this.orderQty = orderQty;
    }
}
