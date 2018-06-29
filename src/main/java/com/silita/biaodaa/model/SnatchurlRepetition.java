package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SnatchurlRepetition {
    private Integer id;

    /**
     * 公告uuid
     */
    private String noticeuuid;

    /**
     * 标题
     */
    private String title;

    /**
     * url
     */
    private String url;

    /**
     * 公示日期
     */
    private Date opendate;

    /**
     * 0=省级；1=市级；2=县级
     */
    private Integer rank;

    /**
     * redisId
     */
    private Integer redisid;

    /**
     * 所属网站id
     */
    private Integer websiteplanid;

    private Date snatchdatetime;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 去重方式  0:自动去重 1:手动去重
     */
    private Integer reptmethod;

    /**
     * 公告来源省份
     */
    private String source;

    /**
     * 内容
     */
    private String content;
}