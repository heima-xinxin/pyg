package cn.itcast.core.controller;


import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UnActiveUserService;
import cn.itcast.core.service.activeUserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/unActiveUser")
public class UnActiveUserController {

    @Reference
    private UnActiveUserService unActiveUserService;

    @RequestMapping("/findAll")
    public List<User> findAll(){
        return unActiveUserService.findAll();
    }

}
