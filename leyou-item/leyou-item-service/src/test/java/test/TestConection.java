package test;

import com.leyou.LeyouItemApplication;
import com.leyou.item.controller.CategoryController;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeyouItemApplication.class)
public class TestConection {
    @Autowired
   DataSource dataSource;

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryController categoryController;

    @Test
    public void test() throws SQLException {
        System.out.println("数据源"+dataSource.getClass()+"\n");
        System.out.println("链接"+dataSource.getConnection());

    }

    @Test
    public void test1(){
      //  List<Category> list = categoryService.queryCategoriesBypid(0);
      //  System.out.println(list);
    }

    @Test
    public void test2(){
      //  categoryController.queryCategoriesBypid(0);
    }
}
