package com.topsports.tootwo2.model;

import org.litepal.crud.DataSupport;

/**
 * Created by tootwo2 on 16/2/14.
 * 订货计划数据
 */
public class ProductPlanInfo extends DataSupport{
    private int id;

    /**
     * 品牌
     */
    private String brdId;
    /**
     * 订货计划编号
     */
    private String orderPlanNo;
    /**
     * 买货单位编号
     */
    private String buyerUnitCode;
    /**
     * 买货单位名称
     */
    private String buyerUnitName;
    /**
     * 产品系列代号
     */
    private String proSeriesId;
    /**
     * 产品系列名称
     */
    private String proSeriesNm;
    /**
     * 产品大类代号
     */
    private String proCateId;
    /**
     * 产品大类名称
     */
    private String proCateNm;
    /**
     * 产品性别代号
     */
    private String proGenderId;
    /**
     * 产品性别名称
     */
    private String proGenderNm;
    /**
     * 进货SKU
     */
    private int inSku;
    /**
     * 进货额
     */
    private int inOrdSal;
    /**
     * 进货量
     */
    private int inOrdQty;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrdId() {
        return brdId;
    }

    public void setBrdId(String brdId) {
        this.brdId = brdId;
    }

    public String getOrderPlanNo() {
        return orderPlanNo;
    }

    public void setOrderPlanNo(String orderPlanNo) {
        this.orderPlanNo = orderPlanNo;
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

    public String getProSeriesId() {
        return proSeriesId;
    }

    public void setProSeriesId(String proSeriesId) {
        this.proSeriesId = proSeriesId;
    }

    public String getProSeriesNm() {
        return proSeriesNm;
    }

    public void setProSeriesNm(String proSeriesNm) {
        this.proSeriesNm = proSeriesNm;
    }

    public String getProCateId() {
        return proCateId;
    }

    public void setProCateId(String proCateId) {
        this.proCateId = proCateId;
    }

    public String getProCateNm() {
        return proCateNm;
    }

    public void setProCateNm(String proCateNm) {
        this.proCateNm = proCateNm;
    }

    public String getProGenderId() {
        return proGenderId;
    }

    public void setProGenderId(String proGenderId) {
        this.proGenderId = proGenderId;
    }

    public String getProGenderNm() {
        return proGenderNm;
    }

    public void setProGenderNm(String proGenderNm) {
        this.proGenderNm = proGenderNm;
    }

    public int getInSku() {
        return inSku;
    }

    public void setInSku(int inSku) {
        this.inSku = inSku;
    }

    public int getInOrdSal() {
        return inOrdSal;
    }

    public void setInOrdSal(int inOrdSal) {
        this.inOrdSal = inOrdSal;
    }

    public int getInOrdQty() {
        return inOrdQty;
    }

    public void setInOrdQty(int inOrdQty) {
        this.inOrdQty = inOrdQty;
    }
}
