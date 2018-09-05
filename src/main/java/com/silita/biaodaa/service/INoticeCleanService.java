package com.silita.biaodaa.service;

import com.snatch.model.EsNotice;

import java.util.List;

public interface INoticeCleanService {
    /**
     *
     * @param esNotice
     * @return
     */
    int countSnastchUrlByUrl (EsNotice esNotice);

    /**
     *
     * @param tempTitle
     * @param url
     * @param esNotice
     * @return
     */
    List<EsNotice> listEsNotice(String tempTitle, String url, EsNotice esNotice);

    /**
     *
     * @param url
     * @param esNotice
     * @return
     */
    List<EsNotice> listEsNotice(String url, EsNotice esNotice);

    /**
     *
     * @param id
     * @param isShow
     * @param source
     */
    void updateIsShowById(String id, int isShow, String source);

    /**
     *
     * @param esNotice
     */
    void insertSnatchurlRepetition(EsNotice esNotice);

    /**
     *
     * @param id
     */
    void deleteSnatchUrl(String id,String source);

    /**
     *
     * @param esNotice
     * @param historyNotice
     */
    int deleteRepetitionAndUpdateDetail(EsNotice esNotice, EsNotice historyNotice);

    /**
     *
     * @param esNotice
     */
    void insertSnatchUrl(EsNotice esNotice);

    /**
     *
     * @param esNotice
     * @return
     */
    Integer getMaxSnatchUrlIdByUrl(EsNotice esNotice);

    /**
     *
     * @param esNotice
     * @param snatchUrlId
     */
    void insertSnatchContent(EsNotice esNotice, Integer snatchUrlId);

    /**
     *
     * @param esNotice
     * @param snatchUrlId
     */
    void insertSnatchPress(EsNotice esNotice, Integer snatchUrlId);

    /**
     *
     * @param esNotice
     */
    void insertDetail(EsNotice esNotice);

    /**
     *
     * @param id
     * @param historyId
     */
    void updateSnatchUrlCert(Integer id, Integer historyId);

    /**
     *
     * @param esNotice
     * @param uuid
     */
    void updateSnatchUrl(EsNotice esNotice, String uuid);

    /**
     *
     * @param notice
     */
    void updateSnatchurlContent(EsNotice notice);

    /**
     *
     * @param notice
     */
    void updateSnatchpress(EsNotice notice);
}
