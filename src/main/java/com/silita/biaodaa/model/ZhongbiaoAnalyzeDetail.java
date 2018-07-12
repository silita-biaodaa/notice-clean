package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ZhongbiaoAnalyzeDetail {
    private Integer id;

    private String redisid;

    /**
     * ??url
     */
    private String noticeurl;

    /**
     * ????
     */
    private String title;

    /**
     * ????
     */
    private String gsdate;

    /**
     * ?
     */
    private String province;

    /**
     * ?
     */
    private String city;

    /**
     * ?
     */
    private String county;

    /**
     * ????
     */
    private String projsum;

    private String projdq;

    private String projxs;

    /**
     * ????
     */
    private String pbmode;

    private String projtype;

    private String projtypeid;

    /**
     * ?????
     */
    private String onename;

    /**
     * ????UUid
     */
    private String oneuuid;

    /**
     * ??
     */
    private String oneoffer;

    /**
     * ?????
     */
    private String oneprojduty;

    private String oneprojdutyuuid;

    /**
     * ?????
     */
    private String oneskillduty;

    /**
     * ???
     */
    private String onesgy;

    /**
     * ???
     */
    private String oneaqy;

    /**
     * ???
     */
    private String onezly;

    /**
     * ?????
     */
    private String twoname;

    /**
     * ??
     */
    private String twooffer;

    /**
     * ?????
     */
    private String twoprojduty;

    /**
     * ?????
     */
    private String twoskillduty;

    /**
     * ???
     */
    private String twosgy;

    /**
     * ???
     */
    private String twoaqy;

    /**
     * ???
     */
    private String twozly;

    /**
     * ?????
     */
    private String threename;

    /**
     * ??
     */
    private String threeoffer;

    /**
     * ?????
     */
    private String threeprojduty;

    /**
     * ?????
     */
    private String threeskillduty;

    /**
     * ???
     */
    private String threesgy;

    /**
     * ???
     */
    private String threeaqy;

    /**
     * ???
     */
    private String threezly;

    /**
     * ????
     */
    private String createddate;

    /**
     * ????
     */
    private String projecttimelimit;

    /**
     * ??????
     */
    private String projectcompletiondate;

    /**
     * ????
     */
    private String block;

    private Integer zhaobdid;

    /**
     * ????
     */
    private Date analyzedate;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return redisId
     */
    public String getRedisid() {
        return redisid;
    }

    /**
     * @param redisid
     */
    public void setRedisid(String redisid) {
        this.redisid = redisid;
    }

    /**
     * 获取??url
     *
     * @return noticeUrl - ??url
     */
    public String getNoticeurl() {
        return noticeurl;
    }

    /**
     * 设置??url
     *
     * @param noticeurl ??url
     */
    public void setNoticeurl(String noticeurl) {
        this.noticeurl = noticeurl;
    }

    /**
     * 获取????
     *
     * @return title - ????
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置????
     *
     * @param title ????
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取????
     *
     * @return gsDate - ????
     */
    public String getGsdate() {
        return gsdate;
    }

    /**
     * 设置????
     *
     * @param gsdate ????
     */
    public void setGsdate(String gsdate) {
        this.gsdate = gsdate;
    }

    /**
     * 获取?
     *
     * @return province - ?
     */
    public String getProvince() {
        return province;
    }

    /**
     * 设置?
     *
     * @param province ?
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 获取?
     *
     * @return city - ?
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置?
     *
     * @param city ?
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取?
     *
     * @return county - ?
     */
    public String getCounty() {
        return county;
    }

    /**
     * 设置?
     *
     * @param county ?
     */
    public void setCounty(String county) {
        this.county = county;
    }

    /**
     * 获取????
     *
     * @return projSum - ????
     */
    public String getProjsum() {
        return projsum;
    }

    /**
     * 设置????
     *
     * @param projsum ????
     */
    public void setProjsum(String projsum) {
        this.projsum = projsum;
    }

    /**
     * @return projDq
     */
    public String getProjdq() {
        return projdq;
    }

    /**
     * @param projdq
     */
    public void setProjdq(String projdq) {
        this.projdq = projdq;
    }

    /**
     * @return projXs
     */
    public String getProjxs() {
        return projxs;
    }

    /**
     * @param projxs
     */
    public void setProjxs(String projxs) {
        this.projxs = projxs;
    }

    /**
     * 获取????
     *
     * @return pbMode - ????
     */
    public String getPbmode() {
        return pbmode;
    }

    /**
     * 设置????
     *
     * @param pbmode ????
     */
    public void setPbmode(String pbmode) {
        this.pbmode = pbmode;
    }

    /**
     * @return projType
     */
    public String getProjtype() {
        return projtype;
    }

    /**
     * @param projtype
     */
    public void setProjtype(String projtype) {
        this.projtype = projtype;
    }

    /**
     * @return projTypeId
     */
    public String getProjtypeid() {
        return projtypeid;
    }

    /**
     * @param projtypeid
     */
    public void setProjtypeid(String projtypeid) {
        this.projtypeid = projtypeid;
    }

    /**
     * 获取?????
     *
     * @return oneName - ?????
     */
    public String getOnename() {
        return onename;
    }

    /**
     * 设置?????
     *
     * @param onename ?????
     */
    public void setOnename(String onename) {
        this.onename = onename;
    }

    /**
     * 获取????UUid
     *
     * @return oneUUid - ????UUid
     */
    public String getOneuuid() {
        return oneuuid;
    }

    /**
     * 设置????UUid
     *
     * @param oneuuid ????UUid
     */
    public void setOneuuid(String oneuuid) {
        this.oneuuid = oneuuid;
    }

    /**
     * 获取??
     *
     * @return oneOffer - ??
     */
    public String getOneoffer() {
        return oneoffer;
    }

    /**
     * 设置??
     *
     * @param oneoffer ??
     */
    public void setOneoffer(String oneoffer) {
        this.oneoffer = oneoffer;
    }

    /**
     * 获取?????
     *
     * @return oneProjDuty - ?????
     */
    public String getOneprojduty() {
        return oneprojduty;
    }

    /**
     * 设置?????
     *
     * @param oneprojduty ?????
     */
    public void setOneprojduty(String oneprojduty) {
        this.oneprojduty = oneprojduty;
    }

    /**
     * @return oneProjDutyUuid
     */
    public String getOneprojdutyuuid() {
        return oneprojdutyuuid;
    }

    /**
     * @param oneprojdutyuuid
     */
    public void setOneprojdutyuuid(String oneprojdutyuuid) {
        this.oneprojdutyuuid = oneprojdutyuuid;
    }

    /**
     * 获取?????
     *
     * @return oneSkillDuty - ?????
     */
    public String getOneskillduty() {
        return oneskillduty;
    }

    /**
     * 设置?????
     *
     * @param oneskillduty ?????
     */
    public void setOneskillduty(String oneskillduty) {
        this.oneskillduty = oneskillduty;
    }

    /**
     * 获取???
     *
     * @return oneSgy - ???
     */
    public String getOnesgy() {
        return onesgy;
    }

    /**
     * 设置???
     *
     * @param onesgy ???
     */
    public void setOnesgy(String onesgy) {
        this.onesgy = onesgy;
    }

    /**
     * 获取???
     *
     * @return oneAqy - ???
     */
    public String getOneaqy() {
        return oneaqy;
    }

    /**
     * 设置???
     *
     * @param oneaqy ???
     */
    public void setOneaqy(String oneaqy) {
        this.oneaqy = oneaqy;
    }

    /**
     * 获取???
     *
     * @return oneZly - ???
     */
    public String getOnezly() {
        return onezly;
    }

    /**
     * 设置???
     *
     * @param onezly ???
     */
    public void setOnezly(String onezly) {
        this.onezly = onezly;
    }

    /**
     * 获取?????
     *
     * @return twoName - ?????
     */
    public String getTwoname() {
        return twoname;
    }

    /**
     * 设置?????
     *
     * @param twoname ?????
     */
    public void setTwoname(String twoname) {
        this.twoname = twoname;
    }

    /**
     * 获取??
     *
     * @return twoOffer - ??
     */
    public String getTwooffer() {
        return twooffer;
    }

    /**
     * 设置??
     *
     * @param twooffer ??
     */
    public void setTwooffer(String twooffer) {
        this.twooffer = twooffer;
    }

    /**
     * 获取?????
     *
     * @return twoProjDuty - ?????
     */
    public String getTwoprojduty() {
        return twoprojduty;
    }

    /**
     * 设置?????
     *
     * @param twoprojduty ?????
     */
    public void setTwoprojduty(String twoprojduty) {
        this.twoprojduty = twoprojduty;
    }

    /**
     * 获取?????
     *
     * @return twoSkillDuty - ?????
     */
    public String getTwoskillduty() {
        return twoskillduty;
    }

    /**
     * 设置?????
     *
     * @param twoskillduty ?????
     */
    public void setTwoskillduty(String twoskillduty) {
        this.twoskillduty = twoskillduty;
    }

    /**
     * 获取???
     *
     * @return twoSgy - ???
     */
    public String getTwosgy() {
        return twosgy;
    }

    /**
     * 设置???
     *
     * @param twosgy ???
     */
    public void setTwosgy(String twosgy) {
        this.twosgy = twosgy;
    }

    /**
     * 获取???
     *
     * @return twoAqy - ???
     */
    public String getTwoaqy() {
        return twoaqy;
    }

    /**
     * 设置???
     *
     * @param twoaqy ???
     */
    public void setTwoaqy(String twoaqy) {
        this.twoaqy = twoaqy;
    }

    /**
     * 获取???
     *
     * @return twoZly - ???
     */
    public String getTwozly() {
        return twozly;
    }

    /**
     * 设置???
     *
     * @param twozly ???
     */
    public void setTwozly(String twozly) {
        this.twozly = twozly;
    }

    /**
     * 获取?????
     *
     * @return threeName - ?????
     */
    public String getThreename() {
        return threename;
    }

    /**
     * 设置?????
     *
     * @param threename ?????
     */
    public void setThreename(String threename) {
        this.threename = threename;
    }

    /**
     * 获取??
     *
     * @return threeOffer - ??
     */
    public String getThreeoffer() {
        return threeoffer;
    }

    /**
     * 设置??
     *
     * @param threeoffer ??
     */
    public void setThreeoffer(String threeoffer) {
        this.threeoffer = threeoffer;
    }

    /**
     * 获取?????
     *
     * @return threeProjDuty - ?????
     */
    public String getThreeprojduty() {
        return threeprojduty;
    }

    /**
     * 设置?????
     *
     * @param threeprojduty ?????
     */
    public void setThreeprojduty(String threeprojduty) {
        this.threeprojduty = threeprojduty;
    }

    /**
     * 获取?????
     *
     * @return threeSkillDuty - ?????
     */
    public String getThreeskillduty() {
        return threeskillduty;
    }

    /**
     * 设置?????
     *
     * @param threeskillduty ?????
     */
    public void setThreeskillduty(String threeskillduty) {
        this.threeskillduty = threeskillduty;
    }

    /**
     * 获取???
     *
     * @return threeSgy - ???
     */
    public String getThreesgy() {
        return threesgy;
    }

    /**
     * 设置???
     *
     * @param threesgy ???
     */
    public void setThreesgy(String threesgy) {
        this.threesgy = threesgy;
    }

    /**
     * 获取???
     *
     * @return threeAqy - ???
     */
    public String getThreeaqy() {
        return threeaqy;
    }

    /**
     * 设置???
     *
     * @param threeaqy ???
     */
    public void setThreeaqy(String threeaqy) {
        this.threeaqy = threeaqy;
    }

    /**
     * 获取???
     *
     * @return threeZly - ???
     */
    public String getThreezly() {
        return threezly;
    }

    /**
     * 设置???
     *
     * @param threezly ???
     */
    public void setThreezly(String threezly) {
        this.threezly = threezly;
    }

    /**
     * 获取????
     *
     * @return createdDate - ????
     */
    public String getCreateddate() {
        return createddate;
    }

    /**
     * 设置????
     *
     * @param createddate ????
     */
    public void setCreateddate(String createddate) {
        this.createddate = createddate;
    }

    /**
     * 获取????
     *
     * @return projectTimeLimit - ????
     */
    public String getProjecttimelimit() {
        return projecttimelimit;
    }

    /**
     * 设置????
     *
     * @param projecttimelimit ????
     */
    public void setProjecttimelimit(String projecttimelimit) {
        this.projecttimelimit = projecttimelimit;
    }

    /**
     * 获取??????
     *
     * @return projectCompletionDate - ??????
     */
    public String getProjectcompletiondate() {
        return projectcompletiondate;
    }

    /**
     * 设置??????
     *
     * @param projectcompletiondate ??????
     */
    public void setProjectcompletiondate(String projectcompletiondate) {
        this.projectcompletiondate = projectcompletiondate;
    }

    /**
     * 获取????
     *
     * @return block - ????
     */
    public String getBlock() {
        return block;
    }

    /**
     * 设置????
     *
     * @param block ????
     */
    public void setBlock(String block) {
        this.block = block;
    }

    /**
     * @return zhaobdId
     */
    public Integer getZhaobdid() {
        return zhaobdid;
    }

    /**
     * @param zhaobdid
     */
    public void setZhaobdid(Integer zhaobdid) {
        this.zhaobdid = zhaobdid;
    }

    /**
     * 获取????
     *
     * @return analyzeDate - ????
     */
    public Date getAnalyzedate() {
        return analyzedate;
    }

    /**
     * 设置????
     *
     * @param analyzedate ????
     */
    public void setAnalyzedate(Date analyzedate) {
        this.analyzedate = analyzedate;
    }
}