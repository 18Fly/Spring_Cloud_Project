package org.project.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.Animal;
import org.project.entity.Help;
import org.project.feign.common.Return;
import org.project.service.AnimalService;
import org.project.service.HelpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/animal")
public class AnimalWeb {

    @Autowired
    private AnimalService animalService;

    @Autowired
    private HelpService helpService;

    /**
     * 获取动物分页信息
     *
     * @param page     当前页数
     * @param pageSize 每页显示数量
     * @param name     【可选】指定名称
     * @return 返回分页信息
     */
    @GetMapping("/page")
    public Return<Page<Animal>> getPageAnimal(int page, int pageSize, String name) {
        Page<Animal> animalPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Animal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Animal::getAname, name);
        lambdaQueryWrapper.orderByDesc(Animal::getUpdatetime);

        animalService.page(animalPage, lambdaQueryWrapper);
        return Return.success(animalPage);
    }

    /**
     * 根据动物AID删除对应信息
     *
     * @param ids 动物ID
     * @return 删除结果返回
     */
    @DeleteMapping
    public Return<String> deleteAnimal(Long[] ids) {
        return animalService.removeByIds(Arrays.asList(ids)) ? Return.success("删除成功") : Return.error("删除失败");
    }

    /**
     * 获取动物信息【修改】
     *
     * @param id 动物ID
     * @return 返回查询动物信息
     */
    @GetMapping("/{id}")
    public Return<Animal> editAnimal(@PathVariable Long id) {
        Animal animal = animalService.getById(id);
        return animal != null ? Return.success(animal) : Return.error("查询失败");
    }

    /**
     * Feign模块内部调用
     *
     * @param id 动物ID
     * @return 返回动物信息
     */
    @GetMapping("/feign/{id}")
    public Animal feignGetAnimal(@PathVariable Long id) {
        return animalService.getById(id);
    }

    /**
     * 保存修改后的动物信息
     *
     * @param animal 动物信息【全字段都包含】
     * @return 返回修改结果
     */
    @PutMapping
    public Return<String> saveEditAnimal(@RequestBody Animal animal) {
        return animalService.updateById(animal) ? Return.success("修改成功") : Return.error("修改失败");
    }

    /**
     * 新增动物信息
     *
     * @param animal 传来的动物信息
     * @return 返回保存结果
     */
    @PostMapping
    public Return<String> saveAnimal(@RequestBody Animal animal) {
        return animalService.save(animal) ? Return.success("新增成功") : Return.error("新增失败");
    }

    /**
     * Feign内部获取动物ID列表
     *
     * @return 动物ID列表
     */
    @GetMapping
    public List<Long> getAnimalIds() {
        LambdaQueryWrapper<Animal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Animal::getAid);

        List<Animal> list = animalService.list(queryWrapper);
        List<Long> longs = list.stream().map(item -> item.getAid()).collect(Collectors.toList());

        return longs;
    }

    /**
     * 用户端获取动物分页信息
     *
     * @param pageCurrent 虚拟页中的当前页
     * @return 动物分页信息
     */
    @GetMapping("/userPage")
    public Return<Page<Animal>> userGetAnimalInformation(Long pageCurrent) {
        if (pageCurrent == null) {
            return Return.error("请求出错!");
        }
        //此处size:6是一页显示的数量
        Page<Animal> animalPage = new Page<>(pageCurrent, 6);
        LambdaQueryWrapper<Animal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(Animal::getUpdatetime);
        lambdaQueryWrapper.eq(Animal::getAstatus,1);

        animalService.page(animalPage, lambdaQueryWrapper);

        return Return.success(animalPage);
    }

    /**
     * 用户端获取动物求助信息
     *
     * @param pageCurrent 虚拟页中的当前页
     * @return 动物求生分页信息
     */
    @GetMapping("/userpage/help")
    public Return<Page<Help>> getHelpInformation(Long pageCurrent) {
        if (pageCurrent == null) {
            return Return.error("请求出错!");
        }
        //此处size:6是一页显示的数量
        Page<Help> helpPage = new Page<>(pageCurrent, 6);
        LambdaQueryWrapper<Help> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(Help::getUpdatetime);
        lambdaQueryWrapper.eq(Help::getEstatus, 1);

        helpService.page(helpPage, lambdaQueryWrapper);

        return Return.success(helpPage);
    }

    /**
     * 保存用户提交的求助信息
     *
     * @param help 用户提交的求助信息
     * @return 保存结果
     */
    @PostMapping("/userpage/help/submit")
    public Return<String> saveHelpInformation(@RequestBody Help help) {
        if (help == null) {
            return Return.error("保存失败，提交信息为空！");
        } else if (help.getEid() == null || help.getCallphone() == null || help.getUid() == null || help.getContent() == null || help.getImages() == null) {
            return Return.error("保存失败，提交信息不全！");
        }

        help.estatus = 0;
        boolean save = helpService.save(help);

        return save ? Return.success("提交成功!") : Return.error("保存失败，数据库过程出错！");
    }

    /**
     * 根据用户ID查询发布的求助信息审核进度
     *
     * @param uid 用户UID
     * @return 用户求助审核信息
     */
    @GetMapping("/userpage/getpublishexamine/{uid}")
    public Return<List<Help>> getHelpExamine(@PathVariable Long uid) {
        if (uid == null) {
            return Return.error("用户ID错误！");
        }

        QueryWrapper<Help> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("helpid", "eid", "uid", "estatus", "images");
        queryWrapper.eq("uid", uid);

        List<Help> list = helpService.list(queryWrapper);
        if (list.isEmpty()) {
            return Return.error("数据库查询失败！");
        }
        return Return.success(list);
    }

    /**
     * 获取求助信息待审核信息
     *
     * @param eid       审核员ID
     * @param page      页标
     * @param pageSize  页数
     * @param number    求助单号ID
     * @param beginTime 开始时间[不必须]
     * @param endTime   结束时间[不必须]
     * @return 求助信息待审核分页信息
     */
    @GetMapping("/help/page")
    public Return<Page<Help>> getExaminePage(Long eid, int page, int pageSize, Integer number, String beginTime, String endTime) {
        Page<Help> examinePage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Help> helpLambdaQueryWrapper = new LambdaQueryWrapper<>();
        helpLambdaQueryWrapper.eq(Help::getEid, eid);
        helpLambdaQueryWrapper.eq(number != null, Help::getHelpid, number);
        helpLambdaQueryWrapper.gt(beginTime != null, Help::getCreatetime, beginTime);
        helpLambdaQueryWrapper.lt(endTime != null, Help::getCreatetime, endTime);
        helpLambdaQueryWrapper.orderByDesc(Help::getUpdatetime);
        helpService.page(examinePage, helpLambdaQueryWrapper);

        return Return.success(examinePage);
    }

    /**
     * 修改求助信息审核状态
     *
     * @param help 审核单号ID+要修改的状态
     * @return 修改结果
     */
    @PutMapping("/help/edit")
    public Return<String> editHelpStatus(@RequestBody Help help) {
        return helpService.updateById(help) ? Return.success("修改成功") : Return.error("修改失败");
    }

    /**
     * Feign内部请求获取动物数量
     *
     * @return 动物总数量
     */
    @GetMapping("/feign/getAnimalCount")
    public Integer getAnimalCount() {
        List<Animal> list = animalService.list();
        if (list.isEmpty()) {
            return null;
        }
        return list.size();
    }

    /**
     * 获取动物年龄信息Echart
     *
     * @return 三个年龄段的动物信息
     */
    @GetMapping("/getanimalage")
    public Return<List<List<Object>>> getAnimalAge() {
        QueryWrapper<Animal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("aage", 1);
        long count = animalService.count(queryWrapper);
        List<Object> age1 = new ArrayList<>();
        age1.add("一岁");
        age1.add((int) count);

        QueryWrapper<Animal> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("aage", 2);
        long count2 = animalService.count(queryWrapper2);
        List<Object> age2 = new ArrayList<>();
        age2.add("两岁");
        age2.add((int) count2);

        QueryWrapper<Animal> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.gt("aage", 2);
        long count3 = animalService.count(queryWrapper3);
        List<Object> age3 = new ArrayList<>();
        age3.add("三岁及以上");
        age3.add((int) count3);
        List<List<Object>> returnValues = new ArrayList<>();
        returnValues.add(age1);
        returnValues.add(age2);
        returnValues.add(age3);

        return Return.success(returnValues);
    }

    /**
     * 获取动物数量
     *
     * @return 动物数量
     */
    @GetMapping("/getanimalmount")
    public Return<?> getAnimalMount() {
        int count = (int) animalService.count();
        return Return.success(count);
    }

    /**
     * Feign内部请求-根据动物ID更新状态
     *
     * @param aid 动物ID
     * @return 修改是否成功布尔值
     */
    @GetMapping("/feign/editanimalstatus/{aid}")
    public boolean editAnimalStatus(@PathVariable Long aid) {
        Animal tmp = new Animal();
        tmp.setAid(aid);
        tmp.setAstatus(0);
        boolean b = animalService.updateById(tmp);
        return b;
    }
}
