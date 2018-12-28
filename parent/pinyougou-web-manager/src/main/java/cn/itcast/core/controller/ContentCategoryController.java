package cn.itcast.core.controller;

import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.service.ContentCategoryService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {

    @Reference
    private ContentCategoryService contentCategoryService;

    @RequestMapping("/findAll")
    public List<ContentCategory> findAll(){
       return contentCategoryService.findAll();
    }

    //新建
    @RequestMapping("/add")
    public Result add(@RequestBody ContentCategory contentCategory){
        try {
            contentCategoryService.add(contentCategory);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    //回显
    @RequestMapping("/findOne")
    public ContentCategory findOne(Long id){
        return contentCategoryService.findOne(id);
    }

    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody ContentCategory contentCategory){
        try {
            contentCategoryService.update(contentCategory);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }

    @RequestMapping("/search")
    public PageResult search(Integer page,Integer rows,@RequestBody ContentCategory contentCategory){
        return contentCategoryService.search(page,rows,contentCategory);
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            contentCategoryService.delete(ids);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }


}
