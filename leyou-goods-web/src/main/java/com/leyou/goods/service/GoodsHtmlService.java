package com.leyou.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintStream;

import java.io.PrintWriter;
import java.util.Map;

@Service
public class GoodsHtmlService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private TemplateEngine engine;

   /**
   *@Description Administrator
   *@Param 实现页面静态化
   *@Return
   *@Author Tdxing
   *@Date 2020/6/9
   *@Time 10:40
   */

   public void createHtml(Long spuId){

       PrintWriter  writer= null;
        try {
            //获取页面数据
            Map<String, Object> map = this.goodsService.loadData(spuId);

            //创建thymeleaf上下文对象
            Context context = new Context();

            //把数据放入上下文中
            context.setVariables(map);

            //创建输出流
            File file = new File("D:\\nginx-1.14.0\\html\\leyou\\item\\" + spuId + ".html");
            writer = new PrintWriter(file);

            //执行页面静态化方法
           engine.process("item", context, writer);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (writer != null){
                writer.close();
            }
        }
   }

}
