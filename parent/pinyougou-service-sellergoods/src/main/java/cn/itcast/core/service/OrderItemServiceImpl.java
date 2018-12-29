package cn.itcast.core.service;

import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService{

    @Autowired
    private OrderItemDao orderItemDao;
    @Override
    public PageResult search(Integer page, Integer rows, String name) {
        PageHelper.startPage(page, rows);
        OrderItemQuery orderItemQuery = new OrderItemQuery();
        orderItemQuery.createCriteria().andSellerIdEqualTo(name);
        Page<OrderItem> p = (Page<OrderItem>) orderItemDao.selectByExample(orderItemQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }
}
