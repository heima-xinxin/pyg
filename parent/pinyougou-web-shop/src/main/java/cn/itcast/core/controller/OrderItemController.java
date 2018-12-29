package cn.itcast.core.controller;

import cn.itcast.core.service.OrderItemService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orderItem")
public class OrderItemController {

    @Reference
    private OrderItemService orderItemService;

    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows) throws Exception {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(1);
        return orderItemService.search(page,rows,name);
    }

}
