package cn.itcast.core.controller;

import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;
    //添加(商家申请入驻)
    @RequestMapping("/add")
    public Result add(@RequestBody Seller seller){
        try {
            //对密码进行加密
            seller.setPassword(new BCryptPasswordEncoder().encode(seller.getPassword()));
            sellerService.add(seller);
            return new Result(true,"申请入驻成功,正在等待审核");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"申请入驻失败");
        }
    }
}
