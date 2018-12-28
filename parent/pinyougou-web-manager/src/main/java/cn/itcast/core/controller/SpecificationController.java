package cn.itcast.core.controller;

import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojoGroup.SpecificationVo;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    @RequestMapping("/findAll")
    public List<Specification> findAll() throws Exception {
        return specificationService.findAll();
    }

    //根据条件继续查询分页
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody Specification specification) throws Exception {
        return specificationService.search(page,rows,specification);
    }

    //保存
    @RequestMapping("/add")
    public Result add(@RequestBody SpecificationVo specificationVo){
        try {
            specificationService.add(specificationVo);
            return new Result(true,"保存成功");
        } catch (Exception e) {
//            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }

    //数据回显
    @RequestMapping("/findOne")
    public SpecificationVo findOne(Long id) throws Exception {
        //根据id查找 回显到页面
      return specificationService.findOne(id);
    }

    //修改数据
    @RequestMapping("/update")
    public Result update(@RequestBody SpecificationVo vo){
        try {
            specificationService.update(vo);
            return new Result(true,"修改成功");
        } catch (Exception e) {
//            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    //删除数据 记得删除相关联的规格数据
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            specificationService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
//            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList() throws Exception {
       return specificationService.selectOptionList();
    }
}
