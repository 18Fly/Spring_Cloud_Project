package org.project.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.project.entity.Adopt;
import org.project.mapper.AdoptMapper;
import org.project.service.AdoptService;
import org.springframework.stereotype.Service;

@Service
public class ImplAdoptService extends ServiceImpl<AdoptMapper, Adopt> implements AdoptService {
}
