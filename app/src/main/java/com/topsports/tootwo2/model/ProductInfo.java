package com.topsports.tootwo2.model;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang.p on 2015/8/27.
 * ReCreated by zhang.p on 2015/11/17.
 * 商品基础资料
 */
public class ProductInfo extends DataSupport {
    private int id;

    /**
     *订货目录ID
     */
    private String newGoodsId;

    /**
     *品牌（品类）
     */
    private String mainBrandCode;

    /**
     *订货计划编号:由 品牌、年度、订货季节 自动产生，不可重复；
     */
    private String orderPlanNo;

    /**
     *订货年季度
     */
    private String orderPlanY;

    /**
     * 品牌季节
     */
    private String brdSeason;
    /**
     *大类
     */
    private String division;

    /**
     * 大类描述
     */
    private String divisionDesc;
    /**
     *款号
     */
    private String modelNo;

    /**
     *色号
     */
    private String colorNo;

    /**
     *产品号
     */
    private String goodsNo;

    /**
     *系统款号
     */
    private String modelNoSys;

    /**
     *系统系列号
     */
    private String categorySys;

    /**
     *性别
     */
    private String gender;

    /**
     *
     */
    private String genderDesc;

    /**
     *款式
     */
    private String category;

    /**
     *
     */
    private String categoryDesc;

    /**
     *系列
     */
    private String marketingDivision;

    /**
     *
     */
    private String marketingDivisionDesc;

    /**
     *产品品名
     */
    private String modelName;

    /**
     *尺寸范围
     */
    private String sizeRange;

    /**
     *系统尺码
     */
    private String systemSize;

    /**
     *年龄层次
     */
    private String ageLevel;

    /**
     *上市日
     */
    private String eastLaunch;

    /**
     *零售价
     */
    private String localRp;

    /**
     *面料
     */
    private String composition;

    /**
     *
     */
    private String compositionDesc;

    /**
     *工艺
     */
    private String technology;

    /**
     *
     */
    private String technologyDesc;

    /**
     *备注
     */
    private String remark;

    /**
     *
     */
    private String custom1;

    /**
     *
     */
    private String custom2;

    /**
     *
     */
    private String custom3;

    /**
     * 精英买手
     */
    private String custom4;

    /**
     *
     */
    private String custom5;

    /**
     *
     */
    private String custom6;

    /**
     *
     */
    private String custom7;

    /**
     *
     */
    private String custom8;

    /**
     *
     */
    private String custom9;

    /**
     *
     */
    private String custom10;

    /**
     *品牌款式
     */
    private String brandStyle;

    /**
     *
     */
    private String brandStyleDesc;

    /**
     *重复上市
     */
    private String repeatListing;

    /**
     *分配必订
     */
    private String havetoOrder;

    /**
     *
     */
    private String havetoOrderDesc;

    /**
     *季节款式
     */
    private String seasonStyle;

    /**
     *市场支持
     */
    private String marketSupport;

    /**
     *
     */
    private String marketSupportDesc;

    /**
     *价格区间
     */
    private String pricePoint;

    /**
     *
     */
    private String pricePointDesc;

    /**
     *主色
     */
    private String mainColor;

    /**
     *副色
     */
    private String asecondaryColor;

    /**
     *支持类型
     */
    private String supportStyle;

    /**
     *适应场地
     */
    private String suitableSituation;

    /**
     *POP
     */
    private String pop;

    /**
     *
     */
    private String popDesc;

    /**
     *搭配
     */
    private String package_;

    /**
     *目录页码
     */
    private String directoryPage;

    /**
     *上市月
     */
    private String listingMonth;

    /**
     *商品描述
     */
    private String goodsDescribe;

    /**
     *上市月
     */
    private String eastLaunchMonth;

    /**
     *0:启用；1：停用
     */
    private String status;

    //用户ID
    private String userId;
    //A,B,C,D
    private String orderLevel;
    //0,1(+),2(-)
    private String orderLevelPlus;
    //标签
    private List<ProductComments> productComments;
    //订货量信息
    private List<ProductOrderInfo> productOrderInfoList;

    public ProductInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNewGoodsId() {
        return newGoodsId;
    }

    public void setNewGoodsId(String newGoodsId) {
        this.newGoodsId = newGoodsId;
    }

    public String getMainBrandCode() {
        return mainBrandCode;
    }

    public void setMainBrandCode(String mainBrandCode) {
        this.mainBrandCode = mainBrandCode;
    }

    public String getOrderPlanNo() {
        return orderPlanNo;
    }

    public void setOrderPlanNo(String orderPlanNo) {
        this.orderPlanNo = orderPlanNo;
    }

    public String getOrderPlanY() {
        return orderPlanY;
    }

    public void setOrderPlanY(String orderPlanY) {
        this.orderPlanY = orderPlanY;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getModelNo() {
        return modelNo;
    }

    public void setModelNo(String modelNo) {
        this.modelNo = modelNo;
    }

    public String getColorNo() {
        return colorNo;
    }

    public void setColorNo(String colorNo) {
        this.colorNo = colorNo;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getModelNoSys() {
        return modelNoSys;
    }

    public void setModelNoSys(String modelNoSys) {
        this.modelNoSys = modelNoSys;
    }

    public String getCategorySys() {
        return categorySys;
    }

    public void setCategorySys(String categorySys) {
        this.categorySys = categorySys;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGenderDesc() {
        return genderDesc;
    }

    public void setGenderDesc(String genderDesc) {
        this.genderDesc = genderDesc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }

    public String getMarketingDivision() {
        return marketingDivision;
    }

    public void setMarketingDivision(String marketingDivision) {
        this.marketingDivision = marketingDivision;
    }

    public String getMarketingDivisionDesc() {
        return marketingDivisionDesc;
    }

    public void setMarketingDivisionDesc(String marketingDivisionDesc) {
        this.marketingDivisionDesc = marketingDivisionDesc;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getSizeRange() {
        return sizeRange;
    }

    public void setSizeRange(String sizeRange) {
        this.sizeRange = sizeRange;
    }

    public String getSystemSize() {
        return systemSize;
    }

    public void setSystemSize(String systemSize) {
        this.systemSize = systemSize;
    }

    public String getAgeLevel() {
        return ageLevel;
    }

    public void setAgeLevel(String ageLevel) {
        this.ageLevel = ageLevel;
    }

    public String getEastLaunch() {
        return eastLaunch;
    }

    public void setEastLaunch(String eastLaunch) {
        this.eastLaunch = eastLaunch;
    }

    public String getLocalRp() {
        return localRp;
    }

    public void setLocalRp(String localRp) {
        this.localRp = localRp;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public String getCompositionDesc() {
        return compositionDesc;
    }

    public void setCompositionDesc(String compositionDesc) {
        this.compositionDesc = compositionDesc;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getTechnologyDesc() {
        return technologyDesc;
    }

    public void setTechnologyDesc(String technologyDesc) {
        this.technologyDesc = technologyDesc;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCustom1() {
        return custom1;
    }

    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    public String getCustom2() {
        return custom2;
    }

    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    public String getCustom3() {
        return custom3;
    }

    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }

    public String getCustom4() {
        return custom4;
    }

    public void setCustom4(String custom4) {
        this.custom4 = custom4;
    }

    public String getCustom5() {
        return custom5;
    }

    public void setCustom5(String custom5) {
        this.custom5 = custom5;
    }

    public String getCustom6() {
        return custom6;
    }

    public void setCustom6(String custom6) {
        this.custom6 = custom6;
    }

    public String getCustom7() {
        return custom7;
    }

    public void setCustom7(String custom7) {
        this.custom7 = custom7;
    }

    public String getCustom8() {
        return custom8;
    }

    public void setCustom8(String custom8) {
        this.custom8 = custom8;
    }

    public String getCustom9() {
        return custom9;
    }

    public void setCustom9(String custom9) {
        this.custom9 = custom9;
    }

    public String getCustom10() {
        return custom10;
    }

    public void setCustom10(String custom10) {
        this.custom10 = custom10;
    }

    public String getBrandStyle() {
        return brandStyle;
    }

    public void setBrandStyle(String brandStyle) {
        this.brandStyle = brandStyle;
    }

    public String getBrandStyleDesc() {
        return brandStyleDesc;
    }

    public void setBrandStyleDesc(String brandStyleDesc) {
        this.brandStyleDesc = brandStyleDesc;
    }

    public String getRepeatListing() {
        return repeatListing;
    }

    public void setRepeatListing(String repeatListing) {
        this.repeatListing = repeatListing;
    }

    public String getHavetoOrder() {
        return havetoOrder;
    }

    public void setHavetoOrder(String havetoOrder) {
        this.havetoOrder = havetoOrder;
    }

    public String getHavetoOrderDesc() {
        return havetoOrderDesc;
    }

    public void setHavetoOrderDesc(String havetoOrderDesc) {
        this.havetoOrderDesc = havetoOrderDesc;
    }

    public String getSeasonStyle() {
        return seasonStyle;
    }

    public void setSeasonStyle(String seasonStyle) {
        this.seasonStyle = seasonStyle;
    }

    public String getMarketSupport() {
        return marketSupport;
    }

    public void setMarketSupport(String marketSupport) {
        this.marketSupport = marketSupport;
    }

    public String getMarketSupportDesc() {
        return marketSupportDesc;
    }

    public void setMarketSupportDesc(String marketSupportDesc) {
        this.marketSupportDesc = marketSupportDesc;
    }

    public String getPricePoint() {
        return pricePoint;
    }

    public void setPricePoint(String pricePoint) {
        this.pricePoint = pricePoint;
    }

    public String getPricePointDesc() {
        return pricePointDesc;
    }

    public void setPricePointDesc(String pricePointDesc) {
        this.pricePointDesc = pricePointDesc;
    }

    public String getMainColor() {
        return mainColor;
    }

    public void setMainColor(String mainColor) {
        this.mainColor = mainColor;
    }

    public String getAsecondaryColor() {
        return asecondaryColor;
    }

    public void setAsecondaryColor(String asecondaryColor) {
        this.asecondaryColor = asecondaryColor;
    }

    public String getSupportStyle() {
        return supportStyle;
    }

    public void setSupportStyle(String supportStyle) {
        this.supportStyle = supportStyle;
    }

    public String getSuitableSituation() {
        return suitableSituation;
    }

    public void setSuitableSituation(String suitableSituation) {
        this.suitableSituation = suitableSituation;
    }

    public String getPop() {
        return pop;
    }

    public void setPop(String pop) {
        this.pop = pop;
    }

    public String getPopDesc() {
        return popDesc;
    }

    public void setPopDesc(String popDesc) {
        this.popDesc = popDesc;
    }

    public String getPackage_() {
        return package_;
    }

    public void setPackage_(String package_) {
        this.package_ = package_;
    }

    public String getDirectoryPage() {
        return directoryPage;
    }

    public void setDirectoryPage(String directoryPage) {
        this.directoryPage = directoryPage;
    }

    public String getListingMonth() {
        return listingMonth;
    }

    public void setListingMonth(String listingMonth) {
        this.listingMonth = listingMonth;
    }

    public String getGoodsDescribe() {
        return goodsDescribe;
    }

    public void setGoodsDescribe(String goodsDescribe) {
        this.goodsDescribe = goodsDescribe;
    }

    public String getEastLaunchMonth() {
        return eastLaunchMonth;
    }

    public void setEastLaunchMonth(String eastLaunchMonth) {
        this.eastLaunchMonth = eastLaunchMonth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderLevel() {
        return orderLevel;
    }

    public void setOrderLevel(String orderLevel) {
        this.orderLevel = orderLevel;
    }

    public String getOrderLevelPlus() {
        return orderLevelPlus;
    }

    public void setOrderLevelPlus(String orderLevelPlus) {
        this.orderLevelPlus = orderLevelPlus;
    }

    public List<ProductComments> getProductComments() {
//        return DataSupport.where("newGoodsID=? and userid=?",newGoodsId,userId).find(ProductComments.class);
        return productComments;
    }

    public void setProductComments(List<ProductComments> productComments) {
        this.productComments = productComments;
    }

    public String getBrdSeason() {
        return brdSeason;
    }

    public void setBrdSeason(String brdSeason) {
        this.brdSeason = brdSeason;
    }

    public List<ProductOrderInfo> getProductOrderInfoList() {
//        return productOrderInfoList;
        return DataSupport.where("newGoodsID=? and userId=?",newGoodsId,userId).find(ProductOrderInfo.class);
    }

    public List<ProductOrderInfo> getProductOrderInfoList(String userId) {
//        return productOrderInfoList;
//        if(productOrderInfoList==null){
//            productOrderInfoList=DataSupport.where("newGoodsID=? and userId=?",newGoodsId,userId).find(ProductOrderInfo.class);
//        }
//        return productOrderInfoList;
        return DataSupport.where("newGoodsID=? and userId=?",newGoodsId,userId).find(ProductOrderInfo.class);
    }

    public void setProductOrderInfoList(List<ProductOrderInfo> productOrderInfoList) {
        this.productOrderInfoList = productOrderInfoList;
    }

    public String getDivisionDesc() {
        return divisionDesc;
    }

    public void setDivisionDesc(String divisionDesc) {
        this.divisionDesc = divisionDesc;
    }
}
