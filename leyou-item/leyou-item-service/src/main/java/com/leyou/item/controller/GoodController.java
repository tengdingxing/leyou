package com.leyou.item.controller;

import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.service.GoodService;
import com.leyou.pageutils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import com.leyou.item.pojo.SpuDetail;

import java.util.List;


@Controller
public class GoodController {

    @Autowired
    private GoodService goodService;

        /**
        *@Description Administrator  根据条件进行商品上下架页面的查询与分页
        *@Param http://api.leyou.com/api/item/spu/page?key=&saleable=true&page=1&rows=5
        *@Return 
        *@Author Tdxing
        *@Date 2020/5/29
        *@Time 10:23
        */
        @GetMapping("spu/page")
        public ResponseEntity<PageResult<SpuBo>> querySpuBoByPage(
               @RequestParam(value = "key",required = false)String key,
               @RequestParam(value = "saleable",required = false)Boolean saleable,
               @RequestParam(value = "page",defaultValue = "1")Integer page,
               @RequestParam(value = "rows",defaultValue = "5")Integer rows

               ){

            PageResult<SpuBo> result = this.goodService.querySupoByPage(key,saleable,page,rows);

            if (result == null || CollectionUtils.isEmpty(result.getItems())){

                return ResponseEntity.ok().build();
            }

            return ResponseEntity.ok(result);
        }

            /**
            *@DescriptionRequest URL: http://api.leyou.com/api/item/goods
             *                   Request Method: POST
            *@Param  新增商品的方法
            *@Return 无返回值
            *@Author Tdxing
            *@Date 2020/5/29
            *@Time 16:55
            */

            @PostMapping("goods")    //@RequestBody SpuBo spuBo 通过此注解来接受前端传来的请求参数
            public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo){
                this.goodService.saveGoods(spuBo);
                System.out.println("SpuBo的值为："+spuBo);
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
            /**回显修改商品页面的spu信息
            *@Description Administrator
            *@Param Request URL: http://api.leyou.com/api/item/spu/detail/2
             * Request Method: GET
            *@Return  null
            *@Author Tdxing
            *@Date 2020/5/29
            *@Time 21:21
            */

            @GetMapping("spu/detail/{spuId}")
            public ResponseEntity<SpuDetail> querySpuDetailBySupId(@PathVariable(value = "spuId")Long spuId){
                SpuDetail spuDetail = this.goodService.querySupDetailBySpuId(spuId);

                if (spuDetail == null){
                    return ResponseEntity.notFound().build();
                }

                return ResponseEntity.ok(spuDetail);
            }
            
            /**    修改商品回显的页面
             *  http://api.leyou.com/api/item/sku/list?id=2
             *          http://localhost:8081/sku/list?id=2
            *@Author Tdxing
            *@Date 2020/5/29
            *@Time 21:38
            */

            @GetMapping("sku/list")
            public ResponseEntity<List<Sku>> querySkusBySpuId(@RequestParam("id")Long spuId){
                List<Sku> skus = this.goodService.querySkusBySpuId(spuId);
                System.out.println("skus的值为："+skus);
                if (CollectionUtils.isEmpty(skus)) {
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok(skus);
            }
            
            /**
            *@Description Administrator
            *@Param Request URL: http://api.leyou.com/api/item/goods
             * Request Method: PUT
            *@Return            浏览器传送过来是json对象，修改和新增方法传参类似
            *@Author Tdxing
            *@Date 2020/5/30
            *@Time 8:01
            */

            @PutMapping("goods")
            public ResponseEntity<Void>updateGoods(@RequestBody SpuBo spuBo){
                this.goodService.updateGoods(spuBo);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            //通过id查询spu
            @GetMapping("spu/{id}")
            public ResponseEntity<Spu>querySpuById(@PathVariable("id")Long id){
                Spu spu = this.goodService.querySpuById(id);
                if (spu == null){
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok(spu);
            }
            
            
}
