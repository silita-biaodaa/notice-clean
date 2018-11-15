package com.silita.biaodaa.service;

import org.junit.Test;

/**
 * Created by dh on 2018/11/15.
 */
public class RegexTest extends ConfigTest{

    @Test
    public void test(){
        String c="‘速度\"’“快''放’假''闪迪开发‘";
        c=formatContent(c);
        System.out.println(c);
    }

    private String  formatContent(String c){
        c = c.replaceAll("['‘’]","##");
        c = c.replaceAll("[\"“”]","&&");
       return c;
    }
}
