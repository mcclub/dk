package com.dk.provider.plat.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.plat.entity.SubUser;

import java.util.Map;

public interface SubUserMapper extends BaseMapper<SubUser> {
    int deleteByPrimaryKey(Long id);

    int insert(SubUser record);

    int insertSelective(SubUser record);

    SubUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SubUser record);

    int updateByPrimaryKey(SubUser record);

    SubUser queryUser(Map map);
}