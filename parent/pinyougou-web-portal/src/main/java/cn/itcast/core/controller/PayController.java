package cn.itcast.core.controller;

import cn.itcast.core.service.PayService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;

    @RequestMapping("/createNative")
    public Map<String,String> createNative(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return payService.createNative(name);
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        try {
            int time=0;

       while (true){

               Map<String,String> map = payService.queryPayStatus(out_trade_no);

//               SUCCESS—支付成功

//               NOTPAY—未支付
//
//               CLOSED—已关闭
//
//               REVOKED—已撤销（付款码支付）
//
//               USERPAYING--用户支付中（付款码支付）
//
//               PAYERROR--支付失败(其他原因，如银行返回失败)
               if ("SUCCESS".equalsIgnoreCase(map.get("trade_state"))){
                   //当支付成功时
                   return new Result(true,"支付成功");
               }
               if ("NOTPAY".equalsIgnoreCase(map.get("trade_state"))
                       || "CLOSED".equalsIgnoreCase(map.get("trade_state"))
                       || "REVOKED".equalsIgnoreCase(map.get("trade_state"))
                       || "USERPAYING".equalsIgnoreCase(map.get("trade_state"))
                       || "PAYERROR".equalsIgnoreCase(map.get("trade_state"))){
                   //当出现这种情况时 我们需要一直去问服务器成功与否
                   //睡五秒重新问
                   Thread.sleep(5000);
                   //但是不能一直问 所以需要有一个时间期限 所以定义一个变量 来记录时间
                   time++;
                   if (time>60){
                       //当大于五分钟时 需要调用微信接口关闭支付通道 此map可做判断 也可不做
                       Map<String,String> map1 =payService.closeOrder(out_trade_no);
                       //告诉用户支付超时
                       return new Result(false,"二维码超时");
                   }
               }
       }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"支付失败");
        }
    }
}
