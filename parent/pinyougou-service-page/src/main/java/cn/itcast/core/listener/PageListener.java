package cn.itcast.core.listener;

import cn.itcast.core.service.StaticPageService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class PageListener implements MessageListener {
    @Autowired
    private StaticPageService staticPageService;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage message1 = (ActiveMQTextMessage) message;

        try {
            String id = message1.getText();
            System.out.println("静态化页面接收id:"+id);

            //静态化处理 生成静态页面
            staticPageService.index(Long.parseLong(id));

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
