package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnatchUrlCert {
    private Integer id;

    /**
     * 公告snatchUrlId
     */
    private Integer contid;

    /**
     * 资质要求
     */
    private String certificate;

    /**
     * 资质uuid
     */
    private String certificateuuid;

    /**
     * 安全生产许可证
     */
    private String licence;

    /**
     * 公告类型0招标；1中标
     */
    private String type;

}