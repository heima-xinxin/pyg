package cn.itcast.core.controller;


import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;
    //商家审核
    //分页查询
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody Seller seller) throws Exception {
      return sellerService.search(page,rows,seller);
    }


    //回显数据
    @RequestMapping("/findOne")
    public Seller findOne(String id){
       return sellerService.findOne(id);
    }

    //修改 (商家审核)
    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId,String status){
        try {
            sellerService.updateStatus(sellerId,status);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

}
