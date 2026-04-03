package com.newgrand.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.newgrand.domain.model.ItemData;
import com.newgrand.mapper.ItemDataMapper;
import com.newgrand.service.ItemDataService;
import org.springframework.stereotype.Service;

@Service
public class ItemDataServiceImpl extends ServiceImpl<ItemDataMapper, ItemData> implements ItemDataService {
}
