package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.sql.rowset.BaseRowSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandDao brandDao;


    @Override
    public List<Brand> findAll() throws Exception {
        return brandDao.selectByExample(null);
    }

    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(null);

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(Brand brand) throws Exception {
        brandDao.insertSelective(brand);
    }

    @Override
    public Brand findOne(Long id) throws Exception {
        return brandDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(Brand brand) throws Exception {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delete(Long[] ids) throws Exception {
        //方式一  遍历
//        for (Long id : ids) {
//            brandDao.deleteByPrimaryKey(id);
//        }

        //方式二
        BrandQuery brandQuery=new BrandQuery();
        brandQuery.createCriteria().andIdIn(Arrays.asList(ids));
        brandDao.deleteByExample(brandQuery);
    }

    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Brand brand) throws Exception {
        //根据条件查询
        PageHelper.startPage(pageNum,pageSize);
        //判断条件 对象是否为空
        BrandQuery brandQuery=new BrandQuery();
        if (null !=brand){
            BrandQuery.Criteria criteria = brandQuery.createCriteria();
            //判断第一个条件 判断name 是否为空并且是否为空串 trim去除空格
            if (null!=brand.getName() && !"".equalsIgnoreCase(brand.getName().trim())){
              criteria.andNameLike("%"+brand.getName().trim()+"%");
            }
            //判断第二个条件 判断firstChar是否为空 并且是否为空串
            if (null!=brand.getFirstChar() && !"".equalsIgnoreCase(brand.getFirstChar().trim())){
                criteria.andFirstCharEqualTo(brand.getFirstChar().trim());
            }

        }
         Page<Brand> page= (Page<Brand>) brandDao.selectByExample(brandQuery);
         return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Map> selectOptionList() throws Exception {
        return brandDao.selectOptionList();
    }
}
