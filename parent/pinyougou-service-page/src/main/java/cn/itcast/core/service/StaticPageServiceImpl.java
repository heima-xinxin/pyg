package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StaticPageServiceImpl implements StaticPageService,ServletContextAware {

    //注入spring公司封装的freemarker配置
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private ItemCatDao itemCatDao;

    //静态页面配置
    public void index(Long id){
        Writer out=null;
        try {
            //获取配置对象
            //获取输出全路径
            String allPath=getPath("/"+id+".html");
            Configuration conf = freeMarkerConfigurer.getConfiguration();

            //获取模板对象  传入相对路径
            Template template = conf.getTemplate("item.ftl");

            //数据
            Map<String,Object> root=new HashMap<>();
            //查询数据 查询库存列表  根据外键关联
            ItemQuery itemQuery = new ItemQuery();
            //判断条件外键关联  并且  判断是否有库存 状态为1
            itemQuery.createCriteria().andGoodsIdEqualTo(id).andStatusEqualTo("1");
            List<Item> itemList = itemDao.selectByExample(itemQuery);
            //放入map
            root.put("itemList",itemList);
            //查询goodsDesc 根据id查询
            GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
            root.put("goodsDesc",goodsDesc);
            //查询分类 获取商品表
            Goods goods = goodsDao.selectByPrimaryKey(id);
            root.put("itemCat1",itemCatDao.selectByPrimaryKey(goods.getCategory1Id()).getName());
            root.put("itemCat2",itemCatDao.selectByPrimaryKey(goods.getCategory2Id()).getName());
            root.put("itemCat3",itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName());
            root.put("goods",goods);


            //输出
            //模板 + 数据 ==输出  //指定输出路径
            out=new OutputStreamWriter(new FileOutputStream(allPath),"UTF-8");
            //处理
            template.process(root,out);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            //关流  判断out流是否为空 不为空 则关闭
            if (null !=out){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    //传入相对路径 获取绝对路径
    public String getPath(String path){
        return servletContext.getRealPath(path);
    }

    private ServletContext servletContext;
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext=servletContext;
    }
}
