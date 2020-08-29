package com.leyou.goods.service;

import com.leyou.goods.client.*;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;

@Service
public class GoodsService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecGroupClient specGroupClient;
    @Autowired
    private SpecParamClient specParamClient;

    public Map<String,Object>loadData(Long spuId){

        Map<String,Object>map = new HashMap<>();

        //根据spuId查询
        Spu spu = this.goodsClient.querySpuById(spuId);

        //根据spuId查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySupId(spuId);

        //查询sku
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spuId);

        System.out.println("skus的值为："+skus);

        //查询分类
        List cids = Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3());
        List names = this.categoryClient.queryNamesByIds(cids);
        List<Map<String,Object>>categories = new ArrayList<>();

        for (int i = 0; i < cids.size(); i++) {
            Map<String,Object> categoryMap = new HashMap<>();
            categoryMap.put("id",cids.get(i));
            categoryMap.put("name",names.get(i));
            categories.add(categoryMap);
        }

        //查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        //查询分组
        List<SpecGroup> groups = this.specGroupClient.querySpecsByCid(spu.getCid3());

        //查询特殊规格参数
        List<SpecParam> specParams = this.specParamClient.querySpecParam(null, spu.getCid3(), null, null);
        Map<Long,Object> paramMap = new HashMap<>();
        specParams.forEach(specParam -> {
            paramMap.put(specParam.getId(),specParam.getName());
        });
        System.out.println("specParams的值为："+specParams);

        //map封装页面需要的所有数据  spu
        map.put("spu", spu);
        // 封装spuDetail
        map.put("spuDetail", spuDetail);

        // 封装sku集合
        map.put("skus", skus);

        // 分类
        map.put("categories", categories);
        // 品牌
        map.put("brand", brand);
        // 规格参数组
        map.put("groups", groups);
        // 查询特殊规格参数
        map.put("paramMap", paramMap);
        return map;

    }

}
