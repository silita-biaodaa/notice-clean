package com.silita.biaodaa.dao_temp;


import com.silita.biaodaa.common.jdbc.Page;

import java.util.List;
import java.util.Map;

/**
 * Created by gmy on 2017/8/29.
 */
@Deprecated
public interface MessagePushDAO {

    /**
     * 查询用户收藏表收藏用户id
     * @return list
     */
    List<Map<String, Object>> queryCollecNoticeForList(List<String> noticeIdLsit);

    /**
     * 把关联到的用户和相关公告id存入send_message表
     * @param userId 用户id
     * @param mainId 公告id
     * @param relationId 公告关联id
     * @return
     */
    void insertSendMessage(String userId, String mainId, String relationId, String snatchUrl, String title);

    /**
     * 定时读取消息表
     * @return
     */
    List<Map<String, Object>> queryMessagePushForList();

    /**
     * 通过读取消息表里的userId取得用户手机号码
     * @param userId
     * @return
     */
    String getUserPhoneByUserId(String userId);

    /**
     * 通过消息表mainId或relationId查询公告
     * @return
     */
    List<Map<String, Object>> getNoticeTitleById(String noticeid);

    /**
     * 根据用户id更新验证码
     * @param userId
     * @return
     */
    void UpdateIsSendByUserId(String userId);

    /**
     * 根据用户id插入消息
     * @param relationId
     */
    void UpdateMessageByUserId(String message, String relationId, int type);

    /**
     * 根据用户ID取得系统推送信息
     * @param userId
     * @return
     */
    Page getSystempMessage(String userId);

    /**
     * 根据用户ID取得变更信息信息
     * @param userId
     * @return
     */
    Page getChangeMessage(String userId);

    /**
     * 查询是否有重复数据
     * @param mainId
     * @param relationId
     * @return
     */
    int getMessageCountByMainIdAndRelationId(String mainId, String relationId);

}
