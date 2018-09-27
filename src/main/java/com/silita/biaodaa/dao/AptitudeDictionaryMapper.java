package com.silita.biaodaa.dao;

import java.util.List;

public interface AptitudeDictionaryMapper {

    /**
     *
     * @param aptitudeName
     * @return
     */
    List<String> getAptitudeNameByMajorName(String aptitudeName);

    /**
     *
     * @param aptitudeUuid
     * @return
     */
    public String getAptitudeNameByMajorUuid(String aptitudeUuid);
}