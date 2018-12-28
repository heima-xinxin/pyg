package cn.itcast.core.listener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class ItemDeleteListener implements MessageListener {
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage message1 = (ActiveMQTextMessage) message;

        try {
            System.out.println("删除所需要的id:"+message1.getText());
            String id = message1.getText();
            //根据id外键关联 清除索引库
            Criteria criteria=new Criteria("item_goodsid").is(id);
            SolrDataQuery solrDataQuery=new SimpleQuery(criteria);
            solrTemplate.delete(solrDataQuery);
            //提交
            solrTemplate.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
