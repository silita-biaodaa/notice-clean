package com.silita.biaodaa.common.elastic;

import com.silita.biaodaa.common.elastic.model.ElasticEntity;
import com.silita.biaodaa.common.elastic.model.EsResultInfo;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by dh on 2017/10/20.
 */
@Component
public class ElaticsearchUtils {
    private Logger logger = Logger.getLogger(ElaticsearchUtils.class);

    private int BATCH_COUNT= 100;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 查询index是否存在
     * @param clazz
     * @return
     */
    public boolean  indexExists(Class< ? extends ElasticEntity> clazz) {
        return elasticsearchTemplate.indexExists(clazz);
    }

    /**
     * 根据id删除索引
     * @param clazz
     * @param id
     */
    public void deleteById(Class<? extends ElasticEntity> clazz, String id ) {
        String delete = elasticsearchTemplate.delete(clazz, id);//索引的id
        logger.debug(delete);//删除的索引的id
    }

    /**
     * 查询count
     * @param indexName
     * @param key
     * @param value
     * @return
     */
    public long queryCount(String indexName, String key,String value) {
        NativeSearchQueryBuilder searchQuery=new NativeSearchQueryBuilder();
        BoolQueryBuilder bqb = QueryBuilders.boolQuery();
        //and
        if(key!= null) {
            bqb.must(buildQueryBuilder(1,key,value));
        }
        searchQuery.withIndices(indexName).withQuery(bqb);
        long count = elasticsearchTemplate.count(searchQuery.build());
        logger.debug(count);
        return count;
    }

    public long queryCount(String indexName, Map<String,String> args) {
        NativeSearchQueryBuilder searchQuery=new NativeSearchQueryBuilder();
        BoolQueryBuilder bqb = QueryBuilders.boolQuery();
        //and
        //多条件联合匹配
        if(args!= null){
            Set<String> keys =  args.keySet();
            for(String key :keys) {
                QueryBuilder mb = buildQueryBuilder(1,key,args.get(key));
                bqb.must(mb);
            }
            searchQuery.withQuery(bqb);
        }

        searchQuery.withIndices(indexName).withQuery(bqb);
        long count = elasticsearchTemplate.count(searchQuery.build());
        logger.debug(count);
        return count;
    }

    /**
     * 完全匹配单个条件
     * @param indexName
     * @param key
     * @param value
     * @param clazz
     * @param pageable
     * @return
     */
    public List< ? extends ElasticEntity> queryForList(String indexName,String key,
                                            String value,Class< ? extends ElasticEntity> clazz,Pageable pageable) {
        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
        BoolQueryBuilder bqb = QueryBuilders.boolQuery();
        bqb.must(buildQueryBuilder(1,key,value));
        searchQuery.withIndices(indexName).withPageable(pageable).withQuery(bqb);
        List<ElasticEntity> list = elasticsearchTemplate.queryForList(searchQuery.build(), (Class<ElasticEntity>) clazz);
        return list;
    }

    /**
     * （前缀具高级特性）匹配单个条件
     * @param indexName
     * @param key
     * @param value
     * @param clazz
     * @param pageable
     * @return
     */
    public List< ? extends ElasticEntity> matchForList(String indexName,String key,
                                                       String value,Class< ? extends ElasticEntity> clazz,Pageable pageable) {
        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
        BoolQueryBuilder bqb = QueryBuilders.boolQuery();
        bqb.must(buildQueryBuilder(2,key,value));
        searchQuery.withIndices(indexName).withPageable(pageable).withQuery(bqb);
        List<ElasticEntity> list = elasticsearchTemplate.queryForList(searchQuery.build(), (Class<ElasticEntity>) clazz);
        return list;
    }

    /**
     * 根据条件完全匹配（分词）索引
     * @param indexName  索引名称
     * @param clazz  实体对象类型
     * @param args  查询条件集合
     * @param pageable  分页对象【 分页，排序】
     * @return
     */
    public EsResultInfo queryForList(String indexName, Class< ? extends ElasticEntity> clazz, Map<String,Object> args, Pageable pageable) {
        EsResultInfo esResultInfo = executeMustQuery(indexName,clazz,args,pageable,1);
        return esResultInfo;
    }

    public EsResultInfo queryForList(String indexName,Class< ? extends ElasticEntity> clazz,Map<String,Object> args,Pageable pageable,List<String> fieldNames) {
        EsResultInfo resultInfo = executeMustQuery(indexName,clazz,args,pageable,1,fieldNames);
        return resultInfo;
    }

    /**
     * 根据条件搜索索引
     * @param indexName  索引名称
     * @param clazz  实体对象类型
     * @param args  查询条件集合
     * @param pageable  分页对象【 分页，排序】
     * @return
     */
    public EsResultInfo matchForList(String indexName,Class< ? extends ElasticEntity> clazz,Map<String,Object> args,Pageable pageable) {
        EsResultInfo esResultInfo = executeMustQuery(indexName,clazz,args,pageable,2);
        return esResultInfo;
    }

    public EsResultInfo matchPhraseQueryForList(String indexName,Class< ? extends ElasticEntity> clazz,Map<String,Object> args,Pageable pageable) {
        EsResultInfo esResultInfo = executeMustQuery(indexName,clazz,args,pageable,4);
        return esResultInfo;
    }

    /**
     * 根据条件搜索索引
     * @param indexName  索引名称
     * @param clazz  实体对象类型
     * @param args  不匹配的查询条件集合
     * @param pageable  分页对象【 分页，排序】
     * @return
     */
    public EsResultInfo notMatchForList(String indexName,Class< ? extends ElasticEntity> clazz,Map<String,String> args,Pageable pageable) {
        EsResultInfo esResultInfo = executeNotMustQuery(indexName,clazz,args,pageable,2);
        return esResultInfo;
    }


    /**
     * 根据条件完全匹配（分词）索引，提供范围匹配
     * @param indexName  索引名称
     * @param clazz  实体对象类型
     * @param args  查询条件集合
     * @param pageable  分页对象【 分页，排序】
     * @param boolFilterBuilder  过滤对象（范围匹配）
     * @return
     */
    public EsResultInfo queryRangeForList(String indexName,Class< ? extends ElasticEntity> clazz,Map<String,Object> args,Pageable pageable,BoolFilterBuilder boolFilterBuilder) {
        EsResultInfo resultInfo  = executeMustQuery(indexName,clazz,args,pageable,1,boolFilterBuilder);
        return resultInfo;
    }

    public EsResultInfo queryRangeForList(String indexName,Class< ? extends ElasticEntity> clazz,Map<String,Object> args,Pageable pageable,BoolFilterBuilder boolFilterBuilder,List<String> fieldNames) {
        EsResultInfo resultInfo = executeMustQuery(indexName,clazz,args,pageable,1,boolFilterBuilder,fieldNames);
        return resultInfo;
    }

    /**
     * 根据条件搜索索引，提供范围匹配
     * @param indexName  索引名称
     * @param clazz  实体对象类型
     * @param args  查询条件集合
     * @param pageable  分页对象【 分页，排序】
     * @param boolFilterBuilder  过滤对象（范围匹配）
     * @return
     */
    public EsResultInfo matchRangeForList(String indexName,Class< ? extends ElasticEntity> clazz,Map<String,Object> args,Pageable pageable,BoolFilterBuilder boolFilterBuilder) {
        EsResultInfo resultInfo = executeMustQuery(indexName,clazz,args,pageable,2,boolFilterBuilder);
        return resultInfo;
    }

    public EsResultInfo matchPhraseQueryRangeForList(String indexName,Class< ? extends ElasticEntity> clazz,Map<String,Object> args,Pageable pageable,BoolFilterBuilder boolFilterBuilder) {
        EsResultInfo resultInfo = executeMustQuery(indexName,clazz,args,pageable,4,boolFilterBuilder);
        return resultInfo;
    }

    /**
     * 根据条件搜索索引，提供范围匹配
     * @param indexName  索引名称
     * @param clazz  实体对象类型
     * @param args  查询条件集合
     * @param pageable  分页对象【 分页，排序】
     * @param boolFilterBuilder  过滤对象（范围匹配）
     * @param fieldNames  指定返回字段
     * @return
     */
    public EsResultInfo matchRangeForList(String indexName,Class< ? extends ElasticEntity> clazz,Map<String,Object> args,Pageable pageable,BoolFilterBuilder boolFilterBuilder,List<String> fieldNames) {
        EsResultInfo resultInfo = executeMustQuery(indexName,clazz,args,pageable,2,boolFilterBuilder,fieldNames);
        return resultInfo;
    }


    /**
     *  根据条件搜索索引，指定返回的字段
     * @param indexName
     * @param clazz
     * @param args
     * @param pageable
     * @param fieldNames
     * @return
     */
    public EsResultInfo matchForList(String indexName,Class< ? extends ElasticEntity> clazz,Map<String,Object> args,Pageable pageable,List<String> fieldNames) {
        EsResultInfo resultInfo = executeMustQuery(indexName,clazz,args,pageable,2,fieldNames);
        return resultInfo;
    }


    public EsResultInfo matchPhraseQueryForList(String indexName,Class< ? extends ElasticEntity> clazz,Map<String,Object> args,Pageable pageable,List<String> fieldNames) {
        EsResultInfo resultInfo = executeMustQuery(indexName,clazz,args,pageable,4,fieldNames);
        return resultInfo;
    }
    /**
     * 根据id查询索引。
     * @param id
     * @param clazz
     * @return
     */
    public ElasticEntity queryForObjectById(String id,Class<ElasticEntity> clazz) {
        GetQuery query = new GetQuery();
        query.setId(id);
        ElasticEntity object = elasticsearchTemplate.queryForObject(query, clazz);
        return object;
    }

    /**
     * 保存，更新单条索引
     * @param elasticEntity
     */
    public void saveOrUpdate(ElasticEntity elasticEntity){
        try {
            IndexQuery indexQuery = new IndexQueryBuilder().withId(String.valueOf(elasticEntity.getId())).withObject(elasticEntity).build();
            elasticsearchTemplate.index(indexQuery);
        }catch(Exception e){
            logger.error(e,e);
        }
    }

    /**
     * 根据条件删除索引
     * @param indexName
     * @param typeName
     * @param fieldName
     * @param fieldValue
     */
    public void delByArgs(String indexName,String typeName,String fieldName,String fieldValue){
        DeleteQuery deleteQuery = new DeleteQuery();
        QueryBuilder queryBuilder1 = QueryBuilders
                .boolQuery().must(QueryBuilders.termQuery(fieldName, fieldValue));
        deleteQuery.setQuery(queryBuilder1);
        deleteQuery.setIndex(indexName);
        deleteQuery.setType(typeName);
        elasticsearchTemplate.delete(deleteQuery);
    }

    /**
     * 建立mapping
     * @param clazz
     */
    public void createMapping(Class<? extends ElasticEntity> clazz){
        elasticsearchTemplate.putMapping(clazz);
    }

    /**
     * 批量插入，更新索引
     * @param elasticEntityList
     */
    public void  multipleIndexing(List<? extends ElasticEntity> elasticEntityList){
        List<IndexQuery> indexQueries = new ArrayList<IndexQuery>();
        for(int i=0; i < elasticEntityList.size();i++) {
            ElasticEntity entity = elasticEntityList.get(i);
            IndexQuery indexQuery = new IndexQueryBuilder().withId(String.valueOf(entity.getId())).withObject(entity).build();
            indexQueries.add(indexQuery);
            if(i>0 && i % BATCH_COUNT==0){
                elasticsearchTemplate.bulkIndex(indexQueries);
                indexQueries.clear();
            }
        }
        //bulk index
        if(indexQueries.size()>0) {
            elasticsearchTemplate.bulkIndex(indexQueries);
        }
    }

    /**
     * 删除Index
     * @param indexName
     * @return
     */
    public boolean deleteIndex (String indexName){
        return elasticsearchTemplate.deleteIndex(indexName);
    }
    public  void createIndex(Class< ? extends ElasticEntity> clazz){
        elasticsearchTemplate.createIndex(clazz);
    }

    /**
     * 删除type
     * @param indexName
     * @param typeName
     */
    public void deleteType (String indexName,String typeName){
        elasticsearchTemplate.deleteType(indexName,typeName);
    }



    /**
     * flag:
     *   1:termQuery
     *   2:matchQuery
     * @param indexName
     * @param clazz
     * @param args
     * @param pageable
     * @param flag
     * @return
     */
    private EsResultInfo executeNotMustQuery(String indexName, Class< ? extends ElasticEntity> clazz, Map<String,String> args, Pageable pageable, int flag, Object... extended){
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder bqb = QueryBuilders.boolQuery();
        //多条件联合匹配
        if(args!= null){
            Set<String> keys =  args.keySet();
            for(String key :keys) {
                QueryBuilder mb = buildQueryBuilder(flag,key,args.get(key));
                bqb.mustNot(mb);
            }
            searchQueryBuilder.withQuery(bqb);
        }

        //其他条件扩展
        if(extended!=null){
            //范围过滤
            if(extended.length==1 && extended[0]!=null){
                BoolFilterBuilder boolFilterBuilder = (BoolFilterBuilder) extended[0];
                if(boolFilterBuilder !=null) {
                    searchQueryBuilder.withFilter(boolFilterBuilder);
                }
            }
        }

        SearchQuery searchQuery = searchQueryBuilder.withIndices(indexName).withPageable(pageable).build();
        List<ElasticEntity> list = elasticsearchTemplate.queryForList(searchQuery, (Class<ElasticEntity>) clazz);
        long total = elasticsearchTemplate.count(searchQuery,clazz);
        logger.debug("total:"+total);
        for(int i=0;i<list.size();i++){
            ElasticEntity yanan = list.get(i);
            logger.debug(yanan.toString());
        }

        EsResultInfo esResultInfo = new EsResultInfo(total,list);
        return esResultInfo;
    }


    /**
     * flag:
     *   1:termQuery
     *   2:matchQuery
     * @param indexName
     * @param clazz
     * @param args
     * @param pageable
     * @param flag
     * @return
     */
    public EsResultInfo executeMustQuery(String indexName, Class<? extends ElasticEntity> clazz, Map<String, Object> args, Pageable pageable, int flag, Object... extended){
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
//        searchQueryBuilder.with
        BoolQueryBuilder bqb = QueryBuilders.boolQuery();
        //多条件联合匹配
        if(args!= null){
            Set<String> keys =  args.keySet();
            for(String key :keys) {
                Object value= args.get(key);
                QueryBuilder mb = buildQueryBuilder(flag, key,value);
                bqb.must(mb);
            }
            searchQueryBuilder.withQuery(bqb);
        }

        //其他条件扩展
        if(extended!=null&& extended.length>0){
            for(Object arg: extended) {
                if (arg != null) {
                    //过滤对象
                    if (arg instanceof BoolFilterBuilder) {
                        BoolFilterBuilder boolFilterBuilder = (BoolFilterBuilder) extended[0];
                        if (boolFilterBuilder != null) {
                            searchQueryBuilder.withFilter(boolFilterBuilder);
                        }
                    } else if (arg instanceof List) {//指定字段
                        searchQueryBuilder.withFields((String[]) ((List<String>) arg).toArray());
                    }
                }
            }

        }

        SearchQuery searchQuery = searchQueryBuilder.withIndices(indexName).withPageable(pageable).build();
        List<ElasticEntity> list = elasticsearchTemplate.queryForList(searchQuery, (Class<ElasticEntity>) clazz);
        long total = elasticsearchTemplate.count(searchQuery,clazz);
        logger.debug("total:"+total);
        for(int i=0;i<list.size();i++){
            ElasticEntity yanan = list.get(i);
            logger.debug(yanan.toString());
        }

        EsResultInfo esResultInfo = new EsResultInfo(total,list);
        return esResultInfo;
    }

    /**
     * flag:
     *   0:matchAllQuery
     *   1:termQuery
     *   2:matchQuery
     *   3:wildcardQuery 通配符查询, 支持 *
     *   4.matchPhraseQuery
     *   5.matchPhrasePrefixQuery
     */
    private QueryBuilder buildQueryBuilder(int flag,Object... key){
        QueryBuilder mb = null;
        if(key != null && key.length==2) {
            switch (flag) {
                case 1:
                    if( key[1] instanceof Collection){
                        mb = QueryBuilders.termsQuery((String)key[0], (Collection)key[1]);
                    }else{
                        mb = QueryBuilders.termQuery((String)key[0], key[1]);
                    }
                    break;
                case 2:
                    mb = QueryBuilders.matchQuery((String) key[0], key[1]);
                    break;
                case 3:
                    mb = QueryBuilders.wildcardQuery((String)key[0], (String)key[1]);
                    break;
                case 4:
                    mb = QueryBuilders.matchPhraseQuery((String)key[0], key[1]);
                    break;
                case 5:
                    mb = QueryBuilders.matchPhrasePrefixQuery((String)key[0], key[1]);
                    break;
            }
        }else{
            if(flag==0) {
                mb = QueryBuilders.matchAllQuery();
            }
        }
        return mb;
    }


}
