package com.dk.provider.oem.mapper;

import com.dk.provider.oem.entity.OemInfo;

public interface OemInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OemInfo record);

    int insertSelective(OemInfo record);

    OemInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OemInfo record);

    int updateByPrimaryKey(OemInfo record);
}