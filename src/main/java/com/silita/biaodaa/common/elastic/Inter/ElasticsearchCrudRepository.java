package com.silita.biaodaa.common.elastic.Inter;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;

/**
 * Created by dh on 2017/10/12.
 */
public interface ElasticsearchCrudRepository<T, ID extends Serializable> extends ElasticsearchRepository<T, ID>, PagingAndSortingRepository<T, ID> {

}

