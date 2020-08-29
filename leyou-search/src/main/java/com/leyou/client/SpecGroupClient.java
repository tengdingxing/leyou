package com.leyou.client;

import com.leyou.item.api.SpecGroupApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface SpecGroupClient extends SpecGroupApi {
}
