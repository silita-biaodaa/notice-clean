package com.silita.biaodaa.service;


import com.silita.biaodaa.dao_temp.SnatchDao;
import com.silita.biaodaa.model.Cert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Deprecated
@Service
public class SnatchServiceImpl implements SnatchService{

	@Autowired
	@Qualifier("snatchDaoImpl")
	private SnatchDao snatchDao;

	@Override
	public void updateCert(Cert c) {
		snatchDao.updateCert(c);
	}

	@Override
	@Cacheable(value = "allZhCache", key="'findsAllCategory'")
	public List<Map<String, Object>> queryzh() {
		return snatchDao.queryzh();
	}

	@Override
	@Cacheable(value = "webSitePlanCache", key="#region")
	public List<Map<String, Object>> querysWebSitePlan(String region) {
		return snatchDao.querysWebSitePlan(region);
	}

	public void insertSnatchRelation(String title){snatchDao.insertSnatchRelation(title);}

	@Override
	public void updateSnatchUrlById(int snatchId) {
		snatchDao.updateSnatchUrlById(snatchId);
	}

	@Override
	public boolean querySnatchUrlCertByContId(int snatchId) {
		int count = snatchDao.querySnatchUrlCertByContId(snatchId);
		if(count == 0) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public List<Map<String, Object>> querySnatchUrl() {
		List<Map<String, Object>> list = snatchDao.querySubSnatchUrl();
		return list;
	}

}
