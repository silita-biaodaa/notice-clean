package com.silita.biaodaa.controller;

import com.silita.biaodaa.service.IElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by IntelliJ Idea 2018.1
 * Company: silita
 * Author: gemingyi
 * Date: 2018-08-01:16:37
 */

@Controller
@RequestMapping("/elasticSearch/")
public class ElasticeSearchController {

    @Autowired
    IElasticSearchService elasticSearchService;

    /**
     * 更新招标es数据
     * @param openDate
     */
    @ResponseBody
    @RequestMapping(value = "updateZhaoBiao",method = RequestMethod.POST,produces="application/json;charset=utf-8")
    public Map<String,Object> updateZhaoBiaoData(@RequestBody String openDate) {
        Map<String,Object> result = new HashMap<>();
        result.put("code",1);
        result.put("msg","更新招标数据成功！");
        try{
            elasticSearchService.insertZhaoBiaoData(openDate);
        } catch (Exception e) {
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    /**
     * 更新中标es数据
     * @param openDate
     */
    @ResponseBody
    @RequestMapping(value = "updateZhongBiao",method = RequestMethod.POST,produces="application/json;charset=utf-8")
    public Map<String,Object> updateZhongBiaoData(@RequestBody String openDate) {
        Map<String,Object> result = new HashMap<>();
        result.put("code",1);
        result.put("msg","更新中标数据成功！");
        try{
            elasticSearchService.insertZhongBiaoData(openDate);
        } catch (Exception e) {
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }
}
