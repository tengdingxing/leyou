package com.leyou.client;

import com.leyou.item.api.CateGoryApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface CategoryClient extends CateGoryApi {
}
