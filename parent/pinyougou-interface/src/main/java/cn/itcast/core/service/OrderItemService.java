package cn.itcast.core.service;

import entity.PageResult;

public interface OrderItemService {
    PageResult search(Integer page, Integer rows, String name);
}
