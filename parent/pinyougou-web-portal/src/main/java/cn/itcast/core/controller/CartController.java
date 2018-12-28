package cn.itcast.core.controller;

import cn.itcast.core.pojo.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.CartService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import entity.Result;
import org.omg.CORBA.ORB;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @RequestMapping("/addGoodsToCartList")
    //allowCredentials可写可不写
    @CrossOrigin(origins = "http://localhost:9006",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response){

        try {
            List<Cart> cartList=null;
            //获取cookie数组
            Cookie[] cookies = request.getCookies();
            //获取cookie数组中的购物车
            Boolean k=false;
            if (null!=cookies && cookies.length>0){
                for (Cookie cookie : cookies) {
                    //判断  是否有购物车
                    if ("CART".equals(cookie.getName())){
                        //有 就取出购物车  为字符串格式 转换成对象
                        cartList =JSON.parseArray(cookie.getValue(),Cart.class);
                        k=true;
                        //找到后退出
                        break;
                    }
                }
            }
            //如果没有 就创建购物车
            if (null ==cartList){
                cartList=new ArrayList<>();
            }
            //4,有 追加当前款
            Cart newCart = new Cart();
            //根据库存id获取库存对象
            Item item = cartService.findItemByItemId(itemId);
            newCart.setSellerId(item.getSellerId());
            //名称不必须存 用不到 如果存了就有可能下面的cookie 过大 存不进去 (cookie是有大小数据限制的)
            List<OrderItem> orderItemList=new ArrayList<>();
            OrderItem orderItem = new OrderItem();
            orderItem.setItemId(itemId);
            orderItem.setNum(num);
            orderItemList.add(orderItem);

            newCart.setOrderItemList(orderItemList);

            //2:判断新购物车的商家是谁 在当前购物车结果集中是否已经存在了
            int newIndexOf = cartList.indexOf(newCart); //为-1 则不存在 >=0则存在
            if (newIndexOf !=-1){
                //即存在 从老购物车结果集中找出那个跟新购物车是同一个商家的老购物车
                Cart oldCart = cartList.get(newIndexOf);
                // 判断新购物车中 新商品 在老购物车中商品结果集是否存在
                List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();
                int indexOf = oldOrderItemList.indexOf(orderItem);
                if (indexOf !=-1){
                    //存在  数量追加
                    oldOrderItemList.get(indexOf).setNum(oldOrderItemList.get(indexOf).getNum()+orderItem.getNum());
                }else{
                    //如果不存在 就添加商品
                    oldOrderItemList.add(orderItem);
                }
            }else{
                //如果不存在 则添加新购物车
                cartList.add(newCart);
            }

            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!"anonymousUser".equals(name)){
                //已登录
                //将当前购物车合并到原来的购物车中
                cartService.merge(cartList,name);
                //如果cookie中有原先购物车集合 就清空 不写也不碍事 但是写了提高效率
                if (k){
                    //清空cookie
                    Cookie cookie = new Cookie("CART",null);
                    //设置存活时间
                    cookie.setMaxAge(0);
                    //设置路径
                    cookie.setPath("/");
                    //响应给页面
                    response.addCookie(cookie);
                }

            }else{
                //未登录
                //先添加购物车到cookie中
                Cookie cookie = new Cookie("CART", JSON.toJSONString(cartList));
                //设置cookie存活时间
                cookie.setMaxAge(60*60*24*5);
                //设置cookie路径
                cookie.setPath("/");

                //回显给浏览器
                response.addCookie(cookie);

            }

            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }

    //查询购物车
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response){

        List<Cart> cartList=null;
        //1获取cookie数组
        Cookie[] cookies = request.getCookies();
        //2获取cookie数组中的购物车
        if (null!=cookies && cookies.length>0){
            for (Cookie cookie : cookies) {
                //判断  是否有购物车
                if ("CART".equals(cookie.getName())){
                    //有 就取出购物车  为字符串格式 转换成对象
                    cartList =JSON.parseArray(cookie.getValue(),Cart.class);
                    //找到后退出
                    break;
                }
            }
        }
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"anonymousUser".equals(name)){
            //已登录
            //3判断如果有 将购物车合并到帐户中原购物车 清空Cookie
             if (null !=cartList){
                 cartService.merge(cartList,name);
                 //清空cookie
                 Cookie cookie = new Cookie("CART",null);
                 //设置存活时间
                 cookie.setMaxAge(0);
                 //设置路径
                 cookie.setPath("/");
                 //响应给页面
                 response.addCookie(cookie);
             }
            //44:将帐户中购物车取出来 (即从缓存中取出来)
                cartList =cartService.findCartListByRedis(name);
        }
        //5将数据装满
        //判断如果不为空
        if (null !=cartList){
            cartList = cartService.findCartList(cartList);
        }

        //6回显

        return cartList;
    }
}
