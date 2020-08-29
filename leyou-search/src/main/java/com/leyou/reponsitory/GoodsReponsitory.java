package com.leyou.reponsitory;

import com.leyou.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsReponsitory extends ElasticsearchRepository<Goods, Long> {
}
