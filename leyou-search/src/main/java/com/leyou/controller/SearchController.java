package com.leyou.controller;

import com.leyou.pageutils.PageResult;
import com.leyou.pojo.Goods;
import com.leyou.pojo.SearchRequest;
import com.leyou.pojo.SearchResult;
import com.leyou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class SearchController {
    
    /**
    *@Description Administrator
    *@Param 搜索页面的请求   http://api.leyou.com/api/search/page
    *@Return  传入的是一个json对象
    *@Author Tdxing
    *@Date 2020/6/3
    *@Time 11:10
    */
    @Autowired
    private GoodsService goodsService;

    @PostMapping("page")
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest request) {
       SearchResult result = this.goodsService.search(request);
        System.out.println("result的结果为："+result);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }
    
}
