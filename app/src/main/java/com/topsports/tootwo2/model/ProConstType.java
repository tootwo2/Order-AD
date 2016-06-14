package com.topsports.tootwo2.model;

import org.litepal.crud.DataSupport;

/**
 * Created by zhang.p on 2015/9/1.
 */
public class ProConstType extends DataSupport {
    private int id;

    private String classType;
    private String classKey;
    private String classValue;
    private String remark;
    private String valid_flg;
    private String version;
    private String custom_Resource;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getClassKey() {
        return classKey;
    }

    public void setClassKey(String classKey) {
        this.classKey = classKey;
    }

    public String getClassValue() {
        return classValue;
    }

    public void setClassValue(String classValue) {
        this.classValue = classValue;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getValid_flg() {
        return valid_flg;
    }

    public void setValid_flg(String valid_flg) {
        this.valid_flg = valid_flg;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCustom_Resource() {
        return custom_Resource;
    }

    public void setCustom_Resource(String custom_Resource) {
        this.custom_Resource = custom_Resource;
    }
}
