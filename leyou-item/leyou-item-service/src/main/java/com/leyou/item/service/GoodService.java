package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import com.leyou.pageutils.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;

    public PageResult<SpuBo> querySupoByPage(String key, Boolean saleable, Integer page, Integer rows) {

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        //添加查询条件

        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }

        //添加商品上下架的过滤条件
        if (saleable != null){
            criteria.andEqualTo("saleable",saleable);
        }

        //添加分页
        PageHelper.startPage(page,rows);

        //执行查询，获取spu集合

        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>();
        //spu集合转化成spubo集合
        List<SpuBo> spuBos = new ArrayList<>();
        spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();

            BeanUtils.copyProperties(spu,spuBo);

            //查询品牌名称
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());

            //查询分类名称
            List<String> names = this.categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names,"/"));

            return spuBos.add(spuBo);
        }).collect(Collectors.toList());


        //返回一个pageResult<SpuBo>结果集

        return new PageResult<>(pageInfo.getTotal(),spuBos);

    }
    /**
    *@Description Administrator
    *@Param
    *@Return  新增商品的service
    *@Author Tdxing
    *@Date 2020/5/29
    *@Time 17:01
    */
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        // 新增spu
        // 设置默认字段
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insertSelective(spuBo);

        // 新增spuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);

        saveSkuAndStock(spuBo);

        //发送数据同步消息
        sendMsg("insert",spuBo.getId());
    }

    /*
    * 发送消息的方法
    * */
    private void sendMsg(String type, Long id) {
        try{
            this.amqpTemplate.convertAndSend("item."+type,id);
        }catch (AmqpException e){
            e.printStackTrace();
        }
    }

    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            // 新增sku
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);

            // 新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }


    public SpuDetail querySupDetailBySpuId(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据spuId查询sku的集合
     * @param spuId
     * @return
     */
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(sku);
        System.out.println("skus的值为："+skus);
      /*  skus.forEach(s -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        });*/
        for (Sku s:skus){
            Stock stock = this.stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        }

        return skus;
    }

    @Transactional
    public void updateGoods(SpuBo spu) {
        // 查询以前sku
        List<Sku> skus = this.querySkusBySpuId(spu.getId());
        // 如果以前存在，则删除
        if(!CollectionUtils.isEmpty(skus)) {
            List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
            // 删除以前库存
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId", ids);
            this.stockMapper.deleteByExample(example);

            // 删除以前的sku
            Sku record = new Sku();
            record.setSpuId(spu.getId());
            this.skuMapper.delete(record);

        }
        // 新增sku和库存
        saveSkuAndStock(spu);

        // 更新spu
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        spu.setValid(null);
        spu.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spu);

        // 更新spu详情
        this.spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());

        //发送消息，同步数据
        sendMsg("update",spu.getId());

    }
        //通过id查询spu
    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }
}
