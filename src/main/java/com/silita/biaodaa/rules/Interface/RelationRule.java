package com.silita.biaodaa.rules.Interface;

import com.snatch.model.EsNotice;

import java.util.Map;

/**
 * Created by dh on 2018/3/14.
 */
public interface RelationRule{
    Map<String, String> executeRule(EsNotice esNotice);
}
