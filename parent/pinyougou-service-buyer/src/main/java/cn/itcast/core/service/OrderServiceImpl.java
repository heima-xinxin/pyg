package cn.itcast.core.service;

import cn.itcast.common.utils.IdWorker;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private PayLogDao payLogDao;


    @Override
    public void add(Order order) {
        //从缓存中获取购物车集合 (即订单集合)
        double td=0;
        List<String> orderIds=new ArrayList<>();
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("CART").get(order.getUserId());
        for (Cart cart : cartList) {
            //添加订单数据
            //设置订单id
            long orderId = idWorker.nextId();
            orderIds.add(String.valueOf(orderId));
            order.setOrderId(orderId);
            //实付金额 先定义为0
            double payment=0;
            //设置付款状态 未付款
            order.setStatus("1");
            //订单创建时间
            order.setCreateTime(new Date());
            //订单更新时间
            order.setUpdateTime(new Date());
            //设置订单来源 pc端
            order.setSourceType("2");
            //商家id
            order.setSellerId(cart.getSellerId());

            //获取订单详情集合
            List<OrderItem> orderItemList = cart.getOrderItemList();
            //遍历
            for (OrderItem orderItem : orderItemList) {
                //添加订单详情数据
                long id = idWorker.nextId();
                orderItem.setId(id);
                //库存id 有了  根据库存id返回item对象
                Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                //设置商品id
                orderItem.setGoodsId(item.getGoodsId());
                //设置订单id
                orderItem.setOrderId(orderId);
                //设置标题
                orderItem.setTitle(item.getTitle());
                //设置单价
                orderItem.setPrice(item.getPrice());
                //商品图片地址
                orderItem.setPicPath(item.getImage());
                //设置商家id
                orderItem.setSellerId(item.getSellerId());
                //设置小计
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));

                //设置订单金额 小计相加
                payment+=orderItem.getTotalFee().doubleValue();

                //添加订单商品详情数据 到数据库
                orderItemDao.insertSelective(orderItem);
            }
            //设置订单总金额
            order.setPayment(new BigDecimal(payment));
            td+=order.getPayment().doubleValue();
            //添加订单
            orderDao.insertSelective(order);
        }

                    //订单添加完成后 需要从缓存中将当前用户的购物车集合给删除
            redisTemplate.boundHashOps("CART").delete(order.getUserId());

            //将订单集合 合并为一个支付单
            PayLog payLog=new PayLog();
            //支付订单号
            long outTradeNo = idWorker.nextId();
            payLog.setOutTradeNo(String.valueOf(outTradeNo));
            //创建日期
            payLog.setCreateTime(new Date());
            //支付金额 换算成分类计算
            payLog.setTotalFee((long)(td*100));
            //用户id
            payLog.setUserId(order.getUserId());
            //交易状态 0  1
            payLog.setTradeState("0");
            //支付类型 1 在线
            payLog.setPayType("1");
            //订单编号列表
            payLog.setOrderList(orderIds.toString().replace("[","").replace("]",""));
            payLogDao.insertSelective(payLog);

            //将payLog  存在缓存中 方便支付时 参数传递
            redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);
    }
}
