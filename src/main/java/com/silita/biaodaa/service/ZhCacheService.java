package com.silita.biaodaa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by dh on 2018/1/17.
 */
@Component
public class ZhCacheService {

    @Autowired
    private SnatchService service;

    private static List<Map<String, Object>> zh= null;

    public synchronized List<Map<String, Object>> getZh(){
        if(zh ==null) {
            zh = service.queryzh();//所有资质
        }
        return zh;
    }

}
