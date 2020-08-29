package com.leyou.item.bo;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;

import java.util.List;
    /**
    *@Description Administrator
    *@Param
    *@Return      此类属于扩展类，专门用于扩展spu中参数不足
    *@Author Tdxing
    *@Date 2020/5/29
    *@Time 16:53
    */
public class SpuBo extends Spu {

    String cname;// 商品分类名称
    
    String bname;// 品牌名称

    SpuDetail spuDetail;//商品详情

    List<Sku> skus; //sku列表

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

        public SpuDetail getSpuDetail() {
            return spuDetail;
        }

        public void setSpuDetail(SpuDetail spuDetail) {
            this.spuDetail = spuDetail;
        }

        public List<Sku> getSkus() {
            return skus;
        }

        public void setSkus(List<Sku> skus) {
            this.skus = skus;
        }
    }