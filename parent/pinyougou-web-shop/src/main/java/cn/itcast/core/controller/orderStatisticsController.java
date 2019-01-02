package cn.itcast.core.controller;

import cn.itcast.core.service.OrderStatisticsService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojoGroup.OrderVo;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/orderStatistics")
public class orderStatisticsController {

    @Reference
    private OrderStatisticsService orderStatisticsService;

    @RequestMapping("/search")
    private List<OrderVo> search(String date) throws ParseException {
        return orderStatisticsService.search(date);

    }
}
