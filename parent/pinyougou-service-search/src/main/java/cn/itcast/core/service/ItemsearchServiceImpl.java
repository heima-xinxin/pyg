package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;
/*
* 1商品分类
* 2品牌
* 3规格
* 4结果集
* 5总条数
* */

@Service
public class ItemsearchServiceImpl implements ItemsearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;


//    //定义搜索对象的结构  category:商品分类
//    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''};
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        //去除中间的空串
        searchMap.put("keywords",searchMap.get("keywords").replaceAll(" ",""));

        Map<String, Object> map = searchHightLight(searchMap);
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);
        //根据分类名称 从缓存中查询品牌集合 和规格集合
        if (null !=categoryList && categoryList.size()>0){
            //通过分类名称获取模板id
            //获取分类列表的第一个
            Object typeId = redisTemplate.boundHashOps("itemCat").get(categoryList.get(0));
            //在根据模板id查询 品牌集合 和规格集合
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList",brandList);
            //获取给个集合
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList",specList);
        }


        return map;
    }
//    查询商品分类 为了去掉重复分类 所以需要根据分组
    public List<String> searchCategoryList(Map<String, String> searchMap){
        Criteria criteria=new Criteria("item_keywords").contains(searchMap.get("keywords"));
        Query query=new SimpleQuery(criteria);
        GroupOptions groupOptions=new GroupOptions();
        groupOptions.addGroupByField("item_category");
        //设置分类域
        query.setGroupOptions(groupOptions);
        GroupPage<Item> items = solrTemplate.queryForGroupPage(query, Item.class);

        //创建一个集合
        List<String> categoryList=new ArrayList<>();

        //获取分类列表
        GroupResult<Item> category = items.getGroupResult("item_category");
        List<GroupEntry<Item>> content = category.getGroupEntries().getContent();
        for (GroupEntry<Item> entry : content) {
            String categoryName = entry.getGroupValue();
            categoryList.add(categoryName);
        }
        return categoryList;


    }




    //设置结果集 总条数   设置高亮
    public Map<String,Object> searchHightLight(Map<String, String> searchMap){
        //根据关键词 查找
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        HighlightQuery query=new SimpleHighlightQuery(criteria);
        //设置过滤条件
//        $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''};
        //判断分类
        if (null !=searchMap.get("category") && !"".equalsIgnoreCase(searchMap.get("category").trim())){
            Criteria criteria1 = new Criteria("item_category").is(searchMap.get("category").trim());
            FilterQuery filterQuery=new SimpleFilterQuery(criteria1);
            query.addFilterQuery(filterQuery);
        }
        //判断品牌
        if (null !=searchMap.get("brand") && !"".equalsIgnoreCase(searchMap.get("brand").trim())){
            Criteria criteria1 = new Criteria("item_brand").is(searchMap.get("brand").trim());
            FilterQuery filterQuery=new SimpleFilterQuery(criteria1);
            query.addFilterQuery(filterQuery);
        }
        //判断价格 0-500  3000-*
        if (null !=searchMap.get("price") && !"".equalsIgnoreCase(searchMap.get("price").trim())){
            String[] p = searchMap.get("price").trim().split("-");
            //将区间分成数组
            //判断 如果包含* 就是大于等于  如果不包含*  就是between and
            FilterQuery filterQuery=null;
            if (searchMap.get("price").trim().contains("*")){
                Criteria criteria1 = new Criteria("item_price").greaterThanEqual(p[0]);
                filterQuery=new SimpleFilterQuery(criteria1);
            }else{
                Criteria criteria1 = new Criteria("item_price").between(p[0],p[1],true,false);
                filterQuery=new SimpleFilterQuery(criteria1);
            }
            query.addFilterQuery(filterQuery);
        }

        //判断规格
        if (null !=searchMap.get("spec") && !"".equalsIgnoreCase(searchMap.get("spec").trim())){
            Map<String,String> spec = JSON.parseObject(searchMap.get("spec").trim(), Map.class);
            //因为规格有多个 所以需要遍历添加条件
            Set<Map.Entry<String, String>> entries = spec.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                FilterQuery filterQuery=new SimpleFilterQuery(new Criteria("item_spec_"+entry.getKey()).is(entry.getValue()));
                query.addFilterQuery(filterQuery);
            }
        }

        //排序
        //    $scope.searchMap='sort':'','sortField':''};

        if (null !=searchMap.get("sortField") && !"".equalsIgnoreCase(searchMap.get("sortField").trim())){
            if ("DESC".equalsIgnoreCase(searchMap.get("sort"))){
                query.addSort(new Sort(Sort.Direction.DESC,"item_"+searchMap.get("sortField")));
            }else{
                query.addSort(new Sort(Sort.Direction.ASC,"item_"+searchMap.get("sortField")));
            }

        }






        //设置分页
        //(当前页-1)*当前页个数
        query.setOffset(( Integer.parseInt(searchMap.get("pageNo"))-1)*Integer.parseInt(searchMap.get("pageSize")));
        query.setRows(Integer.parseInt(searchMap.get("pageSize")));

        //设置高亮
        //开启高亮
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        //设置前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //设置后缀
        highlightOptions.setSimplePostfix("</em>");

        query.setHighlightOptions(highlightOptions);



        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(query, Item.class);
        //获取高亮结果集
        List<HighlightEntry<Item>> highlighted = items.getHighlighted();
        //遍历
        for (HighlightEntry<Item> entry : highlighted) {
            Item item = entry.getEntity();
            List<HighlightEntry.Highlight> highlights = entry.getHighlights();
            if (null != highlights && highlights.size()>0){
               item.setTitle(highlights.get(0).getSnipplets().get(0));
            }
        }
        //定义一个map 将item集合放进去 并且 将总商品数 和 总页数放进去
        Map<String,Object> map=new HashMap<>();
        //放入item集合
        map.put("rows",items.getContent());
        //放入总商品数
        map.put("total",items.getTotalElements());
        //放入总页数
        map.put("totalPages",items.getTotalPages());
        return map;
    }
}
