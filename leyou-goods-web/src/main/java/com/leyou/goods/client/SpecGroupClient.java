package com.leyou.goods.client;

import com.leyou.item.api.SpecGroupApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface SpecGroupClient extends SpecGroupApi {
}
