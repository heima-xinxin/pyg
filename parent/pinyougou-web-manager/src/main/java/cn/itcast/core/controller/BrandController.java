package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<Brand> findAll() throws Exception {
        return brandService.findAll();
    }

    @RequestMapping("/findPage")
    public PageResult findPage(Integer pageNum,Integer pageSize) throws Exception {
        return brandService.findPage(pageNum,pageSize);
    }

    @RequestMapping("/add")
    public Result add(@RequestBody Brand brand){
        try {
            brandService.add(brand);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"保存失败");
    }

    //数据回显
    @RequestMapping("/findOne")
    public Brand findOne(Long id) throws Exception {
        return brandService.findOne(id);
    }

    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody Brand brand){
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"修改失败");
    }

    //删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
//            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    //有条件查询   加了requestBody注解后  该对象不能为空
    @RequestMapping("/search")
    public PageResult search(Integer pageNum,Integer pageSize,@RequestBody Brand brand) throws Exception {
        return brandService.search(pageNum,pageSize,brand);
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList() throws Exception {
        return brandService.selectOptionList();
    }
}
