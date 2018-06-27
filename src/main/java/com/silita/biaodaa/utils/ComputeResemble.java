package com.silita.biaodaa.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class ComputeResemble {

    public static void main(String[] args){
        String title1 = "长沙市轨道交通6号线中段（不含文昌阁站-东郡站）土建工程第三方检测项目四标段";
        String title2 = "长沙市轨道交通6号线中段（不含文昌阁站-东郡站）土建工程第三方检测项目第四标段";
//        System.out.println(removeSign(title1));
//        System.out.println(removeSign(title2));
        String detail1 = "<p><strong>财信商贸中心商业提质改造项目补充通知（二）</strong></p><p>各投标单位：</p><p>财信商贸中心商业提质改造项目因故作出如下更改：</p><p>1、本项目工程量清单以2017年12月25日发布的为准，具体内容详见本通知附件。</p><p>2、本项目图纸以2017年12月25日发布的为准，具体内容详见本通知附件。</p><p>3、原投标文件递交的截止时间及开标时间（投标截止时间，下同）为2018年1月8日10时00分；开标地点为：湘西自治州公共资源交易中心第一开标室（吉首市吉凤街道州行政中心政务服务大楼五楼），<strong>现</strong><strong>更改</strong><strong>为</strong><strong>“</strong>投标文件递交的截止时间及开标时间（投标截止时间，下同）为<strong>201</strong><strong>8</strong><strong>年</strong><strong>1</strong><strong>月</strong><strong>10</strong><strong>日</strong><strong>10</strong><strong>时</strong><strong>00</strong><strong>分</strong>；开标地点为：湘西自治州公共资源交易中心<strong>第</strong><strong>一</strong><strong>开标室</strong>（吉首市吉凤街道州行政中心政务服务大楼五楼）”。</p><p>4、原招标文件中投标保证金的交纳截止时间为2018年1月5日17:00时，<strong>现</strong><strong>更</strong><strong>改为</strong>“投标保证金的交纳截止时间为<strong>201</strong><strong>8</strong><strong>年</strong><strong>1</strong><strong>月</strong><strong>9</strong><strong>日17:00时</strong>”。</p><p>特此通知。</p><p>招标人：湖南湘西财信投资置业有限公司</p><p>地址：吉首市武陵东路9号</p><p>联系人：刘先生</p><p>电话：13874817168</p><p>招标代理机构：湖南方梓项目管理咨询有限公司</p><p>地址：湖南省长沙市星沙经济开发区开元东路恒基凯旋门</p><p>联系人：宋小姐王小姐</p><p>电话：158727960918711028911</p><p>日期：2017年12月25日</p>";
        String detail2 = "<p>各投标单位：</p><p>现对部分投标人提出的有关问题答疑如下：</p><p>1、在审核说明的第十一条，按分部分项工程费计取3%不可预见费。提供的清单中没有列该项。是否要计？如要计，请提供一个统一的标准费用。</p><p><strong>答：按分部分项工程费计取3%不可预见费。</strong></p><p>2、本工程清单的单项工程投标报价汇总表其中“暂估价”一栏有金额，但清单中未提供专业工程暂估价、材料暂估价工程量清单，请提供。</p><p><strong>答：</strong><strong>新增暂估价清单具体详见本次答疑附件，原清单</strong><strong>审核报告和编制说明大纲</strong><strong>中涉及到的专业工程暂估价项目名称、工程内容、工程量及暂估金额，以本附件为准</strong><strong>。</strong><strong>新的审核报告和编制说明大纲以本次发布的为准，投标人在编制投标文件时请参照本次发布的</strong><strong>审核报告和编制说明大纲。</strong></p><p>3、招标文件“第五章工程量清单3.投标报价说明”及要求提供的报价格式“附表d.3单位工程费用计算表（一般计税法）”采用《湖南省住房城乡建设厅关于取消建筑行业劳保基金与增加社会保险有关事项的通知》（湘建价〔2016〕134号）社会保险费率3.18计取，但清单中的审核报告是采用（湘建价〔2017〕134号）社会保险费率3.15计取，请明确。</p><p><strong>答：按最新标准3.15%。</strong></p><p>4、本项目技术标采用暗标，技术标压条厚度能否由投标人自行选择，由招标代理提供。</p><p><strong>答：技术标压条已在购买技术标资料时同布发售，请</strong><strong>用</strong><strong>在招标代理公司购买的压</strong><strong>条装订。</strong></p><p>招标人：湖南湘西财信投资置业有限公司</p><p>招标代理机构：湖南方梓项目管理咨询有限公司</p><p>2017年12月22日</p>";
//        System.out.println(title1+"\n"+title2+"\n"+similarityResult(similarDegreeWrapper(title1,title2)));
        System.out.println(title1+"\n"+title2+"\n"+similarityResult(similarDegreeWrapper(MyStringUtils.deleteHtmlTag(detail1),MyStringUtils.deleteHtmlTag(detail2))));
//        System.out.println(similarDegreeWrapper(title1,title2));
//        System.out.println(similarDegreeWrapper(detail1,detail2));



//        if(similarDegreeWrapper(title1,title2) > 0.8) {
//            System.out.println("标题相似度大于80%，进行详情比对。。");
//            if (similarDegreeWrapper(detail1, detail2) > 0.9) {
//                System.out.println("详情似度大于90%，应该去重。。");
//            }
//        }
    }

    /**
     * 相似度计算前，进行参数换位
     * @param strA
     * @param strB
     * @return
     */
    public static double similarDegreeWrapper(String strA, String strB){
        if(removeSign(strA).length() >= removeSign(strB).length()){
            return SimilarDegree(strA,strB);
        }else{
            return SimilarDegree(strB,strA);
        }

    }

    /*
    * 计算相似度
    * */
    public static double SimilarDegree(String strA, String strB){
        String newStrA = removeSign(strA);
        String newStrB = removeSign(strB);
        //用较大的字符串长度作为分母，相似子串作为分子计算出字串相似度
        int temp = Math.max(newStrA.length(), newStrB.length());
        int temp2 = longestCommonSubstring(newStrA, newStrB).length();
        return temp2 * 1.0 / temp;
    }


    /*
     * 将字符串的所有数据依次写成一行
     * */
    public static String removeSign(String str) {
        StringBuffer sb = new StringBuffer();
        //遍历字符串str,如果是汉字数字或字母，则追加到ab上面
        for (char item : str.toCharArray())
            if (charReg(item)){
                sb.append(item);
            }
        return sb.toString();
    }


    /*
     * 判断字符是否为汉字，数字和字母，
     * 因为对符号进行相似度比较没有实际意义，故符号不加入考虑范围。
     * */
    public static boolean charReg(char charValue) {
        return (charValue >= 0x4E00 && charValue <= 0X9FA5) || (charValue >= 'a' && charValue <= 'z')
                || (charValue >= 'A' && charValue <= 'Z')  || (charValue >= '0' && charValue <= '9');
    }


    /*
     * 求公共子串，采用动态规划算法。
     * 其不要求所求得的字符在所给的字符串中是连续的。
     *
     * */
    public static String longestCommonSubstring(String strA, String strB) {
        char[] chars_strA = strA.toCharArray();
        char[] chars_strB = strB.toCharArray();
        int m = chars_strA.length;
        int n = chars_strB.length;

        /*
         * 初始化矩阵数据,matrix[0][0]的值为0，
         * 如果字符数组chars_strA和chars_strB的对应位相同，则matrix[i][j]的值为左上角的值加1，
         * 否则，matrix[i][j]的值等于左上方最近两个位置的较大值，
         * 矩阵中其余各点的值为0.
        */
        int[][] matrix = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (chars_strA[i - 1] == chars_strB[j - 1])
                    matrix[i][j] = matrix[i - 1][j - 1] + 1;
                else
                    matrix[i][j] = Math.max(matrix[i][j - 1], matrix[i - 1][j]);
            }
        }
        /*
         * 矩阵中，如果matrix[m][n]的值不等于matrix[m-1][n]的值也不等于matrix[m][n-1]的值，
         * 则matrix[m][n]对应的字符为相似字符元，并将其存入result数组中。
         *
         */
        char[] result = new char[matrix[m][n]];
        int currentIndex = result.length - 1;
        while (matrix[m][n] != 0) {
            if (matrix[n] == matrix[n - 1])
                n--;
            else if (matrix[m][n] == matrix[m - 1][n])
                m--;
            else {
                result[currentIndex] = chars_strA[m - 1];
                currentIndex--;
                n--;
                m--;
            }
        }
        return new String(result);
    }


    /*
     * 结果转换成百分比形式
     * */
    public static String similarityResult(double resule){
        return  NumberFormat.getPercentInstance(new Locale( "en ", "US ")).format(resule);
    }
}
