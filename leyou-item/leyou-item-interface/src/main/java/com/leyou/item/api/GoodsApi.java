package com.leyou.item.api;


import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.pageutils.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {

    /**
     * @Description Administrator
     * @Param
     * @Return 根据spuid查询所有的商品集
     * @Author Tdxing
     * @Date 2020/6/2
     * @Time 16:51
     */
    @GetMapping("spu/detail/{spuId}")
    public SpuDetail querySpuDetailBySupId(@PathVariable(value = "spuId") Long spuId);

    /**
     * @Description Administrator
     * @Param spu的分页结果集
     * @Return
     * @Author Tdxing
     * @Date 2020/6/2
     * @Time 16:53
     */
    @GetMapping("spu/page")
    public PageResult<SpuBo> querySpuBoByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows

    );

    @GetMapping("sku/list")
    public List<Sku> querySkusBySpuId(@RequestParam("id")Long spuId);

    @GetMapping("spu/{id}")
    public Spu querySpuById(@PathVariable("id")Long id);


}
