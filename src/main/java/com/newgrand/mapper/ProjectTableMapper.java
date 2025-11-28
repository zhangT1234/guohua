package com.newgrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.domain.model.ProjectTableModel;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Resource;

@Resource
@Mapper
public interface ProjectTableMapper extends BaseMapper<ProjectTableModel> {
}
