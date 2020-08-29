package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;

import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据父节点查询子节点
     * */

    public List<Category> queryCategoriesBypid(Long pid) {
        Category t = new Category();
        t.setParentId(pid);
        return this.categoryMapper.select(t);
    }

    public List<String>queryNamesByIds(List<Long> ids){
        List<Category> categories = this.categoryMapper.selectByIdList(ids);

      return   categories.stream().map(
                category -> category.getName()
        ).collect(Collectors.toList());
    }
}
