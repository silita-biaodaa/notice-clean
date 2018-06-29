package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ZhaobiaoAnalyzeDetail {
    private Integer id;

    private String redisid;

    /**
     * 公告url
     */
    private String noticeurl;

    /**
     * 公告名称
     */
    private String title;

    /**
     * 投标保证金
     */
    private String tbassuresum;

    /**
     * 项目地区
     */
    private String projdq;

    /**
     * 项目县市
     */
    private String projxs;

    /**
     * 项目金额
     */
    private String projsum;

    /**
     * 报名时间
     */
    private String bmstartdate;

    /**
     * 报名截止时间
     */
    private String bmenddate;

    /**
     * 报名截止时间点
     */
    private String bmendtime;

    /**
     * 报名地址
     */
    private String bmsite;

    /**
     * 开标地址
     */
    private String kbsite;

    /**
     * 投标截止日期
     */
    private String tbenddate;

    /**
     * 投标截止日期点
     */
    private String tbendtime;

    /**
     * 公示时间
     */
    private String gsdate;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 县
     */
    private String county;

    /**
     * 公告类型。招标公告1；中标公告2
     */
    private String projtype;

    /**
     * 资质
     */
    private String zzrank;

    /**
     * 评标办法
     */
    private String pbmode;

    /**
     * 投标保证金截止时间
     */
    private String tbassureenddate;

    /**
     * 投标保证金截止时间点
     */
    private String tbassureendtime;

    /**
     * 履约保证金
     */
    private String lyassuresum;

    /**
     * 其他证明金
     */
    private String slprovesum;

    /**
     * 保证金截止时间
     */
    private String assureenddate;

    /**
     * 保证金截止时间点
     */
    private String assureendtime;

    /**
     * 资格审查时间
     */
    private String zgcheckdate;

    /**
     * 开标人员要求
     */
    private String kbstaffask;

    /**
     * 开标文件费
     */
    private String filecost;

    /**
     * 图纸及其他费用
     */
    private String othercost;

    /**
     * 招标人
     */
    private String zbname;

    /**
     * 招标联系人
     */
    private String zbcontactman;

    /**
     * 招标联系方式
     */
    private String zbcontactway;

    /**
     * 代理联系人
     */
    private String dlcontactman;

    /**
     * 代理联系方式
     */
    private String dlcontactway;

    /**
     * 人员要求
     */
    private String personrequest;

    /**
     * 社保要求
     */
    private String shebaorequest;

    /**
     * 业绩要求
     */
    private String yejirequest;

    /**
     * 报名方式
     */
    private String registrationform;

    /**
     * 项目工期
     */
    private String projecttimelimit;

    /**
     * 计划竣工时间
     */
    private String projectcompletiondate;

    /**
     * 补充公共次数
     */
    private String supplementnoticenumber;

    /**
     * 补充公告原因
     */
    private String supplementnoticereason;

    /**
     * 是否流标
     */
    private String flowstandardflag;

    /**
     * 资金来源
     */
    private String money;

    /**
     * 标段信息
     */
    private String block;

    /**
     * 解析时间
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
     * 获取公告url
     *
     * @return noticeUrl - 公告url
     */
    public String getNoticeurl() {
        return noticeurl;
    }

    /**
     * 设置公告url
     *
     * @param noticeurl 公告url
     */
    public void setNoticeurl(String noticeurl) {
        this.noticeurl = noticeurl;
    }

    /**
     * 获取公告名称
     *
     * @return title - 公告名称
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置公告名称
     *
     * @param title 公告名称
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取投标保证金
     *
     * @return tbAssureSum - 投标保证金
     */
    public String getTbassuresum() {
        return tbassuresum;
    }

    /**
     * 设置投标保证金
     *
     * @param tbassuresum 投标保证金
     */
    public void setTbassuresum(String tbassuresum) {
        this.tbassuresum = tbassuresum;
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
     * 获取报名时间
     *
     * @return bmStartDate - 报名时间
     */
    public String getBmstartdate() {
        return bmstartdate;
    }

    /**
     * 设置报名时间
     *
     * @param bmstartdate 报名时间
     */
    public void setBmstartdate(String bmstartdate) {
        this.bmstartdate = bmstartdate;
    }

    /**
     * 获取报名截止时间
     *
     * @return bmEndDate - 报名截止时间
     */
    public String getBmenddate() {
        return bmenddate;
    }

    /**
     * 设置报名截止时间
     *
     * @param bmenddate 报名截止时间
     */
    public void setBmenddate(String bmenddate) {
        this.bmenddate = bmenddate;
    }

    /**
     * 获取报名截止时间点
     *
     * @return bmEndTime - 报名截止时间点
     */
    public String getBmendtime() {
        return bmendtime;
    }

    /**
     * 设置报名截止时间点
     *
     * @param bmendtime 报名截止时间点
     */
    public void setBmendtime(String bmendtime) {
        this.bmendtime = bmendtime;
    }

    /**
     * 获取报名地址
     *
     * @return bmSite - 报名地址
     */
    public String getBmsite() {
        return bmsite;
    }

    /**
     * 设置报名地址
     *
     * @param bmsite 报名地址
     */
    public void setBmsite(String bmsite) {
        this.bmsite = bmsite;
    }

    /**
     * 获取开标地址
     *
     * @return kbSite - 开标地址
     */
    public String getKbsite() {
        return kbsite;
    }

    /**
     * 设置开标地址
     *
     * @param kbsite 开标地址
     */
    public void setKbsite(String kbsite) {
        this.kbsite = kbsite;
    }

    /**
     * 获取投标截止日期
     *
     * @return tbEndDate - 投标截止日期
     */
    public String getTbenddate() {
        return tbenddate;
    }

    /**
     * 设置投标截止日期
     *
     * @param tbenddate 投标截止日期
     */
    public void setTbenddate(String tbenddate) {
        this.tbenddate = tbenddate;
    }

    /**
     * 获取投标截止日期点
     *
     * @return tbEndTime - 投标截止日期点
     */
    public String getTbendtime() {
        return tbendtime;
    }

    /**
     * 设置投标截止日期点
     *
     * @param tbendtime 投标截止日期点
     */
    public void setTbendtime(String tbendtime) {
        this.tbendtime = tbendtime;
    }

    /**
     * 获取公示时间
     *
     * @return gsDate - 公示时间
     */
    public String getGsdate() {
        return gsdate;
    }

    /**
     * 设置公示时间
     *
     * @param gsdate 公示时间
     */
    public void setGsdate(String gsdate) {
        this.gsdate = gsdate;
    }

    /**
     * 获取省
     *
     * @return province - 省
     */
    public String getProvince() {
        return province;
    }

    /**
     * 设置省
     *
     * @param province 省
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 获取市
     *
     * @return city - 市
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置市
     *
     * @param city 市
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取县
     *
     * @return county - 县
     */
    public String getCounty() {
        return county;
    }

    /**
     * 设置县
     *
     * @param county 县
     */
    public void setCounty(String county) {
        this.county = county;
    }

    /**
     * 获取公告类型。招标公告1；中标公告2
     *
     * @return projType - 公告类型。招标公告1；中标公告2
     */
    public String getProjtype() {
        return projtype;
    }

    /**
     * 设置公告类型。招标公告1；中标公告2
     *
     * @param projtype 公告类型。招标公告1；中标公告2
     */
    public void setProjtype(String projtype) {
        this.projtype = projtype;
    }

    /**
     * 获取资质
     *
     * @return zzRank - 资质
     */
    public String getZzrank() {
        return zzrank;
    }

    /**
     * 设置资质
     *
     * @param zzrank 资质
     */
    public void setZzrank(String zzrank) {
        this.zzrank = zzrank;
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
     * 获取投标保证金截止时间
     *
     * @return tbAssureEndDate - 投标保证金截止时间
     */
    public String getTbassureenddate() {
        return tbassureenddate;
    }

    /**
     * 设置投标保证金截止时间
     *
     * @param tbassureenddate 投标保证金截止时间
     */
    public void setTbassureenddate(String tbassureenddate) {
        this.tbassureenddate = tbassureenddate;
    }

    /**
     * 获取投标保证金截止时间点
     *
     * @return tbAssureEndTime - 投标保证金截止时间点
     */
    public String getTbassureendtime() {
        return tbassureendtime;
    }

    /**
     * 设置投标保证金截止时间点
     *
     * @param tbassureendtime 投标保证金截止时间点
     */
    public void setTbassureendtime(String tbassureendtime) {
        this.tbassureendtime = tbassureendtime;
    }

    /**
     * 获取履约保证金
     *
     * @return lyAssureSum - 履约保证金
     */
    public String getLyassuresum() {
        return lyassuresum;
    }

    /**
     * 设置履约保证金
     *
     * @param lyassuresum 履约保证金
     */
    public void setLyassuresum(String lyassuresum) {
        this.lyassuresum = lyassuresum;
    }

    /**
     * 获取其他证明金
     *
     * @return slProveSum - 其他证明金
     */
    public String getSlprovesum() {
        return slprovesum;
    }

    /**
     * 设置其他证明金
     *
     * @param slprovesum 其他证明金
     */
    public void setSlprovesum(String slprovesum) {
        this.slprovesum = slprovesum;
    }

    /**
     * 获取保证金截止时间
     *
     * @return assureEndDate - 保证金截止时间
     */
    public String getAssureenddate() {
        return assureenddate;
    }

    /**
     * 设置保证金截止时间
     *
     * @param assureenddate 保证金截止时间
     */
    public void setAssureenddate(String assureenddate) {
        this.assureenddate = assureenddate;
    }

    /**
     * 获取保证金截止时间点
     *
     * @return assureEndTime - 保证金截止时间点
     */
    public String getAssureendtime() {
        return assureendtime;
    }

    /**
     * 设置保证金截止时间点
     *
     * @param assureendtime 保证金截止时间点
     */
    public void setAssureendtime(String assureendtime) {
        this.assureendtime = assureendtime;
    }

    /**
     * 获取资格审查时间
     *
     * @return zgCheckDate - 资格审查时间
     */
    public String getZgcheckdate() {
        return zgcheckdate;
    }

    /**
     * 设置资格审查时间
     *
     * @param zgcheckdate 资格审查时间
     */
    public void setZgcheckdate(String zgcheckdate) {
        this.zgcheckdate = zgcheckdate;
    }

    /**
     * 获取开标人员要求
     *
     * @return kbStaffAsk - 开标人员要求
     */
    public String getKbstaffask() {
        return kbstaffask;
    }

    /**
     * 设置开标人员要求
     *
     * @param kbstaffask 开标人员要求
     */
    public void setKbstaffask(String kbstaffask) {
        this.kbstaffask = kbstaffask;
    }

    /**
     * 获取开标文件费
     *
     * @return fileCost - 开标文件费
     */
    public String getFilecost() {
        return filecost;
    }

    /**
     * 设置开标文件费
     *
     * @param filecost 开标文件费
     */
    public void setFilecost(String filecost) {
        this.filecost = filecost;
    }

    /**
     * 获取图纸及其他费用
     *
     * @return otherCost - 图纸及其他费用
     */
    public String getOthercost() {
        return othercost;
    }

    /**
     * 设置图纸及其他费用
     *
     * @param othercost 图纸及其他费用
     */
    public void setOthercost(String othercost) {
        this.othercost = othercost;
    }

    /**
     * 获取招标人
     *
     * @return zbName - 招标人
     */
    public String getZbname() {
        return zbname;
    }

    /**
     * 设置招标人
     *
     * @param zbname 招标人
     */
    public void setZbname(String zbname) {
        this.zbname = zbname;
    }

    /**
     * 获取招标联系人
     *
     * @return zbContactMan - 招标联系人
     */
    public String getZbcontactman() {
        return zbcontactman;
    }

    /**
     * 设置招标联系人
     *
     * @param zbcontactman 招标联系人
     */
    public void setZbcontactman(String zbcontactman) {
        this.zbcontactman = zbcontactman;
    }

    /**
     * 获取招标联系方式
     *
     * @return zbContactWay - 招标联系方式
     */
    public String getZbcontactway() {
        return zbcontactway;
    }

    /**
     * 设置招标联系方式
     *
     * @param zbcontactway 招标联系方式
     */
    public void setZbcontactway(String zbcontactway) {
        this.zbcontactway = zbcontactway;
    }

    /**
     * 获取代理联系人
     *
     * @return dlContactMan - 代理联系人
     */
    public String getDlcontactman() {
        return dlcontactman;
    }

    /**
     * 设置代理联系人
     *
     * @param dlcontactman 代理联系人
     */
    public void setDlcontactman(String dlcontactman) {
        this.dlcontactman = dlcontactman;
    }

    /**
     * 获取代理联系方式
     *
     * @return dlContactWay - 代理联系方式
     */
    public String getDlcontactway() {
        return dlcontactway;
    }

    /**
     * 设置代理联系方式
     *
     * @param dlcontactway 代理联系方式
     */
    public void setDlcontactway(String dlcontactway) {
        this.dlcontactway = dlcontactway;
    }

    /**
     * 获取人员要求
     *
     * @return personRequest - 人员要求
     */
    public String getPersonrequest() {
        return personrequest;
    }

    /**
     * 设置人员要求
     *
     * @param personrequest 人员要求
     */
    public void setPersonrequest(String personrequest) {
        this.personrequest = personrequest;
    }

    /**
     * 获取社保要求
     *
     * @return shebaoRequest - 社保要求
     */
    public String getShebaorequest() {
        return shebaorequest;
    }

    /**
     * 设置社保要求
     *
     * @param shebaorequest 社保要求
     */
    public void setShebaorequest(String shebaorequest) {
        this.shebaorequest = shebaorequest;
    }

    /**
     * 获取业绩要求
     *
     * @return yejiRequest - 业绩要求
     */
    public String getYejirequest() {
        return yejirequest;
    }

    /**
     * 设置业绩要求
     *
     * @param yejirequest 业绩要求
     */
    public void setYejirequest(String yejirequest) {
        this.yejirequest = yejirequest;
    }

    /**
     * 获取报名方式
     *
     * @return registrationForm - 报名方式
     */
    public String getRegistrationform() {
        return registrationform;
    }

    /**
     * 设置报名方式
     *
     * @param registrationform 报名方式
     */
    public void setRegistrationform(String registrationform) {
        this.registrationform = registrationform;
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
     * 获取补充公共次数
     *
     * @return supplementNoticeNumber - 补充公共次数
     */
    public String getSupplementnoticenumber() {
        return supplementnoticenumber;
    }

    /**
     * 设置补充公共次数
     *
     * @param supplementnoticenumber 补充公共次数
     */
    public void setSupplementnoticenumber(String supplementnoticenumber) {
        this.supplementnoticenumber = supplementnoticenumber;
    }

    /**
     * 获取补充公告原因
     *
     * @return supplementNoticeReason - 补充公告原因
     */
    public String getSupplementnoticereason() {
        return supplementnoticereason;
    }

    /**
     * 设置补充公告原因
     *
     * @param supplementnoticereason 补充公告原因
     */
    public void setSupplementnoticereason(String supplementnoticereason) {
        this.supplementnoticereason = supplementnoticereason;
    }

    /**
     * 获取是否流标
     *
     * @return flowStandardFlag - 是否流标
     */
    public String getFlowstandardflag() {
        return flowstandardflag;
    }

    /**
     * 设置是否流标
     *
     * @param flowstandardflag 是否流标
     */
    public void setFlowstandardflag(String flowstandardflag) {
        this.flowstandardflag = flowstandardflag;
    }

    /**
     * 获取资金来源
     *
     * @return money - 资金来源
     */
    public String getMoney() {
        return money;
    }

    /**
     * 设置资金来源
     *
     * @param money 资金来源
     */
    public void setMoney(String money) {
        this.money = money;
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
     * 获取解析时间
     *
     * @return analyzeDate - 解析时间
     */
    public Date getAnalyzedate() {
        return analyzedate;
    }

    /**
     * 设置解析时间
     *
     * @param analyzedate 解析时间
     */
    public void setAnalyzedate(Date analyzedate) {
        this.analyzedate = analyzedate;
    }
}