package com.newgrand.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.newgrand.domain.model.ProjectTableModel;
import com.newgrand.mapper.ProjectTableMapper;
import com.newgrand.service.ProjectTableService;
import org.springframework.stereotype.Service;

/**
 * @author 赵洋
 */
@Service
public class ProjectTableServiceImpl extends ServiceImpl<ProjectTableMapper, ProjectTableModel> implements ProjectTableService {
}
