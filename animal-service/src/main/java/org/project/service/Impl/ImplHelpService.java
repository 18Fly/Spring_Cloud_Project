package org.project.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.project.entity.Help;
import org.project.mapper.HelpMapper;
import org.project.service.HelpService;
import org.springframework.stereotype.Service;

@Service
public class ImplHelpService extends ServiceImpl<HelpMapper, Help> implements HelpService {
}
