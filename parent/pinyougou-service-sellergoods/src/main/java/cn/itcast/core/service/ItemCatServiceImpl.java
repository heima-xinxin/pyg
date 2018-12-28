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

}
