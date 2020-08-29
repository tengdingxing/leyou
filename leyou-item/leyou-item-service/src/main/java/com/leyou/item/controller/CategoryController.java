package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private  CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<List<Category>>queryCategoriesBypid(@RequestParam(value = "pid",defaultValue = "0")Long pid){
        //报出400错误，参数不合法
        if (pid == null || pid<0){
            return ResponseEntity.badRequest().build();
        }
        //正常
        List<Category> categories = this.categoryService.queryCategoriesBypid(pid);
        //报出404，集合为空，说明服务器未找到资源
        if (CollectionUtils.isEmpty(categories)){
            return  ResponseEntity.notFound().build();
        }
        //查询成功
        return ResponseEntity.ok(categories);
    }

    @GetMapping
    public ResponseEntity<List<String>>queryNamesByIds(@RequestParam("ids")List<Long> ids){

        List<String> names = this.categoryService.queryNamesByIds(ids);
        if (CollectionUtils.isEmpty(names)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }
}
