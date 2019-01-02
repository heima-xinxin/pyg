package cn.itcast.core.service;

import entity.PageResult;
import pojoGroup.OrderVo;

import java.text.ParseException;
import java.util.List;

public interface OrderStatisticsService {
    List<OrderVo> search(String date) throws ParseException;
}
