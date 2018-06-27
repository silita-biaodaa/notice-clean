package com.silita.biaodaa.dao_temp;

import com.silita.biaodaa.common.elastic.model.ElasticEntity;
import com.snatch.model.AnalyzeDetail;
import com.snatch.model.AnalyzeDetailZhongBiao;
import com.snatch.model.EsNotice;

import java.util.List;
import java.util.Map;

/**
 * 湖南数据整合专用
 * @author Administrator
 *
 */
@Deprecated
public interface SnatchNoticeHuNanDao {

	boolean insertEsNotice(EsNotice notice);

	void insertEsNoticeElasticSearch(EsNotice notice);

	int insertZhaobiaoAnalyzeDetail(AnalyzeDetail ad);

	int insertZhongBiaoAnalyzeDetail(AnalyzeDetailZhongBiao ad);

	int insertOrUpdateAnalyzeDetail(AnalyzeDetail ad);

	int insertOrUpdateAnalyzeDetailZhongBiao(AnalyzeDetailZhongBiao ad);

	boolean insertEsNoticeZhongBiao(EsNotice notice);	//中标

	List<Map<String,Object>> querysLikeNotice(String titleTemp, String openDate);

	void insertSnatchRelation(String mainId, String nextId);

	//查询2个公告id所关联的所有公告id（不包含自己）
	List<String> querysDifferNextId(String zhaobId, String thisId);

	/**
	 * 招标公告插入ES(新增)
	 * @param notice
     */
	void insertZhaobiaoEsNotice(EsNotice notice);

	/**
	 * 招标公告更新ES(新增)
	 * @param notice
     */
	void updateZhaobiaoEsNotice(EsNotice notice);

	/**
	 * 中标公告插入ES(新增)
	 * @param notice
     */
	void insertZhongbiaoEsNotice(EsNotice notice);

	List<String> querysLikeUrl(String urlKey, String source);

	List<String> querysLikeUrl(List<String> urlKeys, String source);

	String queryThisId(String thisUrl, String source);

	/**
	 * 用标题模糊查询前后3个月的公告
	 * @param no
	 * @return
	 */
	List<Map<String,Object>> querySimilarityNotice (EsNotice no, String websiteUrl, String tempTitle);


	List<String> queryRelationNextIds(String nextId);

	/**
	 * 批量插入关联表
	 * @param thisId
	 * @param nextIds
	 */
	Map<String,String> batchInsertRelation(String thisId, List<String> nextIds);

	int queryForInt(String var1, Object[] var2);

	boolean isNoticeExists(EsNotice esNotice);



	void updateSnatchUrlCert(Integer id, Integer historyId);

	/**
	 * 用标题、公示时间、地区、类型 搜索 前后3天且不属于同一网站的公告
	 * @param tempTile
	 * @return
	 */
	List<EsNotice> queryNoticeList(String tempTile, String url, EsNotice esNotice);

	/**
	 * 查询同一地区前后3天相同类型公告
	 * @param
	 */
	List<EsNotice> queryNoticeList(String url, EsNotice esNotice);


	void handleNotRepeatZhaobiao(EsNotice notice);

	/**
	 * 公告插数据库
	 * @param n
	 * @return
	 */
	Map<String, String> insertNotice(EsNotice n);

	/**
	 * 插入重复记录表
	 * @param n
	 */
	void insertNoticeRepetition(EsNotice n);

	void deleteSnatchUrlById(String id);

	/**
	 * 插公告表
	 */
	void insertNewUrl(EsNotice esNotice);

	void insertCompress(EsNotice esNotice, Long snatchUrlId);

	/**
	 * 插入公告内容表
	 * @param id
	 * @param n
	 */
	void insertSnatchContent (int id, EsNotice n);

	/**
	 * 更新snatchurl表的状态
	 * @param id
	 */
	void updateSnatchurlStatus(int id, String source);

	/**
	 * 更新snatchurl的isShow字段
	 */
	void updateSnatchurlisShow(String id, int isShow, String source);

	/**
	 * 更新snatchurl表的公告信息
	 * @param notice
	 */
	void updateSnatchurlNotice (EsNotice notice, String uuid);

	void updateSnatchContent (EsNotice notice);

	void updateSnatchPress (EsNotice notice);

	void deleteIndexById(Class<? extends ElasticEntity> clazz, String id);

	void deleteRelationInfo(Integer relationId);

	void updateSnatchurlEdit(Integer id);

	void editZhaobiaoDetail(Integer id, Integer historyId);

	void editZhongbiaoDetail(Integer id, Integer historyId);
}
