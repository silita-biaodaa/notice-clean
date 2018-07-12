package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZhaobiaoDetail {

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
     * 公示时间
     */
    private String gsdate;

    /**
     * 项目地区
     */
    private String projdq;

    /**
     * 项目县市
     */
    private String projxs;

    /**
     * 项目类型
     */
    private String projtype;

    private String projtypeid;

    /**
     * 资质
     */
    private String zzrank;

    /**
     * 项目金额
     */
    private String projsum;

    /**
     * 评标办法
     */
    private String pbmode;

    /**
     * 报名开始时间
     */
    private String bmstartdate;

    /**
     * 报名结束时间
     */
    private String bmenddate;

    /**
     * 报名结束时点
     */
    private String bmendtime;

    /**
     * 报名地点
     */
    private String bmsite;

    /**
     * 投标保证金额
     */
    private String tbassuresum;

    /**
     * 投标保证金截止时间
     */
    private String tbassureenddate;

    /**
     * 投标保证金截止时点
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
     * 保证金截止时点
     */
    private String assureendtime;

    /**
     * 资格审查时间
     */
    private String zgcheckdate;

    /**
     * 投标截止时间
     */
    private String tbenddate;

    /**
     * 投标截止时点
     */
    private String tbendtime;

    /**
     * 开标人员要求
     */
    private String kbstaffask;

    /**
     * 开标地点
     */
    private String kbsite;

    /**
     * 开标文件费
     */
    private String filecost;

    /**
     * 图纸及其它费
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
     * 代理人
     */
    private String dlname;

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
     * 创建时间
     */
    private String createddate;

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
     * 补充公告次数
     */
    private String supplementarynoticenumber;

    /**
     * 补充公告原因
     */
    private String supplementarynoticereason;

    /**
     * 是否流标
     */
    private String flowstandardflag;

    /**
     * 标段信息
     */
    private String block;

    /**
     * 资金来源
     */
    private String money;

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
     * 获取项目类型
     *
     * @return projType - 项目类型
     */
    public String getProjtype() {
        return projtype;
    }

    /**
     * 设置项目类型
     *
     * @param projtype 项目类型
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
     * 获取报名开始时间
     *
     * @return bmStartDate - 报名开始时间
     */
    public String getBmstartdate() {
        return bmstartdate;
    }

    /**
     * 设置报名开始时间
     *
     * @param bmstartdate 报名开始时间
     */
    public void setBmstartdate(String bmstartdate) {
        this.bmstartdate = bmstartdate;
    }

    /**
     * 获取报名结束时间
     *
     * @return bmEndDate - 报名结束时间
     */
    public String getBmenddate() {
        return bmenddate;
    }

    /**
     * 设置报名结束时间
     *
     * @param bmenddate 报名结束时间
     */
    public void setBmenddate(String bmenddate) {
        this.bmenddate = bmenddate;
    }

    /**
     * 获取报名结束时点
     *
     * @return bmEndTime - 报名结束时点
     */
    public String getBmendtime() {
        return bmendtime;
    }

    /**
     * 设置报名结束时点
     *
     * @param bmendtime 报名结束时点
     */
    public void setBmendtime(String bmendtime) {
        this.bmendtime = bmendtime;
    }

    /**
     * 获取报名地点
     *
     * @return bmSite - 报名地点
     */
    public String getBmsite() {
        return bmsite;
    }

    /**
     * 设置报名地点
     *
     * @param bmsite 报名地点
     */
    public void setBmsite(String bmsite) {
        this.bmsite = bmsite;
    }

    /**
     * 获取投标保证金额
     *
     * @return tbAssureSum - 投标保证金额
     */
    public String getTbassuresum() {
        return tbassuresum;
    }

    /**
     * 设置投标保证金额
     *
     * @param tbassuresum 投标保证金额
     */
    public void setTbassuresum(String tbassuresum) {
        this.tbassuresum = tbassuresum;
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
     * 获取投标保证金截止时点
     *
     * @return tbAssureEndTime - 投标保证金截止时点
     */
    public String getTbassureendtime() {
        return tbassureendtime;
    }

    /**
     * 设置投标保证金截止时点
     *
     * @param tbassureendtime 投标保证金截止时点
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
     * 获取保证金截止时点
     *
     * @return assureEndTime - 保证金截止时点
     */
    public String getAssureendtime() {
        return assureendtime;
    }

    /**
     * 设置保证金截止时点
     *
     * @param assureendtime 保证金截止时点
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
     * 获取投标截止时间
     *
     * @return tbEndDate - 投标截止时间
     */
    public String getTbenddate() {
        return tbenddate;
    }

    /**
     * 设置投标截止时间
     *
     * @param tbenddate 投标截止时间
     */
    public void setTbenddate(String tbenddate) {
        this.tbenddate = tbenddate;
    }

    /**
     * 获取投标截止时点
     *
     * @return tbEndTime - 投标截止时点
     */
    public String getTbendtime() {
        return tbendtime;
    }

    /**
     * 设置投标截止时点
     *
     * @param tbendtime 投标截止时点
     */
    public void setTbendtime(String tbendtime) {
        this.tbendtime = tbendtime;
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
     * 获取开标地点
     *
     * @return kbSite - 开标地点
     */
    public String getKbsite() {
        return kbsite;
    }

    /**
     * 设置开标地点
     *
     * @param kbsite 开标地点
     */
    public void setKbsite(String kbsite) {
        this.kbsite = kbsite;
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
     * 获取图纸及其它费
     *
     * @return otherCost - 图纸及其它费
     */
    public String getOthercost() {
        return othercost;
    }

    /**
     * 设置图纸及其它费
     *
     * @param othercost 图纸及其它费
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
     * 获取代理人
     *
     * @return dlName - 代理人
     */
    public String getDlname() {
        return dlname;
    }

    /**
     * 设置代理人
     *
     * @param dlname 代理人
     */
    public void setDlname(String dlname) {
        this.dlname = dlname;
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
     * 获取创建时间
     *
     * @return createdDate - 创建时间
     */
    public String getCreateddate() {
        return createddate;
    }

    /**
     * 设置创建时间
     *
     * @param createddate 创建时间
     */
    public void setCreateddate(String createddate) {
        this.createddate = createddate;
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
     * 获取补充公告次数
     *
     * @return supplementaryNoticeNumber - 补充公告次数
     */
    public String getSupplementarynoticenumber() {
        return supplementarynoticenumber;
    }

    /**
     * 设置补充公告次数
     *
     * @param supplementarynoticenumber 补充公告次数
     */
    public void setSupplementarynoticenumber(String supplementarynoticenumber) {
        this.supplementarynoticenumber = supplementarynoticenumber;
    }

    /**
     * 获取补充公告原因
     *
     * @return supplementaryNoticeReason - 补充公告原因
     */
    public String getSupplementarynoticereason() {
        return supplementarynoticereason;
    }

    /**
     * 设置补充公告原因
     *
     * @param supplementarynoticereason 补充公告原因
     */
    public void setSupplementarynoticereason(String supplementarynoticereason) {
        this.supplementarynoticereason = supplementarynoticereason;
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
}