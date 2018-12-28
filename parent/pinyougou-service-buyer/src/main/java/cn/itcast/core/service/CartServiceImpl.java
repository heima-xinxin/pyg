package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@SuppressWarnings("all")
public class CartServiceImpl implements CartService {
    @Autowired
    private ItemDao itemDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Item findItemByItemId(Long itemId) {
        return itemDao.selectByPrimaryKey(itemId);
    }

    @Override
    public List<Cart> findCartList(List<Cart> cartList) {
        //将数据封装填满
        for (Cart cart : cartList) {
            //sellerId 有了
            //sellerName  没有
            Item item=null;
            List<OrderItem> orderItemList = cart.getOrderItemList();
            for (OrderItem orderItem : orderItemList) {
                //获取库存对象
                item= findItemByItemId(orderItem.getItemId());
                //填值
                orderItem.setPicPath(item.getImage());
                orderItem.setTitle(item.getTitle());
                orderItem.setPrice(item.getPrice());
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
            }
            //设置商品名称
            cart.setSellerName(item.getSeller());
        }
        return cartList;
    }

    @Override
    public void merge(List<Cart> cartList, String name) {
        //获取到原来的购物车集合
        List<Cart> oldCartList = (List<Cart>) redisTemplate.boundHashOps("CART").get(name);
        //将新的购物车与原来的购物车合并
        oldCartList = merge1(cartList,oldCartList);
        //将老的购物车集合放到缓存中
        redisTemplate.boundHashOps("CART").put(name,oldCartList);
    }

    @Override
    public List<Cart> findCartListByRedis(String name) {
        return (List<Cart>) redisTemplate.boundHashOps("CART").get(name);
    }

    public List<Cart> merge1(List<Cart> newCartList,List<Cart> oldCartList){
        //将新的购物车 合并到原来的购物车中
        if (null !=newCartList && newCartList.size()>0){
            if (null!=oldCartList && oldCartList.size()>0){
                //如果两个都有值 就合并
                //遍历新购物车 添加到老购物车中
                for (Cart newCart : newCartList) {
                    //2:判断老购物车中有没有此商家
                    int newIndexOf = oldCartList.indexOf(newCart); //为-1 则不存在 >=0则存在
                    if (newIndexOf !=-1){
                        //即存在 从老购物车结果集中找出那个跟新购物车是同一个商家
                        Cart oldCart = oldCartList.get(newIndexOf);
                        // 判断新购物车中 新商品 在老购物车中商品结果集是否存在
                        List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();
                        //获取新购物车的商品集合
                        List<OrderItem> newOrderItemList = newCart.getOrderItemList();
                        //遍历新购物车的商品集合在原先购物车中商品结果集是否存在
                        for (OrderItem newOrderItem : newOrderItemList) {
                            int indexOf = oldOrderItemList.indexOf(newOrderItem);
                            if (indexOf !=-1){
                                //存在  数量追加
                                oldOrderItemList.get(indexOf).setNum(oldOrderItemList.get(indexOf).getNum()+newOrderItem.getNum());
                            }else{
                                //如果不存在 就添加商品
                                oldOrderItemList.add(newOrderItem);
                            }
                        }

                    }else{
                        //如果不存在 则添加新购物车
                        oldCartList.add(newCart);
                    }
                }


            }else{
                //如果老购物车没值 就返回新购物车
                return newCartList;
            }
        }
//        else{
//            //如果新购物车没有值 就返回老购物车
//            return oldCartList;
//        }

        //最终返回老购物车
        return oldCartList;
    }
}
