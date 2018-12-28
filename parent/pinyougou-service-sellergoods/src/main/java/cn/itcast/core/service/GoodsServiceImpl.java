package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;
import pojoGroup.GoodsVo;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
@SuppressWarnings("All")
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private BrandDao brandDao;

    @Override
    public void add(GoodsVo vo) {
        //设置商品新增状态
        vo.getGoods().setAuditStatus("0");
        goodsDao.insertSelective(vo.getGoods());
        //将id设置主键回显
        //将id赋值给goodsDesc
        vo.getGoodsDesc().setGoodsId(vo.getGoods().getId());
        //添加
        goodsDescDao.insertSelective(vo.getGoodsDesc());
        //库存表添加
        //判断是否启用规格
        if ("1".equals(vo.getGoods().getIsEnableSpec())){
            List<Item> itemList = vo.getItemList();
            for (Item item : itemList) {
                //标题为 名字 + 规格1 规格2
                //获取名字
                String title=vo.getGoods().getGoodsName();
                //获取规格数据
                //{"机身内存":"16G","网络":"联通3G"}  spec 为json串的格式 需要转成对象map
                String spec = item.getSpec();
                Map<String,String> map = JSON.parseObject(spec, Map.class);
                //获取map的所有值
                Set<Map.Entry<String, String>> entries = map.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    String specName = entry.getValue();
                    title+= specName;
                }
                //设置标题
                item.setTitle(title);
                //设置图片
                //[{"color":"粉色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXq2AFIs5AAgawLS1G5Y004.jpg"},{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXrWAcIsOAAETwD7A1Is874.jpg"}]
                //从大字段中获取第一张图片 放入item(库存表中)
                String itemImages = vo.getGoodsDesc().getItemImages();
                //将json串转换为对象结果集
                List<Map> imageList = JSON.parseArray(itemImages, Map.class);

                //判断集合
                if (null !=imageList && imageList.size()>0){
                    // 取出第一张
                    item.setImage((String) imageList.get(0).get("url"));
                }
                //设置分类id 以及分类名称 追踪第三级分类
                Long categoryId = vo.getGoods().getCategory3Id();
                //根据id查询分类名称
                ItemCat itemCat = itemCatDao.selectByPrimaryKey(categoryId);
                item.setCategoryid(categoryId);
                item.setCategory(itemCat.getName());
                //设置添加时间
                item.setCreateTime(new Date());
                //设置修改时间
                item.setUpdateTime(new Date());
                //设置商品id
                item.setGoodsId(vo.getGoods().getId());
                //设置seller_Id
                item.setSellerId(vo.getGoods().getSellerId());
                //根据sellerId 获取seller Name 公司名字
                Seller seller = sellerDao.selectByPrimaryKey(vo.getGoods().getSellerId());
                item.setSeller(seller.getName());
                //设置品牌名称
                //通过品牌id获取品牌名称
                Brand brand = brandDao.selectByPrimaryKey(vo.getGoods().getBrandId());
                item.setBrand(brand.getName());

                //添加item
                itemDao.insertSelective(item);

            }

        }


    }

    @Override
    public PageResult search(Integer page, Integer rows, Goods goods) {
        PageHelper.startPage(page,rows);
        PageHelper.orderBy("id desc");
        GoodsQuery goodsQuery = new GoodsQuery();
        if (null !=goods){
            GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
            //判断状态
            if (null !=goods.getAuditStatus() && !"".equals(goods.getAuditStatus())){
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            //判断name
            if (null !=goods.getGoodsName() && !"".equalsIgnoreCase(goods.getGoodsName().trim())){
                criteria.andGoodsNameLike("%"+goods.getGoodsName().trim()+"%");
            }
            //当前用户
            if (null !=goods.getSellerId()){
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            //判断
            criteria.andIsDeleteIsNull();
        }
        Page<Goods> page1= (Page<Goods>) goodsDao.selectByExample(goodsQuery);
        return new PageResult(page1.getTotal(),page1.getResult());
    }

    @Override
    public GoodsVo findOne(Long id) {
        GoodsVo vo = new GoodsVo();
        //封装goods
        Goods goods = goodsDao.selectByPrimaryKey(id);
        vo.setGoods(goods);
        //封装goodsDesc
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        vo.setGoodsDesc(goodsDesc);
        //根据外键查询
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(itemQuery);
        vo.setItemList(items);
        return vo;
    }

    @Override
    public void update(GoodsVo vo) {
        goodsDao.updateByPrimaryKeySelective(vo.getGoods());
        goodsDescDao.updateByPrimaryKeySelective(vo.getGoodsDesc());

        //修改库存表 先删除在添加
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(vo.getGoods().getId());
        itemDao.deleteByExample(itemQuery);
        //添加
        if ("1".equals(vo.getGoods().getIsEnableSpec())) {
            List<Item> itemList = vo.getItemList();
            for (Item item : itemList) {
                //标题为 名字 + 规格1 规格2
                //获取名字
                String title = vo.getGoods().getGoodsName();
                //获取规格数据
                //{"机身内存":"16G","网络":"联通3G"}  spec 为json串的格式 需要转成对象map
                String spec = item.getSpec();
                Map<String, String> map = JSON.parseObject(spec, Map.class);
                //获取map的所有值
                Set<Map.Entry<String, String>> entries = map.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    String specName = entry.getValue();
                    title += specName;
                }
                //设置标题
                item.setTitle(title);
                //设置图片
                //[{"color":"粉色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXq2AFIs5AAgawLS1G5Y004.jpg"},{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXrWAcIsOAAETwD7A1Is874.jpg"}]
                //从大字段中获取第一张图片 放入item(库存表中)
                String itemImages = vo.getGoodsDesc().getItemImages();
                //将json串转换为对象结果集
                List<Map> imageList = JSON.parseArray(itemImages, Map.class);

                //判断集合
                if (null != imageList && imageList.size() > 0) {
                    // 取出第一张
                    item.setImage((String) imageList.get(0).get("url"));
                }
                //设置分类id 以及分类名称 追踪第三级分类
                Long categoryId = vo.getGoods().getCategory3Id();
                //根据id查询分类名称
                ItemCat itemCat = itemCatDao.selectByPrimaryKey(categoryId);
                item.setCategoryid(categoryId);
                item.setCategory(itemCat.getName());
                //设置添加时间
                item.setCreateTime(new Date());
                //设置修改时间
                item.setUpdateTime(new Date());
                //设置商品id
                item.setGoodsId(vo.getGoods().getId());
                //设置seller_Id
                item.setSellerId(vo.getGoods().getSellerId());
                //根据sellerId 获取seller Name 公司名字
                Seller seller = sellerDao.selectByPrimaryKey(vo.getGoods().getSellerId());
                item.setSeller(seller.getName());
                //设置品牌名称
                //通过品牌id获取品牌名称
                Brand brand = brandDao.selectByPrimaryKey(vo.getGoods().getBrandId());
                item.setBrand(brand.getName());

                //添加item
                itemDao.insertSelective(item);

            }

        }
    }
    @Autowired
    private Destination queueSolrDeleteDestination;

    @Override
    public void delete(Long[] ids) {
          //删除  即将状态修改
        for (Long id : ids) {
            Goods goods = goodsDao.selectByPrimaryKey(id);
            goods.setIsDelete("1");
            goodsDao.updateByPrimaryKeySelective(goods);

            //这一步需要交给service-search层去做  传递id给MQ
            //删除后 将索引库中数据删除
            //根据外键id删除
            //将id传给search层 传给MQ
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(String.valueOf(id));
                }
            });

        }
    }


    //商品审核

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination topicPageAndSolrDestination;

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            Goods goods = goodsDao.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsDao.updateByPrimaryKeySelective(goods);

            //判断状态是否通过
            if ("1".equals(status)){

                //通过就将id传递给MQ
                jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(String.valueOf(id));
                    }
                });


            }
        }
    }
}
