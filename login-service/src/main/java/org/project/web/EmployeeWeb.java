package org.project.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.Employee;
import org.project.feign.common.Return;
import org.project.feign.utils.JWTUtils;
import org.project.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeWeb {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 员工管理账号登录
     *
     * @param req      请求
     * @param employee 请求体
     * @return 返回获取的员工信息
     */
    @PostMapping("/login")
    public Return<Employee> login(HttpServletRequest req, @RequestBody Employee employee) {
        String password = DigestUtils.md5DigestAsHex(employee.getEpassword().getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getEname, employee.getEname());
        Employee one = employeeService.getOne(queryWrapper);
        if (one == null) {
            return Return.error("登录失败，账号不存在。");
        }
        if (!one.getEpassword().equals(password)) {
            return Return.error("登录失败，密码错误。");

        }
        if (one.getEstatus() == 0) {
            return Return.error("登录失败，账号已禁用。");
        }

        // 更新当前用户登录时间和IP
        employeeService.updateById(one);
        // 生成获取Token并传给前端
        String toekn = JWTUtils.generateToken(one.getEname());
        req.setAttribute("userPower", one.getEpower());
        if (redisTemplate.opsForValue().get(one.getEname()) != null) {
            redisTemplate.delete(one.getEname());
        }
        // 将以用户ID:Token的形式将Token存到Redis中，并设置60分钟过期，其他微服务直接从Redis来校验当前用户操作
        redisTemplate.opsForValue().set(one.getEname(), toekn, 60, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(toekn, one.getEpower(), 60, TimeUnit.MINUTES);
        return Return.success(one, toekn);
    }

    /**
     * 员工管理账号退出
     *
     * @param ename 用户名
     * @return 返回字符串
     */
    @PostMapping("/logout")
    public Return<String> logout(@RequestBody String ename, HttpServletRequest req) {
        String[] split = ename.split("\"");

        redisTemplate.delete(split[1]);
        redisTemplate.delete(req.getHeader("Authorization"));
        return Return.success("退出成功");
    }

    /**
     * 员工列表获取
     *
     * @param page     第几页
     * @param pageSize 每页显示
     * @param name     【可选】用户名
     * @return 已整理好的用户列表
     */
    @GetMapping("/page")
    public Return<Page<Employee>> pageList(int page, int pageSize, String name) {
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like((name != null), Employee::getEname, name);
        queryWrapper.orderByDesc(Employee::getUpdatetime);

        employeeService.page(pageInfo, queryWrapper);

        return Return.success(pageInfo);
    }

    /**
     * 修改员工账号状态
     *
     * @param employee 员工信息
     * @return 结果
     */
    @PutMapping
    public Return<String> editStatus(@RequestBody Employee employee) {
        LambdaUpdateWrapper<Employee> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Employee::getEstatus, employee.getEstatus());
        updateWrapper.eq(Employee::getEid, employee.getEid());
        return employeeService.update(updateWrapper) ? Return.success("修改成功") : Return.error("修改失败");
    }

    /**
     * 新增员工信息
     *
     * @param employee 员工信息
     * @return 结果
     */
    @PostMapping
    public Return<String> addEmployee(@RequestBody Employee employee) {
        employee.setEpassword(DigestUtils.md5DigestAsHex(employee.getEpassword().getBytes()));
        employee.setEpower(0);

        return employeeService.save(employee) ? Return.success("添加成功") : Return.error("添加失败");
    }

    /**
     * 获取员工信息-修改
     *
     * @param id 员工ID
     * @return 将要修改的员工信息
     */
    @GetMapping("/{id}")
    public Return<Employee> employeEdit(@PathVariable Long id) {
        Employee byId = employeeService.getById(id);
        byId.setEpassword("");
        return Return.success(byId);
    }

    /**
     * Feign获取员工ID列表
     *
     * @return 返回员工ID列表
     */
    @GetMapping
    public List<Long> getEmployeeIds() {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Employee::getEid);

        List<Employee> list = employeeService.list(queryWrapper);
        List<Long> longs = list.stream().map(item -> item.getEid()).collect(Collectors.toList());
        return longs;
    }

    /**
     * 用户端获取审核员姓名和ID
     *
     * @return 审核员姓名和ID
     */
    @GetMapping("/userpage/employeeinfo")
    public List<Employee> userPageGetInfo() {
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("eid", "ename");

        List<Employee> list = employeeService.list(queryWrapper);
        return list;
    }
}
