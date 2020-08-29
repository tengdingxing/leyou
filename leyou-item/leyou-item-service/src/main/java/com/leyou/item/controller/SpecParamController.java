package com.leyou.item.controller;

import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("spec")
public class SpecParamController {
    @Autowired
    private SpecParamService specParamService;
        /**
        *@Description      http://api.leyou.com/api/item/spec/params?gid=1
        *@Param 通过gid查询商品的规格
        *@Return  list
        *@Author Tdxing
        *@Date 2020/5/28
        *@Time 21:23
        */
        @GetMapping("params")
        public ResponseEntity<List<SpecParam>> querySpecParam(
                @RequestParam(value = "gid", required = false)Long gid,
                @RequestParam(value = "cid", required = false)Long cid,
                @RequestParam(value = "generic", required = false)Boolean generic,
                @RequestParam(value = "searching", required = false)Boolean searching

        ){

          List<SpecParam> specParams =  this.specParamService.querySpecParam(gid,cid,generic,searching);

          if (CollectionUtils.isEmpty(specParams)){
              return ResponseEntity.notFound().build();
          }
            System.out.println("specParams的值为："+specParams);
          return  ResponseEntity.ok(specParams);
        }
}
