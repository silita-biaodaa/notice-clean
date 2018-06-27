package com.silita.biaodaa.utils;

import com.silita.biaodaa.common.Constant;
import com.snatch.model.EsNotice;

/**
 * Created by dh on 2018/5/11.
 */
public class RouteUtils {

    public static String routeTableName(String tbName, EsNotice esNotice){
        String source = esNotice.getSource();
        return routeTableName(tbName,source);
    }

    public static String routeTableName(String tbName, String source) {
        //湖南数据暂不路由
        if(source.equals(Constant.HUNAN_SOURCE)){
            return tbName;
        }else{
            return tbName+"_"+source;
        }
    }
}
