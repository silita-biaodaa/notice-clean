package com.silita.biaodaa.service;

import com.silita.biaodaa.utils.MyStringUtils;
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
        String con ="<div class=\"TRS_Editor\">\n" +
                " <div class=\"TRS_PreAppend\" style=\"overflow-x: hidden; word-break: break-all\"> \n" +
                "  <p class=\"MsoNormal\" style=\"margin-bottom: 7.5pt; background: rgb(255,255,255); word-break: break-all; text-align: left; line-height: 150%\"><span style=\"font-size: 12pt; font-family: 宋体; background: rgb(255,255,255); color: rgb(53,53,53); line-height: 150%\"><font face=\"宋体\">一、项目名称：双峰县第二次全县污染源普查入户调查三方机构服务费用项目</font></span></p> \n" +
                "  <p class=\"MsoNormal\" style=\"margin-bottom: 7.5pt; background: rgb(255,255,255); word-break: break-all; text-align: left; line-height: 150%\"><span style=\"font-size: 12pt; font-family: 宋体; background: rgb(255,255,255); color: rgb(53,53,53); line-height: 150%\"><font face=\"宋体\">二、采购编号：双峰财采计</font>2018-0342</span></p> \n" +
                "  <p class=\"MsoNormal\" style=\"margin-bottom: 7.5pt; background: rgb(255,255,255); word-break: break-all; text-align: left; line-height: 150%\"><span style=\"font-size: 12pt; font-family: 宋体; background: rgb(255,255,255); color: rgb(53,53,53); line-height: 150%\"><font face=\"宋体\">三、项目编号：</font></span><span style=\"font-size: 12pt; font-family: 宋体; background: rgb(255,255,255); color: rgb(53,53,53); line-height: 150%\">GDHCLD-2018126 &nbsp;</span></p> \n" +
                "  <p class=\"MsoNormal\" style=\"margin-bottom: 7.5pt; background: rgb(255,255,255); word-break: break-all; text-align: left; line-height: 150%\"><span style=\"font-size: 12pt; font-family: 宋体; background: rgb(255,255,255); color: rgb(53,53,53); line-height: 150%\"><font face=\"宋体\">四、采购方式：竞争性磋商</font></span></p> \n" +
                "  <p class=\"MsoNormal\" style=\"margin-bottom: 7.5pt; background: rgb(255,255,255); word-break: break-all; text-align: left; line-height: 150%\"><span style=\"font-size: 12pt; font-family: 宋体; background: rgb(255,255,255); color: rgb(53,53,53); line-height: 150%\"><font face=\"宋体\">五、采购项目预算（最高限价）：</font>458505.92元</span></p> \n" +
                "  <p class=\"MsoNormal\" style=\"margin-bottom: 7.5pt; background: rgb(255,255,255); word-break: break-all; text-align: left; line-height: 150%\"><span style=\"font-size: 12pt; font-family: 宋体; background: rgb(255,255,255); color: rgb(53,53,53); line-height: 150%\"><font face=\"宋体\">六、更正内容：原磋商文件的磋商公告第二、供应商资质要求，</font>2、供应商特定资格条件2.1 “投标人必须具有建设项目环境影响评价乙级资质”现更正为“投标人必须具有建设项目环境影响评价乙级及以上资质”。</span></p> \n" +
                "  <p class=\"MsoNormal\" style=\"margin-bottom: 7.5pt; background: rgb(255,255,255); word-break: break-all; text-align: left; line-height: 150%\"><span style=\"font-size: 12pt; font-family: 宋体; background: rgb(255,255,255); color: rgb(53,53,53); line-height: 150%\"><font face=\"宋体\">七、本更正公告为磋商文件的组成部分，对所涉及的上述内容作出相应调整和更正，若本公告与此前磋商文件内容有不一致之处，应以本更正公告为准，其它内容不变</font></span><span style=\"font-size: 12pt; font-family: 宋体; color: rgb(53,53,53); line-height: 150%\"><font face=\"宋体\">。</font></span></p> \n" +
                "  <p class=\"MsoNormal\" style=\"margin-bottom: 7.5pt; background: rgb(255,255,255); word-break: break-all; text-align: left; line-height: 150%\"><span style=\"font-size: 12pt; font-family: 宋体; color: rgb(53,53,53); line-height: 150%\">&nbsp;</span></p> \n" +
                "  <p class=\"MsoNormal\" style=\"margin-bottom: 7.5pt; background: rgb(255,255,255); word-break: break-all; text-align: left; line-height: 150%\"><span style=\"font-size: 12pt; font-family: 宋体; color: rgb(53,53,53); line-height: 150%\"><font face=\"宋体\">采购</font> <font face=\"宋体\">人：双峰县环境保护局</font></span></p> \n" +
                "  <p class=\"MsoNormal\" style=\"margin-bottom: 7.5pt; background: rgb(255,255,255); word-break: break-all; text-align: left; line-height: 150%\"><span style=\"font-size: 12pt; font-family: 宋体; color: rgb(53,53,53); line-height: 150%\"><font face=\"宋体\">联</font> <font face=\"宋体\">系</font> <font face=\"宋体\">人：罗先生</font></span></p> \n" +
                "  <p class=\"MsoNormal\" style=\"margin-bottom: 7.5pt; background: rgb(255,255,255); word-break: break-all; text-align: left; line-height: 150%\"><span style=\"font-size: 12pt; font-family: 宋体; color: rgb(53,53,53); line-height: 150%\"><font face=\"宋体\">电</font> &nbsp;&nbsp;&nbsp;<font face=\"宋体\">话：</font>0738-6821250</span></p> \n" +
                "  <p class=\"MsoNormal\" style=\"margin-bottom: 7.5pt; background: rgb(255,255,255); word-break: break-all; text-align: left; line-height: 150%\"><span style=\"font-size: 12pt; font-family: 宋体; color: rgb(53,53,53); line-height: 150%\"><font face=\"宋体\">采购代理机构：国鼎和诚招标咨询有限公司</font> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></p> \n" +
                "  <p class=\"MsoNormal\" style=\"margin-bottom: 7.5pt; background: rgb(255,255,255); word-break: break-all; text-align: left; line-height: 150%\"><span style=\"font-size: 12pt; font-family: 宋体; color: rgb(53,53,53); line-height: 150%\"><font face=\"宋体\">联系人：刘先生</font> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></p> \n" +
                "  <p class=\"MsoNormal\" style=\"margin-bottom: 7.5pt; background: rgb(255,255,255); word-break: break-all; text-align: left; line-height: 150%\"><span style=\"font-size: 12pt; font-family: 宋体; color: rgb(53,53,53); line-height: 150%\"><font face=\"宋体\">电</font> &nbsp;<font face=\"宋体\">话：</font> 15907387900</span></p> \n" +
                "  <p class=\"MsoNormal\" style=\"margin-bottom: 7.5pt; background: rgb(255,255,255); word-break: break-all; text-align: left; line-height: 150%\"><span style=\"font-size: 12pt; font-family: 宋体; color: rgb(53,53,53); line-height: 150%\"><font face=\"宋体\">电</font> &nbsp;&nbsp;&nbsp;<font face=\"宋体\">话：</font>0738-6827388</span></p> \n" +
                "  <p class=\"MsoNormal\" style=\"margin-bottom: 7.5pt; background: rgb(255,255,255); word-break: break-all; text-align: left; line-height: 150%; text-indent: 28pt\"><span style=\"font-size: 12pt; font-family: 宋体; background: rgb(255,255,255); color: rgb(53,53,53); line-height: 150%\">&nbsp;</span></p> \n" +
                "  <p class=\"MsoNormal\" style=\"text-indent: 21pt\"><span style=\"font-size: 10.5pt; font-family: 'Times New Roman'\">&nbsp;</span></p> \n" +
                " </div>\n" +
                "</div> \n" +
                "<div class=\"div_appendix\"> \n" +
                "</div> \n" +
                "<div class=\"div_picAppendix hide\" style=\"display:none\"> \n" +
                "</div>";
        List<String> list = extractKeysList(con);
        for(String s:list){
            System.out.println(s);
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
        List<String> keysList = null;
        String moneyReg = "([1-9][\\d]{0,10}|0)(\\.[\\d]{1,6})?([元]|[万元 \\n])";
        String dateTimeReg = "([1-9]\\d{1,3}[-年]+(0[1-9]|1[0-2]|[1-9])[-月]+(0[1-9]|[1-2][0-9]|3[0-1])[日]?)|((20|21|22|23|[0]?[0-1]\\d)[:：]+[0-5]\\d([:：]?[0-5]\\d)?)";
        if(MyStringUtils.isNotNull(content)){
            List<String> mList = matchSegmentList(content,moneyReg);
            List<String> timeList = matchSegmentList(content,dateTimeReg);
            if(!mList.isEmpty()){
                keysList = mList;
                keysList.addAll(timeList);
            }else{
                keysList = timeList;
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

}
