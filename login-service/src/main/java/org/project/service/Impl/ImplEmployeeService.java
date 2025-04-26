package org.project.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.project.entity.Employee;
import org.project.mapper.EmployeeMapper;
import org.project.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class ImplEmployeeService extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
