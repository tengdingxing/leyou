package com.leyou.client;

import com.leyou.item.api.SpecParamApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface SpecParamClient extends SpecParamApi {
}
