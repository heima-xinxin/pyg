package cn.itcast.core.service;

import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {
    @Autowired
    private ContentDao contentDao;



    @Override
    public PageResult search(Integer page, Integer rows, Content content) {
        PageHelper.startPage(page,rows);
        Page<Content> p= (Page<Content>) contentDao.selectByExample(null);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public void add(Content content) {
        contentDao.insertSelective(content);

        //新建是清除缓存
        redisTemplate.boundHashOps("content").delete(content.getCategoryId());
    }

    @Override
    public Content findOne(Long id) {
        return contentDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(Content content) {
        //修改的时候 需要将修改前的分类缓存删了 并且将修改后的缓存给清空 (因为有可能修改的时候给分类id给修改了)
        //所以 修改前 先根据id相同 获取到数据库中content对象
        Content contentByDB = contentDao.selectByPrimaryKey(content.getId());
        //将操作mysql数据库的语句放到redis操作之前
        //spring 可以控制mysql事务 但不支持事务  mysql支持事务
        contentDao.updateByPrimaryKeySelective(content);
        //判断 分类id是否改变
        if (!contentByDB.getCategoryId().equals(content.getCategoryId())){
            //如果不相等 就将原来的现在的都删了
            redisTemplate.boundHashOps("content").delete(contentByDB.getCategoryId());
        }
        redisTemplate.boundHashOps("content").delete(content.getCategoryId());
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            Content content = contentDao.selectByPrimaryKey(id);
            contentDao.deleteByPrimaryKey(id);

            //删除 也需要清除缓存
            //根据id获取对象
            redisTemplate.boundHashOps("content").delete(content.getCategoryId());
        }
    }
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Content> findByCategoryId(Long categoryId) {
        //第一次获取从redis中获取
        List<Content> contents = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);

        //根据分类id查询
        if (null == contents){
            ContentQuery contentQuery = new ContentQuery();
            contentQuery.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
            contentQuery.setOrderByClause("sort_order desc");
            contents = contentDao.selectByExample(contentQuery);
            //存入数据库
            redisTemplate.boundHashOps("content").put(categoryId,contents);
            //设置存活时间
            redisTemplate.boundHashOps("content").expire(20,TimeUnit.HOURS);
        }
        return contents;
    }



    //根据广告分类的ID 查询 广告结果集
//    @Override
//    public List<Content> findByCategoryId(Long categoryId) {
//        //1:先查缓存
//        List<Content> contentList = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
//        if(null == contentList || contentList.size() == 0){
//            //3:没有 查询Mysql数据库
//            ContentQuery contentQuery = new ContentQuery();
//            contentQuery.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
//            contentQuery.setOrderByClause("sort_order desc");
//            contentList = contentDao.selectByExample(contentQuery);
//            //保存一份到缓存中 (时间)
//            redisTemplate.boundHashOps("content").put(categoryId,contentList);
//            //设置存活时间 一天
//            redisTemplate.boundHashOps("content").expire(24, TimeUnit.HOURS);
//
//        }
//        //4:直接返回
//        return contentList;
//
//
//    }
}
