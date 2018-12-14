package com.silita.biaodaa.service;

import com.silita.biaodaa.utils.MyStringUtils;
import com.snatch.model.EsNotice;
import org.apache.commons.collections.list.TreeList;
import org.junit.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dh on 2018/8/31.
 */
public class SingleTest {
    @Test
    public void testUUid(){
        for(int i=0; i<25;i++) {
            String uuid = UUID.randomUUID().toString();
            System.out.println(uuid.replaceAll("-", ""));
        }
    }

    private String replaceString(String title,String regex,String targetStr){
        Pattern ptn = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = ptn.matcher(title);
        String tmp=null;
        while (matcher.find()) {
            tmp=  matcher.group();
            if(tmp!=null && !tmp.equals(title)) {
                title = title.replaceAll(tmp, targetStr);
            }
        }
        return title;
    }

    private String replacePunctuation(String title,String regex,String targetStr){
        Pattern ptn = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = ptn.matcher(title);
        String tmp=null;
        while (matcher.find()) {
            tmp=  "\\"+matcher.group();
            if(tmp!=null && !tmp.equals(title)) {
                title = title.replaceAll(tmp, targetStr);
            }
        }
        return title;
    }

    @Test
    public void testRegex(){
        String titleKey = "永州,.植 物园（临时）停[车](场建设)（工程项目）招标公告";
        System.out.println(titleKey);
        titleKey = replacePunctuation(titleKey,"([.。，,.;；：:\\(\\)（）\\[\\]【】])","%");
        System.out.println(titleKey);
        titleKey = replaceString(titleKey,"(^关于)|([ ])|(招标)|(中标)|(项目)|(施工)|(工程)","%");
        System.out.println(titleKey);
        while(titleKey.indexOf("%%") != -1){
            titleKey =titleKey.replaceAll("%%","%");
        }
        System.out.println(titleKey);
    }

    @Test
    public void testIterator(){
        Set setList = new TreeSet();
        setList.add(111);
        setList.add(111);
        setList.add(111);
        setList.add(2222);
        setList.add(2222);
        setList.add(2222);
        System.out.println("size:"+setList.size());
        Iterator iter = setList.iterator();
        while (iter.hasNext()) {
            if(iter.next().equals(111))
            iter.remove();
        }
        System.out.println("size:"+setList.size());
        Iterator iter2 = setList.iterator();
        while (iter2.hasNext()) {
            System.out.print(iter2.next());
        }
    }

    @org.junit.Test
    public void testOne()throws Exception{
        String[] conarr ={"基本信息项目名称：冷水江市禾青镇X246线炉竹公路提质改造工程施工项目编号：A4313810001000038项目所属区域：冷水江市招标人：冷水江市禾青镇人民政府保证金：14.7万元多标：内容<center>冷水江市禾青镇X246线炉竹公路提质改造工程施工招标公告</center><p>冷水江市禾青镇X246线炉竹公路提质改造工程施工招标公告</p><p>1．招标条件</p><p>本招标项目冷水江市禾青镇X246线炉竹公路提质改造工程施工,施工图设计已由冷水江市交通运输局以冷交字【2018】39号文批准。项目业主为冷水江市禾青镇人民镇府，建设资金为市级补助资金及自筹资金，招标代理机构为湖南建科工程项目管理有限公司。项目已具备招标条件，现对该项目的施工进行国内公开招标。</p><p>2．项目概况与招标范围</p><p>2.1建设规模：</p><p>（1）本项目位于湖南省冷水江市禾青镇，路线通过地段属丘陵地貌，该路段地形起伏较小；公路起于湍江村，途经湍江村、炉竹村、黄场村终点止于沙禾公路，全长4953.5米。其中K0+120至K0+660段为新建公路。其余路段为改造路段。本项目估算总投资7357228元。</p><p>（2）该工程为改建工程，按四级公路标准建设，设计速度20Km/h，路基宽度为7米，路面结构为沥青砼路面(以设计图纸为准）。</p><p>2.2技术标准：</p><p>（1）荷载标准：公路—II级，其余技术指标应符合部颁《公路工程技术标准》（JTGB01-2014）中的规定。</p><p>（2）设计宽度：现状车行道宽度大部分在4米左右。本次改造将全段车行道加宽至5米，保证1米路肩+5米车行道+1米路肩，对于无法加宽部分则宽度不变。</p><p>2.3招标范围：路基工程、路面工程、排水、绿化、防护工程等，具体以施工图及工程量清单为准。</p><p>2.4标段划分：本次招标共1个标段。</p><p>2.5计划工期：90日历天（3个月），缺陷责任期2年，保修期5年。</p><p>3．投标人资格要求</p><p>3.1本次招标要求投标人属于记录在交通运输部或湖南省交通运输厅公路建设市场信用信息管理系统中的从业单位。具备独立法人资格，持有有效的营业执照，安全生产许可证，具有建设行政主管部门颁发的公路工程施工总承包叁级及以上资质,并在人员、设备、资金等方面具备相应的施工能力。</p><p>3.2拟任项目经理应为本企业注册的具有公路工程专业贰级及以上注册建造师证书、公路工程相关专业中级及以上技术职称证书，具有交通运输行政主管部门核发的B类《安全生产考核合格证书》。未在其他在建工程项目任职或仍在其他项目上任职，则投标人应提供由该项目发包人出具的承诺在本项目中标后能够从该项目撤离的书面证明材料原件。中标备案以后原则上不允许更换。</p><p>3.3拟任项目总工应为本企业具有公路工程相关专业中级及以上技术职称证书，具有交通运输行政主管部门核发的B类《安全生产考核合格证书》，未在其他在建工程项目任职或仍在其他项目上任职，则投标人应提供由该项目发包人出具的承诺本项目中标后能够从该项目撤离的书面证明材料原件。中标备案以后原则上不允许更换。</p><p>3.4本次投标不接受联合体投标申请。</p><p>3.5与招标人存在利害关系可能影响招标公正性的法人、其他组织或者个人，不得参加投标。单位负责人为同一人或者存在控股、管理关系的不同单位，不得参加本招标项目投标，否则均按废标处理。</p><p>3.6招标人不接受存在《公路工程标准施工招标文件》（2018年版）\"投标人须知\"第1.4.4项情形之一或被湖南省交通运输厅评为2017年度D级、连续两年（2016年和2017年）评为C级及以下、连续三年（2015～2017年）评为B级及以下信用等级的投标人报名。</p><p>3.7投标企业、企业法定代表人和拟任项目负责人（项目经理）近三年（2015年9月1日以后）以来有行贿犯罪记录的不得参与本项目的投标。</p><p>4．评标办法</p><p>本次评标办法参照湘交基建【2013】307号文《湖南省公路水运工程项目招标分类资审随机分配合理低价法实施办法（试行）》，采用资格后审的合理低价法。</p><p>5.投标保证金</p><p>5.1、投标保证金的金额：人民币壹拾肆万柒仟元整。</p><p>5.2、缴纳时间：2018年9月29/strong日至2018年10月18日17时止，以银行到账为准，否则，其投标担保视为无效。</p><p>5.3、缴纳方式：投标保证金必须是从投标人单位的基本账户以银行转账、电汇、银行汇票或网银转账的方式一次性按时、准确、足额转入投标保证金生成的子账号中。不接受以现金或单位结算通卡方式提交投标保证金。</p><p>开户名称：娄底市公共资源交易中心保证金专户</p><p>开户银行：中国工商银行股份有限公司娄底兴城支行</p><p>银行账户：投标人随机获取对应本项目（标段）投标保证金子账号</p><p>5.4、投标保证金子账户的获取：</p><p>5.4.1投标人登陆娄底市公共资源交易网（http://ldggzy.hnloudi.gov.cn），进入本项目招标公告，在网页右上方点击\"获取投标保证金子账户\"链接，获取本项目（标段）的《投标保证金账号信息单》（可下载、打印），该信息单注明的账户为投标人交纳本项目（标段）投标保证金的唯一账号，请注意保密。</p><p>5.4.2投标人在提交投标保证金时，应按照随机获取的《投标保证金账号信息单》中账号信息准确填写银行账单，投标人可在办结投标保证金交纳后，登录\"娄底公共资源交易网\"(http://ldggzy.hnloudi.gov.cn)，点击\"投标保证金\"栏目，查询和了解本项目投标保证金到账、退还等相关信息。</p><p>5.4.3本项目本次招标出现流标或废标情况的，投标保证金将即时原路退还至交纳账户。重新组织招标时，投标人应当重新获取投标保证金账号，并按新账号交纳投标保证金。</p><p>5.4.4因投标人自身原因，未按要求从其基本账户按时、准确、足额向该项目子账号缴纳投标保证金的，其投标将被拒绝，招标人依法当众认定并宣布其投标无效。</p><p>5.4.5投标人应及时在娄底市公共资源交易网查询投标保证金到账情况，因网络或系统原因导致交纳的保证金查询不到，请及时与交易中心信息技术科联系（0738-6371187）。投标人未及时查询投标保证金到账情况，不及时与交易中心联系处理相关问题，致使不能获得项目投标资格的，后果自行承担。</p><p>5.4.6因投标人自身原因，未按要求从其基本账户交纳投标保证金的，自行承担保证金退还不及时的后果。投标人已经递交本项目的投标保证金，但因自身原因不能参加本项目开标的，其投标保证金在开标后才能退还。</p><p>6．招标文件的获取</p><p>6.1凡有意参加投标者，请从2018年9月29日～2018年10月18日17时止，自行在娄底市公共资源交易网（http://ldggzy.hnloudi.gov.cn/）进行网上下载招标相关文件（包括招标文件、设计图纸、工程量清单及招标控制价、修改及补充文件等)。</p><p>6.2招标文件每套售价人民币600元，递交投标文件时缴纳，一律现金支付，售后不退。</p><p>6.3澄清答疑采用网上发布方式。招标人对招标文件、工程量清单的澄清答疑均采用在娄底市公共资源交易网（http://ldggzy.hnloudi.gov.cn/）上发布，投标人自行下载。</p><p>7．投标文件的递交及相关事宜</p><p>7.1递交投标文件截止时间（投标截止时间，下同）为2018年10月19日上午09时30分，投标人应于当日09时00分至09时30分将投标文件第一信封（商务和技术文件）和投标文件第二信封（投标报价）递交至娄底市公共资源交易中心四楼（地址：娄底市湘中大道娄底市政务大楼4楼）交招标人签收。请投标人法定代表人持本人身份证原件及法定代表人资格证明书或授权委托人持本人身份证原件及授权委托书参加开标会。</p><p>7.2逾期送达、不按照招标文件要求密封或者未送达指定地点或采用邮寄等方式送达的投标文件，招标人不予受理。</p><p>8.发布公告的媒介</p><p>本次招标公告同时在湖南省招标投标监管网、娄底市公共资源交易网上发布。</p><p>9.联系方式</p><p>招标人：冷水江市禾青镇人民镇府</p><p>地址：冷水江市禾青镇人民镇府</p><p>联系人：李小明</p><p>电话：18773803131</p><p>招标代理机构：湖南建科工程项目管理有限公司</p><p>地址：长沙县星沙街道东六路266号华润置广场12栋</p><p>联系人：文女士</p><p>电话：18173876820</p><p>监督部门：冷水江市交通运输局</p><p>地址：冷水江市金竹西路5号</p><p>电话：0738-5391330</p><p>邮政编号：417000</p><p>日期：2018年09月29日</p>发表日期：2018/9/299:09</p>",
        "<p>冷水江市禾青镇X246线炉竹公路提质改造工程施工招标公告</p><p>1．招标条件</p><p>本招标项目冷水江市禾青镇X246线炉竹公路提质改造工程施工,施工图设计已由冷水江市交通运输局以冷交字【2018】39号文批准。项目业主为冷水江市禾青镇人民镇府，建设资金为市级补助资金及自筹资金，招标代理机构为湖南建科工程项目管理有限公司。项目已具备招标条件，现对该项目的施工进行国内公开招标。</p><p>2．项目概况与招标范围</p><p>2.1建设规模：</p><p>（1）本项目位于湖南省冷水江市禾青镇，路线通过地段属丘陵地貌，该路段地形起伏较小；公路起于湍江村，途经湍江村、炉竹村、黄场村终点止于沙禾公路，全长4953.5米。其中K0+120至K0+660段为新建公路。其余路段为改造路段。本项目估算总投资7357228元。</p><p>（2）该工程为改建工程，按四级公路标准建设，设计速度20Km/h，路基宽度为7米，路面结构为沥青砼路面(以设计图纸为准）。</p><p>2.2技术标准：</p><p>（1）荷载标准：公路—II级，其余技术指标应符合部颁《公路工程技术标准》（JTGB01-2014）中的规定。</p><p>（2）设计宽度：现状车行道宽度大部分在4米左右。本次改造将全段车行道加宽至5米，保证1米路肩+5米车行道+1米路肩，对于无法加宽部分则宽度不变。</p><p>2.3招标范围：路基工程、路面工程、排水、绿化、防护工程等，具体以施工图及工程量清单为准。</p><p>2.4标段划分：本次招标共1个标段。</p><p>2.5计划工期：90日历天（3个月），缺陷责任期2年，保修期5年。</p><p>3．投标人资格要求</p><p>3.1本次招标要求投标人属于记录在交通运输部或湖南省交通运输厅公路建设市场信用信息管理系统中的从业单位。具备独立法人资格，持有有效的营业执照，安全生产许可证，具有建设行政主管部门颁发的公路工程施工总承包叁级及以上资质,并在人员、设备、资金等方面具备相应的施工能力。</p><p>3.2拟任项目经理应为本企业注册的具有公路工程专业贰级及以上注册建造师证书、公路工程相关专业中级及以上技术职称证书，具有交通运输行政主管部门核发的B类《安全生产考核合格证书》。未在其他在建工程项目任职或仍在其他项目上任职，则投标人应提供由该项目发包人出具的承诺在本项目中标后能够从该项目撤离的书面证明材料原件。中标备案以后原则上不允许更换。</p><p>3.3拟任项目总工应为本企业具有公路工程相关专业中级及以上技术职称证书，具有交通运输行政主管部门核发的B类《安全生产考核合格证书》，未在其他在建工程项目任职或仍在其他项目上任职，则投标人应提供由该项目发包人出具的承诺本项目中标后能够从该项目撤离的书面证明材料原件。中标备案以后原则上不允许更换。</p><p>3.4本次投标不接受联合体投标申请。</p><p>3.5与招标人存在利害关系可能影响招标公正性的法人、其他组织或者个人，不得参加投标。单位负责人为同一人或者存在控股、管理关系的不同单位，不得参加本招标项目投标，否则均按废标处理。</p><p>3.6招标人不接受存在《公路工程标准施工招标文件》（2018年版）\"投标人须知\"第1.4.4项情形之一或被湖南省交通运输厅评为2017年度D级、连续两年（2016年和2017年）评为C级及以下、连续三年（2015～2017年）评为B级及以下信用等级的投标人报名。</p><p>3.7投标企业、企业法定代表人和拟任项目负责人（项目经理）近三年（2015年9月1日以后）以来有行贿犯罪记录的不得参与本项目的投标。</p><p>4．评标办法</p><p>本次评标办法参照湘交基建【2013】307号文《湖南省公路水运工程项目招标分类资审随机分配合理低价法实施办法（试行）》，采用资格后审的合理低价法。</p><p>5.投标保证金</p><p>5.1、投标保证金的金额：人民币壹拾肆万柒仟元整。</p><p>5.2、缴纳时间：2018年9月29日至2018年10月18日17时止，以银行到账为准，否则，其投标担保视为无效。</p><p>5.3、缴纳方式：投标保证金必须是从投标人单位的基本账户以银行转账、电汇、银行汇票或网银转账的方式一次性按时、准确、足额转入投标保证金生成的子账号中。不接受以现金或单位结算通卡方式提交投标保证金。</p><p>开户名称：娄底市公共资源交易中心保证金专户</p><p>开户银行：中国工商银行股份有限公司娄底兴城支行</p><p>银行账户：投标人随机获取对应本项目（标段）投标保证金子账号</p><p>5.4、投标保证金子账户的获取：</p><p>5.4.1投标人登陆娄底市公共资源交易网（http://ldggzy.hnloudi.gov.cn），进入本项目招标公告，在网页右上方点击\"获取投标保证金子账户\"链接，获取本项目（标段）的《投标保证金账号信息单》（可下载、打印），该信息单注明的账户为投标人交纳本项目（标段）投标保证金的唯一账号，请注意保密。</p><p>5.4.2投标人在提交投标保证金时，应按照随机获取的《投标保证金账号信息单》中账号信息准确填写银行账单，投标人可在办结投标保证金交纳后，登录\"娄底公共资源交易网\"(http://ldggzy.hnloudi.gov.cn)，点击\"投标保证金\"栏目，查询和了解本项目投标保证金到账、退还等相关信息。</p><p>5.4.3本项目本次招标出现流标或废标情况的，投标保证金将即时原路退还至交纳账户。重新组织招标时，投标人应当重新获取投标保证金账号，并按新账号交纳投标保证金。</p><p>5.4.4因投标人自身原因，未按要求从其基本账户按时、准确、足额向该项目子账号缴纳投标保证金的，其投标将被拒绝，招标人依法当众认定并宣布其投标无效。</p><p>5.4.5投标人应及时在娄底市公共资源交易网查询投标保证金到账情况，因网络或系统原因导致交纳的保证金查询不到，请及时与交易中心信息技术科联系（0738-6371187）。投标人未及时查询投标保证金到账情况，不及时与交易中心联系处理相关问题，致使不能获得项目投标资格的，后果自行承担。</p><p>5.4.6因投标人自身原因，未按要求从其基本账户交纳投标保证金的，自行承担保证金退还不及时的后果。投标人已经递交本项目的投标保证金，但因自身原因不能参加本项目开标的，其投标保证金在开标后才能退还。</p><p>6．招标文件的获取</p><p>6.1凡有意参加投标者，请从2018/u年9月29日～2018年10月18日17时止，自行在娄底市公共资源交易网（http://ldggzy.hnloudi.gov.cn/）进行网上下载招标相关文件（包括招标文件、设计图纸、工程量清单及招标控制价、修改及补充文件等)。</p><p>6.2招标文件每套售价人民币600元，递交投标文件时缴纳，一律现金支付，售后不退。</p><p>6.3澄清答疑采用网上发布方式。招标人对招标文件、工程量清单的澄清答疑均采用在娄底市公共资源交易网（http://ldggzy.hnloudi.gov.cn/）上发布，投标人自行下载。</p><p>7．投标文件的递交及相关事宜</p><p>7.1递交投标文件截止时间（投标截止时间，下同）为2018年10月19日上午09时30分，投标人应于当日09时00分至09时30分将投标文件第一信封（商务和技术文件）和投标文件第二信封（投标报价）递交至娄底市公共资源交易中心四楼（地址：娄底市湘中大道娄底市政务大楼4楼）交招标人签收。请投标人法定代表人持本人身份证原件及法定代表人资格证明书或授权委托人持本人身份证原件及授权委托书参加开标会。</p><p>7.2逾期送达、不按照招标文件要求密封或者未送达指定地点或采用邮寄等方式送达的投标文件，招标人不予受理。</p><p>8.发布公告的媒介</p><p>本次招标公告同时在湖南省招标投标监管网、娄底市公共资源交易网上发布。</p><p>9.联系方式</p><p>招标人：冷水江市禾青镇人民镇府</p><p>地址：冷水江市禾青镇人民镇府</p><p>联系人：李小明</p><p>电话：18773803131</p><p>招标代理机构：湖南建科工程项目管理有限公司</p><p>地址：长沙县星沙街道东六路266号华润置广场12栋</p><p>联系人：文女士</p><p>电话：18173876820</p><p>监督部门：冷水江市交通运输局</p><p>地址：冷水江市金竹西路5号</p><p>电话：0738-5391330</p><p>邮政编号：417000</p><p>日期：2018年09月29日</p>"};
        for(int i=0;i<conarr.length;i++) {
            String con = conarr[i];
            List<String> list = extractKeysList(con);
            for (String s : list) {
                System.out.println(s);
            }
            if (list != null)
                System.out.println("############size:" + list.size());
        }
    }

    private String replaceString(String str,String regex){
        Pattern ptn = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = ptn.matcher(str);
        String tmp=null;
        while (matcher.find()) {
            tmp=  matcher.group();
            System.out.println(tmp);
        }
        return str;
    }

    private List<String> extractKeysList(String content)throws Exception{
        List<String> keysList = new TreeList();
        String moneyReg = "([1-9][\\d]{0,10}|0)(\\.[\\d]{1,6})?([元]|[万元 \\n])";
        String dateTimeReg = "([1-9]\\d{1,3}[-年]+(0[1-9]|1[0-2]|[1-9])[-月]+(0[1-9]|[1-2][0-9]|3[0-1])[日]?)|((20|21|22|23|[0]?[0-1]\\d)[:：]+[0-5]\\d([:：]?[0-5]\\d)?)";
        String email="([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}";
        String phone="((13\\d|14[57]|15[^4,\\D]|17[678]|18\\d)\\d{8}|170[059]\\d{7})|(0\\d{2,3}-\\d{7,8})|(\\d{7,8})";
        if(MyStringUtils.isNotNull(content)){
            List<String> mList = matchSegmentList(content,moneyReg);
            List<String> timeList = matchSegmentList(content,dateTimeReg);
            List<String> emailList = matchSegmentList(content,email);
            List<String> phoneList = matchSegmentList(content,phone);
            if(!mList.isEmpty()){
                keysList.addAll(mList);

            }
            if(!timeList.isEmpty()){
                keysList.addAll(timeList);
            }
            if(!emailList.isEmpty()){
                keysList.addAll(emailList);
            }
            if(!phoneList.isEmpty()){
                keysList.addAll(phoneList);
            }
        }
        return keysList;
    }

    private List<String> matchSegmentList(String str,String regex){
        List<String> resList= new TreeList();
        Pattern ptn = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = ptn.matcher(str);
        String tmp=null;
        while (matcher.find()) {
            tmp=  matcher.group();
            resList.add(tmp);
        }
        return resList;
    }

    @Test
    public void testParamClear(){
//        EsNotice esNotice = new EsNotice();
//        System.out.println(esNotice);
//        resetCbj(esNotice);
//        System.out.println(esNotice);
        String s = "11";
        System.out.println(s);
        resetStr(s);
        System.out.println(s);
    }

    private void resetCbj(EsNotice esNotice){
        esNotice=null;
        System.out.println(esNotice);
    }

    private void resetStr(String ss){
        ss=null;
        System.out.println(ss);
    }


    @Test
    public void testParam(){
        List t = new ArrayList<>();
        t.add(1);
        Map map = new HashMap();
        map.put(1,t);
        t=null;
        System.out.println(t);
        System.out.println(map.get(1));
    }

    @Test
    public void testSort(){
        List<Map<String,Object>> list = new ArrayList<>();
        for(int i=1;i<5;i++) {
            Map t = new HashMap();
            t.put("preName", "first");
            t.put("uuid", "first" + i);
            list.add(t);
        }

        for(int i=0;i<10;i++){
            Map m = new HashMap();
            m.put("preName","p111");
            m.put("uuid","p111"+i);
            list.add(m);
        }
        for(int i=99;i<110;i++){
            Map m = new HashMap();
            m.put("preName","sssss");
            m.put("uuid","sssss"+i);
            list.add(m);
        }

        for(int i=22;i<33;i++){
            Map m = new HashMap();
            m.put("preName","last");
            m.put("uuid","last"+i);
            list.add(m);
        }

        Map<String,List<Map>> map = new HashMap<>(20);
        sortToMap(list,map);
        System.out.println(map.toString());
    }

    private Map<String,List<Map>> sortToMap(List<Map<String,Object>>  quaAlias,Map sortAlias){
        List<Map<String,Object>> tmpList = null;
        String beforeName = null;
        int len = quaAlias.size();
        for(int i=0; i<len;i++){
            Map aliaMap = quaAlias.get(i);
            String preName = aliaMap.get("preName").toString();
            if(i==0){
                beforeName= preName;
                tmpList = new ArrayList<>();
                tmpList.add(aliaMap);
            }else{
                if (!preName.equals(beforeName)) {
                    sortAlias.put(beforeName, tmpList);
                    beforeName = preName;
                    tmpList = new ArrayList<>();
                }else{
                }
                tmpList.add(aliaMap);
            }
            if(i==len-1) {//收尾
                if(!tmpList.isEmpty()) {
                    sortAlias.put(preName, tmpList);
                }
            }
        }
        return sortAlias;
    }


}
