package org.project.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.project.entity.Examine;
import org.project.mapper.ExamineMapper;
import org.project.service.ExamineService;
import org.springframework.stereotype.Service;

@Service
public class ImplExamineService extends ServiceImpl<ExamineMapper, Examine> implements ExamineService {

}
