package com.silita.biaodaa.rules.Interface;

import com.snatch.model.EsNotice;

/**
 * Created by dh on 2018/3/14.
 */
public interface RepeatRule {
    String IS_UPDATED = "isUpdated";//新进有效公告已替换旧公告

    String IS_NEW = "isNewNotice";//新进有效公告

    String IS_REPEATED = "isRepeated";//新公告已进去重表

    boolean executeRule(EsNotice esNotice);
}
