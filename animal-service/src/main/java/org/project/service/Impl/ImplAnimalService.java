package org.project.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.project.entity.Animal;
import org.project.mapper.AnimalMapper;
import org.project.service.AnimalService;
import org.springframework.stereotype.Service;

@Service
public class ImplAnimalService extends ServiceImpl<AnimalMapper, Animal> implements AnimalService {
}
