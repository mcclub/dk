package com.dk.provider.classI.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.classI.entity.ClassRate;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClassRateMapper extends BaseMapper<ClassRate> {
    int deleteByPrimaryKey(Long id);

    int insert(ClassRate record);

    int insertSelective(ClassRate record);

    ClassRate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ClassRate record);

    int updateByPrimaryKey(ClassRate record);
}