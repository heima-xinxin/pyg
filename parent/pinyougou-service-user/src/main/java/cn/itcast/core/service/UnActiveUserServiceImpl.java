package cn.itcast.core.service;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class UnActiveUserServiceImpl implements UnActiveUserService {
    @Autowired
    private UserDao userDao;


    @Override
    public List<User> findAll() {
        return userDao.selectUnActiveUser();
    }
}
