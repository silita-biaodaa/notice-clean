package com.silita.biaodaa.rules.Interface;

import com.snatch.model.EsNotice;

import java.util.Set;

public interface RepeatFilter {

    String filterRule(EsNotice esNotice, Set<EsNotice> matchSet) throws Exception;
}
