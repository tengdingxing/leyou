package com.leyou.item.controller;

import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import com.leyou.pageutils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 查询品牌列表
     *
     * */
    @GetMapping("page")      //请求参数  key=&page=1&rows=5&sortBy=id&desc=false
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",defaultValue = "false") Boolean desc,
            @RequestParam(value = "key",required = false) String key){

        PageResult<Brand> result =  this.brandService.queryBrandByPageAndSort(page,rows,sortBy,desc,key);

        if (CollectionUtils.isEmpty(result.getItems())){

            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 新增品牌
     *
     * */
    @PostMapping
    public  ResponseEntity<Void> saveBrand(@RequestParam(value = "cids") List<Long> categories,Brand brand){
        this.brandService.saveBrand(categories,brand);
        return /*ResponseEntity.status(HttpStatus.OK).build() 或*/ ResponseEntity.ok().build();
    }

    /**
     *@Description http://api.leyou.com/api/item/brand/cid/121
     *@Param  根据品牌分类的id 来获取该分类下的所有品牌
     *@Return list
     *@Author QJiangPing
     *@Date 2020/5/29
     *@Time 15:30
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid")Long cid){

        List<Brand> brands = this.brandService.queryBrandByCid(cid);

        if (CollectionUtils.isEmpty(brands)){

            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(brands);
    }
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id){

        Brand brand = this.brandService.queryBrandByid(id);

        if (brand == null ){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }
}
