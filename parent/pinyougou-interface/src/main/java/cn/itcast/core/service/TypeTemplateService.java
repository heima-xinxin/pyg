package cn.itcast.core.service;

import cn.itcast.core.pojo.template.TypeTemplate;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {
    PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate) throws Exception;

    void add(TypeTemplate typeTemplate) throws Exception;

    TypeTemplate findOne(Long id) throws Exception;

    void update(TypeTemplate typeTemplate) throws Exception;

    void delete(Long[] ids) throws Exception;

    List<Map> findBySpecList(Long id);

//    List<Map> selectOptionList() throws Exception;
}
