package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("spec")
public interface SpecGroupApi {
    @GetMapping("groups/{cid}")
    public List<SpecGroup> querySpecGroupByCid(@PathVariable("cid")Long cid);

    @GetMapping("{cid}")
    public List<SpecGroup>querySpecsByCid(@PathVariable("cid")Long cid);
}
