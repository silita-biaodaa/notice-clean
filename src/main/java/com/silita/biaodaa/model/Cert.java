package com.silita.biaodaa.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Cert extends Mo {
	private Integer certificateUrlId;
	private String companyName, title,
	address, 
	legalPerson, 
	registeredCapital, //注册资本
	registeredNo, //工商登记证号
	certificateNo, //资质证书编号
	mianCertificate01, 
	mianCertificate02, 
	mianCertificate03, 
	mianStep01, 
	mianStep02, 
	mianStep03, 
	jobRange, //承包范围
	remark;
	private String change;//变更记录描述
	private Date setupDate; //成立日期
	private Date openningDate;//发证日期 
	//private Prize prize;
	private String url;
	private String certificate;
	private String uuid;
	private String certificateStep;
	private Date updateDate;
	
	private Date updateSee;
	private Integer updateSeeNumber;
	private Integer updateNumber;
	private String companyCode,province,city,lastName,deparment,companyType,companyProperty;
	private String score3,score2,score1,scoreSum;
	private String subcompanyName;
	
	private String companyNamePy;//企业名称拼音
	
	
	public String getCompanyNamePy() {
		return companyNamePy;
	}
	public void setCompanyNamePy(String companyNamePy) {
		this.companyNamePy = companyNamePy;
	}
	public String getSubcompanyName() {
		return subcompanyName;
	}
	public void setSubcompanyName(String subcompanyName) {
		this.subcompanyName = subcompanyName;
	}
	public String getScoreSum() {
		return scoreSum;
	}
	public void setScoreSum(String scoreSum) {
		this.scoreSum = scoreSum;
	}
	public String getScore3() {
		return score3;
	}
	public void setScore3(String score3) {
		this.score3 = score3;
	}
	public String getScore2() {
		return score2;
	}
	public void setScore2(String score2) {
		this.score2 = score2;
	}
	public String getScore1() {
		return score1;
	}
	public void setScore1(String score1) {
		this.score1 = score1;
	}
	
	public Integer getCertificateUrlId() {
		return certificateUrlId;
	}
	public void setCertificateUrlId(Integer certificateUrlId) {
		this.certificateUrlId = certificateUrlId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLegalPerson() {
		return legalPerson;
	}
	public void setLegalPerson(String legalPerson) {
		this.legalPerson = legalPerson;
	}
	public String getRegisteredCapital() {
		return registeredCapital;
	}
	public void setRegisteredCapital(String registeredCapital) {
		this.registeredCapital = registeredCapital;
	}
	public String getRegisteredNo() {
		return registeredNo;
	}
	public void setRegisteredNo(String registeredNo) {
		this.registeredNo = registeredNo;
	}
	public String getCertificateNo() {
		return certificateNo;
	}
	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}
	public String getMianCertificate01() {
		return mianCertificate01;
	}
	public void setMianCertificate01(String mianCertificate01) {
		this.mianCertificate01 = mianCertificate01;
	}
	public String getMianCertificate02() {
		return mianCertificate02;
	}
	public void setMianCertificate02(String mianCertificate02) {
		this.mianCertificate02 = mianCertificate02;
	}
	public String getMianCertificate03() {
		return mianCertificate03;
	}
	public void setMianCertificate03(String mianCertificate03) {
		this.mianCertificate03 = mianCertificate03;
	}
	public String getMianStep01() {
		return mianStep01;
	}
	public void setMianStep01(String mianStep01) {
		this.mianStep01 = mianStep01;
	}
	public String getMianStep02() {
		return mianStep02;
	}
	public void setMianStep02(String mianStep02) {
		this.mianStep02 = mianStep02;
	}
	public String getMianStep03() {
		return mianStep03;
	}
	public void setMianStep03(String mianStep03) {
		this.mianStep03 = mianStep03;
	}
	public String getJobRange() {
		return jobRange;
	}
	public void setJobRange(String jobRange) {
		this.jobRange = jobRange;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getChange() {
		return change;
	}
	public void setChange(String change) {
		this.change = change;
	}
	public Date getSetupDate() {
		return setupDate;
	}
	public void setSetupDate(Date setupDate) {
		this.setupDate = setupDate;
	}
	public Date getOpenningDate() {
		return openningDate;
	}
	public void setOpenningDate(Date openningDate) {
		this.openningDate = openningDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
//	public Prize getPrize() {
//		return prize;
//	}
//	public void setPrize(Prize prize) {
//		this.prize = prize;
//	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCertificate() {
		return certificate;
	}
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getCertificateStep() {
		return certificateStep;
	}
	public void setCertificateStep(String certificateStep) {
		this.certificateStep = certificateStep;
	}
	private static  SimpleDateFormat c= new SimpleDateFormat("yyyy-MM-dd");
	public String getUpdateDate() {
		return c.format(updateDate);
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getUpdateSee() {
		return c.format(updateSee);
	}
	public void setUpdateSee(Date updateSee) {
		this.updateSee = updateSee;
	}
	public Integer getUpdateSeeNumber() {
		return updateSeeNumber;
	}
	public void setUpdateSeeNumber(Integer updateSeeNumber) {
		this.updateSeeNumber = updateSeeNumber;
	}
	public Integer getUpdateNumber() {
		return updateNumber;
	}
	public void setUpdateNumber(Integer updateNumber) {
		this.updateNumber = updateNumber;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getDeparment() {
		return deparment;
	}
	public void setDeparment(String deparment) {
		this.deparment = deparment;
	}
	public String getCompanyType() {
		return companyType;
	}
	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}
	public String getCompanyProperty() {
		return companyProperty;
	}
	public void setCompanyProperty(String companyProperty) {
		this.companyProperty = companyProperty;
	}
	
	
	
}
