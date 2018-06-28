package com.silita.biaodaa.model;

/**
 * Created by 91567 on 2017/9/15.
 */
public class MessagePush extends Mo{
    private String userId;
    private String mainId;
    private String relationId;
    private String snatchUrl;
    private String title;
    private String message;
    private String createDate;
    private int isSend;
    private int isSystem;
    private int type;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getMainId() {
        return mainId;
    }
    public void setMainId(String mainId) {
        this.mainId = mainId;
    }
    public String getRelationId() {
        return relationId;
    }
    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }
    public String getSnatchUrl() {
        return snatchUrl;
    }
    public void setSnatchUrl(String snatchUrl) {
        this.snatchUrl = snatchUrl;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getCreateDate() {
        return createDate;
    }
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    public int getIsSend() {
        return isSend;
    }
    public void setIsSend(int isSend) {
        this.isSend = isSend;
    }
    public int getIsSystem() {
        return isSystem;
    }
    public void setIsSystem(int isSystem) {
        this.isSystem = isSystem;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
}
