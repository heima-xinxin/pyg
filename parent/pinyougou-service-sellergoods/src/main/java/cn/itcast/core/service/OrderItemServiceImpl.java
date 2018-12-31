package cn.itcast.core.service;

import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
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
    private OrderDao orderDao;
    @Override
    public PageResult search(Integer page, Integer rows, String name) {
        PageHelper.startPage(page, rows);
        OrderQuery orderQuery = new OrderQuery();
        orderQuery.createCriteria().andSellerIdEqualTo(name);
        Page<Order> p = (Page<Order>) orderDao.selectByExample(orderQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }
}
