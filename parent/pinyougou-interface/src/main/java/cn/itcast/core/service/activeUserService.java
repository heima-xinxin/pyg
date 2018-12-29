package cn.itcast.core.service;

import cn.itcast.core.pojo.user.User;
import entity.PageResult;

import java.util.List;

public interface activeUserService {

    List<User> findAll();
}
