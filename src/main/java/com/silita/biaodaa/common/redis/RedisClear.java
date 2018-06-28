package com.silita.biaodaa.common.redis;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by dh on 2017/8/31.
 */
@Component
public class RedisClear {

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    public RedisUtils redisUtils;

    /**
     * 从redis中清理公告内容
     * @param id 公告ID
     */
    public void clearGonggaoContent(String id){
        redisUtils.del(RedisConstantInterface.HN_GG_CONTENT+id);
    }

    public void clearRepeatNotice(String id){
        clearGonggaoContent(id);
        clearGonggaoDataContent(id);
        logger.info("清理重复缓存公告内容:" + id);
    }

    public void clearGonggaoContent(long id){
        clearGonggaoContent(String.valueOf(id));
    }

    /**
     * 从redis中清理公告大文本内容
     * @param id 公告ID
     */
    public void clearGonggaoDataContent(String id){
        redisUtils.del(RedisConstantInterface.HN_GG_DATA_CONTENT_PHONE+id);
        redisUtils.del(RedisConstantInterface.HN_GG_DATA_CONTENT+id);
    }

    public void clearGonggaoDataContent(long id){
        clearGonggaoDataContent(String.valueOf(id));
    }
    /**
     * 从redis中清理公告表单信息（维度数据）
     * @param id 公告ID
     */
    public void clearGonggaoForm(String id){
        redisUtils.del(RedisConstantInterface.HN_GG_FORM+id);
    }

    public void clearGonggaoForm(long id){
        clearGonggaoForm(String.valueOf(id));
    }

    /**
     * 从redis中清理公告资质匹配的企业列表
     * @param id 公告ID
     */
    public void clearGonggaoMatchCorp(String id){
        redisUtils.del(RedisConstantInterface.HN_GG_MATCH+id);
    }
    public void clearGonggaoMatchCorp(long id){
        clearGonggaoMatchCorp(String.valueOf(id));
    }

    /**
     * 从redis中清理公告的相关公告信息
     * @param id 公告ID
     */
    public void clearGonggaoRelation(String id){
        redisUtils.del(RedisConstantInterface.HN_GG_RELACTION+id);
    }
    public void clearGonggaoRelation(long id){
        clearGonggaoRelation(String.valueOf(id));
    }

    /**
     * 从redis中清理公告的文件列表
     * @param id 公告ID
     */
    public void clearGonggaoFileList(String id){
        redisUtils.del(RedisConstantInterface.HN_GG_FILELIST+id);
    }
    public void clearGonggaoFileList(long id){
        clearGonggaoFileList(String.valueOf(id));
    }


    /**
     * 从redis中清理企业基本信息
     * @param id 企业ID
     */
    public void clearCorpInfo(String id){
        redisUtils.del(RedisConstantInterface.HN_CORP_INFO+id);
    }

    /**
     * 从redis中清理企业资质信息
     * @param id 企业ID[翻页参数]
     */
    public void clearCorpZz(String id){
        redisUtils.del(RedisConstantInterface.HN_CORP_ZZ+id);
    }

    /**
     * 从redis中批量清理模糊匹配到的企业资质信息
     * @param prefix 企业ID
     */
    public void clearCorpZzBatch(String prefix){
        redisUtils.batchDel(RedisConstantInterface.HN_CORP_ZZ+prefix);
    }

    /**
     * 从redis中清理企业人员信息
     * @param id 企业ID[翻页参数]
     */
    public void clearCorpStaff(String id){
        redisUtils.del(RedisConstantInterface.HN_CORP_STAFF+id);
    }

    /**
     * 从redis中批量清理模糊匹配到的企业人员信息
     * @param prefix 企业ID
     */
    public void clearCorpStaffBatch(String prefix){
        redisUtils.batchDel(RedisConstantInterface.HN_CORP_STAFF+prefix);
    }

}
