package com.zhiyong.gateway.dal.domain;

import java.util.Date;

public class AppCfg {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column app.id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column app.create_time
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column app.update_time
     *
     * @mbggenerated
     */
    private Date updateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column app.deleted
     *
     * @mbggenerated
     */
    private Integer deleted;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column app.creator
     *
     * @mbggenerated
     */
    private String creator;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column app.updater
     *
     * @mbggenerated
     */
    private String updater;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column app.app_name
     *
     * @mbggenerated
     */
    private String appName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column app.app_key
     *
     * @mbggenerated
     */
    private String appKey;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column app.app_secret
     *
     * @mbggenerated
     */
    private String appSecret;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column app.app_type
     *
     * @mbggenerated
     */
    private String appType;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column app.id
     *
     * @return the value of app.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column app.id
     *
     * @param id the value for app.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column app.create_time
     *
     * @return the value of app.create_time
     *
     * @mbggenerated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column app.create_time
     *
     * @param createTime the value for app.create_time
     *
     * @mbggenerated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column app.update_time
     *
     * @return the value of app.update_time
     *
     * @mbggenerated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column app.update_time
     *
     * @param updateTime the value for app.update_time
     *
     * @mbggenerated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column app.deleted
     *
     * @return the value of app.deleted
     *
     * @mbggenerated
     */
    public Integer getDeleted() {
        return deleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column app.deleted
     *
     * @param deleted the value for app.deleted
     *
     * @mbggenerated
     */
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column app.creator
     *
     * @return the value of app.creator
     *
     * @mbggenerated
     */
    public String getCreator() {
        return creator;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column app.creator
     *
     * @param creator the value for app.creator
     *
     * @mbggenerated
     */
    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column app.updater
     *
     * @return the value of app.updater
     *
     * @mbggenerated
     */
    public String getUpdater() {
        return updater;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column app.updater
     *
     * @param updater the value for app.updater
     *
     * @mbggenerated
     */
    public void setUpdater(String updater) {
        this.updater = updater == null ? null : updater.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column app.app_name
     *
     * @return the value of app.app_name
     *
     * @mbggenerated
     */
    public String getAppName() {
        return appName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column app.app_name
     *
     * @param appName the value for app.app_name
     *
     * @mbggenerated
     */
    public void setAppName(String appName) {
        this.appName = appName == null ? null : appName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column app.app_key
     *
     * @return the value of app.app_key
     *
     * @mbggenerated
     */
    public String getAppKey() {
        return appKey;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column app.app_key
     *
     * @param appKey the value for app.app_key
     *
     * @mbggenerated
     */
    public void setAppKey(String appKey) {
        this.appKey = appKey == null ? null : appKey.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column app.app_secret
     *
     * @return the value of app.app_secret
     *
     * @mbggenerated
     */
    public String getAppSecret() {
        return appSecret;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column app.app_secret
     *
     * @param appSecret the value for app.app_secret
     *
     * @mbggenerated
     */
    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret == null ? null : appSecret.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column app.app_type
     *
     * @return the value of app.app_type
     *
     * @mbggenerated
     */
    public String getAppType() {
        return appType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column app.app_type
     *
     * @param appType the value for app.app_type
     *
     * @mbggenerated
     */
    public void setAppType(String appType) {
        this.appType = appType == null ? null : appType.trim();
    }
}