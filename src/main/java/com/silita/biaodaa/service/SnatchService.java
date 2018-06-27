package com.silita.biaodaa.service;


import com.silita.biaodaa.model.Cert;

import java.util.List;
import java.util.Map;

@Deprecated
public interface SnatchService {

	/**
	 * 更新企业信息
	 * @param c
	 * @return
	 */
	void updateCert(Cert c);
	
	/**
	 * 加载资质词典库
	 * @param 
	 * @return
	 */
	List<Map<String,Object>> queryzh();

	/**
	 * 查询各地区网站
	 * @param region
	 * @return
	 */
	List<Map<String, Object>> querysWebSitePlan(String region);


	void insertSnatchRelation(String title);

	void updateSnatchUrlById(int snatchId);

	boolean querySnatchUrlCertByContId(int snatchId);

	List<Map<String, Object>> querySnatchUrl();


}

