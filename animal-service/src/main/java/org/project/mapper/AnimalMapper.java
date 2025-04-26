package org.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.project.entity.Animal;

@Mapper
public interface AnimalMapper extends BaseMapper<Animal> {
}
