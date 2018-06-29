package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Snatchpress {
    private Integer id;

    private Integer snatchurlid;

    /**
     * 压缩后内容
     */
    private String press;
}