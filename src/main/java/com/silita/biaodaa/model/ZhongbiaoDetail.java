package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZhongbiaoDetail {

    private Integer id;

    /**
     * 公告id
     */
    private Integer snatchurlid;

    /**
     * 项目名称
     */
    private String projname;

    /**
     * 公示日期
     */
    private String gsdate;

    /**
     * 项目金额
     */
    private String projsum;

    /**
     * 项目地区
     */
    private String projdq;

    /**
     * 项目县市
     */
    private String projxs;

    /**
     * 评标办法
     */
    private String pbmode;

    private String projtype;

    private String projtypeid;

    /**
     * 第一候选人
     */
    private String onename;

    /**
     * 候选单位UUid
     */
    private String oneuuid;

    /**
     * 报价
     */
    private String oneoffer;

    /**
     * 项目负责人
     */
    private String oneprojduty;

    private String oneprojdutyuuid;

    /**
     * 技术负责人
     */
    private String oneskillduty;

    /**
     * 施工员
     */
    private String onesgy;

    /**
     * 安全员
     */
    private String oneaqy;

    /**
     * 质量员
     */
    private String onezly;

    /**
     * 第二候选人
     */
    private String twoname;

    /**
     * 报价
     */
    private String twooffer;

    /**
     * 项目负责人
     */
    private String twoprojduty;

    /**
     * 技术负责人
     */
    private String twoskillduty;

    /**
     * 施工员
     */
    private String twosgy;

    /**
     * 安全员
     */
    private String twoaqy;

    /**
     * 质量员
     */
    private String twozly;

    /**
     * 第三候选人
     */
    private String threename;

    /**
     * 报价
     */
    private String threeoffer;

    /**
     * 项目负责人
     */
    private String threeprojduty;

    /**
     * 技术负责人
     */
    private String threeskillduty;

    /**
     * 施工员
     */
    private String threesgy;

    /**
     * 安全员
     */
    private String threeaqy;

    /**
     * 质量员
     */
    private String threezly;

    /**
     * 创建日期
     */
    private String createddate;

    /**
     * 项目工期
     */
    private String projecttimelimit;

    /**
     * 计划竣工时间
     */
    private String projectcompletiondate;

    /**
     * 标段信息
     */
    private String block;

    /**
     * 招标标段id
     */
    private Integer zhaobdid;

    /**
     * 第一联合人一
     */
    private String onename2;

    /**
     * 第一联合人一id
     */
    private String oneuuid2;

    /**
     * 第一联合人二
     */
    private String onename3;

    /**
     * 第一联合人二id
     */
    private String oneuuid3;

    /**
     * 第二联合人一
     */
    private String twoname2;

    /**
     * 第二联合人二
     */
    private String twoname3;

    /**
     * 第三联合人一
     */
    private String threename2;

    /**
     * 第三联合人二
     */
    private String threename3;

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
     * 获取公告id
     *
     * @return snatchUrlId - 公告id
     */
    public Integer getSnatchurlid() {
        return snatchurlid;
    }

    /**
     * 设置公告id
     *
     * @param snatchurlid 公告id
     */
    public void setSnatchurlid(Integer snatchurlid) {
        this.snatchurlid = snatchurlid;
    }

    /**
     * 获取项目名称
     *
     * @return projName - 项目名称
     */
    public String getProjname() {
        return projname;
    }

    /**
     * 设置项目名称
     *
     * @param projname 项目名称
     */
    public void setProjname(String projname) {
        this.projname = projname;
    }

    /**
     * 获取公示日期
     *
     * @return gsDate - 公示日期
     */
    public String getGsdate() {
        return gsdate;
    }

    /**
     * 设置公示日期
     *
     * @param gsdate 公示日期
     */
    public void setGsdate(String gsdate) {
        this.gsdate = gsdate;
    }

    /**
     * 获取项目金额
     *
     * @return projSum - 项目金额
     */
    public String getProjsum() {
        return projsum;
    }

    /**
     * 设置项目金额
     *
     * @param projsum 项目金额
     */
    public void setProjsum(String projsum) {
        this.projsum = projsum;
    }

    /**
     * 获取项目地区
     *
     * @return projDq - 项目地区
     */
    public String getProjdq() {
        return projdq;
    }

    /**
     * 设置项目地区
     *
     * @param projdq 项目地区
     */
    public void setProjdq(String projdq) {
        this.projdq = projdq;
    }

    /**
     * 获取项目县市
     *
     * @return projXs - 项目县市
     */
    public String getProjxs() {
        return projxs;
    }

    /**
     * 设置项目县市
     *
     * @param projxs 项目县市
     */
    public void setProjxs(String projxs) {
        this.projxs = projxs;
    }

    /**
     * 获取评标办法
     *
     * @return pbMode - 评标办法
     */
    public String getPbmode() {
        return pbmode;
    }

    /**
     * 设置评标办法
     *
     * @param pbmode 评标办法
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
     * 获取第一候选人
     *
     * @return oneName - 第一候选人
     */
    public String getOnename() {
        return onename;
    }

    /**
     * 设置第一候选人
     *
     * @param onename 第一候选人
     */
    public void setOnename(String onename) {
        this.onename = onename;
    }

    /**
     * 获取候选单位UUid
     *
     * @return oneUUid - 候选单位UUid
     */
    public String getOneuuid() {
        return oneuuid;
    }

    /**
     * 设置候选单位UUid
     *
     * @param oneuuid 候选单位UUid
     */
    public void setOneuuid(String oneuuid) {
        this.oneuuid = oneuuid;
    }

    /**
     * 获取报价
     *
     * @return oneOffer - 报价
     */
    public String getOneoffer() {
        return oneoffer;
    }

    /**
     * 设置报价
     *
     * @param oneoffer 报价
     */
    public void setOneoffer(String oneoffer) {
        this.oneoffer = oneoffer;
    }

    /**
     * 获取项目负责人
     *
     * @return oneProjDuty - 项目负责人
     */
    public String getOneprojduty() {
        return oneprojduty;
    }

    /**
     * 设置项目负责人
     *
     * @param oneprojduty 项目负责人
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
     * 获取技术负责人
     *
     * @return oneSkillDuty - 技术负责人
     */
    public String getOneskillduty() {
        return oneskillduty;
    }

    /**
     * 设置技术负责人
     *
     * @param oneskillduty 技术负责人
     */
    public void setOneskillduty(String oneskillduty) {
        this.oneskillduty = oneskillduty;
    }

    /**
     * 获取施工员
     *
     * @return oneSgy - 施工员
     */
    public String getOnesgy() {
        return onesgy;
    }

    /**
     * 设置施工员
     *
     * @param onesgy 施工员
     */
    public void setOnesgy(String onesgy) {
        this.onesgy = onesgy;
    }

    /**
     * 获取安全员
     *
     * @return oneAqy - 安全员
     */
    public String getOneaqy() {
        return oneaqy;
    }

    /**
     * 设置安全员
     *
     * @param oneaqy 安全员
     */
    public void setOneaqy(String oneaqy) {
        this.oneaqy = oneaqy;
    }

    /**
     * 获取质量员
     *
     * @return oneZly - 质量员
     */
    public String getOnezly() {
        return onezly;
    }

    /**
     * 设置质量员
     *
     * @param onezly 质量员
     */
    public void setOnezly(String onezly) {
        this.onezly = onezly;
    }

    /**
     * 获取第二候选人
     *
     * @return twoName - 第二候选人
     */
    public String getTwoname() {
        return twoname;
    }

    /**
     * 设置第二候选人
     *
     * @param twoname 第二候选人
     */
    public void setTwoname(String twoname) {
        this.twoname = twoname;
    }

    /**
     * 获取报价
     *
     * @return twoOffer - 报价
     */
    public String getTwooffer() {
        return twooffer;
    }

    /**
     * 设置报价
     *
     * @param twooffer 报价
     */
    public void setTwooffer(String twooffer) {
        this.twooffer = twooffer;
    }

    /**
     * 获取项目负责人
     *
     * @return twoProjDuty - 项目负责人
     */
    public String getTwoprojduty() {
        return twoprojduty;
    }

    /**
     * 设置项目负责人
     *
     * @param twoprojduty 项目负责人
     */
    public void setTwoprojduty(String twoprojduty) {
        this.twoprojduty = twoprojduty;
    }

    /**
     * 获取技术负责人
     *
     * @return twoSkillDuty - 技术负责人
     */
    public String getTwoskillduty() {
        return twoskillduty;
    }

    /**
     * 设置技术负责人
     *
     * @param twoskillduty 技术负责人
     */
    public void setTwoskillduty(String twoskillduty) {
        this.twoskillduty = twoskillduty;
    }

    /**
     * 获取施工员
     *
     * @return twoSgy - 施工员
     */
    public String getTwosgy() {
        return twosgy;
    }

    /**
     * 设置施工员
     *
     * @param twosgy 施工员
     */
    public void setTwosgy(String twosgy) {
        this.twosgy = twosgy;
    }

    /**
     * 获取安全员
     *
     * @return twoAqy - 安全员
     */
    public String getTwoaqy() {
        return twoaqy;
    }

    /**
     * 设置安全员
     *
     * @param twoaqy 安全员
     */
    public void setTwoaqy(String twoaqy) {
        this.twoaqy = twoaqy;
    }

    /**
     * 获取质量员
     *
     * @return twoZly - 质量员
     */
    public String getTwozly() {
        return twozly;
    }

    /**
     * 设置质量员
     *
     * @param twozly 质量员
     */
    public void setTwozly(String twozly) {
        this.twozly = twozly;
    }

    /**
     * 获取第三候选人
     *
     * @return threeName - 第三候选人
     */
    public String getThreename() {
        return threename;
    }

    /**
     * 设置第三候选人
     *
     * @param threename 第三候选人
     */
    public void setThreename(String threename) {
        this.threename = threename;
    }

    /**
     * 获取报价
     *
     * @return threeOffer - 报价
     */
    public String getThreeoffer() {
        return threeoffer;
    }

    /**
     * 设置报价
     *
     * @param threeoffer 报价
     */
    public void setThreeoffer(String threeoffer) {
        this.threeoffer = threeoffer;
    }

    /**
     * 获取项目负责人
     *
     * @return threeProjDuty - 项目负责人
     */
    public String getThreeprojduty() {
        return threeprojduty;
    }

    /**
     * 设置项目负责人
     *
     * @param threeprojduty 项目负责人
     */
    public void setThreeprojduty(String threeprojduty) {
        this.threeprojduty = threeprojduty;
    }

    /**
     * 获取技术负责人
     *
     * @return threeSkillDuty - 技术负责人
     */
    public String getThreeskillduty() {
        return threeskillduty;
    }

    /**
     * 设置技术负责人
     *
     * @param threeskillduty 技术负责人
     */
    public void setThreeskillduty(String threeskillduty) {
        this.threeskillduty = threeskillduty;
    }

    /**
     * 获取施工员
     *
     * @return threeSgy - 施工员
     */
    public String getThreesgy() {
        return threesgy;
    }

    /**
     * 设置施工员
     *
     * @param threesgy 施工员
     */
    public void setThreesgy(String threesgy) {
        this.threesgy = threesgy;
    }

    /**
     * 获取安全员
     *
     * @return threeAqy - 安全员
     */
    public String getThreeaqy() {
        return threeaqy;
    }

    /**
     * 设置安全员
     *
     * @param threeaqy 安全员
     */
    public void setThreeaqy(String threeaqy) {
        this.threeaqy = threeaqy;
    }

    /**
     * 获取质量员
     *
     * @return threeZly - 质量员
     */
    public String getThreezly() {
        return threezly;
    }

    /**
     * 设置质量员
     *
     * @param threezly 质量员
     */
    public void setThreezly(String threezly) {
        this.threezly = threezly;
    }

    /**
     * 获取创建日期
     *
     * @return createdDate - 创建日期
     */
    public String getCreateddate() {
        return createddate;
    }

    /**
     * 设置创建日期
     *
     * @param createddate 创建日期
     */
    public void setCreateddate(String createddate) {
        this.createddate = createddate;
    }

    /**
     * 获取项目工期
     *
     * @return projectTimeLimit - 项目工期
     */
    public String getProjecttimelimit() {
        return projecttimelimit;
    }

    /**
     * 设置项目工期
     *
     * @param projecttimelimit 项目工期
     */
    public void setProjecttimelimit(String projecttimelimit) {
        this.projecttimelimit = projecttimelimit;
    }

    /**
     * 获取计划竣工时间
     *
     * @return projectCompletionDate - 计划竣工时间
     */
    public String getProjectcompletiondate() {
        return projectcompletiondate;
    }

    /**
     * 设置计划竣工时间
     *
     * @param projectcompletiondate 计划竣工时间
     */
    public void setProjectcompletiondate(String projectcompletiondate) {
        this.projectcompletiondate = projectcompletiondate;
    }

    /**
     * 获取标段信息
     *
     * @return block - 标段信息
     */
    public String getBlock() {
        return block;
    }

    /**
     * 设置标段信息
     *
     * @param block 标段信息
     */
    public void setBlock(String block) {
        this.block = block;
    }

    /**
     * 获取招标标段id
     *
     * @return zhaobdId - 招标标段id
     */
    public Integer getZhaobdid() {
        return zhaobdid;
    }

    /**
     * 设置招标标段id
     *
     * @param zhaobdid 招标标段id
     */
    public void setZhaobdid(Integer zhaobdid) {
        this.zhaobdid = zhaobdid;
    }

    /**
     * 获取第一联合人一
     *
     * @return oneName2 - 第一联合人一
     */
    public String getOnename2() {
        return onename2;
    }

    /**
     * 设置第一联合人一
     *
     * @param onename2 第一联合人一
     */
    public void setOnename2(String onename2) {
        this.onename2 = onename2;
    }

    /**
     * 获取第一联合人一id
     *
     * @return oneUUid2 - 第一联合人一id
     */
    public String getOneuuid2() {
        return oneuuid2;
    }

    /**
     * 设置第一联合人一id
     *
     * @param oneuuid2 第一联合人一id
     */
    public void setOneuuid2(String oneuuid2) {
        this.oneuuid2 = oneuuid2;
    }

    /**
     * 获取第一联合人二
     *
     * @return oneName3 - 第一联合人二
     */
    public String getOnename3() {
        return onename3;
    }

    /**
     * 设置第一联合人二
     *
     * @param onename3 第一联合人二
     */
    public void setOnename3(String onename3) {
        this.onename3 = onename3;
    }

    /**
     * 获取第一联合人二id
     *
     * @return oneUUid3 - 第一联合人二id
     */
    public String getOneuuid3() {
        return oneuuid3;
    }

    /**
     * 设置第一联合人二id
     *
     * @param oneuuid3 第一联合人二id
     */
    public void setOneuuid3(String oneuuid3) {
        this.oneuuid3 = oneuuid3;
    }

    /**
     * 获取第二联合人一
     *
     * @return twoName2 - 第二联合人一
     */
    public String getTwoname2() {
        return twoname2;
    }

    /**
     * 设置第二联合人一
     *
     * @param twoname2 第二联合人一
     */
    public void setTwoname2(String twoname2) {
        this.twoname2 = twoname2;
    }

    /**
     * 获取第二联合人二
     *
     * @return twoName3 - 第二联合人二
     */
    public String getTwoname3() {
        return twoname3;
    }

    /**
     * 设置第二联合人二
     *
     * @param twoname3 第二联合人二
     */
    public void setTwoname3(String twoname3) {
        this.twoname3 = twoname3;
    }

    /**
     * 获取第三联合人一
     *
     * @return threeName2 - 第三联合人一
     */
    public String getThreename2() {
        return threename2;
    }

    /**
     * 设置第三联合人一
     *
     * @param threename2 第三联合人一
     */
    public void setThreename2(String threename2) {
        this.threename2 = threename2;
    }

    /**
     * 获取第三联合人二
     *
     * @return threeName3 - 第三联合人二
     */
    public String getThreename3() {
        return threename3;
    }

    /**
     * 设置第三联合人二
     *
     * @param threename3 第三联合人二
     */
    public void setThreename3(String threename3) {
        this.threename3 = threename3;
    }
}