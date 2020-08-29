package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.pojo.SpecGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecGroupService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamService specParamService;

    public List<SpecGroup> querySpecGroupByCid(Long cid) {

        SpecGroup t = new SpecGroup();
        t.setCid(cid);
        return this.specGroupMapper.select(t);
    }

    public List querySpecsByCid(Long cid) {
        //查询规格组
        List<SpecGroup> groups = this.querySpecGroupByCid(cid);

        groups.forEach(group ->{
            //查询组内规格参数
            group.setParams(this.specParamService.querySpecParam(group.getId(),null,null,null));
        } );
        return groups;
    }
}
