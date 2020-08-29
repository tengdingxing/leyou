package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.pageutils.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rows, String sortBy, Boolean desc, String key) {

        //初始化example
        Example example =new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        //分页
        PageHelper.startPage(page,rows);
        //过滤,根据name进行，模糊查询，或者根据首字母进行查询
       if (StringUtils.isNotBlank(key)) {
           criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
       }
       //添加排序条件
       /*if(StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy+ "" + (desc ? "desc" : "asc"));

        }*/

     /*  if (StringUtils.isNotBlank(sortBy)){
           example.setOrderByClause(sortBy+""+(desc?"desc":"asc"));
       }*/

        //查询
        List<Brand> brands = (Page<Brand>) brandMapper.selectByExample(example);
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        return new PageResult<>(pageInfo.getTotal(),pageInfo.getList());
    }

    @Transactional//使用这个事务注解，要执行全执行成功才执行
    public void saveBrand(List<Long> cids, Brand brand) {
        //先增加在维护
        this.brandMapper.insertSelective(brand);
        //再维护中间表
        for (Long c : cids) {
            this.brandMapper.insertCategoryByCidAndBid(c,brand.getId());
        }
    }

    public List<Brand> queryBrandByCid(Long cid) {

        return this.brandMapper.queryBrandByCid(cid);
    }

    public Brand queryBrandByid(Long id) {
        return this.brandMapper.selectByPrimaryKey(id);
    }
}
