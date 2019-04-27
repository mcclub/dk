package com.dk.provider.classI.mapper;

import com.dk.provider.classI.entity.ClassRate;

public interface ClassRateMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ClassRate record);

    int insertSelective(ClassRate record);

    ClassRate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ClassRate record);

    int updateByPrimaryKey(ClassRate record);
}