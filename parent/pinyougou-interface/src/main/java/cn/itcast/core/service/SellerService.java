package cn.itcast.core.service;

import cn.itcast.core.pojo.seller.Seller;
import entity.PageResult;

public interface SellerService {
    void add(Seller seller) throws Exception;

    PageResult search(Integer page, Integer rows, Seller seller) throws Exception;

    Seller findOne(String id) ;

    void updateStatus(String sellerId, String status) throws Exception;
}
