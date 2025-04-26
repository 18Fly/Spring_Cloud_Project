package org.project.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.project.dto.Adopt_Animal;
import org.project.dto.Adopt_Ids;
import org.project.entity.Adopt;
import org.project.feign.clients.AnimalClient;
import org.project.feign.clients.EmployeeClient;
import org.project.feign.clients.ExamineClient;
import org.project.feign.clients.UserClient;
import org.project.feign.common.Return;
import org.project.feign.pojo.Animal;
import org.project.service.AdoptService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/adopt")
public class AdoptWeb {

    @Autowired
    private AdoptService adoptService;

    @Autowired
    private AnimalClient animalClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private EmployeeClient employeeClient;

    @Autowired
    private ExamineClient examineClient;

    /**
     * 获取领养信息分页列表
     *
     * @param page     当前页数
     * @param pageSize 每页展示数量
     * @return 返回领养信息
     */
    @GetMapping("/page")
    public Return<Page<Adopt_Animal>> getAdoptPage(int page, int pageSize) {
        Page<Adopt> adoptPage = new Page<>(page, pageSize);
        Page<Adopt_Animal> adoptAnimalPage = new Page<>();

        LambdaQueryWrapper<Adopt> adoptLambdaQueryWrapper = new LambdaQueryWrapper<>();
        adoptLambdaQueryWrapper.orderByDesc(Adopt::getCreatetime);

        adoptService.page(adoptPage, adoptLambdaQueryWrapper);

        //用SpringBean拷贝功能，将Adopt的分页数据拷贝到Adopt_Animal上，同时忽略records属性
        BeanUtils.copyProperties(adoptPage, adoptAnimalPage, "records");
        //使用Feign为records属性集重新封装数据
        List<Adopt_Animal> adoptAnimalList = adoptPage.getRecords().stream().map((item) -> {
            Adopt_Animal adoptAnimal = new Adopt_Animal();
            BeanUtils.copyProperties(item, adoptAnimal);
            Animal animalById = animalClient.getAnimalById(item.getAid());
            if (animalById != null) {
                adoptAnimal.setAimages(animalById.getAimages());
                adoptAnimal.setAname(animalById.getAname());
            }
            return adoptAnimal;
        }).collect(Collectors.toList());

        adoptAnimalPage.setRecords(adoptAnimalList);

        return Return.success(adoptAnimalPage);
    }

    /**
     * Feign内部获取动物、用户、员工等ID列表
     *
     * @return 返回ID列表对象
     */
    @GetMapping("/add")
    public Return<Adopt_Ids> getAddListId() {
        Adopt_Ids adoptIds = new Adopt_Ids();
        adoptIds.setAid(animalClient.getAnimalIds());
        adoptIds.setEid(employeeClient.getEmployeeIds());
        adoptIds.setUid(userClient.getUserIds());

        return Return.success(adoptIds);
    }

    /**
     * 保存领养信息
     *
     * @param adopt 传过来的领养信息
     * @return 返回领养结果
     */
    @PostMapping
    public Return<String> addAdoptInformation(@RequestBody Adopt adopt) {
        return adoptService.save(adopt) ? Return.success("保存成功") : Return.error("保存失败");
    }

    /**
     * 根据传来的领养单IDs，删除对应的列表
     *
     * @param ids 传来的多个领养单ID
     * @return 返回删除结果
     */
    @DeleteMapping
    public Return<String> deleteAdoptByIds(@RequestParam("ids") List<Long> ids) {
        return adoptService.removeByIds(ids) ? Return.success("删除成功") : Return.error("删除失败");
    }

    /**
     * Feign内部根据领养表ID获取对应领养单的ID信息
     * [下标]0:动物ID 1:用户ID 2:审核员ID
     *
     * @param atid 领养表ID
     * @return 返回IDs列表
     */
    @GetMapping("/feign/{atid}")
    public List<Long> getIds2Examine(@PathVariable Long atid) {
        Adopt byId = adoptService.getById(atid);
        List<Long> ids = new ArrayList<>();
        ids.add(byId.getAid());
        ids.add(byId.getUid());
        ids.add(byId.getEid());

        return ids;
    }

    /**
     * 保存用户端提交来的领养信息
     *
     * @param adopt 用户提交的领养信息
     * @return 领养信息保存结果
     */
    @PostMapping("/userpage/adoptsubmit")
    public Return<String> adoptSubmitDeal(@RequestBody Adopt adopt) {
        if (adopt.getEid() == null || adopt.getAid() == null || adopt.getUid() == null || adopt == null) {
            return Return.error("信息不全!");
        }

        boolean save = adoptService.save(adopt);

        if (save) {
            save = examineClient.saveRelativeAdopt(adopt.getAtid());
        }

        return save ? Return.success("领养信息提交完成!") : Return.success("领养信息提交失败!");
    }


    /**
     * Feign内部请求-根据用户ID获取领养单号ID
     *
     * @param uid 用户ID
     * @return 领养单号ID[多个]
     */
    @GetMapping("/feign/getUidAtid/{uid}")
    public List<Long> accordingUidgetAtid(@PathVariable Long uid) {
        if (uid == null) {
            return null;
        }
        LambdaQueryWrapper<Adopt> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(Adopt::getAtid);
        lambdaQueryWrapper.eq(Adopt::getUid, uid);

        List<Adopt> list = adoptService.list(lambdaQueryWrapper);
        List<Long> retrunValue = new ArrayList<>();
        for (Adopt adopt : list) {
            retrunValue.add(adopt.getAtid());
        }

        return retrunValue;
    }

    /**
     * Feign内部请求，获取用户领养单的审核频率
     *
     * @param atid 领养单id
     * @return 审核频率 [0]:按周 [1]:按月
     */
    @GetMapping("/feign/getAdoptAfy/{atid}")
    public Integer getAdoptAfy(@PathVariable Long atid) {
        Adopt byId = adoptService.getById(atid);
        if (byId == null) {
            return null;
        }
        return byId.getAfy();
    }

    /**
     * Feign内部请求，根据领养单ID获取对应用户ID
     *
     * @param atid 领养单ID
     * @return 用户ID
     */
    @GetMapping("/feign/getAdoptUid/{atid}")
    public Long getAdoptUid(@PathVariable Long atid) {
        Adopt byId = adoptService.getById(atid);
        return byId.getUid();
    }

    /**
     * Feign内部请求，根据领养单ID获取对应动物ID
     *
     * @param atid 领养单ID
     * @return 动物ID
     */
    @GetMapping("/feign/getAdoptAid/{atid}")
    public Long getAdoptAid(@PathVariable Long atid) {
        Adopt byId = adoptService.getById(atid);
        return byId.getAid();
    }
}
