package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.SnatchpressMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
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

    /**
     * 获取资质大类前缀
     * @param preSize 前缀字符个数
     * @return
     */
    @Cacheable(value = "preCertCache", key="'queryQuaCategory'+#preSize")
    public List<Map<String,Object>>  queryQuaCategory(int preSize){
        logger.info("#######查询数据库 preCertCache...");
        if(preSize<5)preSize= 5;
        Map param = new HashMap();
        param.put("preSize",preSize);
//        param.put("preChiSize",preSize*3);
        return snatchpressMapper.queryQuaCategory(param);
    }

    @Cacheable(value="sortQuaAlias",key = "'sortQuaAlias'+#preSize")
    public  Map<String,List<Map<String, Object>>>  sortQuaAlias(int preSize){
        logger.info("#######查询数据库 sortQuaAlias...");
        if(preSize<5)preSize= 5;

        Map param = new HashMap();
        param.put("preSize",preSize);
        List<Map<String,Object>>  quaAlias = snatchpressMapper.queryQuaCategoryAlias(param);
        Map sortAlias = new HashMap(3000);
        sortToMap(quaAlias,sortAlias);
        quaAlias=null;
        return  sortAlias;
    }

    /**
     *
     * @param quaAlias
     * @return
     */
    private Map<String,List<Map>> sortToMap(List<Map<String,Object>>  quaAlias,Map sortAlias){
        List<Map<String,Object>> tmpList = null;
        String beforeName = null;
        int len = quaAlias.size();
        for(int i=0; i<len;i++){
            Map aliaMap = quaAlias.get(i);
            String preName = aliaMap.get("preName").toString();
            if(i==0){
                beforeName= preName;
                tmpList = new ArrayList<>();
                tmpList.add(aliaMap);
            }else{
                if (!preName.equals(beforeName)) {
                    sortAlias.put(beforeName, tmpList);
                    beforeName = preName;
                    tmpList = new ArrayList<>();
                }else{
                }
                tmpList.add(aliaMap);
            }
            if(i==len-1) {//收尾
                if(!tmpList.isEmpty()) {
                    sortAlias.put(preName, tmpList);
                }
            }
        }
        return sortAlias;
    }
}
