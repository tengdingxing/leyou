package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.service.SpecGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("spec")
public class SpecGroupController {
    @Autowired
    private SpecGroupService specGroupService;
        /**
        *@Description 根据分类id查询分组
        *@Param    http://api.leyou.com/api/item/spec/groups/3
        *@Return list
        *@Author Tdxing
        *@Date 2020/5/28
        *@Time 19:53
        */
     @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroupByCid(@PathVariable("cid")Long cid){

            List<SpecGroup> specGroups = this.specGroupService.querySpecGroupByCid(cid);

            if (CollectionUtils.isEmpty(specGroups)){
                return ResponseEntity.notFound().build();
            }
         System.out.println("specGroups 的值为："+specGroups);
            return ResponseEntity.ok(specGroups);
    }

    @GetMapping("{cid}")
    public ResponseEntity<List<SpecGroup>>querySpecsByCid(@PathVariable("cid")Long cid){

       List list = this.specGroupService.querySpecsByCid(cid);

       if(list == null || list.size() == 0){
           return ResponseEntity.notFound().build();
       }
         return ResponseEntity.ok(list);
    }

}
