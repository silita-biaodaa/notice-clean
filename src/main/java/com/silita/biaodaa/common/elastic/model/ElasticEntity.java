package com.silita.biaodaa.common.elastic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;

import java.io.Serializable;

/**
 * Created by dh on 2017/10/20.
 */
public class ElasticEntity implements Serializable {
    @Id
    @Field(index = FieldIndex.not_analyzed, store = true)
    private String id;

    @Version
    private Long version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
