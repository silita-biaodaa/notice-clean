package com.silita.biaodaa.dao_temp;

import com.alibaba.fastjson.JSON;
import com.silita.biaodaa.common.elastic.ElaticsearchUtils;
import com.silita.biaodaa.common.elastic.indexes.IdxZhaobiaoSnatch;
import com.silita.biaodaa.common.jdbc.JdbcBase;
import com.silita.biaodaa.common.jdbc.Page;
import com.silita.biaodaa.disruptor.DisruptorOperator;
import com.silita.biaodaa.model.Cert;
import com.silita.biaodaa.model.SnatchUrl;
import com.silita.biaodaa.service.ZhService;
import com.silita.biaodaa.utils.ChineseCompressUtil;
import com.silita.biaodaa.utils.CommonUtil;
import com.snatch.model.EsNotice;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.*;

@Deprecated
@Repository
public class SnatchDaoImpl extends JdbcBase implements SnatchDao {

	@Autowired
	private Client client;

	@Autowired
	private ElaticsearchUtils elaticsearchUtils;

	@Autowired
	private DisruptorOperator disruptorOperator;


	private ZhService zhService;

	String uuid;

	@Override
	public boolean isExistUrl(String href) {
		return this.getJdbcTemplate().queryForObject("select count(1) num from mishu.snatchurl where url=?", new Object[]{href}, Integer.class) > 0;
	}

	@Override
	public synchronized void insertNewUrl(String href, String title ,String uuid,Long snatchPlanId,Integer type,String openDate,int randomNum,int biddingType,int otherType,int isShow) {
		this.getJdbcTemplate().update("INSERT INTO mishu.snatchurl(" +
						"url,title, snatchDatetime, snatchPlanId," +
						"`type`,`uuid`,`status`,openDate," +
						"`range`,randomNum,biddingType,otherType," +
						"isShow,tableName,suuid)VALUES(" +
						"?,?,NOW(),?," +
						"?,?,0,?," +
						"YEAR(?),?,?,?," +
						"?,'mishu.snatchurl',REPLACE(UUID(),'-',''))",
				href,title,snatchPlanId,
				type,uuid,openDate,
				openDate,randomNum,biddingType,otherType,
				isShow);
	}

	@Override
	public synchronized void insertFile(String href, String title ,String uuid,Long snatchPlanId,Integer type,String openDate) {
		this.getJdbcTemplate().update("INSERT INTO mishu.snatchurl_notice(url,title, snatchDatetime, snatchPlanId, `type`,`uuid`,`status`,openDate)VALUES(?,?,NOW(),?,?,?,0,?)",
				href,title,snatchPlanId,type,uuid,openDate);
	}

	@Override
	public synchronized void insertPublic(String href, String title ,String uuid,Long snatchPlanId,Integer type,String openDate) {
		this.getJdbcTemplate().update("INSERT INTO mishu.snatchurl_notice(url,title, snatchDatetime, snatchPlanId, `type`,`uuid`,`status`,openDate)VALUES(?,?,NOW(),?,?,?,0,?)",
				href,title,snatchPlanId,type,uuid,openDate);
	}

	@Override
	public List<SnatchUrl> querysZhaoBiaoGongGaoUrl(Long snatchPlanId) {
		return this.querys("select * from mishu.snatchurl where status=0 and snatchPlanId=?",new Object[]{snatchPlanId}, SnatchUrl.class);
	}

	@Override
	public void insertUrlContent(Long snatchUrlId, String html) {
		if(this.queryForInt("select count(1) num from mishu.snatchurlcontent where snatchUrlId=?",new Object[]{snatchUrlId})<1){
			this.getJdbcTemplate().update("INSERT INTO mishu.snatchurlcontent (content, snatchUrlId)VALUES(?,?)", html,snatchUrlId);
			this.getJdbcTemplate().update("update mishu.snatchurl set status=1 where id=?", snatchUrlId);
		}
	}


	/**
	 * 湖南招标网招标信息中标专用
	 */
	@Override
	public void insertNewUrlAndContentZhongbiao(String url, String title, String uuid,int randomNum,
												String html,String openDate,List<String> list) {
		if( this.queryForInt("select count(id) num from mishu.snatchurl where url=?",new Object[]{url})<1){
			insertNewUrl(url,title,uuid,(long) 3,2,openDate, randomNum,0,0,0);
			int id = this.queryForInt("select id from mishu.snatchurl where url=?",new Object[]{url});
			this.getJdbcTemplate().update("INSERT INTO mishu.snatchurlcontent (content, snatchUrlId)VALUES(?,?)", html,id);
			ChineseCompressUtil util=new ChineseCompressUtil();
			String plainHtml=util.getPlainText(html);
			insertCompress(plainHtml,Long.valueOf((id)));
			this.getJdbcTemplate().update("update mishu.snatchurl set status=1 where id=?", id);

			insertSnatchRelation(Long.valueOf((id)), list);//公告相关信息关系表

			//insertElasticSearch(Long.valueOf((id)));//将整理后的文档存入搜索引擎
		}
	}

	/**
	 * 湖南招标网招标信息候选人
	 */
	@Override
	public void insertNewUrlAndHouXuan(String url, String title, String uuid,int randomNum,
									   String html,String openDate,List<String> list) {
		if( this.queryForInt("select count(id) num from mishu.snatchurl where url=?",new Object[]{url})<1){
			insertNewUrl(url,title,uuid,(long) 4,2,openDate,randomNum,0,0,0);
			int id = this.queryForInt("select id from mishu.snatchurl where url=?",new Object[]{url});
			this.getJdbcTemplate().update("INSERT INTO mishu.snatchurlcontent (content, snatchUrlId)VALUES(?,?)", html,id);
			ChineseCompressUtil util=new ChineseCompressUtil();
			String plainHtml=util.getPlainText(html);
			insertCompress(plainHtml,Long.valueOf((id)));
			this.getJdbcTemplate().update("update mishu.snatchurl set status=1 where id=?", id);

			insertSnatchRelation(Long.valueOf((id)), list);//公告相关信息关系表

			//insertElasticSearch(Long.valueOf((id)));//将整理后的文档存入搜索引擎
		}
	}


	/**
	 * 公告相关信息
	 */
	public void insertSnatchRelation(Long snatchUrlId,List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			List<Map<String,Object>> idlist=this.getJdbcTemplate().queryForList("SELECT id from mishu.snatchurl WHERE url=?", list.get(i));
			if(idlist.size()>0){
				this.getJdbcTemplate().update("INSERT INTO mishu.snatchrelation (mainId, nextId, lastChangeTime,relationMethod)VALUES(?,?,NOW(),0)", snatchUrlId,idlist.get(0).get("id"));
			}
		}
	}

	@Override
	/**
	 * 相关公告关联方法
	 * @title 公告标题
	 */
	 public void insertSnatchRelation(String title){
		if(!"".equals(title.trim())){
			//1.根据title找到id
			String sql = "SELECT id FROM mishu.snatchurl WHERE title=?";
			List<Map<String,Object>> idlist = this.getJdbcTemplate().queryForList(sql,title);
			String mainId = "";
			if(idlist.size()>0){
				mainId = String.valueOf(idlist.get(0).get("id"));
			}

			//2.处理title
			String[] keywords = {"招标","答疑","补充","澄清","延期","流标","修改","中标"};
			String titleTemp = "";
			int index = 0;
			for (int i = 0; i < keywords.length; i++) {
				if((index=title.indexOf(keywords[i]))!=-1) break;
			}
			if(index!=-1){
				titleTemp = title.substring(0,index);
			}else {
				titleTemp = title.substring(0, 10);
			}


			//3.查询类似公告
			sql = "SELECT id,title FROM mishu.snatchurl WHERE title LIKE ?";
			List<Map<String,Object>> list = this.getJdbcTemplate().queryForList(sql, titleTemp+"%");

			//4.将数据插入关联表
			if(list.size()>1){
				for (Map<String,Object> map:list) {
					if(String.valueOf(map.get("title")).contains("招标")){
						for (int i = 0; i < list.size(); i++) {
							String nextId = String.valueOf(list.get(i).get("id"));
							if(mainId.equals(nextId)) continue;
							this.getJdbcTemplate().update("INSERT INTO mishu.snatchrelation (mainId, nextId, lastChangeTime,relationMethod)VALUES(?,?,NOW(),0)", Long.valueOf(mainId),Long.valueOf(nextId));
//							logger.info("\n------------------------------------------------------------------------------");
//							logger.info(title+"\n\t与\n"+String.valueOf(list.get(i).get("title"))+"\n关联成功");
//							logger.info("------------------------------------------------------------------------------");
						}
						return;
					}
				}
//				logger.info("\t相关公告中无招标公告！关联失败！\n");
			}else{
				logger.info(title+"\t无相关公告！\n");
			}
		}
	}

	@Override
	public List<Map<String, Object>> queryAnalyzeRangeByField(String field) {
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList("select * from mishu_snatch.analyze_range where field=?", field);
		return list;
	}

	public Page querysSnatchUrl(SnatchUrl s) {
		String sql="select * from mishu.snatchurl s where 1=1";
		List<String> params=new ArrayList<String>();
		if(this.vertify(s.getTitle())){
			sql+=" and s.title like ? ";
			params.add("%"+s.getTitle()+"%");
		}
		if(this.vertify(s.getType())){
			sql+=" and s.type = ? ";
			params.add(String.valueOf(s.getType()));
		}
		if(this.vertify(s.getOpenDate())){
			sql+=" and s.openDate = ? ";
			params.add(s.getOpenDate());
		}

		sql+=" order by s.openDate desc";

		return this.queryForPage(sql, params,s);
	}

	@Override
	public void updateMaxIdPlan(Integer maxId,Integer panId) {
		this.getJdbcTemplate().update("UPDATE mishu.snatchplan SET snatchLastSize=? WHERE id=?", maxId,panId);
	}

	@Override
	public Integer getLastMaxId(Integer panId) {
		return this.queryForInt("SELECT snatchLastSize FROM mishu.snatchplan WHERE id=?",new Object[]{panId});
	}

	@Override
	public void updateCertificateByHandle(String url, String uuid,
										  String companyName, String address, String legalPerson,
										  String setupDate, String registeredCapital, String registeredNo,String  certificateNo,
										  String openningDate, String mianCertificate01, String mianCertificate02, String mianCertificate03,
										  String mianStep01, String mianStep02, String mianStep03, String jobRange,
										  String change,String remark) {
		if( this.queryForInt("select count(id) num from mishu.cert_url where url=?",new Object[]{url})<1){
			String sql="INSERT INTO mishu.cert_url( url, title, snatchPlanId,uuid)VALUES(?,?,?,?)";
			this.getJdbcTemplate().update(sql, url,companyName,5,uuid);
			int id = this.queryForInt("select id from mishu.cert_url where url=?",new Object[]{url});
			String insertDetail="INSERT INTO mishu.cert_src ( "+
					" certificateUrlId, companyName, address, legalPerson, "+
					" setupDate, registeredCapital, registeredNo, certificateNo,  "+
					" openningDate, mianCertificate01, mianCertificate02, mianCertificate03, "+
					" mianStep01, mianStep02, mianStep03, jobRange,  "+
					" `change`, remark) "+
					" VALUES(?,?,?,?, "+
					" ?,?,?,?, "+
					" ?,?,?,?, "+
					" ?,?)";
			this.getJdbcTemplate().update(insertDetail, id, companyName, address, legalPerson,
					setupDate, registeredCapital, registeredNo, certificateNo,
					openningDate, mianCertificate01, mianCertificate02, mianCertificate03,
					mianStep01, mianStep02, mianStep03, jobRange,
					change, remark);
			this.getJdbcTemplate().update("update mishu.cert_url set status=1 where id=?", id);

		}
	}




	/**
	 * 企业业绩增加方法
	 * @param list
	 * @param uuid
	 */
	public void insertQyyj(List<Map<String, Object>> list,String uuid){
		for (int i = 0; i < list.size(); i++) {
			//业绩是否已经存在
			if(this.queryForInt("SELECT count(srcUUid) num FROM mishu.cert_qyyj WHERE projName =? AND srcUUid=?", new Object[]{list.get(i).get("projName"),uuid})<1){

				//查找词典表类似业绩uuid
				String zhuuid ="";
				String sqlzh = "SELECT p.uuid FROM prize_zh p WHERE p.type ='ls' AND ? LIKE CONCAT('%',p.mateName,'%')";
				List<Map<String, Object>> zh = this.getJdbcTemplate().queryForList(sqlzh, list.get(i).get("projSite"));
				if(zh.size()>0){
					zhuuid=zh.get(0).get("uuid").toString();
				}


				String sql="INSERT INTO mishu.cert_qyyj (" +
						"srcUUid, projName, ownerName, projDutyName, " +
						"projDutyIdcard, projDutyCN, projType, projSite, " +
						"finishDate, zPrice, zbPrice, projSkillDuty, " +
						"zbBookDate, zbBookNo, projDes,zhUUid)" +
						"VALUES( " +
						"?,?,?,?,"+
						"?,?,?,?,"+
						"?,?,?,?,"+
						"?,?,?,?)";
				this.getJdbcTemplate().update(sql, uuid,list.get(i).get("projName"),list.get(i).get("ownerName"),list.get(i).get("projDutyName"),
						list.get(i).get("projDutyIdcard"),list.get(i).get("projDutyCN"),list.get(i).get("projType"),list.get(i).get("projSite"),
						list.get(i).get("finishDate"),list.get(i).get("zPrice"),list.get(i).get("zbPrice"),list.get(i).get("projSkillDuty"),
						list.get(i).get("zbBookDate"),list.get(i).get("zbBookNo"),list.get(i).get("projDes"),zhuuid);
			}
		}
	}

	/**
	 * 企业获奖增加方法
	 * @param list
	 * @param uuid
	 */
	public void insertQyhj(List<Map<String, Object>> list,String uuid){
		for (int i = 0; i < list.size(); i++) {
			//奖项是否已经存在
			if(this.queryForInt("SELECT count(srcUUid) num FROM mishu.cert_qyhj WHERE projName =? AND srcUUid=?", new Object[]{list.get(i).get("projName"),uuid})<1){

				//查找词典表获奖uuid
				String zhuuid ="";
				String sqlzh = "SELECT p.uuid FROM prize_zh p WHERE (p.type = 'gjjhj' OR p.type = 'sjhj') AND ? LIKE CONCAT('%',p.mateName,'%')";
				List<Map<String, Object>> zh = this.getJdbcTemplate().queryForList(sqlzh, list.get(i).get("awardName"));
				if(zh.size()>0){
					zhuuid=zh.get(0).get("uuid").toString();
				}

				String sql="INSERT INTO mishu.cert_qyhj (" +
						"srcUUid, awardName, projName, projType, " +
						"projSite, awardSection, awardDate, awardContent, " +
						"years, zhUUid) "+
						"VALUES( " +
						"?,?,?,?,"+
						"?,?,?,?," +
						"?,?)";
				this.getJdbcTemplate().update(sql, uuid,list.get(i).get("awardName"),list.get(i).get("projName"),list.get(i).get("projType"),
						list.get(i).get("projSite"),list.get(i).get("awardSection"),list.get(i).get("awardDate"),list.get(i).get("awardContent"),
						list.get(i).get("years"),zhuuid);
			}
		}
	}


	/**
	 * 信用等级增加方法
	 * @param list
	 * @param uuid
	 */
	public void insertXydj(List<Map<String, Object>> list,String uuid){
		for (int i = 0; i < list.size(); i++) {

			String a="";

			//3A
			if(list.get(i).get("crName").toString().indexOf("AAA")!=-1){
				a="AAA";
			}else if(list.get(i).get("crName").toString().indexOf("AA")!=-1){
				a="AA";
			}else{
				a=list.get(i).get("crName").toString();
			}

			//查找词典表信用等级uuid
			String zhuuid="";
			String sqlzh = "SELECT p.uuid FROM prize_zh p WHERE (p.type = 'qyxy3a' OR p.type = 'qyxy2a') AND p.mateName =?";
			List<Map<String, Object>> zh = this.getJdbcTemplate().queryForList(sqlzh, a);
			if(zh.size()>0){
				zhuuid=zh.get(0).get("uuid").toString();
			}

			//信用等级是否已经存在
			if(this.queryForInt("SELECT count(srcUUid) num FROM mishu.cert_xydj WHERE crName =? AND srcUUid=?", new Object[]{list.get(i).get("crName"),uuid})<1){

				String sql="INSERT INTO mishu.cert_xydj (srcUUid, crName, issueDate, years, zhUUid)VALUES(?,?,?,?,?)";
				this.getJdbcTemplate().update(sql, uuid,list.get(i).get("crName"),list.get(i).get("issueDate"),list.get(i).get("years"),zhuuid);
			}
		}
	}

	/**
	 * 抢险救灾增加方法
	 * @param list
	 * @param uuid
	 */
	public void insertQxjz(List<Map<String, Object>> list,String uuid){
		for (int i = 0; i < list.size(); i++) {
			//奖项是否已经存在
			if(this.queryForInt("SELECT count(srcUUid) num FROM mishu.cert_qxjz WHERE qxjzEvent =? AND srcUUid=?", new Object[]{list.get(i).get("qxjzEvent"),uuid})<1){

				String sql="INSERT INTO mishu.cert_qxjz (" +
						"srcUUid, qxjzEvent, citeTitle, awardSectionType, " +
						"issueSection, issueDate, citeContent) "+
						"VALUES( " +
						"?,?,?,?,"+
						"?,?,?)";
				this.getJdbcTemplate().update(sql, uuid,list.get(i).get("qxjzEvent"),list.get(i).get("citeTitle"),list.get(i).get("awardSectionType"),
						list.get(i).get("issueSection"),list.get(i).get("issueDate"),list.get(i).get("citeContent"));
			}
		}
	}

	/**
	 * 安全认证增加方法
	 * @param map
	 * @param uuid
	 */
	public void insertAqrz(Map<String, Object> map,String uuid){
		//判断是否存在
		if(this.queryForInt("SELECT count(srcUUid) num FROM mishu.cert_aqrz WHERE srcUUid=?", new Object[]{uuid})<1){
			if(!"".equals(map.get("secureLevel"))){
				//查找词典表安全认证uuid
				String sqlzh = "SELECT p.uuid FROM prize_zh p WHERE (p.type = 'sjaqrz' OR p.type = 'djaqrz') AND  (? LIKE CONCAT('%',p.mateName,'%') OR ? LIKE CONCAT('%',p.mateName,'%'))";
				String zhuuid ="";
				List<Map<String, Object>> zh = this.getJdbcTemplate().queryForList(sqlzh, map.get("secureLevel").toString()+map.get("secureRank").toString(),map.get("secureRank").toString()+map.get("secureLevel").toString());
				if(zh.size()>0){
					zhuuid=zh.get(0).get("uuid").toString();
				}


				String sql="INSERT INTO mishu.cert_aqrz (srcUUid, secureLevel, secureRank, zhUUid) VALUES(?,?,?,?)";
				this.getJdbcTemplate().update(sql, uuid,map.get("secureLevel"),map.get("secureRank"),zhuuid);
			}
		}
	}

	/**
	 * 人员无在建证明增加方法
	 * @param map
	 * @param uuid
	 */
	public void insertRywzjzm(Map<String, Object> map,String uuid){
		//清除之前的无在建证明
		String sqldel = "DELETE FROM mishu.cert_rywzjzm WHERE srcUUid =?";
		this.getJdbcTemplate().update(sqldel, uuid);

		if(!"".equals(map.get("signUnit"))){
			String sql="INSERT INTO mishu.cert_rywzjzm (srcUUid, signUnit, signDate) VALUES(?,?,?)";
			this.getJdbcTemplate().update(sql, uuid,map.get("signUnit"),map.get("signDate"));
		}
	}


	@Override
	public void insertFileNotice(String urls, String title, String date, String html){
		UUID uuid = UUID.randomUUID();
		if( this.queryForInt("select count(id) num from mishu.snatchurl_notice where url=?",new Object[]{urls})<1){
			insertFile(urls, title, uuid.toString(),(long) 4,10, date);
			int id = this.queryForInt("select id from mishu.snatchurl_notice where url=?",new Object[]{urls});
			ChineseCompressUtil util=new ChineseCompressUtil();
			this.getJdbcTemplate().update("INSERT INTO mishu.snatchpress_notice (press, snatchUrlId)VALUES(?,?)", html,id);
			this.getJdbcTemplate().update("update mishu.snatchurl_notice set status=1 where id=?", id);
		}
	}

	@Override
	public void insertPublicNotice(String urls, String title, String date, String html){
		UUID uuid = UUID.randomUUID();
		if( this.queryForInt("select count(id) num from mishu.snatchurl_notice where url=?",new Object[]{urls})<1){
			insertPublic(urls, title, uuid.toString(),(long) 4,11, date);
			int id = this.queryForInt("select id from mishu.snatchurl_notice where url=?",new Object[]{urls});
			ChineseCompressUtil util=new ChineseCompressUtil();
			this.getJdbcTemplate().update("INSERT INTO mishu.snatchpress_notice (press, snatchUrlId)VALUES(?,?)", html,id);
			this.getJdbcTemplate().update("update mishu.snatchurl_notice set status=1 where id=?", id);
		}
	}

	@Override
	public void insertNewUrlAndHhuoJiang(String url, String title, String uuid,
										 String html, String companySn) {
		int oldNUm=this.queryForInt("select count(id) num from mishu.prize where url=?",new Object[]{url});
		if( oldNUm<1){
			this.getJdbcTemplate().update("INSERT INTO mishu.prize(companySn, title, url)VALUES(?,?,?)", companySn,title,url);
		}
		Integer id=this.queryForInt("select id from mishu.prize where url=?",new Object[]{url});
		if(oldNUm<1){
			this.getJdbcTemplate().update("INSERT INTO mishu.prizecontent (prizeId, content)VALUES(?,?)", id,html);

			this.getJdbcTemplate().update("update mishu.prize set status=1 where id=?", id);
		}else{
			this.getJdbcTemplate().update("update mishu.prizecontent set content=? where prizeId=?", html,id);
		}

	}



	@Override
	public SnatchUrl getZhaoBiaoContent(int ID) {
		List<Map<String,Object>> list= this.getJdbcTemplate().queryForList("SELECT content,snatchUrlId FROM mishu.snatchurlcontent WHERE id=?", ID);
		if(list.size()>0){
			Map<String,Object> m=list.get(0);
			SnatchUrl c=new SnatchUrl();
			c.setContent(String.valueOf(m.get("content")));
			c.setSnatchPlanId((Long.parseLong(String.valueOf(m.get("snatchUrlId")))));
			return c;
		}else{
			return null;
		}
	}

	@Override
	public void insertCompress(String text, Long snatchUrlId) {
		this.getJdbcTemplate().update("INSERT INTO mishu.snatchpress (press, snatchUrlId)VALUES(?,?)", text,snatchUrlId);

	}

	private void insertElasticSearch(Long snatchUrlId){
		IndexRequestBuilder builder=client.prepareIndex("mishu_index", "hunanzhaobiaowang");
		Map<String,Object> map=querysContendsToLunece(snatchUrlId);
		if(map!=null){
			String json= JSON.toJSONString(map);
			IndexResponse response = builder.setId(String.valueOf(map.get("snatchUrlId"))).setSource(json).execute().actionGet();
			System.out.println("--------------------------"+String.valueOf(map.get("snatchUrlId")));
		}
	}

	@Override
	public Map<String,Object> querysContendsToLunece(Long nowMaxId) {
		String sql="SELECT u.title,u.openDate,u.type,p.press content,p.snatchUrlId FROM snatchurl u,snatchpress p WHERE u.id=p.snatchUrlId AND u.id=?";
		List<Map<String,Object>> list=this.getJdbcTemplate().queryForList(sql, nowMaxId);
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}

	@Override
	public Map<String, Object> querysCertToLunece(long nowMaxId) {
		String sql="SELECT 	* FROM mishu.cert_src WHERE id=?";
		List<Map<String,Object>> list=this.getJdbcTemplate().queryForList(sql, nowMaxId);
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}

	private Map<String, Object> querysCertToLuneceByUrlId(long certificateUrlId) {
		String sql="SELECT 	* FROM mishu.cert_src WHERE certificateUrlId=?";
		List<Map<String,Object>> list=this.getJdbcTemplate().queryForList(sql, certificateUrlId);
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}

	@Override
	public void updateModifyNumberCert(String num, int id) {
		String update="UPDATE mishu.cert_src set updateSee=CURRENT_DATE(),updateSeeNumber=? WHERE id=?";
		this.getJdbcTemplate().update(update, num,id);

	}

	@Override
	public void saveModifyNumberCert(String num, int id) {
		String update="UPDATE mishu.cert_src set updateNumber=?,updateSee=CURRENT_DATE(),updateSeeNumber=?,updateDate= CURRENT_DATE() WHERE id=?";
		this.getJdbcTemplate().update(update, num,num,id);

	}
	@Override
	public void updateCert(Cert c){
		String sql="UPDATE mishu.cert_src SET certificateUrlId = ? ,companyName = ? ,address = ? , legalPerson = ? , "+
				"setupDate = ? , registeredCapital = ? , registeredNo = ? , certificateNo = ? ,  "+
				"openningDate = ? , mianCertificate01 = ? , mianCertificate02 = ? , mianCertificate03 = ? ,  "+
				"mianStep01 = ? , mianStep02 = ? , mianStep03 = ? , jobRange = ? ,  "+
				"`change` = ? , remark = ? ,modifyUser=?,modifyDate=NOW(), "+
				"companyCode = ? , province = ? , city = ? , lastName = ? , "+
				"deparment = ? , companyType = ? , companyProperty = ? "+
				"WHERE id = ?";
		this.getJdbcTemplate().update(sql, c.getCertificateUrlId(),c.getCompanyName(),c.getAddress(),c.getLegalPerson(),
				c.getSetupDate(),c.getRegisteredCapital(),c.getRegisteredNo(),c.getCertificateNo(),
				c.getOpenningDate(),c.getMianCertificate01(),c.getMianCertificate02(),c.getMianCertificate03(),
				c.getMianStep01(),c.getMianStep02(),c.getMianStep03(),c.getJobRange(),
				c.getChange(),c.getRemark(),this.user().name(),
				c.getCompanyCode(),c.getProvince(),c.getCity(),c.getLastName(),
				c.getDeparment(),c.getCompanyType(),c.getCompanyProperty(),
				c.getId());
	}

	//放内存
	@Override
	//@Cacheable(value = "productCategoryCache", key="'findsAllCategory'")
	public List<Map<String,Object>> queryzh(){
		List<Map<String,Object>> list = null;
		String sql ="select `name`,mainUUid,rank from all_zh where rank <>'' and finalUuid <>'' and finalUuid is not null";
		list=this.getJdbcTemplate().queryForList(sql,new Object[]{});
		return list;

	}

	@Autowired
	ZhService ZhService;

	/**
	 * 招标公告资质匹配
	 */
	@Override
	public List insertUrlCert(int id , EsNotice notice) {
		//公告信息
		String sql1="SELECT b.press FROM snatchpress b where b.snatchUrlId = ?";
		Map<String, Object> snalist=this.getJdbcTemplate().queryForList(sql1,new Object[]{id}).get(0);
		SoftReference<String> contentRef = new SoftReference<String>(snalist.get("press").toString());
		List<Map<String,Object>> zh =new ArrayList<Map<String,Object>>();

		List<Map<String, Object>> list = zhService.queryzh();
		for (int i = 0; i < list.size(); i++) {
			int num = contentRef.get().indexOf(list.get(i).get("name").toString());
			if(num != -1){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", list.get(i).get("name"));//匹配公告资质最全名称
				map.put("uuid", list.get(i).get("mainUUid"));//匹配资质类型id
				map.put("rank", list.get(i).get("rank").toString());//等级
				String str="";
				if(contentRef.get().indexOf("安全生产许可证") != -1){//查找安全生产许可证
					map.put("licence","yes");//有安全生产许可证条件
				}
				if(num>5){
					str= contentRef.get().substring(num-5,num);//查找和
				}
				if(str.indexOf("和") !=-1){
					map.put("type", "AND");//和
				}else{
					map.put("type", "OR");//或
				}

				for(int j=0;j<zh.size();j++ ){
					if(map.get("uuid").equals(zh.get(j).get("uuid"))){
						if(Integer.parseInt(map.get("rank").toString())>Integer.parseInt(zh.get(j).get("rank").toString())){
							zh.remove(j);
							zh.add(j,map);
						}
						map=null;
						break;
					}
				}
				if(map!=null){
					zh.add(map);
				}
			}
		}
		list=null;

		if (zh.size() == 0) {   //匹配不到的别名存入Unanalysis_aptitude表
			String zzRank = "";
			String rangeHtml = "";
			List<Map<String, Object>> arList = this.queryAnalyzeRangeByField("zzRank");
			for (int k = 0; k < arList.size(); k++) {
				String start = arList.get(k).get("rangeStart").toString();
				String end = arList.get(k).get("rangeEnd").toString();
				int indexStart = 0;
				int indexEnd = 0;
				if (!"".equals(start)) {
					indexStart = contentRef.get().indexOf(start);//范围开始位置
				}
				if (!"".equals(end)) {
					indexEnd = contentRef.get().lastIndexOf(end);
				}
				if (indexStart != -1 && indexEnd != -1) {
					if (indexEnd > indexStart) {
						rangeHtml = contentRef.get().substring(indexStart, indexEnd);
						rangeHtml = rangeHtml.replaceAll("\\s*", "");    //去空格
						if (rangeHtml.length() > 30) {
							rangeHtml = rangeHtml.substring(0, 30);
						}
						zzRank = rangeHtml.replace("颁发的", "").replace("核发的", "").replace("具备", "").replace("具有", "");
						if (zzRank.indexOf("级") == -1) {
							zzRank = "";
						}
						if (zzRank.length() > 0) {
							break;
						}
					}
				}
			}
			if (!"".equals(zzRank)) {
				String message = "";
				logger.info("#####为解析到" + zzRank + "#####");
				this.insertUnanalysis_aptitude(id, zzRank, message);
			}
		}
		for (int k = 0; k < zh.size(); k++) {
			String sqlname="SELECT MIN(id) id, majorName from mishu.aptitude_dictionary where majorUUid = ?";
			Map<String, Object> mapname = this.getJdbcTemplate().queryForMap(sqlname, zh.get(k).get("uuid"));	//查询标准名称
			if(mapname !=null){
				if(mapname.get("majorName")!=null && zh.get(k).get("rank") !=null){
					String certificate=mapname.get("majorName").toString()+CommonUtil.spellRank(zh.get(k).get("rank").toString());	//规范化资质名称
					String uuid = CommonUtil.spellUuid(zh.get(k).get("uuid").toString(), zh.get(k).get("rank").toString());	//拼接资质uuid
					this.getJdbcTemplate().update("INSERT INTO mishu.snatch_url_cert (contId,certificate,certificateUUid,type,licence)VALUES(?,?,?,?,?)", id,certificate,uuid.replaceAll("'", ""),zh.get(k).get("type"),zh.get(k).get("licence"));
				}
			}
		}

		insertUrlBuild(id,contentRef);
		//建造师资格匹配
		try{
			insertZhaobiaoEsNotice(notice);	//ES
		} catch (Exception e) {
			logger.error("@@@@ES招标入库报错" + e);
		}
		return zh;
	}

	/**
	 * 招标公告建造师匹配
	 */
	@Override
	public void insertUrlBuild(int id,Reference<String> contentRef) {
		String zz="";
		String muuid="";
		String licence="";//安全生产许可证
		int rank=0;
		String uuid="";
		String content = contentRef.get();
		int star = content.indexOf("注册建造师");
		if(star != -1){
			String str= content.substring(star-20,star+50);
			List<Map<String,Object>> list=zhService.getBuildZhList();
			for (int i = 0; i < list.size(); i++) {
				int num =str.indexOf(list.get(i).get("name").toString());
				if(num != -1){
					if(list.get(i).get("name").toString().length()>zz.length()){
						zz = list.get(i).get("name").toString();
						muuid = list.get(i).get("mainUUid").toString();
						rank = Integer.parseInt(list.get(i).get("rank").toString());
					}
					if(str.indexOf("B") != -1 ||str.indexOf("Ｂ")!=-1){
						licence="B";
					}else if(str.indexOf("A") != -1 ||str.indexOf("Ａ")!=-1){
						licence="A";
					}
				}
			}

			String sqlWhere="";
			String sqlzh = "select DISTINCT finalUUid from build_zh where mainUUid=?";
			if(rank !=0){
				if(rank==21){
					sqlWhere = " and rank <=2";
				}else{
					sqlWhere = " and rank ="+rank+"";
				}
			}
			List<Map<String,Object>> all =  this.getJdbcTemplate().queryForList(sqlzh+sqlWhere, muuid);

			for (int j = 0; j < all.size(); j++) {
				if(uuid.equals("")) {
					//zzall +=all.get(j).get("name").toString();
					uuid +=all.get(j).get("finalUUid").toString();
				}
				else{
					//zzall +=","+all.get(j).get("name").toString();
					uuid +=","+all.get(j).get("finalUUid").toString();
				}
			}
			this.getJdbcTemplate().update("INSERT INTO snatch_url_build(contId,certificate,certificateUUid,licence)VALUES(?,?,?,?)", id,zz,uuid,licence);
		}
	}

	@Override
	public List<Map<String,Object>> queryBuildAll(){

		String sql ="select a.zczy,a.rank,a.licence,b.certificateUrlId from cert_build a LEFT JOIN cert_src b ON a.srcUUid=b.uuid WHERE b.certificateUrlId <>'' AND a.licenceDate >= CURRENT_DATE()";
		List<Map<String,Object>> list=this.getJdbcTemplate().queryForList(sql,new Object[]{});
		List<Map<String,Object>> listAll = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String,Object> map = new HashMap<String, Object>();
			if( list.get(i).get("certificateUrlId").toString() != null){
				String str[] = list.get(i).get("zczy").toString().split(",");
				if(str.length ==0){
					map.put("build", list.get(i).get("zczy").toString()+list.get(i).get("rank").toString());
					map.put("certificateUrlId", list.get(i).get("certificateUrlId").toString());
					map.put("licence", list.get(i).get("licence"));
					listAll.add(map);
				}else{
					for (int j = 0; j < str.length; j++) {
						map.put("build", str[j]+list.get(i).get("rank").toString());
						map.put("certificateUrlId", list.get(i).get("certificateUrlId").toString());
						map.put("licence", list.get(i).get("licence"));
						listAll.add(map);
					}
				}
			}

		}
		return listAll;
	}

	@Override
	public void insertzz(String uuid) {
		String sql ="select certificateNo,mianCertificate01,mianCertificate02 from cert_src where uuid=?";
		Cert cert = this.query(sql, new Object[]{uuid}, Cert.class);
		if(this.vertify(cert.getMianCertificate01())){
			String zhux=cert.getMianCertificate01();
			String no=cert.getCertificateNo();
			String dj = getMianCertificateStep(zhux);
			String ty="0";//主项
			if(this.queryForInt("SELECT count(finalUUid) num FROM mishu.all_zh WHERE `name` =?", new Object[]{zhux})>0){
				String finalUUid_0 = this.getJdbcTemplate().queryForObject("SELECT DISTINCT finalUUid FROM mishu.all_zh WHERE `name` =?", new Object[]{zhux}, String.class);
				String sql1="INSERT INTO  mishu.cert_zz (srcUUid,zzName,zzNo,zzType,zzRank,zhUUid) VALUES(?,?,?,?,?,?)";
				this.getJdbcTemplate().update(sql1, uuid,zhux,no,ty,dj,finalUUid_0);
			}
		}
		if(this.vertify(cert.getMianCertificate02())){
			String zengx=cert.getMianCertificate02();
			String[] z= zengx.split("；");
			for(int j = 0;j<z.length;j++ ){
				String zx = z[j].trim();
				String no = "";
				String dj = getMianCertificateStep(zx);
				String ty="1";//曾项
				if(this.queryForInt("SELECT count(finalUUid) num FROM mishu.all_zh WHERE `name` =?", new Object[]{zx})>0){
					String finalUUid_1 = this.getJdbcTemplate().queryForObject("SELECT DISTINCT finalUUid FROM mishu.all_zh WHERE `name` =?", new Object[]{zx}, String.class);
					String sql2 = "INSERT INTO  mishu.cert_zz (srcUUid,zzName,zzNo,zzType,zzRank,zhUUid) VALUES(?,?,?,?,?,?)";
					this.getJdbcTemplate().update(sql2, uuid,zx,no,ty,dj,finalUUid_1);
				}
			}
		}

	}


	private String getMianCertificateStep(String mid) {
		// 贰级,叁级,壹级,特级,无等级
		if(mid.indexOf("特级")!=-1){
			return "特级";
		}
		if(mid.indexOf("壹级")!=-1){
			return "壹级";
		}
		if(mid.indexOf("贰级")!=-1){
			return "贰级";
		}
		if(mid.indexOf("叁级")!=-1){
			return "叁级";
		}
		if(mid.indexOf("无等级")!=-1){
			return "无等级";
		}
		return "无等级";
	}


	//建造师
	@Override
	public void insetZcjianaoshi(List<Map<String, Object>> list) {
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			int num=this.queryForInt("SELECT count(id) FROM mishu.cert_src WHERE companyName =?",new Object[]{map.get("qymc")});
			String uuid="";
			if(num>0){
				String sql ="SELECT uuid FROM mishu.cert_src WHERE companyName =?";
				uuid = this.getJdbcTemplate().queryForObject(sql, new Object[]{map.get("qymc")}, String.class);
			}
			int qc=this.queryForInt("SELECT count(id) FROM mishu.cert_build WHERE qymc =? and zgzsbh=?",new Object[]{map.get("qymc"),map.get("zgzsbh")});
			if(qc==0){
				String myuuid=CommonUtil.getUUID();
				String sqlinsert="INSERT INTO mishu.cert_build (uuid,qymc, xm, zcbh, zsbh, zgzsbh, zclb, zczy, zcyxq, rank, srcUUid,licence)VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
				this.getJdbcTemplate().update(sqlinsert, myuuid,map.get("qymc"),map.get("xm"),map.get("zcbh"),map.get("zsbh"),map.get("zgzsbh"),map.get("zclb"),map.get("zczy"),map.get("zcyxq"),map.get("rank"),uuid,"");
				String zczy = map.get("zczy").toString();
				String zcyxq = map.get("zcyxq").toString();
				String str[] = zczy.split(",");
				String str2[] = zcyxq.split(",");
				for(int s=0;s<str.length;s++){
					//查找资质词典库对应的finaluuid
					String finaluuid = this.getJdbcTemplate().queryForObject("SELECT finalUUid FROM build_zh WHERE `name` = ?",new Object[]{str[s]},String.class);
					String sqllist="INSERT INTO mishu.cert_build_list (parentUUid,zczy,zcyxq,zhUUid)VALUES(?,?,?,?)";
					this.getJdbcTemplate().update(sqllist, myuuid,str[s],str2[s],finaluuid);
				}
			}
		}
	}

	//test方法、批量抓取建筑信息资质企业单位和法人，excel导出
	@Override
	public void insertCertExcel(String companyName, String legalPerson, String sz) {
		String sql1="INSERT INTO  mishu.excel_test (companyName,legalPerson,sz) VALUES(?,?,?)";
		this.getJdbcTemplate().update(sql1, companyName,legalPerson,sz);
	}



	/**
	 * mihsu_snatch各地方数据公共方法
	 */

	public synchronized int insertNotice(String url,String title,String date,int randomNum, String content,String table){
		int id = 0;
		uuid= CommonUtil.getUUID();
		this.getJdbcTemplate().update("INSERT INTO "+table+"(url,title,date,randomNum,tableName,uuid)VALUES(?,?,?,?,?,?);", url,title,date,randomNum,table,uuid);
		id = this.queryForInt("select id from "+table+" where url=?",new Object[]{url});
		this.getJdbcTemplate().update("INSERT INTO "+table+"_content (contentId, content)VALUES(?,?)", id,content);
		return id;
	}





	private Map<String, Object> queryMishuContents(long id,String table) {
		String sql="SELECT c.url,c.title,DATE_FORMAT(c.date, '%Y-%m-%d') date,t.content,c.randomNum,c.tableName,t.contentId,CONCAT(c.title,c.date,t.content) AS part FROM "+table+" c,"+table+"_content t  WHERE c.id=t.contentId AND c.id=?";
		List<Map<String,Object>> list=this.getJdbcTemplate().queryForList(sql, id);
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}

	public int selectElasticsearch(long id,String table){
		int num = 0;

		if(isExists("mishu")){
			QueryBuilder queryBuilder = QueryBuilders.disMaxQuery()
					.add(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("contentId", id))
							.must(QueryBuilders.termQuery("tableName", table)));

			SearchResponse response = client.prepareSearch("mishu")
					.setTypes("elasticsearch")
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setQuery(queryBuilder)
					.setExplain(true)
					.execute()
					.actionGet();

			SearchHits searchHits = response.getHits();
			num = (int) searchHits.getTotalHits();

		}
		return num;
	}
	
	public boolean isExists(String indexName){
		IndicesExistsResponse response = client.admin().indices().exists(new IndicesExistsRequest().indices(new String[]{indexName})).actionGet();
		return response.isExists();
	}

	public List<Object[]> queryRepeat(long id,String table,String title) {
		List<Object[]> infoList=new ArrayList<Object[]>();
		
		if(isExists("mishu")){
			Operator operator = null;
			QueryBuilder queryBuilder = QueryBuilders.disMaxQuery()
					.add(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("title", title).operator(Operator.AND)));

			SearchResponse response = client.prepareSearch("mishu")
					.setTypes("elasticsearch")
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setQuery(queryBuilder)
					.setExplain(true)
					.execute()
					.actionGet();

			SearchHits searchHits = response.getHits();
			SearchHit[] hits = searchHits.getHits();
			for (int i = 0; i < hits.length; i++) {
				SearchHit hit = hits[i];
				Map item=new HashMap();
				item.put("url", hit.getSource().get("url"));
				item.put("title", hit.getSource().get("title"));
				item.put("tableName", hit.getSource().get("tableName"));
				item.put("contentId", hit.getSource().get("contentId"));

				infoList.add(new Object[]{id,table,hit.getSource().get("contentId"),hit.getSource().get("tableName")});
			}
		}
		return infoList;
	}

	public Map<String, Object> querySnatchurlContents(long id) {
		String sql = "SELECT c.id contentId ,c.url,c.title,DATE_FORMAT(c.openDate, '%Y-%m-%d') date,t.content,c.randomNum, 'mishu.snatchurl' tableName,t.snatchUrlId,CONCAT(c.title,c.openDate,t.content) AS part FROM mishu.snatchurl c,mishu.snatchurlcontent t  WHERE c.id=t.snatchUrlId AND c.id=? AND c.type=0";
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, id);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}


	@Override
	public List<Map<String, Object>> querysWebSitePlan(String region) {
		return this.getJdbcTemplate().queryForList("select * from mishu_snatch.website_plan where region=?", region);
	}

	@Override
	public void insertUnanalysis_aptitude(int snatchIUrlId, String aptitude, String snatchContent) {
		String sql = "INSERT INTO mishulog.unanalysis_aptitude(snatchUrlId, aptitude, snatchContent) VALUES(?, ?, ?)";
		this.getJdbcTemplate().update(sql, snatchIUrlId, aptitude, snatchContent);
	}

	@Override
	public void updateSnatchUrlById(int snatchId) {
		String sql = "UPDATE mishu.snatchurl SET isShow = 1 WHERE id = ?";
		this.getJdbcTemplate().update(sql, snatchId);
	}

	@Override
	public int querySnatchUrlCertByContId(int snatchId) {
		String sql = "SELECT COUNT(1) FROM mishu.snatch_url_cert WHERE contId = ?";
		int count = this.getJdbcTemplate().queryForObject(sql, new Object[]{snatchId}, Integer.class);
		return count;
	}

	@Override
	public List<Map<String, Object>> querySubSnatchUrl() {
		String sql = "SELECT `id`, `title` FROM mishu.snatchurl ORDER BY id DESC LIMIT 100";
		return this.getJdbcTemplate().queryForList(sql, new Object[]{});
	}

	@Override
	public void insertZhaobiaoEsNotice(EsNotice notice) {
		if(notice != null) {
			IdxZhaobiaoSnatch zhaobiaoDoc = new IdxZhaobiaoSnatch();
			zhaobiaoDoc.setId(notice.getUuid());    //id
			zhaobiaoDoc.setSnatchId(notice.getUuid());  //公告id
			zhaobiaoDoc.setUrl(notice.getUrl());
			String gsDate = notice.getOpenDate();
			if(gsDate != null && !gsDate.trim().equals("")) {
				gsDate = gsDate.replaceAll("[^\\d]","");    //ES查询不需要-
			}
			zhaobiaoDoc.setGsDate(gsDate);
			zhaobiaoDoc.setTitle(notice.getTitle());

			String sql = "SELECT `certificate`, `certificateUuid` FROM mishu.snatch_url_cert WHERE contId = ?";
			List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, notice.getUuid());
			String zzTemp = "";

			for (int i = 0; i < list.size(); i++) {
				String temp = "";
				if (i == list.size() - 1) {
					temp = (String) list.get(i).get("certificate");
				} else {
					temp = list.get(i).get("certificate") + ",";
				}
				zzTemp = zzTemp + temp;
			}
//			zzTemp = zzTemp.replaceAll("壹", "一").replaceAll("贰", "二").replaceAll("叁", "三");

			String aptitudeName = "";
			if (list.size() > 0) {
				String uuid = (String) list.get(0).get("certificateUuid");
				if (uuid != "") {
					uuid = uuid.substring(0, uuid.indexOf("/"));
					String sql2 = "SELECT `aptitudeName` FROM mishu.aptitude_dictionary WHERE majorUuid LIKE '" + uuid + "%'";
					List<String> aptitudeNameList = this.getJdbcTemplate().queryForList(sql2, String.class);
					if (aptitudeNameList.size() >= 1) {
						aptitudeName = aptitudeNameList.get(0);
					}
				}
			}

			zhaobiaoDoc.setAptitudeName(aptitudeName);	//总类型
			zhaobiaoDoc.setCertificate(zzTemp);	//标准名称资质
			zhaobiaoDoc.setPbMode(notice.getDetail().getPbMode());
			zhaobiaoDoc.setProjDq(notice.getDetail().getProjDq());
			zhaobiaoDoc.setProjType(notice.getDetail().getProjType());
			String tbEndDate = notice.getDetail().getTbEndDate();
			if(tbEndDate != null && !tbEndDate.trim().equals("")) {
				tbEndDate = tbEndDate.replaceAll("[^\\d]","");    //ES查询不需要-
			}
			zhaobiaoDoc.setTbEndDate(tbEndDate);
			String projSum = notice.getDetail().getProjSum();
			if(projSum != null && !projSum.trim().equals("")) {
				zhaobiaoDoc.setProjSum(Double.parseDouble(projSum));
			}

//			zhaobiaoDoc.setContent(notice.getContent());
			zhaobiaoDoc.setBiddingType(notice.getBiddingType());	//公告区分
			zhaobiaoDoc.setBmSite(notice.getDetail().getBmSite());
			String bmEndDate = notice.getDetail().getBmEndDate();
			if(bmEndDate != null && !bmEndDate.trim().equals("")) {
				bmEndDate = bmEndDate.replaceAll("[^\\d]","");  //ES查询不需要-
			}
			zhaobiaoDoc.setBmEndDate(bmEndDate);
			zhaobiaoDoc.setOtherType(notice.getOtherType());	//公告类型
			zhaobiaoDoc.setEdit(0);

			zhaobiaoDoc.setProvince(notice.getProvince());  //省
			zhaobiaoDoc.setCity(notice.getCity());  //市
			zhaobiaoDoc.setCounty(notice.getCounty());  //地区
			zhaobiaoDoc.setProjXs(notice.getDetail().getProjXs());
			zhaobiaoDoc.setType(notice.getType());  //类型:0招标信息，招标变更1，中标结果2
			zhaobiaoDoc.setTableName(notice.getTableName());    //表名

			elaticsearchUtils.saveOrUpdate(zhaobiaoDoc);

		}
	}


}
