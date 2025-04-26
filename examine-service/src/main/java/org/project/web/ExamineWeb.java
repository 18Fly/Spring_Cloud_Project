package org.project.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.project.dto.Examine_Adopt;
import org.project.entity.Examine;
import org.project.entity.Images;
import org.project.entity.ReturnImages;
import org.project.feign.clients.AdoptClient;
import org.project.feign.clients.AnimalClient;
import org.project.feign.clients.UserClient;
import org.project.feign.common.CustomException;
import org.project.feign.common.Return;
import org.project.service.ExamineService;
import org.project.service.ImagesService;
import org.project.utils.ImageExamine;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/examine")
public class ExamineWeb {

    //用户审核sams?charsetEncoding=UTF-8保存路径
    @Value("${save_path}")
    private String save_path;

    @Autowired
    private ExamineService examineService;

    @Autowired
    private ImagesService imagesService;

    @Autowired
    private AdoptClient adoptClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private AnimalClient animalClient;

    /**
     * 保存用户提交的审核图片
     *
     * @param file 文件对象
     * @param req  获取用户UID
     * @return 返回图片uri
     */
    @PostMapping("/upload")
    public Return<String> saveImages(@RequestBody MultipartFile file, HttpServletRequest req) {
        String originalFilename = file.getOriginalFilename();
        String[] split = new String[]{};
        if (originalFilename != null) {
            split = originalFilename.split("\\.");
        }
        String imgUrl = UUID.randomUUID() + "." + split[split.length - 1];

        File path = new File(save_path);
        if (path.exists()) {
            path.mkdirs();
        }

        File savePath = null;
        try {
            file.transferTo(savePath = new File(save_path + imgUrl));
        } catch (Exception ex) {
            log.error("图片上传出现问题\r" + ex.getMessage());
        }

        //使用metadata-extractor来分析图片文件的元数据中的创建时间，判断用户上传的审核图片合法性
        Boolean imageExamine = ImageExamine.getImageExamine(savePath);

        //保存图片与相关联用户的信息到图片历史库中
        Long uid = Long.valueOf(req.getHeader("Uid"));
        Images item = new Images(uid, imgUrl);
        imagesService.save(item);

        return imageExamine ? Return.success(imgUrl) : Return.error("图片不符合要求，请重新上传!");
    }

    /**
     * 获取审查页面分页信息
     * 再根据审核单中的领养单号，查询对应的用户、动物、员工ID
     *
     * @param page     当前页数
     * @param pageSize 每页显示
     * @param number   审核单ID
     * @return 分页信息
     */
    @GetMapping("/page")
    public Return<Page<Examine_Adopt>> getExaminePage(int page, int pageSize, Integer number, String beginTime, String endTime) {
        Page<Examine> examinePage = new Page<>(page, pageSize);
        Page<Examine_Adopt> examineAdoptPage = new Page<>();

        LambdaQueryWrapper<Examine> examineLambdaQueryWrapper = new LambdaQueryWrapper<>();
        examineLambdaQueryWrapper.eq(number != null, Examine::getEmid, number);
        examineLambdaQueryWrapper.gt(beginTime != null, Examine::getCreatetime, beginTime);
        examineLambdaQueryWrapper.lt(endTime != null, Examine::getCreatetime, endTime);
        examineLambdaQueryWrapper.orderByDesc(Examine::getUpdatetime);
        examineService.page(examinePage, examineLambdaQueryWrapper);

        BeanUtils.copyProperties(examinePage, examineAdoptPage, "records");
        List<Examine_Adopt> longs = examinePage.getRecords().stream().map(item -> {
            Examine_Adopt examineAdopt = new Examine_Adopt();
            BeanUtils.copyProperties(item, examineAdopt);

            Long atid = item.getAtid();
            List<Long> ids2Examine = adoptClient.getIds2Examine(atid);

            //[下标]0:动物ID 1:用户ID 2:审核员ID
            examineAdopt.setAid(ids2Examine.get(0));
            examineAdopt.setUid(ids2Examine.get(1));
            examineAdopt.setEid(ids2Examine.get(2));

            //根据用户ID获取用户名称
            String userName = userClient.getUserName(ids2Examine.get(1));
            examineAdopt.setUserName(userName);

            examineAdopt.setUserCallPhone(userClient.getUserCallPhone(ids2Examine.get(1)));

            return examineAdopt;
        }).collect(Collectors.toList());

        examineAdoptPage.setRecords(longs);

        return Return.success(examineAdoptPage);
    }

    /**
     * 根据传来的状态值和ID修改对应审核信息
     *
     * @param examine 状态值和审核单ID
     * @return 返回修改结果字符串
     */
    @PutMapping
    public Return<String> editExamineStatus(@RequestBody Examine examine) {
        if (examine.getEstatus() == 1) {
            Examine byId = examineService.getById(examine);
            Long adoptAid = adoptClient.getAdoptAid(byId.getAtid());
            animalClient.editAnimalStatus(adoptAid);
        }
        return examineService.updateById(examine) ? Return.success("审核状态修改成功") : Return.error("审核状态修改失败");
    }

    /**
     * Feign内部请求-新生成的领养单与审核信息关联
     *
     * @param atid 领养表ID
     * @return 保存结果
     */
    @GetMapping("/feign/relativeadopt/{atid}")
    public Boolean saveRelativeAdopt(@PathVariable Long atid) {
        if (atid == null) {
            return false;
        }
        Examine examine = new Examine();
        examine.setAtid(atid);
        boolean save = examineService.save(examine);
        return save;
    }

    /**
     * 用户页面--获取当前用户的领养单的审核信息
     *
     * @param uid 用户ID
     * @return 相关审核信息
     */
    @GetMapping("/userpage/getadoptexamine/{uid}")
    public Return<List<Examine_Adopt>> getAdoptExamine(@PathVariable Long uid) {
        if (uid == null) {
            Return.error("请携带用户ID再次请求!");
        }

        List<Long> adopts = adoptClient.getUserAtid(uid);
        if (adopts == null) {
            return Return.error("用户ID不存在或者没有相关领养信息!");
        }

        QueryWrapper<Examine> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("atid", adopts);

        List<Examine> list = examineService.list(queryWrapper);
        List<Examine_Adopt> examineAdoptList = new ArrayList<>();
        if (list == null) {
            throw new CustomException("查询出错!");
        }

        list.forEach(item -> {
            Examine_Adopt examineAdopt = new Examine_Adopt();
            BeanUtils.copyProperties(item, examineAdopt);
            Integer uAfy = adoptClient.getAdoptAfy(item.getAtid());
            examineAdopt.setAfy(uAfy);
            examineAdoptList.add(examineAdopt);
        });
        return Return.success(examineAdoptList);
    }

    /**
     * 获取动物领养信息，用于构建ECharts数据图表
     *
     * @return 未领养和已领养的动物
     */
    @GetMapping("/getadoptanimalinfo")
    public Return<Integer[]> getAdoptAnimalInfo() {
        QueryWrapper<Examine> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("estatus", 1);
        List<Examine> list = examineService.list(queryWrapper);
        Integer animalCount = animalClient.getAnimalCount();
        if (list.isEmpty() || animalCount == null) {
            return Return.error("数据出错");
        }

        Integer[] counts = new Integer[]{animalCount - list.size(), list.size()};

        return Return.success(counts);
    }

    /**
     * 审核信息统计Echarts
     *
     * @return 审核信息状态
     */
    @GetMapping("/getadoptinfo")
    public Return<Integer[]> getAdoptStatusInfo() {
        QueryWrapper<Examine> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("estatus", new Integer[]{0, 1, -1});
        queryWrapper.groupBy("estatus");
        queryWrapper.select("count(*) as total");
        List<Map<String, Object>> maps = examineService.listMaps(queryWrapper);
        List<String> total = maps.stream().map(item -> item.get("total").toString()).collect(Collectors.toList());
        Integer[] values = new Integer[]{Integer.valueOf(total.get(0)), Integer.valueOf(total.get(1)), Integer.valueOf(total.get(2))};

        return Return.success(values);
    }

    /**
     * 获取用户上传的相关多张图片 展示回传照片
     *
     * @param uid 用户id
     * @return 用户上传的图片url链接
     */
    @GetMapping("/getimages/{uid}")
    public Return<List<ReturnImages>> getUserSubmitImages(@PathVariable Long uid) {
        LambdaQueryWrapper<Images> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Images::getEu, 1);
        queryWrapper.eq(Images::getUid, uid);

        List<Images> list = imagesService.list(queryWrapper);
        if (list.isEmpty()) {
            return Return.error("没有相关信息!");
        }

        List<ReturnImages> values = new ArrayList<>();
        list.forEach(item -> {
            ReturnImages temp = new ReturnImages();
            temp.setName(item.getEuimages());
            temp.setUrl("http://localhost/backend/images/examine/" + item.getEuimages());
            values.add(temp);
        });

        return Return.success(values);
    }

    /**
     * 删除用户上传过的照片（仅删除逻辑关系，但文件仍存在磁盘中）
     *
     * @param image 图片名称
     * @return 返回删除结果
     */
    @DeleteMapping("/deleteimages/{image}")
    public Return<String> deleteUserImages(@PathVariable String image) {
        LambdaQueryWrapper<Images> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Images::getEuimages, image);

        boolean remove = imagesService.remove(queryWrapper);
        return remove ? Return.success("删除成功") : Return.error("删除失败");
    }
}
