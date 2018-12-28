package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pojoGroup.SpecificationVo;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {
    //注入dao层
    @Autowired
    private SpecificationDao specificationDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    @Override
    public List<Specification> findAll() throws Exception {
        return specificationDao.selectByExample(null);
    }

    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Specification specification) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        SpecificationQuery specificationQuery = new SpecificationQuery();
        SpecificationQuery.Criteria criteria = specificationQuery.createCriteria();
        if (null != specification.getSpecName() && !"".equalsIgnoreCase(specification.getSpecName().trim())) {
            criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
        }

        Page<Specification> page = (Page<Specification>) specificationDao.selectByExample(specificationQuery);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(SpecificationVo specificationVo) throws Exception {
        //添加规格
        specificationDao.insertSelective(specificationVo.getSpecification());
        //将主键 返回 添加从表的数据
        //获取主键id
        Long id =specificationVo.getSpecification().getId();
        //获取从表规格集合
        List<SpecificationOption> specificationOptionList = specificationVo.getSpecificationOptionList();
        //遍历添加
        for (SpecificationOption specificationOption : specificationOptionList) {
           specificationOption.setSpecId(id);
           specificationOptionDao.insertSelective(specificationOption);
        }

    }

    @Override
    public SpecificationVo findOne(Long id) throws Exception {
        SpecificationVo vo = new SpecificationVo();
        //封装vo
        Specification specification = specificationDao.selectByPrimaryKey(id);
        vo.setSpecification(specification);
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        specificationOptionQuery.createCriteria().andSpecIdEqualTo(id);
        specificationOptionQuery.setOrderByClause("orders desc");
        List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(specificationOptionQuery);
        vo.setSpecificationOptionList(specificationOptions);

        return vo;
    }

    @Override
    public void update(SpecificationVo vo) throws Exception {
        //修改规格表
        specificationDao.updateByPrimaryKeySelective(vo.getSpecification());
        //修改相连表的数据
        //首先根据外键删除相关的数据
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        query.createCriteria().andSpecIdEqualTo(vo.getSpecification().getId());
        specificationOptionDao.deleteByExample(query);
        //删除之后添加
        //获取集合
        List<SpecificationOption> specificationOptionList = vo.getSpecificationOptionList();
        //遍历集合
        for (SpecificationOption specificationOption : specificationOptionList) {
            //获取表中的外键id
            specificationOption.setSpecId(vo.getSpecification().getId());
            //添加
            specificationOptionDao.insertSelective(specificationOption);
        }
    }

    @Override
    public void delete(Long[] ids) throws Exception {
        //遍历集合
        for (Long id : ids) {
            //首先删除规格表中的数据
            specificationDao.deleteByPrimaryKey(id);
            //删除相关联的从表中的数据
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            query.createCriteria().andSpecIdEqualTo(id);
            specificationOptionDao.deleteByExample(query);
        }
    }

    @Override
    public List<Map> selectOptionList() throws Exception {
        return specificationDao.selectOptionList();
    }
}

