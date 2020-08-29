package com.leyou.goods.controller;

import com.leyou.goods.service.GoodsHtmlService;
import com.leyou.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
    *@Description Administrator
    *@Param          http://www.leyou.com/item/88.html
    *@Return 
    *@Author Tdxing
    *@Date 2020/6/6
    *@Time 15:59
    */
@Controller
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsHtmlService goodsHtmlService;

    @GetMapping("item/{id}.html")

    public String toItemPage(@PathVariable("id")Long id,  Model model){
        Map<String, Object> map = this.goodsService.loadData(id);
        model.addAllAttributes(map);

        //页面静态化
        this.goodsHtmlService.createHtml(id);

        return "item";
    }
}
