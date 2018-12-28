package cn.itcast.core.service;

import cn.itcast.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {

    List<ItemCat> findByParentId(Long parentId) throws Exception;

    void add(ItemCat itemCat) throws Exception;

    ItemCat findOne(Long id) throws Exception;

    void update(ItemCat itemCat) throws Exception;

    void delete(Long[] ids) throws Exception;

    List<ItemCat> findAll();
}
