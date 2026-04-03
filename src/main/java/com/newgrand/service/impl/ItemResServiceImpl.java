package com.newgrand.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.newgrand.domain.model.ItemRes;
import com.newgrand.mapper.ItemResMapper;
import com.newgrand.service.ItemResService;
import org.springframework.stereotype.Service;

@Service
public class ItemResServiceImpl extends ServiceImpl<ItemResMapper, ItemRes> implements ItemResService {
}
