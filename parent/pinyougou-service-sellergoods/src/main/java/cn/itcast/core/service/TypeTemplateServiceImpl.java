package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateDao typeTemplateDao;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate) throws Exception {
       //查询所有
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);
        for (TypeTemplate template : typeTemplates) {
            //将品牌的json串转成对象 [{"id":1,"text":"联想"},{"id":3,"text":"三星"},{"id":2,"text":"华为"}]
            List<Map> brandList = JSON.parseArray(template.getBrandIds(), Map.class);
            //然后将品牌集合放入缓存中  键为模板id  值为品牌列表
            redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);
            //规格集合 不仅需要显示规格列表  还需要  根据规格外键查询规格属性表中的属性  下面查有 直接调用
            List<Map> specList = findBySpecList(template.getId());
            redisTemplate.boundHashOps("specList").put(template.getId(),specList);
        }


        PageHelper.startPage(page,rows);
        PageHelper.orderBy("id desc");
        TypeTemplateQuery query = new TypeTemplateQuery();
        if (null !=typeTemplate){
            TypeTemplateQuery.Criteria criteria = query.createCriteria();
            if (null !=typeTemplate.getName() && !"".equalsIgnoreCase(typeTemplate.getName().trim())){
                criteria.andNameLike("%"+typeTemplate.getName().trim()+"%");
            }
        }
        Page<TypeTemplate> p = (Page<TypeTemplate>) typeTemplateDao.selectByExample(query);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public void add(TypeTemplate typeTemplate) throws Exception {
        typeTemplateDao.insertSelective(typeTemplate);
    }

    @Override
    public TypeTemplate findOne(Long id) throws Exception {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(TypeTemplate typeTemplate) throws Exception {
        typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }

    @Override
    public void delete(Long[] ids) throws Exception {
        for (Long id : ids) {
            typeTemplateDao.deleteByPrimaryKey(id);
        }
    }

    @Override
    public List<Map> findBySpecList(Long id) {
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        //获取specId
        String specIds = typeTemplate.getSpecIds();
        //转换成map
        List<Map> specsList = JSON.parseArray(specIds, Map.class);
        //根据规格id查询规格属性表
        for (Map map : specsList) {
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            //直接添加map默认为object类型 object类型不能直接转换为特殊类型long 需要先转换成简单类型
            query.createCriteria().andSpecIdEqualTo((long)(Integer)map.get("id"));
            List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(query);
            map.put("options",specificationOptions);

        }
        return specsList;

    }



}
