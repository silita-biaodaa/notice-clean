package com.silita.biaodaa.service;

import com.silita.biaodaa.dao_temp.MessagePushDAO;
import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gmy on 2017/8/29.
 */
@Deprecated
@Service("messagePushService")
public class MessagePushServiceImpl implements MessagePushService {

    @Autowired
    private MessagePushDAO messagePushDAO;

    private static Logger logger = Logger.getLogger(MessagePushServiceImpl.class);

    @Override
    public boolean queryCollectNotice(Map<String, Object> map, String snatchUrl) {
        String mainId = (String)map.get("mainId");
        String relationId = (String)map.get("nextId");
        List<String> nextIds = (List<String>)map.get("nextIds");
        if(MyStringUtils.isNull(mainId) && MyStringUtils.isNull(relationId)){
            return false;
        }

        List<String> noticeIdList = new ArrayList<String>();
        if(MyStringUtils.isNotNull(mainId)) {
            noticeIdList.add(mainId);
        }
        if(MyStringUtils.isNotNull(relationId)) {
            noticeIdList.add(relationId);
        }
        List<Map<String, Object>> list = this.messagePushDAO.queryCollecNoticeForList(noticeIdList);    //遍历用户收藏表当前-3个月前

        for(Map resMap : list){
            String userId = resMap.get("userId").toString();
            String title = "您收藏的公告‘" + resMap.get("title").toString() + "'有新增关联公告，请及时查看公告详情。";
            if(mainId.equals(resMap.get("noticeId").toString())) {
                this.messagePushDAO.insertSendMessage(userId, mainId, relationId, snatchUrl, title);  //插入消息表
            }else if(relationId.equals(resMap.get("noticeId").toString())) {
                this.messagePushDAO.insertSendMessage(userId, relationId, mainId, snatchUrl, title);
            }
            logger.debug("insertSendMessage:" +title +"[userId:"+userId+"][mainId:"+mainId+"][relationId:"+relationId+"][snatchUrl:"+snatchUrl+"]");
        }
        return true;
    }
}