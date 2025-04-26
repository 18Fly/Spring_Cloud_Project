package org.project.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.project.entity.User;
import org.project.mapper.UserMapper;
import org.project.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class ImplUserService extends ServiceImpl<UserMapper, User> implements UserService {
}
