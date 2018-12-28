package cn.itcast.core.listener;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.fastjson.JSON;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.List;
import java.util.Map;

public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemDao itemDao;
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage atm = (ActiveMQTextMessage) message;

        try {
            System.out.println("添加到索引库:"+atm.getText());
            //如果通过就将数据添加到索引库
            //添加索引库这一步需要移到service-search层 为了降低耦合 方便维护 将数据id 发送到MQ
            String id = atm.getText();
            ItemQuery itemQuery=new ItemQuery();
                itemQuery.createCriteria().andGoodsIdEqualTo(Long.parseLong(id)).andStatusEqualTo("1").andIsDefaultEqualTo("1");
                List<Item> items = itemDao.selectByExample(itemQuery);
                for (Item item : items) {
                item.setSpecMap(JSON.parseObject(item.getSpec(),Map.class));
            }
                solrTemplate.saveBeans(items,1000);



        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
