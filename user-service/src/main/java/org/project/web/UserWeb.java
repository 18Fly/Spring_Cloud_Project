package org.project.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.User;
import org.project.feign.common.Return;
import org.project.feign.utils.JWTUtils;
import org.project.service.UserService;
import org.project.utils.OnlineUserCounterService;
import org.project.utils.VerCodeGenerateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserWeb {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private OnlineUserCounterService counterService;

    /**
     * 获取用户分页信息
     *
     * @param page     当前页数
     * @param pageSize 每页展示信息
     * @param name     姓名搜索（可选）
     * @return 返回用户分页信息
     */
    @GetMapping("/page")
    public Return<Page<User>> getUserPage(int page, int pageSize, String name) {
        Page<User> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, User::getUname, name);
        queryWrapper.orderByDesc(User::getUpdatetime);

        userService.page(pageInfo, queryWrapper);

        return Return.success(pageInfo);
    }

    /**
     * 添加用户信息
     *
     * @param user 要添加的用户信息
     * @return 返回添加结果
     */
    @PostMapping
    public Return<String> addUser(@RequestBody User user) {
        user.setUpassword(DigestUtils.md5DigestAsHex(user.getUpassword().getBytes()));

        return userService.save(user) ? Return.success("添加成功") : Return.error("添加失败");
    }

    /**
     * 根据UID获取用户信息
     *
     * @param uid 用户ID
     * @return 根据ID获取的信息
     */
    @GetMapping("/{uid}")
    public Return<User> editUser(@PathVariable Long uid) {
        User byId = userService.getById(uid);
        return byId != null ? Return.success(byId) : Return.error("获取当前用户失败");
    }

    /**
     * feign内部请求-根据UID获取用户姓名
     *
     * @param uid 用户ID
     * @return 根据ID获取的信息
     */
    @GetMapping("/feign/{uid}")
    public String getUserName(@PathVariable Long uid) {
        User byId = userService.getById(uid);
        return byId != null ? byId.getUname() : "";
    }

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 返回结果
     */
    @PutMapping
    public Return<String> updateUser(@RequestBody User user) {
        return userService.updateById(user) ? Return.success("修改成功") : Return.error("修改出错");
    }

    /**
     * Feign获取用户ID列表
     *
     * @return 用户ID列表
     */
    @GetMapping
    public List<Long> getUserIds() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getUid);

        List<User> list = userService.list(queryWrapper);
        List<Long> longs = list.stream().map(item -> item.getUid()).collect(Collectors.toList());

        return longs;
    }

    /**
     * Feign内部根据用户ID获取用户手机号
     *
     * @param id 用户ID
     * @return 手机号
     */
    @GetMapping("/callphone/{id}")
    public String getUserCallPhone(@PathVariable Long id) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(User::getUcallphone);
        lambdaQueryWrapper.eq(User::getUid, id);

        User one = userService.getOne(lambdaQueryWrapper);

        return one != null ? one.getUcallphone() : "手机号查询失败";
    }

    /**
     * 用户登录验证
     *
     * @param user 传过来的用户登录信息
     * @return 返回相关用户信息结果
     */
    @PostMapping("/login")
    public Return<User> authUserLogin(@RequestBody User user) {
        if ("".equals(user.getUname()) || "".equals(user.getUpassword()) || user.getUname() == null || user.getUpassword() == null) {
            return Return.error("请正确填写用户和密码!");
        }

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUname, user.getUname());
        lambdaQueryWrapper.eq(User::getUpassword, user.getUpassword());

        User one = userService.getOne(lambdaQueryWrapper);

        if (one == null) {
            return Return.error("登录失败,用户名/密码错误!");
        }

        if (one.getUstatus() == 0) {
            return Return.error("登录失败，用户已被禁用！");
        }

        String tokenUser = JWTUtils.generateTokenUser(one.getUname());
        if (redisTemplate.opsForValue().get(user.getUname()) != null) {
            redisTemplate.delete(user.getUname());
        }
        redisTemplate.opsForValue().set(user.getUname(), tokenUser, 60, TimeUnit.MINUTES);
        counterService.incrementOnlineUserCount();

        userService.updateById(user);

        return Return.success(one, tokenUser);
    }

    /**
     * 给注册用户发送邮件验证码
     *
     * @param user 注册用户部分信息
     * @return 邮箱验证码发送结果
     */
    @GetMapping("/register/getcode")
    public Return<String> authUserRegister(User user) {
        if (user.getUname() == null || "".equals(user.getUname()) || user.getUpassword() == null || "".equals(user.getUpassword()) || user.getUemail() == null || "".equals(user.getUemail())) {
            return Return.error("请输入要注册的用户和密码!");
        }

        //检测当前注册用户是否已存在/被禁用
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUemail, user.getUemail());
        User one = userService.getOne(queryWrapper);
        if (one != null && one.getUstatus() == 0) {
            return Return.error("注册失败，账号及其关联邮箱地址已存在/已禁用!");
        }
        //获取随机四位验证码
        String emailCode = VerCodeGenerateUtil.getVerCode();
        //把验证码存在Redis中，有效期60秒
        redisTemplate.opsForValue().set(emailCode, user.getUemail(), 60, TimeUnit.SECONDS);
        //发送方邮箱
        String to = user.getUemail();
        //邮件主题
        String subject = "流浪动物领养系统[验证码发送]";
        //邮件内容
        String text = "您的验证码为：" + emailCode + "，有效期为60秒!";
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("zhiweieee@foxmail.com");
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        javaMailSender.send(mailMessage);

        return Return.success("验证码已发送!有效期60秒!");
    }

    /**
     * 验证验证码正确性并注册用户
     *
     * @param vCode 验证码
     * @param user  用户名和密码信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Return<String> registerUser(String vCode, @RequestBody User user) {
        if (vCode == null || "".equals(vCode) || user == null) {
            return Return.error("请输入正确的用户名、密码、邮箱以及验证码");
        }

        //数据库必填字段，默认性别男、年龄18、默认头像
        user.setUsex(0);
        user.setUage(18);
        user.setUimage("83f7da2c-2f7a-4f58-8c23-2a0528bb-3432-4f0b-be9e-4db9c9b8af35.png");

        String o = (String) redisTemplate.opsForValue().get(vCode);
        if (o == null) {
            return Return.error("验证码错误!");
        }
        if (!o.equals(user.getUemail())) {
            return Return.error("验证码错误!");
        }

        redisTemplate.delete(vCode);
        user.setUstatus(1);
        boolean save = userService.save(user);

        return save ? Return.success("注册成功！") : Return.error("注册失败！");
    }

    /**
     * 修改用户个人信息
     *
     * @param user 要修改成的用户信息
     * @return 把修改成的用户信息返回给前端再次存在localStorage
     */
    @PostMapping("/edit")
    public Return<User> editUserInfo(@RequestBody User user) {
        if (user == null || user.getUid() == null) {
            return Return.error("修改失败，请携带修改内容！");
        }

        boolean b = userService.updateById(user);
        return b ? Return.success(user) : Return.error("修改失败");
    }

    /**
     * 获取用户一周内的登录次数
     *
     * @return 用户活跃度数组信息
     */
    @GetMapping("/getuseractive")
    public Return<List<List<Object>>> getUserActive() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<List<Object>> returnValues = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            LocalDateTime beginTime = localDateTime.with(LocalTime.MIN).minusDays(i);
            LocalDateTime endTime = localDateTime.with(LocalTime.MAX).minusDays(i);
            queryWrapper.gt("updatetime", beginTime);
            queryWrapper.lt("updatetime", endTime);
            long count = userService.count(queryWrapper);

            List<Object> objectList = new ArrayList<>();
            objectList.add(beginTime.format(formatter));
            objectList.add(count);

            returnValues.add(objectList);
        }

        return Return.success(returnValues);
    }

    /**
     * 获取当前在线用户
     *
     * @return 当前活跃用户
     */
    @GetMapping("/getusercount")
    public Return<?> getUserCount() {
        return Return.success((int) counterService.getOnlineUserCount());
    }
}
