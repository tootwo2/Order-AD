package com.topsports.tootwo2.model;

import org.litepal.crud.DataSupport;

/**
 * Created by tootwo2 on 16/2/19.
 */
public class ProductHisTrend extends DataSupport{
    private String newGoodsId;
    private String goodsNo;
    private String modelName;
    private String brdSeason;

    private String buyerUnitId;
    private String buyerUnitNm;
    /**
     * 大区代号
     */
    private String regionId;
    /**
     * 大区名称
     */
    private String regionNm;
    /**
     * 省区代号
     */
    private String provId;
    /**
     * 省区名称
     */
    private String provNm;
    /**
     * 管理城市代号
     */
    private String mgmtCityId;
    /**
     * 广利城市名称
     */
    private String mgmtCityNm;

    /*上市一个月数据*/
    private int totalSalQty1;
    private int totalSalAmt1;
    private int totalSalNosPrmAmt1;
    private int totalSalPrmAmt1;
    private int invQty1;
    private int invAmt1;

    /*上市两个月数据*/
    private int totalSalQty2;
    private int totalSalAmt2;
    private int totalSalNosPrmAmt2;
    private int totalSalPrmAmt2;
    private int invQty2;
    private int invAmt2;

    /*上市三个月数据*/
    private int totalSalQty3;
    private int totalSalAmt3;
    private int totalSalNosPrmAmt3;
    private int totalSalPrmAmt3;
    private int invQty3;
    private int invAmt3;

    private int distrOrgNum;
    private int salOrgNum;

    /**
     * 订货量
     */
    private int orderNum;

    public String getNewGoodsId() {
        return newGoodsId;
    }

    public void setNewGoodsId(String newGoodsId) {
        this.newGoodsId = newGoodsId;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getBrdSeason() {
        return brdSeason;
    }

    public void setBrdSeason(String brdSeason) {
        this.brdSeason = brdSeason;
    }

    public int getTotalSalQty1() {
        return totalSalQty1;
    }

    public void setTotalSalQty1(int totalSalQty1) {
        this.totalSalQty1 = totalSalQty1;
    }

    public int getTotalSalAmt1() {
        return totalSalAmt1;
    }

    public void setTotalSalAmt1(int totalSalAmt1) {
        this.totalSalAmt1 = totalSalAmt1;
    }

    public int getTotalSalNosPrmAmt1() {
        return totalSalNosPrmAmt1;
    }

    public void setTotalSalNosPrmAmt1(int totalSalNosPrmAmt1) {
        this.totalSalNosPrmAmt1 = totalSalNosPrmAmt1;
    }

    public int getTotalSalPrmAmt1() {
        return totalSalPrmAmt1;
    }

    public void setTotalSalPrmAmt1(int totalSalPrmAmt1) {
        this.totalSalPrmAmt1 = totalSalPrmAmt1;
    }

    public int getTotalSalQty2() {
        return totalSalQty2;
    }

    public void setTotalSalQty2(int totalSalQty2) {
        this.totalSalQty2 = totalSalQty2;
    }

    public int getTotalSalAmt2() {
        return totalSalAmt2;
    }

    public void setTotalSalAmt2(int totalSalAmt2) {
        this.totalSalAmt2 = totalSalAmt2;
    }

    public int getTotalSalNosPrmAmt2() {
        return totalSalNosPrmAmt2;
    }

    public void setTotalSalNosPrmAmt2(int totalSalNosPrmAmt2) {
        this.totalSalNosPrmAmt2 = totalSalNosPrmAmt2;
    }

    public int getTotalSalPrmAmt2() {
        return totalSalPrmAmt2;
    }

    public void setTotalSalPrmAmt2(int totalSalPrmAmt2) {
        this.totalSalPrmAmt2 = totalSalPrmAmt2;
    }

    public int getTotalSalQty3() {
        return totalSalQty3;
    }

    public void setTotalSalQty3(int totalSalQty3) {
        this.totalSalQty3 = totalSalQty3;
    }

    public int getTotalSalAmt3() {
        return totalSalAmt3;
    }

    public void setTotalSalAmt3(int totalSalAmt3) {
        this.totalSalAmt3 = totalSalAmt3;
    }

    public int getTotalSalNosPrmAmt3() {
        return totalSalNosPrmAmt3;
    }

    public void setTotalSalNosPrmAmt3(int totalSalNosPrmAmt3) {
        this.totalSalNosPrmAmt3 = totalSalNosPrmAmt3;
    }

    public int getTotalSalPrmAmt3() {
        return totalSalPrmAmt3;
    }

    public void setTotalSalPrmAmt3(int totalSalPrmAmt3) {
        this.totalSalPrmAmt3 = totalSalPrmAmt3;
    }

    public int getDistrOrgNum() {
        return distrOrgNum;
    }

    public void setDistrOrgNum(int distrOrgNum) {
        this.distrOrgNum = distrOrgNum;
    }

    public int getSalOrgNum() {
        return salOrgNum;
    }

    public void setSalOrgNum(int salOrgNum) {
        this.salOrgNum = salOrgNum;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public int getInvQty1() {
        return invQty1;
    }

    public void setInvQty1(int invQty1) {
        this.invQty1 = invQty1;
    }

    public int getInvAmt1() {
        return invAmt1;
    }

    public void setInvAmt1(int invAmt1) {
        this.invAmt1 = invAmt1;
    }

    public int getInvQty2() {
        return invQty2;
    }

    public void setInvQty2(int invQty2) {
        this.invQty2 = invQty2;
    }

    public int getInvAmt2() {
        return invAmt2;
    }

    public void setInvAmt2(int invAmt2) {
        this.invAmt2 = invAmt2;
    }

    public int getInvQty3() {
        return invQty3;
    }

    public void setInvQty3(int invQty3) {
        this.invQty3 = invQty3;
    }

    public int getInvAmt3() {
        return invAmt3;
    }

    public void setInvAmt3(int invAmt3) {
        this.invAmt3 = invAmt3;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getRegionNm() {
        return regionNm;
    }

    public void setRegionNm(String regionNm) {
        this.regionNm = regionNm;
    }

    public String getProvId() {
        return provId;
    }

    public void setProvId(String provId) {
        this.provId = provId;
    }

    public String getProvNm() {
        return provNm;
    }

    public void setProvNm(String provNm) {
        this.provNm = provNm;
    }

    public String getMgmtCityId() {
        return mgmtCityId;
    }

    public void setMgmtCityId(String mgmtCityId) {
        this.mgmtCityId = mgmtCityId;
    }

    public String getMgmtCityNm() {
        return mgmtCityNm;
    }

    public void setMgmtCityNm(String mgmtCityNm) {
        this.mgmtCityNm = mgmtCityNm;
    }

    public String getBuyerUnitId() {
        return buyerUnitId;
    }

    public void setBuyerUnitId(String buyerUnitId) {
        this.buyerUnitId = buyerUnitId;
    }

    public String getBuyerUnitNm() {
        return buyerUnitNm;
    }

    public void setBuyerUnitNm(String buyerUnitNm) {
        this.buyerUnitNm = buyerUnitNm;
    }
}
