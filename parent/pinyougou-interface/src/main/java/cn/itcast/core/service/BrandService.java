package cn.itcast.core.service;


import cn.itcast.core.pojo.good.Brand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {
    public List<Brand> findAll() throws Exception;

    PageResult findPage(Integer pageNum, Integer pageSize) throws Exception;

    void add(Brand brand) throws Exception;

    Brand findOne(Long id) throws Exception;

    void update(Brand brand) throws Exception;

    void delete(Long[] ids) throws Exception;

    PageResult search(Integer pageNum, Integer pageSize, Brand brand) throws Exception;

    List<Map> selectOptionList() throws Exception;
}
