package cn.itcast.core.service;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination smsDestination;
    @Autowired
    private UserDao userDao;


    @Override
    public void sendCode(String phone) {
        //生成验证码
        String random = RandomStringUtils.randomNumeric(6);
        //存入缓存
        redisTemplate.boundValueOps(phone).set(random);
        //设置存活时间  120200
        redisTemplate.boundValueOps(phone).expire(5, TimeUnit.DAYS);
        System.out.println(random);

        //发送数据给微服务
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("phone",phone);
                mapMessage.setString("signName","品优购");
                mapMessage.setString("templateCode","SMS_1000000");//先假装为SMS_1000000
                mapMessage.setString("templateParam","{\"number\":\""+random+"\"}");//{"number":"123123"}
                return mapMessage;
            }
        });

    }

    @Override
    public void add(User user, String smscode) {
        //判断验证码是否正确
        //从缓存中获取验证码
        String code = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        //首先判断是否为空 因为如果时间到了 验证码被清空就会失效
        if (null !=code){
            //在进行判断验证码是否相等
            if (code.equals(smscode)){
                //如果验证码相同 就添加到数据库
                //将数据表中的非空字段给赋值
                user.setCreated(new Date());
                user.setUpdated(new Date());
                user.setStatus("0");
                userDao.insertSelective(user);
            }else{
                throw new RuntimeException("验证码错误");
            }
        }else{
            throw new RuntimeException("验证码已失效");
        }
    }
}
