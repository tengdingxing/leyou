package com.leyou.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecParamClient;

import com.leyou.item.pojo.*;

import com.leyou.pojo.Goods;
import com.leyou.pojo.SearchRequest;
import com.leyou.pojo.SearchResult;
import com.leyou.reponsitory.GoodsReponsitory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecParamClient specParamClient;
    @Autowired
    private GoodsReponsitory goodsReponsitory;
    private static final ObjectMapper MAPPER = new ObjectMapper();


    public Goods buildGoods(Spu spu) throws IOException {
        //创建商品对象
        Goods good = new Goods();

        //查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        //查询分类的名称
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //查询spu下的所有sku
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spu.getId());
        List<Long> prices = new ArrayList<>();
        List<Map<String,Object>> skuMapList = new ArrayList<>();

        //遍历数组，获取价格的集合
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String,Object> skuMap = new HashMap<>();
            skuMap.put("id",sku.getId());
            skuMap.put("title",sku.getTitle());
            skuMap.put("price",sku.getPrice());
            skuMap.put("image", StringUtils.isNotBlank(sku.getImages())? StringUtils.split(sku.getImages(), ",")[0] : "");
            skuMapList.add(skuMap);
        });

        //查询出所有的规格参数
        List<SpecParam> params = this.specParamClient.querySpecParam(null, spu.getCid3(), null, true);

        //查询出spuDetail。获取规格参数
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySupId(spu.getId());

        // 获取通用的规格参数
        Map<Long, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>() {
        });
        // 获取特殊的规格参数
        Map<Long, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<Object>>>() {
        });
        //定义Map接收{规格参数名，规格参数值}
        Map<String, Object>paramMap = new HashMap<>();
        params.forEach(param ->{
            //判断规格参数是否 通用
            if(param.getGeneric()){
                String value = genericSpecMap.get(param.getId()).toString();
                //判断是否是数值类型
                if(param.getGeneric()){
                    value = chooseSegment(value, param);
                }
                // 把参数名和值放入结果集中
                paramMap.put(param.getName(),value);

            }else {
                paramMap.put(param.getName(),specialSpecMap.get(param.getId()));
            }
        } );

        // 设置参数
        good.setId(spu.getId());
        good.setCid1(spu.getCid1());
        good.setCid2(spu.getCid2());
        good.setCid3(spu.getCid3());
        good.setBrandId(spu.getBrandId());
        good.setCreateTime(spu.getCreateTime());
        good.setSubTitle(spu.getSubTitle());
        good.setAll(spu.getTitle() + brand.getName() + StringUtils.join(names, " "));
        good.setPrice(prices);
        good.setSkus(MAPPER.writeValueAsString(skuMapList));
        good.setSpecs(paramMap);


        return good;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public SearchResult search(SearchRequest request) {
        String key = request.getKey();
        System.out.println("key的值为"+key);
        System.out.println("rquest的值为："+request);
        //判断是否有搜索条件，如果没有，直接返回null，不允许搜索全部商品
        if(StringUtils.isBlank(key)){
            return null;
        }
        //构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加查询条件
       // MatchQueryBuilder basicQuery = QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND);
        BoolQueryBuilder basicQuery = buildBoolQueryBuilder(request);
        queryBuilder.withQuery(basicQuery);
        //对key进行全文检索查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("all",key).operator(Operator.AND));

        //通过sourceFilter设置返回结果字段，我们只需要id，skus，subTitle
        //queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));

        //分页
        int page = request.getPage();
        int size = request.getSize();
        queryBuilder.withPageable(PageRequest.of(page-1,size));


        //获取categories和brand的聚合
        String categoryAggName = "categories";
        String brandAggName = "brands";

        //进行聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        // 执行搜索，获取搜索的结果集
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)this.goodsReponsitory.search(queryBuilder.build());
        
        //解析聚合结果集
        List<Map<String, Object>> categories = getCategoryAggResult(goodsPage.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));

        //规格参数聚合
        List<Map<String,Object>>specs = null;
        //判断分类聚合的结果集大小，等于1且不为空
        if (!CollectionUtils.isEmpty(categories) && categories.size()==1){
            specs = getParamAggResult((Long) categories.get(0).get("id"),basicQuery);
        }

        System.out.println("规格参数的值为："+specs);

        //查询获取结果
        Page<Goods> pageInfo = this.goodsReponsitory.search(queryBuilder.build());
        //封装成自己的分页结果集并返回
        return new SearchResult(pageInfo.getTotalElements(), pageInfo.getTotalPages(), pageInfo.getContent(), categories, brands,specs);
    }
    
    /**
    *@Description Administrator
    *@Param 构建bool查询
    *@Return 
    *@Author Tdxing
    *@Date 2020/6/6
    *@Time 10:54
    */

    private BoolQueryBuilder buildBoolQueryBuilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));

        // 添加过滤条件
        if (CollectionUtils.isEmpty(request.getFilter())){
            return boolQueryBuilder;
        }
        for (Map.Entry<String, String> entry : request.getFilter().entrySet()) {

            String key = entry.getKey();
            // 如果过滤条件是“品牌”, 过滤的字段名：brandId
            if (StringUtils.equals("品牌", key)) {
                key = "brandId";
            } else if (StringUtils.equals("分类", key)) {
                // 如果是“分类”，过滤字段名：cid3
                key = "cid3";
            } else {
                // 如果是规格参数名，过滤字段名：specs.key.keyword
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key, entry.getValue()));
        }

        return boolQueryBuilder;
    }

    /**
    *@Description Administrator
    *@Param 规格参数
    *@Return
    *@Author Tdxing
    *@Date 2020/6/5
    *@Time 16:30
    */

    private List<Map<String, Object>> getParamAggResult(Long id, BoolQueryBuilder basicQuery) {

        // 创建自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 基于基本的查询条件，聚合规格参数
        queryBuilder.withQuery(basicQuery);
        // 查询要聚合的规格参数
        List<SpecParam> params = this.specParamClient.querySpecParam(null, id, null, true);
        // 添加聚合
        params.forEach(param -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword"));
        });
        // 只需要聚合结果集，不需要查询结果集
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));

        // 执行聚合查询
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)this.goodsReponsitory.search(queryBuilder.build());

        // 定义一个集合，收集聚合结果集
        List<Map<String, Object>> paramMapList = new ArrayList<>();
        // 解析聚合查询的结果集
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            // 放入规格参数名
            map.put("k", entry.getKey());
            // 收集规格参数值
            List<Object> options = new ArrayList<>();
            // 解析每个聚合
            StringTerms terms = (StringTerms)entry.getValue();
            // 遍历每个聚合中桶，把桶中key放入收集规格参数的集合中
            terms.getBuckets().forEach(bucket -> options.add(bucket.getKeyAsString()));
            map.put("options", options);
            paramMapList.add(map);
        }

        return paramMapList;
    }

    /**
     * 解析品牌聚合结果集
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        //处理结果集
        LongTerms terms = (LongTerms)aggregation;

        //解析所有桶的id，查询品牌，并返回一个品牌的集合
     return terms.getBuckets().stream().map(bucket -> {
          return this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        //处理结果集，用于获取桶
        LongTerms terms = (LongTerms) aggregation;

      return  terms.getBuckets().stream().map(bucket -> {
            Map<String,Object>map = new HashMap<>();
            //获取桶分类的id
            long id = bucket.getKeyAsNumber().longValue();
            //根据id分类查询分类名称
            List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(id));

            map.put("id",id);
            map.put("name",names.get(0));
            return map;
        }).collect(Collectors.toList());

    }

    public void save(Long id) throws IOException {
        Spu spu = this.goodsClient.querySpuById(id);
        Goods goods = this.buildGoods(spu);
        this.goodsReponsitory.save(goods);
    }
}
