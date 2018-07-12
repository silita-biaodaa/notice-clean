package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.SnatchurlRepetition;

public interface SnatchurlRepetitionMapper {
    /**
     *
     * @param snatchurlRepetition
     */
    void insertSnatchurlRepetition(SnatchurlRepetition snatchurlRepetition);

    /**
     *
     * @param id
     */
    void deleteSnatchurlRepetition(Long id);
}