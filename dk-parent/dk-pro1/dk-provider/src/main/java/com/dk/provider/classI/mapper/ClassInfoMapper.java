package com.dk.provider.classI.mapper;

import com.dk.provider.classI.entity.ClassInfo;

public interface ClassInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ClassInfo record);

    int insertSelective(ClassInfo record);

    ClassInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ClassInfo record);

    int updateByPrimaryKey(ClassInfo record);
}