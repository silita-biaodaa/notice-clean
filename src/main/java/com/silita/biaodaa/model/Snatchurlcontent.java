package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Snatchurlcontent {
    private Integer id;

    /**
     * 对应snatchUrl的id
     */
    private Integer snatchurlid;

    /**
     * 抓取页面的内容
     */
    private String content;

    private String press;

    private String contentpress;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取对应snatchUrl的id
     *
     * @return snatchUrlId - 对应snatchUrl的id
     */
    public Integer getSnatchurlid() {
        return snatchurlid;
    }

    /**
     * 设置对应snatchUrl的id
     *
     * @param snatchurlid 对应snatchUrl的id
     */
    public void setSnatchurlid(Integer snatchurlid) {
        this.snatchurlid = snatchurlid;
    }

    /**
     * 获取抓取页面的内容
     *
     * @return content - 抓取页面的内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置抓取页面的内容
     *
     * @param content 抓取页面的内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return press
     */
    public String getPress() {
        return press;
    }

    /**
     * @param press
     */
    public void setPress(String press) {
        this.press = press;
    }

    /**
     * @return contentPress
     */
    public String getContentpress() {
        return contentpress;
    }

    /**
     * @param contentpress
     */
    public void setContentpress(String contentpress) {
        this.contentpress = contentpress;
    }
}