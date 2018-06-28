package com.silita.biaodaa.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dh on 2017/5/3.
 */
public class MyStringUtils {

	public static final String[] KEY_WORDS = {"答疑", "澄清", "延期", "流标", "修改", "采购", "补充", "答疑", "补遗", "监理", "控制价"};

	public static List<String> titleKWordList() {
		List<String> keyList = null;
		keyList = Arrays.asList(KEY_WORDS);
		return keyList;
	}

	public static String subZhaobiaoTile(String title) {
		title = title.replaceAll("【", "").replaceAll("】", "");
		title = title.replaceAll("\\s*", "");

		int titleEndIdx1 = title.lastIndexOf("施工招标公告");
		int titleEndIdx2 = title.lastIndexOf("招标公告");
		int titleEndIdx3 = title.lastIndexOf("公告");
		if (titleEndIdx1 != -1) {
			title = title.substring(0, titleEndIdx1);
			return title;
		} else if (titleEndIdx2 != -1) {
			title = title.substring(0, titleEndIdx2);
			return title;
		} else if (titleEndIdx3 != -1) {
			title = title.substring(0, titleEndIdx3);
			return title;
		} else {
			return title;
		}
	}

	public static String subZhongBiaoTile(String title) {
		title = title.replaceAll("【", "").replaceAll("】", "");
		title = title.replaceAll("\\s*", "");

		int titleEndIdx5 = title.lastIndexOf("中标候选人公告");
		int titleEndIdx4 = title.lastIndexOf("中标候选人公示");
		int titleEndIdx3 = title.lastIndexOf("中标候选人");
		int titleEndIdx1 = title.lastIndexOf("中标公告");
		int titleEndIdx6 = title.lastIndexOf("中标公示");
		int titleEndIdx2 = title.lastIndexOf("公告");
		int titleEndIdx7 = title.lastIndexOf("公示");

		if (titleEndIdx5 != -1) {
			title = title.substring(0, titleEndIdx5);
			return title;
		} else if (titleEndIdx4 != -1) {
			title = title.substring(0, titleEndIdx4);
			return title;
		} else if (titleEndIdx3 != -1) {
			title = title.substring(0, titleEndIdx3);
			return title;
		} else if (titleEndIdx1 != -1) {
			title = title.substring(0, titleEndIdx1);
			return title;
		} else if (titleEndIdx6 != -1) {
			title = title.substring(0, titleEndIdx6);
			return title;
		} else if (titleEndIdx2 != -1) {
			title = title.substring(0, titleEndIdx2);
			return title;
		} else if (titleEndIdx7 != -1) {
			title = title.substring(0, titleEndIdx7);
			return title;
		} else {
			return title;
		}
	}

	/**
	 * 把目标字符中的符号替换成目标符号
	 *
	 * @param targetStr  目标字符
	 * @param replaceStr 目标符号
	 * @return
	 */
	public static String replaceSymbol(String targetStr, String replaceStr) {
		if (MyStringUtils.isNotNull(targetStr)) {
			Pattern p = Pattern.compile("\\p{P}");
			Matcher m = p.matcher(targetStr);
			while (m.find()) {
				targetStr = targetStr.replace(m.group(), replaceStr);
			}
			return targetStr;
		} else {
			return null;
		}
	}

	//祛除字符中的所有空格
	public static String trimInnerSpaceStr(String str) {
		if (str != null) {
			str = str.replaceAll(" ", "");
		}
		return str;
	}

	public static boolean isNotNull(String str) {
		return str != null && !str.trim().equals("");
	}

	public static boolean isNull(String str) {
		return !isNotNull(str);
	}

	public static List<String> StringSplit(String str, int num) {
		int length = str.length();
		List<String> listStr = new ArrayList<String>();
		int lineNum = length % num == 0 ? length / num : length / num + 1;
		String subStr = "";
		for (int i = 1; i <= lineNum; i++) {
			if (i < lineNum) {
				subStr = str.substring((i - 1) * num, i * num);
			} else {
				subStr = str.substring((i - 1) * num, length);
			}
			listStr.add(subStr);
		}
		return listStr;
	}


	//去除标点符号
	public static String deletePunctuation(String str) {
		Pattern p = Pattern.compile("\\p{P}");
		Matcher m = p.matcher(str);
		while (m.find()) {
			str = str.replace(m.group(), "");
		}
		return str;
	}

	/**
	 * 从目标内容中祛除以开头、结尾关键字包含的片段
	 *
	 * @param content  目标内容
	 * @param startStr 开头关键字
	 * @param endStr   结尾关键字
	 * @return
	 */
	public static String excludeStringByKey(String content, String startStr, String endStr) {
		int sSstart = content.indexOf(startStr);
		int eEnd = content.indexOf(endStr);
		if (sSstart != -1 && eEnd != -1) {
			content = content.substring(0, sSstart) + content.substring(eEnd + endStr.length());
		}
		if (content.indexOf(startStr) != -1) {
			content = excludeStringByKey(content, startStr, endStr);
		}
		return content;
	}

	public static String deleteHtmlTag(String content) {
		content = content.replaceAll("\\s*", ""); // 去除空格
		String regEx_html = "<.+?>"; // HTML标签的正则表达式
		Pattern pattern = Pattern.compile(regEx_html);
		Matcher matcher = pattern.matcher(content);
		content = matcher.replaceAll("");
		content = content.replaceAll("&nbsp;", "");
		return content;
	}


	/**
	 * 获取url同站点，同类别的前缀
	 * @param url
	 * @return
	 */
	public static String parseWebSiteUrl(String url) {
		String res = null;
		String regex = "^((https|http|ftp|rtsp|mms)?:\\/\\/).*\\/";
		Pattern onlinePtn = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher  matcher = onlinePtn.matcher(url);
		if (matcher.find()) {
			res = matcher.group();
		}
		return res;
	}



	public static void main(String [] args){
		System.out.println(parseWebSiteUrl("http://ggzy.xiangtan.gov.cn/project/4481.jspx?channelId=78?type=cg_qita&openDate=2018-05-21"));
//		Long startTime= System.currentTimeMillis();
//		String tiele="平遥县喜村至赵村公路改造工程施工招标公告";
//		String titleTemp1 = MyStringUtils.excludeStringByKey(tiele,"【","】");
//		titleTemp1 = MyStringUtils.excludeStringByKey(titleTemp1,"（","）");
//		titleTemp1 = MyStringUtils.excludeStringByKey(titleTemp1,"(",")");
//		titleTemp1 = MyStringUtils.excludeStringByKey(titleTemp1,"[","]");
//		titleTemp1 = MyStringUtils.excludeStringByKey(titleTemp1,"<",">");
//		titleTemp1 = MyStringUtils.excludeStringByKey(titleTemp1,"《","》");
//		titleTemp1 = MyStringUtils.replaceSymbol(titleTemp1,"");
//		titleTemp1 = MyStringUtils.trimInnerSpaceStr(titleTemp1);
//		System.out.println(titleTemp1);
//		Long endTime= System.currentTimeMillis();
//		System.out.print(endTime-startTime);
//
	}

}
