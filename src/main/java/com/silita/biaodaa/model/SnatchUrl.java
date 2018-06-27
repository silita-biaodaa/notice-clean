package com.silita.biaodaa.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SnatchUrl extends Mo {
	private static final long serialVersionUID = 1L;
	private Integer edit;//人员编辑0/1
	private String url="";
	private Integer urlPage; 
	private Date snatchDatetime;
	private Long snatchPlanId;
	private String type="";//招标公告0，中标候选1，中标结果2
	private String uuid=""; 
	private String title="";
	private String content="";
	private String openDate;//发布时间
	final static SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd");
	
	
	
	public String getOpenDate() {
		return openDate;
	}
	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}
	public Integer getEdit() {
		return edit;
	}
	public void setEdit(Integer edit) {
		this.edit = edit;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getUrlPage() {
		return urlPage;
	}
	public void setUrlPage(Integer urlPage) {
		this.urlPage = urlPage;
	}
	public Date getSnatchDatetime() {
		return snatchDatetime;
	}
	public void setSnatchDatetime(Date snatchDatetime) {
		this.snatchDatetime = snatchDatetime;
	}
	public String getSnatchDay() {
		return snatchDatetime.getMonth()+"-"+snatchDatetime.getDay();
	}

	public Long getSnatchPlanId() {
		return snatchPlanId;
	}
	public void setSnatchPlanId(Long snatchPlanId) {
		this.snatchPlanId = snatchPlanId;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setTypeToInt(String type2) {
		if("zhaobiaoxinxi".equals(type2)){
			type="0";
		}else if("zhongbiao".equals(type2)){
			type="2";
		}else if("tongzhi".equals(type2)){
			type="10";
		}else if("qishi".equals(type2)){
			type="11";
		}
		
	}
	public String getTypeTitle() {
		if(null==type){
			return  "查询所有信息";
		}else if("0"==type){
			return "招标信息";
		}else if("1"==type){
			return "中标候选人信息";
		}else if("2"==type){
			return "中标结果信息";
		}else if("10"==type){
			return "文件通知";
		}else if("11"==type){
			return "公示启事";
		}else{
			return  "查询所有信息";
		}
	}
//	public Date getOpenDate() {
//		return openDate;
//	}
//	public String getOpenDateView() {
//		return format.format(openDate);
//	}
//	public void setOpenDate(Date openDate) {
//		this.openDate = openDate;
//	}
	
	

}
