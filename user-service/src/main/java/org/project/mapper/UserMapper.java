package org.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.project.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
