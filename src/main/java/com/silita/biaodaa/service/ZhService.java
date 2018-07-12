package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.SnatchpressMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by dh on 2018/7/11.
 */
@Service
public class ZhService {
    Logger logger = Logger.getLogger(QuaParseService.class);

    @Autowired
    SnatchpressMapper snatchpressMapper;

    @Cacheable(value = "allZhCache", key="'new_findsAllCategory'")
    public List<Map<String, Object>> queryzh() {
        logger.info("#######查询数据库 queryzh...");
        return snatchpressMapper.queryzh();
    }


    @Cacheable(value = "buildZhList", key="'getBuildZhList'")
    public List<Map<String,Object>> getBuildZhList(){
        return snatchpressMapper.getBuildZhList();
    }
}
