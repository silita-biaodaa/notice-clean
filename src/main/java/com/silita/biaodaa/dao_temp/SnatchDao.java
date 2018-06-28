package com.silita.biaodaa.dao_temp;

import com.silita.biaodaa.model.Cert;
import com.silita.biaodaa.model.SnatchUrl;
import com.snatch.model.EsNotice;
import org.springframework.cache.annotation.Cacheable;

import java.lang.ref.Reference;
import java.util.List;
import java.util.Map;

@Deprecated
public interface SnatchDao {

	boolean isExistUrl(String href);

	void insertNewUrl(String href, String title, String uuid, Long snatchPlanId, Integer type, String openDate, int randomNum, int biddingType, int otherType, int isShow);

	List<SnatchUrl> querysZhaoBiaoGongGaoUrl(Long snatchPlanId);

	void insertUrlContent(Long snatchUrlId, String html);

	/**
	 * 湖南招标网招标信息中标专用
	 */
	void insertNewUrlAndContentZhongbiao(String url, String title, String uuid, int randomNum,
                                         String html, String openDate, List<String> list);
	/**
	 * 湖南招标网信息更新最大ID
	 */
	void updateMaxIdPlan(Integer maxId, Integer panId);

	Integer getLastMaxId(Integer panId);
	/**
	 * 插入湖南招标网候选人
	 * @param url
	 * @param title
	 * @param uuid
	 * @param html
	 */
	void insertNewUrlAndHouXuan(String url, String title, String uuid, int randomNum,
                                String html, String openDate, List<String> list) ;


	void updateCertificateByHandle(String url, String uuid,
                                   String companyName, String address, String legalPerson,
                                   String setupDate, String registeredCapital, String registeredNo, String certificateNo,
                                   String openningDate, String mianCertificate01, String mianCertificate02, String mianCertificate03,
                                   String mianStep01, String mianStep02, String mianStep03, String jobRange,
                                   String change, String remark);


	void insertNewUrlAndHhuoJiang(String url, String title, String uuid,
                                  String html, String companySn);

	
	SnatchUrl getZhaoBiaoContent(int ID);

	void insertCompress(String text, Long snatchUrlId);
	/**
	 * 查询湖南招标网信息
	 * @param nowMaxId
	 * @return
	 */
	Map<String,Object> querysContendsToLunece(Long nowMaxId);
	
	/**
	 * 查询资质信息
	 * @param i
	 * @return
	 */
	Map<String, Object> querysCertToLunece(long i);
	
	/**
	 * 更新修改次数
	 * @param num
	 * @param id
	 */
	void updateModifyNumberCert(String num, int id);
	/**
	 * 每次用户更新保存日期子后则保存两个时间
	 * @param num
	 * @param id
	 */
	void saveModifyNumberCert(String num, int id);
	
	void updateCert(Cert c);

	List insertUrlCert(int id, EsNotice notice);

	List<Map<String,Object>> queryzh();

	/**
	 * 插入企业资质
	 * @param
	 * @return
	 */
	void insertzz(String uuid);

	/**
	 * 插入建造师
	 * @param list
	 * @return
	 */
	void insetZcjianaoshi(List<Map<String, Object>> list);


	void insertCertExcel(String companyName, String legalPerson, String sz);

	/**
	 * 插入公告建造师条件
	 * @param id
	 * @return
	 */
	void insertUrlBuild(int id, Reference<String> contentRef);

	/**
	 * 资质词典库放入缓存
	 * @param 
	 * @return
	 */
	@Cacheable(value = "productCategoryCache", key="'findsAllBuild'")
	List<Map<String, Object>> queryBuildAll();


	void insertFileNotice(String urls, String title, String date, String html);

	void insertFile(String href, String title, String uuid, Long snatchPlanId,
                    Integer type, String openDate);

	void insertPublicNotice(String urls, String title, String date, String html);
 
	void insertPublic(String href, String title, String uuid,
                      Long snatchPlanId, Integer type, String openDate);


	List<Map<String, Object>> querysWebSitePlan(String region);

	void insertSnatchRelation(String title);

	List<Map<String, Object>> queryAnalyzeRangeByField(String field);

	void insertUnanalysis_aptitude(int snatchIUrlId, String aptitude, String snatchContent);

	void updateSnatchUrlById(int snatchId);

	int querySnatchUrlCertByContId(int snatchId);

	List<Map<String, Object>> querySubSnatchUrl();

	/**
	 * 招标公告入ES
	 * @param notice
     */
	void insertZhaobiaoEsNotice(EsNotice notice);
}
