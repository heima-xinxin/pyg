package cn.itcast.core.service;

import cn.itcast.core.pojo.specification.Specification;
import entity.PageResult;
import pojoGroup.SpecificationVo;

import java.util.List;
import java.util.Map;

public interface SpecificationService {
    List<Specification> findAll() throws Exception;

    PageResult search(Integer pageNum, Integer pageSize, Specification specification) throws Exception;

    void add(SpecificationVo specificationVo) throws Exception;

    SpecificationVo findOne(Long id) throws Exception;

    void update(SpecificationVo vo) throws Exception;

    void delete(Long[] ids) throws Exception;

    List<Map> selectOptionList() throws Exception;
}
