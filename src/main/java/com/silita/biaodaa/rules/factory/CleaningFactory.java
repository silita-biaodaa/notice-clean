package com.silita.biaodaa.rules.factory;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.rules.Interface.CleaningTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by dh on 2018/3/14.
 */
@Component
public class CleaningFactory {
    @Autowired
    @Qualifier("huNanCleanTemplate")
    private CleaningTemplate huNanCleanTemplate;

    @Autowired
    @Qualifier("othersCleanTemplate")
    private CleaningTemplate OthersCleanTemplate;

    public CleaningTemplate getClearnTemplate(String source){
        return buildRules(source);
    }

    private CleaningTemplate buildRules(String source){
        switch(source){
            case "country": return OthersCleanTemplate;
            case "beij": return OthersCleanTemplate;
            case "tianj": return OthersCleanTemplate;
            case "hebei": return OthersCleanTemplate;
            case "sanx": return OthersCleanTemplate;
            case "neimg": return OthersCleanTemplate;
            case "liaon": return OthersCleanTemplate;
            case "jil": return OthersCleanTemplate;
            case "heilj": return OthersCleanTemplate;
            case "shangh": return OthersCleanTemplate;
            case "jiangs": return OthersCleanTemplate;
            case "zhej": return OthersCleanTemplate;
            case "anh": return OthersCleanTemplate;
            case "fuj": return OthersCleanTemplate;
            case "jiangx": return OthersCleanTemplate;
            case "shand": return OthersCleanTemplate;
            case "henan": return OthersCleanTemplate;
            case "hubei": return OthersCleanTemplate;
            case "guangd": return OthersCleanTemplate;
            case "guangx": return OthersCleanTemplate;
            case "hain": return OthersCleanTemplate;
            case "chongq": return OthersCleanTemplate;
            case "sichuan": return OthersCleanTemplate;
            case "guiz": return OthersCleanTemplate;
            case "yunn": return OthersCleanTemplate;
            case "shanxi": return OthersCleanTemplate;
            case "gans": return OthersCleanTemplate;
            case "qingh": return OthersCleanTemplate;
            case "ningx": return OthersCleanTemplate;
            case Constant.HUNAN_SOURCE: return huNanCleanTemplate;
            case "xinjiang": return OthersCleanTemplate;
            case "xizang": return OthersCleanTemplate;
            default:return OthersCleanTemplate;
        }
    }

}
