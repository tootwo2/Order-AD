package com.topsports.tootwo2.model;

/**
 * Created by tootwo2 on 16/2/22.
 * 新老货号对照数据
 */
public class ProductOldCompare {
    /**
     * 货号
     */
    private String proId;
    /**
     * 品牌季节
     */
    private String brdSeason;
    /**
     * 老货号
     */
    private String proIdOld;
    /**
     * 货品名称
     */
    private String proNmOld;
    /**
     * 价格带
     */
    private String priceBandNm;
    /**
     * 款式名称
     */
    private String proStyleNm;
    /**
     * 大类名称
     */
    private String proCateNm;
    /**
     * 性别名称
     */
    private String proGenderNm;
    /**
     * 老品牌季节
     */
    private String brdSeasonOld;


    public String getProId() {
        return proId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public String getBrdSeason() {
        return brdSeason;
    }

    public void setBrdSeason(String brdSeason) {
        this.brdSeason = brdSeason;
    }

    public String getProIdOld() {
        return proIdOld;
    }

    public void setProIdOld(String proIdOld) {
        this.proIdOld = proIdOld;
    }

    public String getProNmOld() {
        return proNmOld;
    }

    public void setProNmOld(String proNmOld) {
        this.proNmOld = proNmOld;
    }

    public String getPriceBandNm() {
        return priceBandNm;
    }

    public void setPriceBandNm(String priceBandNm) {
        this.priceBandNm = priceBandNm;
    }

    public String getProStyleNm() {
        return proStyleNm;
    }

    public void setProStyleNm(String proStyleNm) {
        this.proStyleNm = proStyleNm;
    }

    public String getProCateNm() {
        return proCateNm;
    }

    public void setProCateNm(String proCateNm) {
        this.proCateNm = proCateNm;
    }

    public String getProGenderNm() {
        return proGenderNm;
    }

    public void setProGenderNm(String proGenderNm) {
        this.proGenderNm = proGenderNm;
    }

    public String getBrdSeasonOld() {
        return brdSeasonOld;
    }

    public void setBrdSeasonOld(String brdSeasonOld) {
        this.brdSeasonOld = brdSeasonOld;
    }
}
