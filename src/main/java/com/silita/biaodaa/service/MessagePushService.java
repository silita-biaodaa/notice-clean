package com.silita.biaodaa.service;

import java.util.Map;

/**
 * Created by gmy on 2017/8/29.
 */
@Deprecated
public interface MessagePushService {

    /**
     * 每次有新公告关联上时、匹配用户近3个月收藏表
     * @param map
     * @param snatchUrl 新来的公告url
     * @return
     */
    boolean queryCollectNotice(Map<String, String> map, String snatchUrl);

}
