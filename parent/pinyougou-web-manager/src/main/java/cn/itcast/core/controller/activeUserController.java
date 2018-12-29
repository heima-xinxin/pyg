package cn.itcast.core.controller;


import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.activeUserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activeUser")
public class activeUserController {

    @Reference
    private activeUserService activeUserService;

    @RequestMapping("/findAll")
    public List<User> findAll(){
        return activeUserService.findAll();
    }

}
