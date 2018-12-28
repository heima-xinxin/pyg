package cn.itcast.core.service;

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerDao sellerDao;

    @Override
    public void add(Seller seller) throws Exception {
        //将状态封装给对象
        seller.setStatus("0");
        seller.setCreateTime(new Date());
        //添加
        sellerDao.insertSelective(seller);
    }

    @Override
    public PageResult search(Integer page, Integer rows, Seller seller) throws Exception {
        PageHelper.startPage(page,rows);
        SellerQuery query = new SellerQuery();
        if (null !=seller){
            SellerQuery.Criteria criteria = query.createCriteria();
            criteria.andStatusEqualTo(seller.getStatus());
            if (null !=seller.getName() && !"".equalsIgnoreCase(seller.getName().trim())){
                criteria.andNameLike("%"+seller.getName().trim()+"%");
            }
            if (null !=seller.getNickName() && !"".equalsIgnoreCase(seller.getNickName().trim())){
                criteria.andNickNameLike("%"+seller.getNickName().trim()+"%");
            }
        }
        Page<Seller> page1 = (Page<Seller>) sellerDao.selectByExample(query);
        return new PageResult(page1.getTotal(),page1.getResult());
    }

    @Override
    public Seller findOne(String id){
        //查询
         return sellerDao.selectByPrimaryKey(id);

    }

    @Override
    public void updateStatus(String sellerId, String status) throws Exception {
        //根据id查找seller
        Seller seller = sellerDao.selectByPrimaryKey(sellerId);
        //修改status
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);
    }
}
