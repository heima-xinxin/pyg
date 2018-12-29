package cn.itcast.core.service;

import cn.itcast.core.pojo.user.User;

import java.util.List;

public interface UnActiveUserService {

    List<User> findAll();
}
