package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.good.Goods;
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
import pojoGroup.OrderVo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderStatisticsServiceImpl implements OrderStatisticsService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Override
    public  List<OrderVo> search(String date) throws ParseException {
        //TODO 时间区间
        List<OrderVo> orderVoList = new ArrayList<>();
        OrderItemQuery orderItemQuery = new OrderItemQuery();
        if (null != date){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            Date parse = simpleDateFormat.parse(date);
            OrderQuery orderQuery = new OrderQuery();
            orderQuery.createCriteria().andPaymentTimeBetween(parse,new Date());
            List<Order> orders = orderDao.selectByExample(orderQuery);
            for (Order order : orders) {
                orderItemQuery.createCriteria().andOrderIdEqualTo(order.getOrderId());
            }

        }
        List<OrderItem> orderItemList = orderItemDao.selectByExample(orderItemQuery);
        for (OrderItem orderItem : orderItemList) {
            OrderVo orderVo = new OrderVo();
            Goods goods = goodsDao.selectByPrimaryKey(orderItem.getGoodsId());
            orderVo.setGoodName(goods.getGoodsName());
            orderVo.setNum(orderItem.getNum());
            orderVo.setTotalFee(orderItem.getTotalFee().doubleValue());

            orderVoList.add(orderVo);
        }
        return orderVoList;
    }
}
