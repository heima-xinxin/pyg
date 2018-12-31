package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<ItemCat> findByParentId(Long parentId) throws Exception {
        //查询所有分类 放入缓存中
        List<ItemCat> itemCats = findAll();
        for (ItemCat itemCat : itemCats) {
            //key为分类名称  value为模板id  在通过模板表查询品牌和规格
            if (null !=itemCat.getName()){
                redisTemplate.boundHashOps("itemCat").put(itemCat.getName(),itemCat.getTypeId());
            }
        }
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        itemCatQuery.createCriteria().andParentIdEqualTo(parentId);
        return itemCatDao.selectByExample(itemCatQuery);
    }

    @Override
    public void add(ItemCat itemCat) throws Exception {
        itemCatDao.insertSelective(itemCat);
    }

    @Override
    public ItemCat findOne(Long id) throws Exception {
        return itemCatDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(ItemCat itemCat) throws Exception {
        itemCatDao.updateByPrimaryKeySelective(itemCat);
    }

    @Override
    public void delete(Long[] ids) throws Exception {
        for (Long id : ids) {
            itemCatDao.deleteByPrimaryKey(id);
            ItemCatQuery query = new ItemCatQuery();
            query.createCriteria().andParentIdEqualTo(id);
            List<ItemCat> itemCats = itemCatDao.selectByExample(query);
            for (ItemCat itemCat : itemCats) {
                itemCatDao.deleteByPrimaryKey(itemCat.getId());
                ItemCatQuery itemCatQuery = new ItemCatQuery();
                itemCatQuery.createCriteria().andParentIdEqualTo(itemCat.getId());
                itemCatDao.deleteByExample(itemCatQuery);
            }

        }
    }

    @Override
    public List<ItemCat> findAll() {
       return itemCatDao.selectByExample(null);
    }

    //查询分类集合
    @Override
    public List<ItemCat> findItemCatList()throws Exception {
        //从缓存中查询首页商品分类
        List<ItemCat> itemCatList = (List<ItemCat>) redisTemplate.boundHashOps("itemCatList").get("indexItemCat");

        //如果缓存中没有数据，则从数据库查询再存入缓存
        if(itemCatList == null){
            //查询出1级商品分类的集合
            List<ItemCat> itemCatList1 = findByParentId(0L);
            //遍历1级商品分类的集合
            for (ItemCat itemCat1 : itemCatList1) {
                //查询2级商品分类的集合(将1级商品分类的id作为条件)
                List<ItemCat> itemCatList2 = findByParentId(itemCat1.getId());
                //遍历2级商品分类的集合
                for (ItemCat itemCat2 : itemCatList2) {
                    //查询3级商品分类的集合(将2级商品分类的父id作为条件)
                    List<ItemCat> itemCatList3 = findByParentId(itemCat2.getId());
                    //将2级商品分类的集合封装到2级商品分类实体中
                    itemCat2.setItemCatList(itemCatList3);
                }
                /*到这一步的时候，3级商品分类已经封装到2级分类中*/
                //将2级商品分类的集合封装到1级商品分类实体中
                itemCat1.setItemCatList(itemCatList2);
            }
            //存入缓存
            redisTemplate.boundHashOps("itemCatList").put("indexItemCat",itemCatList1);
            return itemCatList1;
        }
        //到这一步，说明缓存中有数据，直接返回
        return itemCatList;

    }
}
